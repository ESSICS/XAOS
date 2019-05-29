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
package se.europeanspallationsource.xaos.tools.annotation.impl;


import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.ModuleElement.ProvidesDirective;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
import se.europeanspallationsource.xaos.tools.annotation.ServiceProviders;
import se.europeanspallationsource.xaos.tools.lang.AbstractAnnotationProcessor;

import static javax.lang.model.SourceVersion.RELEASE_12;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.PACKAGE;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.ModuleElement.DirectiveKind.PROVIDES;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;


/**
 * {@link Processor} for the {@link ServiceProvider} source-level annotation.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="http://bits.netbeans.org/8.1/javadoc/org-openide-util-lookup/overview-summary.html">NetBeans Lookup API</a>
 */
@SupportedSourceVersion( RELEASE_12 )
@SupportedAnnotationTypes( {
	"se.europeanspallationsource.xaos.tools.annotation.ServiceProvider",
	"se.europeanspallationsource.xaos.tools.annotation.ServiceProviders"
} )
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceProviderProcessor extends AbstractAnnotationProcessor {

	private final Map<TypeElement, Boolean> verifiedClasses = new WeakHashMap<>();

	public ServiceProviderProcessor() {
		//	public for ServiceLoader
	}

	@Override
	public Iterable<? extends Completion> getCompletions( Element annotated, AnnotationMirror annotation, ExecutableElement attr, String userText ) {

		if ( processingEnv == null || annotated == null || !annotated.getKind().isClass() ) {
			return Collections.emptyList();
		} else if ( annotation == null || !"se.europeanspallationsource.xaos.tools.annotation.ServiceProvider".contentEquals(( (QualifiedNameable) annotation.getAnnotationType().asElement() ).getQualifiedName()) ) {
			return Collections.emptyList();
		} else if ( !"service".contentEquals(attr.getSimpleName()) ) {
			return Collections.emptyList();
		}

		TypeElement jlObject = getEnvironment().getElementUtils().getTypeElement("java.lang.Object");

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
				.map(tm -> (TypeElement) getEnvironment().getTypeUtils().asElement(tm))
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
			return true;
		} else {
			return handleProcess(annotations, roundEnv);
		}

	}

	/**
	 * The regular body of {@link #process}. Called during regular rounds if
	 * there are no outstanding errors. In the last round, one of the processors
	 * will write out generated registrations.
	 *
	 * @param annotations As in {@link #process}.
	 * @param roundEnvironment    As in {@link #process}.
	 * @return As in {@link #process}.
	 */
	private boolean handleProcess( Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment ) {

		roundEnvironment.getElementsAnnotatedWith(ServiceProvider.class).forEach(element -> {

			ServiceProvider sp = element.getAnnotation(ServiceProvider.class);

			if ( sp != null ) {
				handleProcess(element, ServiceProvider.class, sp);
			}

		});

		roundEnvironment.getElementsAnnotatedWith(ServiceProviders.class).forEach(element -> {

			ServiceProviders spp = element.getAnnotation(ServiceProviders.class);

			if ( spp != null ) {
				for ( ServiceProvider sp : spp.value() ) {
					handleProcess(element, ServiceProviders.class, sp);
				}
			}

		});

		return true;

	}

	private void handleProcess( Element clazz, Class<? extends Annotation> annotation, ServiceProvider svc ) {
		try {
			//	A trick to capture the TypeMirror of the service class.
			svc.service();
		} catch ( MirroredTypeException ex ) {
			handleProcess(clazz, annotation, ex.getTypeMirror());
		}
	}

	/**
	 * If the class does not have an appropriate signature, an error will be printed and the handling skipped.
	 *
	 * @param el         The annotated element.
	 * @param annotation The (top-level) annotation registering the service, for diagnostic purposes.
	 * @param type       The type to which the implementation must be assignable.
	 */
	private void handleProcess( Element el, Class<? extends Annotation> annotation, TypeMirror type ) {

		if ( el.getKind() != CLASS ) {
			getMessager().printMessage(
				ERROR,
				annotation.getName() + " is not applicable to a " + el.getKind(),
				el
			);
			return;
		} else if ( el.getEnclosingElement().getKind() == CLASS && !el.getModifiers().contains(STATIC) ) {
			getMessager().printMessage(
				ERROR,
				"Inner class needs to be static to be annotated with @ServiceProvider",
				el
			);
			return;
		}

		TypeElement clazz = (TypeElement) el;
		String impl = binaryName(clazz);
		String xface = binaryName((TypeElement) getEnvironment().getTypeUtils().asElement(type));

		if ( !getEnvironment().getTypeUtils().isAssignable(clazz.asType(), type) ) {

			AnnotationMirror ann = findAnnotationMirror(clazz, annotation);

			getMessager().printMessage(
				ERROR,
				impl + " is not assignable to " + xface,
				clazz,
				ann,
				findAnnotationValue(ann, "service")
			);
			return;

		}

		Boolean verify = verifiedClasses.get(clazz);

		if ( verify == null ) {

			verify = verifyServiceProviderSignature(clazz, annotation);

			verifiedClasses.put(clazz, verify);

		}

		if ( !verify ) {
			return;
		}

		handleProcess(clazz, (TypeElement) getEnvironment().getTypeUtils().asElement(type));

	}

	private void handleProcess( TypeElement providerElement, TypeElement serviceElement ) {

		ModuleElement moduleElement = getEnvironment().getElementUtils().getModuleOf(providerElement);
		long count = moduleElement.getDirectives().stream()
			.filter(d -> d.getKind() == PROVIDES)
			.filter(d -> ( (ProvidesDirective) d ).getService().getQualifiedName().equals(serviceElement.getQualifiedName()))
			.filter(d -> ( (ProvidesDirective) d ).getImplementations().stream().filter(e -> e.getQualifiedName().equals(providerElement.getQualifiedName())).count() > 0)
			.count();

		if ( count == 0 ) {
			getMessager().printMessage(
				WARNING,
				MessageFormat.format(
					"Missing ''provides'' directive for {0} annotated service provider."
				  + "\nAdd the following directives to the ''module-info.java'' class:"
				  + "\n------------------------------------------------------------------------"
				  + "\nuses {1};"
				  + "\nprovides {1}"
				  + "\n    with {0};"
				  + "\n------------------------------------------------------------------------",
					binaryName(providerElement),
					binaryName(serviceElement)
				)
			);
		}

	}

	private boolean verifyServiceProviderSignature( TypeElement clazz, Class<? extends Annotation> annotation ) {

		AnnotationMirror ann = findAnnotationMirror(clazz, annotation);

		if ( !clazz.getModifiers().contains(PUBLIC) ) {
			getMessager().printMessage(ERROR, clazz + " must be public", clazz, ann);
			return false;
		} else if ( clazz.getModifiers().contains(ABSTRACT) ) {
			getMessager().printMessage(ERROR, clazz + " must not be abstract", clazz, ann);
			return false;
		} else if ( clazz.getEnclosingElement().getKind() != PACKAGE && !clazz.getModifiers().contains(STATIC) ) {
			getMessager().printMessage(ERROR, clazz + " must be static", clazz, ann);
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
			getMessager().printMessage(ERROR, clazz + " must have a public no-argument constructor", clazz, ann);
			return false;
		}

		return true;

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
