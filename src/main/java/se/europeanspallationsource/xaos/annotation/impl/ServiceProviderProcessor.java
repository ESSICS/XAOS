/*
 * Copyright 2018 European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.annotation.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import se.europeanspallationsource.xaos.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.annotation.ServiceProviders;

import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.PACKAGE;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_PATH;


/**
 * {@link Processor} for the {@link ServiceProvider} source-level annotation.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="http://bits.netbeans.org/8.1/javadoc/org-openide-util-lookup/overview-summary.html">NetBeans Lookup API</a>
 */
@SupportedSourceVersion( RELEASE_8 )
@SupportedAnnotationTypes( {
	"se.europeanspallationsource.xaos.annotation.ServiceProvider",
	"se.europeanspallationsource.xaos.annotation.ServiceProviders"
} )
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceProviderProcessor extends AbstractProcessor {

	private final Map<Filer, Map<String, List<Element>>> originatingElementsByProcessor = new WeakHashMap<>();
	private final Map<Filer, Map<String, SortedSet<ServiceLoaderLine>>> outputFilesByProcessor = new WeakHashMap<>();
	private final Map<TypeElement, Boolean> verifiedClasses = new WeakHashMap<>();

	public ServiceProviderProcessor() {
		//	public for ServiceLoader
	}

	@Override
	public Iterable<? extends Completion> getCompletions( Element annotated, AnnotationMirror annotation, ExecutableElement attr, String userText ) {

		if ( processingEnv == null || annotated == null || !annotated.getKind().isClass() ) {
			return Collections.emptyList();
		} else if ( annotation == null || !"se.europeanspallationsource.framework.annotation.ServiceProvider".contentEquals(( (QualifiedNameable) annotation.getAnnotationType().asElement() ).getQualifiedName()) ) {
			return Collections.emptyList();
		} else if ( !"service".contentEquals(attr.getSimpleName()) ) {
			return Collections.emptyList();
		}

		TypeElement jlObject = processingEnv.getElementUtils().getTypeElement("java.lang.Object");

		if ( jlObject == null ) {
			return Collections.emptyList();
		}

		Collection<Completion> result = new LinkedList<>();
		List<TypeElement> toProcess = new LinkedList<>();

		toProcess.add((TypeElement) annotated);

		while ( !toProcess.isEmpty() ) {

			TypeElement c = toProcess.remove(0);

			result.add(new TypeCompletion(c.getQualifiedName().toString() + ".class"));

			List<TypeMirror> parents = new LinkedList<>();

			parents.add(c.getSuperclass());
			parents.addAll(c.getInterfaces());
			parents.stream()
				.filter(tm -> tm != null && tm.getKind() == DECLARED)
				.map(tm -> (TypeElement) processingEnv.getTypeUtils().asElement(tm))
				.filter(type -> !jlObject.equals(type))
				.forEachOrdered(type -> toProcess.add(type));

		}

		return result;
	}

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {

		if ( roundEnv.errorRaised() ) {
			return false;
		}

		if ( roundEnv.processingOver() ) {
			writeServices();
			outputFilesByProcessor.clear();
			originatingElementsByProcessor.clear();
			return true;
		} else {
			return handleProcess(annotations, roundEnv);
		}

	}

	/**
	 * Register a service.
	 * If the class does not have an appropriate signature, an error will be printed and the registration skipped.
	 *
	 * @param el         The annotated element.
	 * @param annotation The (top-level) annotation registering the service, for diagnostic purposes.
	 * @param type       The type to which the implementation must be assignable.
	 * @param order      A position at which to register, or {@link Integer#MAX_VALUE} to skip.
	 */
	protected final void register( Element el, Class<? extends Annotation> annotation, TypeMirror type, int order ) {

		if ( el.getKind() != CLASS ) {
			processingEnv.getMessager().printMessage(
				ERROR,
				annotation.getName() + " is not applicable to a " + el.getKind(),
				el
			);
			return;
		} else if ( el.getEnclosingElement().getKind() == CLASS && !el.getModifiers().contains(STATIC) ) {
			processingEnv.getMessager().printMessage(
				ERROR,
				"Inner class needs to be static to be annotated with @ServiceProvider",
				el
			);
			return;
		}

		TypeElement clazz = (TypeElement) el;
		String impl = processingEnv.getElementUtils().getBinaryName(clazz).toString();
		String xface = processingEnv.getElementUtils().getBinaryName((TypeElement) processingEnv.getTypeUtils().asElement(type)).toString();

		if ( !processingEnv.getTypeUtils().isAssignable(clazz.asType(), type) ) {

			AnnotationMirror ann = findAnnotationMirror(clazz, annotation);

			processingEnv.getMessager().printMessage(
				ERROR,
				impl + " is not assignable to " + xface,
				clazz,
				ann,
				findAnnotationValue(ann, "service")
			);

			return;
		}

		String rsrc = "META-INF/services/" + xface;
		Boolean verify = verifiedClasses.get(clazz);

		if ( verify == null ) {

			verify = verifyServiceProviderSignature(clazz, annotation);

			verifiedClasses.put(clazz, verify);

		}

		if ( !verify ) {
			return;
		}

		registerImpl(clazz, impl, rsrc, order);

	}

	/**
	 * @param element    A source element.
	 * @param annotation A type of annotation.
	 * @return The instance of that annotation on the element, or {@code null}
	 *         if not found.
	 */
	private AnnotationMirror findAnnotationMirror( Element element, Class<? extends Annotation> annotation ) {

		for ( AnnotationMirror ann : element.getAnnotationMirrors() ) {
			if ( processingEnv.getElementUtils().getBinaryName((TypeElement) ann.getAnnotationType().asElement()).contentEquals(annotation.getName()) ) {
				return ann;
			}
		}

		return null;

	}

	/**
	 * @param annotation An annotation instance ({@code null} permitted).
	 * @param name       The name of an attribute of that annotation.
	 * @return The corresponding value if found.
	 */
	private AnnotationValue findAnnotationValue( AnnotationMirror annotation, String name ) {

		if ( annotation != null ) {
			for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet() ) {
				if ( entry.getKey().getSimpleName().contentEquals(name) ) {
					return entry.getValue();
				}
			}
		}

		return null;

	}

	/**
	 * The regular body of {@link #process}. Called during regular rounds if
	 * there are no outstanding errors. In the last round, one of the processors
	 * will write out generated registrations.
	 *
	 * @param annotations As in {@link #process}.
	 * @param roundEnv    As in {@link #process}.
	 * @return As in {@link #process}.
	 */
	private boolean handleProcess( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {

		roundEnv.getElementsAnnotatedWith(ServiceProvider.class).forEach(( el ) -> {

			ServiceProvider sp = el.getAnnotation(ServiceProvider.class);

			if ( sp != null ) {
				register(el, ServiceProvider.class, sp);
			}

		});

		roundEnv.getElementsAnnotatedWith(ServiceProviders.class).forEach(( el ) -> {

			ServiceProviders spp = el.getAnnotation(ServiceProviders.class);

			if ( spp != null ) {
				for ( ServiceProvider sp : spp.value() ) {
					register(el, ServiceProviders.class, sp);
				}
			}

		});

		return true;

	}

	private void register( Element clazz, Class<? extends Annotation> annotation, ServiceProvider svc ) {
		try {
			svc.service();
			assert false;
		} catch ( MirroredTypeException ex ) {
			register(clazz, annotation, ex.getTypeMirror(), svc.order());
		}
	}

	private void registerImpl( TypeElement clazz, String impl, String rsrc, int order ) {

		Filer filer = processingEnv.getFiler();
		Map<String, List<Element>> originatingElements = originatingElementsByProcessor.get(filer);

		if ( originatingElements == null ) {

			originatingElements = new HashMap<>(3);

			originatingElementsByProcessor.put(filer, originatingElements);

		}

		List<Element> origEls = originatingElements.get(rsrc);

		if ( origEls == null ) {

			origEls = new ArrayList<>(3);

			originatingElements.put(rsrc, origEls);

		}

		origEls.add(clazz);

		Map<String, SortedSet<ServiceLoaderLine>> outputFiles = outputFilesByProcessor.get(filer);

		if ( outputFiles == null ) {

			outputFiles = new HashMap<>(3);

			outputFilesByProcessor.put(filer, outputFiles);

		}

		SortedSet<ServiceLoaderLine> lines = outputFiles.get(rsrc);

		if ( lines == null ) {

			lines = new TreeSet<>();

			try {

				try {

					FileObject in = filer.getResource(SOURCE_PATH, "", rsrc);

					in.openInputStream().close();
					processingEnv.getMessager().printMessage(
						ERROR,
						"Cannot generate " + rsrc + " because it already exists in sources: " + in.toUri()
					);

					return;

				} catch ( NullPointerException ex ) {
					// trying to prevent java.lang.NullPointerException
					// at com.sun.tools.javac.util.DefaultFileManager.getFileForOutput(DefaultFileManager.java:1078)
					// at com.sun.tools.javac.util.DefaultFileManager.getFileForOutput(DefaultFileManager.java:1054)
					// at com.sun.tools.javac.processing.JavacFiler.getResource(JavacFiler.java:434)
					// at org.netbeans.modules.openide.util.AbstractServiceProviderProcessor.register(AbstractServiceProviderProcessor.java:163)
					// at org.netbeans.modules.openide.util.ServiceProviderProcessor.register(ServiceProviderProcessor.java:99)
				} catch ( FileNotFoundException | NoSuchFileException x ) {
					// Good.
				}

				try {

					FileObject in = filer.getResource(CLASS_OUTPUT, "", rsrc);

					try ( InputStream is = in.openInputStream() ) {
						ServiceLoaderLine.parse(new InputStreamReader(is, "UTF-8"), lines); // NOI18N
					}

				} catch ( FileNotFoundException | NoSuchFileException x ) {
					// OK, created for the first time
				}
			} catch ( IOException x ) {
				processingEnv.getMessager().printMessage(ERROR, x.toString());
				return;
			}

			outputFiles.put(rsrc, lines);

		}

		lines.add(new ServiceLoaderLine(impl, order));

	}

	private boolean verifyServiceProviderSignature( TypeElement clazz, Class<? extends Annotation> annotation ) {

		AnnotationMirror ann = findAnnotationMirror(clazz, annotation);

		if ( !clazz.getModifiers().contains(PUBLIC) ) {
			processingEnv.getMessager().printMessage(ERROR, clazz + " must be public", clazz, ann);
			return false;
		} else if ( clazz.getModifiers().contains(ABSTRACT) ) {
			processingEnv.getMessager().printMessage(ERROR, clazz + " must not be abstract", clazz, ann);
			return false;
		} else if ( clazz.getEnclosingElement().getKind() != PACKAGE && !clazz.getModifiers().contains(STATIC) ) {
			processingEnv.getMessager().printMessage(ERROR, clazz + " must be static", clazz, ann);
			return false;
		}

		boolean hasDefaultCtor = false;

		for ( ExecutableElement constructor : ElementFilter.constructorsIn(clazz.getEnclosedElements()) ) {
			if ( constructor.getModifiers().contains(PUBLIC) && constructor.getParameters().isEmpty() ) {
				hasDefaultCtor = true;
				break;
			}
		}

		if ( !hasDefaultCtor ) {
			processingEnv.getMessager().printMessage(ERROR, clazz + " must have a public no-argument constructor", clazz, ann);
			return false;
		}

		return true;

	}

	private void writeServices() {

		outputFilesByProcessor.entrySet().forEach(outputFiles -> {

			Filer filer = outputFiles.getKey();

			outputFiles.getValue().entrySet().forEach(entry -> {
				try {

					List<Element> elements = originatingElementsByProcessor.get(filer).get(entry.getKey());
					FileObject out = filer.createResource(
						StandardLocation.CLASS_OUTPUT,
						"",
						entry.getKey(),
						elements.toArray(new Element[elements.size()])
					);

					try (
						OutputStream os = out.openOutputStream();
						PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8")) ) {
						entry.getValue().forEach(line -> line.write(w));
						w.flush();
					}

				} catch ( IOException x ) {
					processingEnv.getMessager().printMessage(
						ERROR,
						"Failed to write to " + entry.getKey() + ": " + x.toString()
					);
				}
			});

		});

	}

	private static final class TypeCompletion implements Completion {

		private final String type;

		TypeCompletion( String type ) {
			this.type = type;
		}

		@Override
		public String getMessage() {
			return null;
		}

		@Override
		public String getValue() {
			return type;
		}

	}

}
