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


import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import se.europeanspallationsource.xaos.tools.annotation.Bundle;
import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;
import se.europeanspallationsource.xaos.tools.lang.AbstractAnnotationProcessor;

import static javax.lang.model.SourceVersion.RELEASE_12;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.ERROR;


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
				"Inner class/interface cannot be be annotated with @Bundle.",
				element
			);
			return;
		}

		PackageElement pckage = getEnvironment().getElementUtils().getPackageOf(element);





		//	TODO:CR To be implemented.
	}

	private void handleBundleItem( Element element, Class<?> annotation, BundleItem bundleItem ) {
		//	TODO:CR To be implemented.
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
		//	TODO:CR To be implemented.
		return true;
	}

}
