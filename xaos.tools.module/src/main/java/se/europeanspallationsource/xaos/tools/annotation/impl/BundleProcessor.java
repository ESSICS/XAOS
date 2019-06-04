/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.tools.annotation.impl;


import java.io.BufferedWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import org.apache.commons.lang3.StringUtils;
import se.europeanspallationsource.xaos.tools.annotation.Bundle;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.lang.AbstractAnnotationProcessor;

import static javax.lang.model.SourceVersion.RELEASE_12;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;


/**
 *
 * @author claudio.rosati@esss.se
 */
@SupportedSourceVersion( RELEASE_12 )
@SupportedAnnotationTypes( {
	"se.europeanspallationsource.xaos.tools.annotation.Bundle",
	"se.europeanspallationsource.xaos.tools.annotation.BundleItem",
	"se.europeanspallationsource.xaos.tools.annotation.BundleItems"
} )
@SuppressWarnings( "ClassWithoutLogger" )
public class BundleProcessor extends AbstractAnnotationProcessor {

	public static final String DEFAULT_BUNDLE_NAME = "Bundle";

	/*
	 * Keys are package names.
	 * Values are maps whos keys are bundle names and values are bundle descriptors.
	 */
	private final Map<String, Map<String, BundleDescriptor>> descriptorsMap = new HashMap<>(32);

	public BundleProcessor() {
		//	public for ServiceLoader
	}

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {

		if ( roundEnv.errorRaised() ) {
			return false;
		}

		if ( roundEnv.processingOver() ) {
			return writeResourceBundles();
		} else {
			return handleProcess(annotations, roundEnv);
		}

	}

	private BundleDescriptor addBundleDescriptor( String packageName, String className, String bundleName ) {

		Map<String, BundleDescriptor> descriptors = descriptorsMap.get(packageName);

		if ( descriptors == null ) {

			descriptors = new HashMap<>(8);

			descriptorsMap.put(packageName, descriptors);

		}

		BundleDescriptor descriptor = descriptors.get(bundleName);

		if ( descriptor == null ) {

			descriptor = new BundleDescriptor(packageName, className, bundleName);

			descriptors.put(bundleName, descriptor);

		} else {
			descriptor.addProperties(className);
		}

		return descriptor;

	}

	/**
	 * Fixes up comments adding the '#' character if needed.
	 *
	 * @param comment The comment to be fixed up.
	 * @return The fixed up comment splitted in lines.
	 */
	private String[] fixup( String comment ) {

		String prefix = "";

		if ( !comment.startsWith("#") ) {

			prefix = "#";

			if ( !comment.startsWith(" ") ) {
				prefix += " ";
			}

		}

		String[] lines = comment.split("\n");

		for ( int i = 0; i < lines.length; i++ ) {
			lines[i] = prefix + lines[i];
		}

		return lines;

	}

	private void handleBundle( Element element, Bundle bundle ) {

		if ( element.getKind() != CLASS && element.getKind() != INTERFACE ) {
			getMessager().printMessage(
				ERROR,
				"@Bundle is not applicable to a " + element.getKind(),
				element
			);
			return;
		} else if ( element.getEnclosingElement().getKind() == CLASS || element.getEnclosingElement().getKind() == INTERFACE ) {
			getMessager().printMessage(
				ERROR,
				MessageFormat.format(
					"Inner class/interface [{0}] cannot be be annotated with @Bundle.",
					element.getSimpleName()
				),
				element
			);
			return;
		}

		PackageElement packageElement = getElements().getPackageOf(element);
		String packageName = packageElement.getQualifiedName().toString();
		String className = element.getSimpleName().toString();
		String bundleName = bundle.name();

		addBundleDescriptor(packageName, className, bundleName);

	}

	@SuppressWarnings( "AssignmentToMethodParameter" )
	private void handleBundleItem( Element element, Class<?> annotation, BundleItem bundleItem ) {

		String className;

		switch ( element.getKind() ) {
			case CLASS:
			case INTERFACE:
				className = element.getSimpleName().toString();
				break;
			case FIELD:
			case METHOD: {

					Element elmnt = element;

					while ( elmnt.getEnclosingElement().getKind() == CLASS
						 || elmnt.getEnclosingElement().getKind() == INTERFACE ) {
						elmnt = elmnt.getEnclosingElement();
					}

					className = elmnt.getSimpleName().toString();

				}
				break;
			default:
				getMessager().printMessage(
					ERROR,
					MessageFormat.format(
						"@{0} is not applicable to a {1}.",
						annotation.getSimpleName(),
						element.getKind()
					),
					element
				);
				return;
		}

		PackageElement packageElement = getElements().getPackageOf(element);
		String packageName = packageElement.getQualifiedName().toString();
		String bundleName = DEFAULT_BUNDLE_NAME;

		//	If the class/interfate containing the @BundleItem annotation is
		//	annotated with @Bundle, get the bundle name from this annotation.
		TypeElement typeElement = getElements().getTypeElement​(packageName + "." + className);
		Optional<? extends AnnotationMirror> optionalMirror = getElements().getAllAnnotationMirrors(typeElement).stream()
			.filter(am -> am.getAnnotationType().asElement().getSimpleName().contentEquals(Bundle.class.getSimpleName()))
			.findFirst();

		if ( optionalMirror.isPresent() ) {
			bundleName = optionalMirror.get().getElementValues().entrySet().stream()
				.filter(e -> e.getKey().getSimpleName().contentEquals("name"))
				.map(e -> e.getValue().getValue().toString())
				.findFirst()
				.orElse(DEFAULT_BUNDLE_NAME);
		}

		addBundleDescriptor(packageName, className, bundleName).addProperties(className, bundleItem);

	}

