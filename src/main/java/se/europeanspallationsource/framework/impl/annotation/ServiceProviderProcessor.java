/*
 * Copyright 2018 claudiorosati.
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
package se.europeanspallationsource.framework.impl.annotation;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.WeakHashMap;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import se.europeanspallationsource.framework.annotation.ServiceProvider;

import static javax.lang.model.SourceVersion.RELEASE_7;
import static javax.tools.Diagnostic.Kind.ERROR;


/**
 * {@link Processor} for the {@link ServiceProvider} source-level annotation.
 * 
 * @author claudio.rosati@esss.se
 * @see <a href="http://bits.netbeans.org/8.1/javadoc/org-openide-util-lookup/overview-summary.html">NetBeans Lookup API</a>
 */
@SupportedSourceVersion(RELEASE_7)
@SupportedAnnotationTypes( {
	"se.europeanspallationsource.framework.annotation.ServiceProvider"
} )
public class ServiceProviderProcessor extends AbstractProcessor {

	private final Map<Filer, Map<String, List<Element>>> originatingElementsByProcessor = new WeakHashMap<>();
	private final Map<Filer, Map<String, SortedSet<ServiceLoaderLine>>> outputFilesByProcessor = new WeakHashMap<>();
	private final Map<TypeElement, Boolean> verifiedClasses = new WeakHashMap<>();

	public ServiceProviderProcessor() {
		//	public for ServiceLoader
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
     * The regular body of {@link #process}. Called during regular rounds if
	 * there are no outstanding errors. In the last round, one of the processors
	 * will write out generated registrations.
	 *
     * @param annotations As in {@link #process}.
     * @param roundEnv As in {@link #process}.
     * @return As in {@link #process}.
     */
	private boolean handleProcess( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {

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
						PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8"))
					) {
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

}