	private boolean handleProcess( Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment ) {

		roundEnvironment.getElementsAnnotatedWith(Bundle.class).forEach(element -> {

			Bundle bundle = element.getAnnotation(Bundle.class);

			if ( bundle != null ) {
				handleBundle(element, bundle);
			}

		});

		roundEnvironment.getElementsAnnotatedWith(BundleItem.class).forEach(element -> {

			BundleItem bundleItem = element.getAnnotation(BundleItem.class);

			if ( bundleItem != null ) {
				handleBundleItem(element, BundleItem.class, bundleItem);
			}

		});

		roundEnvironment.getElementsAnnotatedWith(BundleItems.class).forEach(element -> {

			BundleItems bundleItems = element.getAnnotation(BundleItems.class);

			if ( bundleItems != null ) {
				for ( BundleItem bundleItem : bundleItems.value() ) {
					handleBundleItem(element, BundleItems.class, bundleItem);
				}
			}

		});

		return true;

	}

	private boolean writeResourceBundles() {
		
		descriptorsMap.values().stream()
			.flatMap(descriptorMap -> descriptorMap.values().stream())
			.forEach(descriptor -> {
				try {

					FileObject resource = getFiler().createResource(
						CLASS_OUTPUT,
						descriptor.packageName,
						descriptor.bundleName + ".properties"
					);

					try ( BufferedWriter writer = new BufferedWriter(resource.openWriter()) ) {
						descriptor.itemsMap.entrySet().forEach(entry -> {

							String className = entry.getKey();

							entry.getValue().values().forEach(bundleItem -> {

								try {

									if ( StringUtils.isNotBlank(bundleItem.comment()) ) {

										String[] commentLines = fixup(bundleItem.comment());

										for ( String line : commentLines )  {
											writer.write(line);
											writer.newLine();
										}

									}

									writer.write(className);
									writer.write(".");
									writer.write(bundleItem.key());
									writer.write("=");
									writer.write(bundleItem.message());
									writer.newLine();

								} catch ( IOException ex ) {
									getMessager().printMessage(
										ERROR,
										MessageFormat.format(
											"Unable to write a bundle item [bundle: {0}.{1}.properties, key: {2}, message: {3}, {4}: {5}].",
											descriptor.packageName,
											descriptor.bundleName,
											bundleItem.key(),
											bundleItem.message(),
											ex.getClass().getName(),
											ex.getMessage()
										)
									);
								}

							});

							try {
								writer.newLine();
							} catch ( IOException ex ) {
								getMessager().printMessage(
									ERROR,
									MessageFormat.format(
										"Unable to write new line [bundle: {0}.{1}.properties, {2}: {3}].",
										descriptor.packageName,
										descriptor.bundleName,
										ex.getClass().getName(),
										ex.getMessage()
									)
								);
							}

						});
					}

				} catch ( IOException ex ) {
					getMessager().printMessage(
						ERROR,
						MessageFormat.format(
							"Unable to create resource {0}.{1}.properties [{2}: {3}].",
							descriptor.packageName,
							descriptor.bundleName,
							ex.getClass().getName(),
							ex.getMessage()
						)
					);
				}
			});

		return true;

	}

	private class BundleDescriptor {

		final String bundleName;
		/*
		 * Keys are class names.
		 * Values are maps whose keys are property keys, and values are bundle
		 * items.
		 */
		final SortedMap<String, SortedMap<String, BundleItem>> itemsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		final String packageName;

		BundleDescriptor ( String packageName, String className, String bundleName ) {

			this.packageName = packageName;
			this.bundleName = bundleName;

			addProperties(className);

		}

		final SortedMap<String, BundleItem> addProperties ( String className ) {

			SortedMap<String, BundleItem> items = itemsMap.get(className);

			if ( items == null ) {

				items = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

				itemsMap.put(className, items);

			}

			return items;

		}

		final void addProperties ( String className, BundleItem bundleItem ) {
			addProperties(className).put(bundleItem.key(), bundleItem);
		}

	}

}
