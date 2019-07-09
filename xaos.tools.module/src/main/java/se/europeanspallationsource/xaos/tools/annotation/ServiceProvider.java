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
package se.europeanspallationsource.xaos.tools.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Declarative verification of a singleton service provider. By marking an
 * implementation class with this annotation, you automatically verify that the
 * implementation to be loaded by {@link ServiceLoader} is valid and that a 
 * corresponding <b>provides … with</b> directive is present into the
 * {@code module-info.java} file. The service provider class must be public and
 * have a public no-argument constructor.
 *
 * <p>
 * Example of usage:</p>
 *
 * <pre>
 *   package my.module;
 *   import my.module.spi.SomeService;
 *   import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;
 *
 *   &#64;ServiceProvider(service=SomeService.class)
 *   public class MyService implements SomeService {
 *     ...
 *   }</pre>
 *
 * <p>
 * <b>Note:</b> when using {@link ServiceProvider} the {@link #service()} class
 * must be listed in the {@code module-info} class inside a <b>uses</b> statement.
 * Moreover a <b>provides … with</b> statement must also be added to declare the
 * annotated class as provider for the parameter class.</p>
 *
 * @author claudio.rosati@esss.se
 * @see <a href="http://bits.netbeans.org/8.1/javadoc/org-openide-util-lookup/overview-summary.html">NetBeans Lookup API</a>
 */
@Documented
@Repeatable(ServiceProviders.class)
@Retention( RUNTIME )
@Target( TYPE )
public @interface ServiceProvider {

    /**
     * An optional number determining the load order of this service relative to
	 * others. Lower-numbered services are returned in the lookup result first.
     * Services with no specified position are returned last (ordered by name),
	 * followed by services registered using the standard Java mechanism.
	 *
	 * @return The load order of this service relative to others.
     */
    int order() default Integer.MAX_VALUE - 1;

	/**
	 * The interface (or abstract class) to register this implementation under.
	 * It is an error if the implementation class is not in fact assignable to
	 * the interface.
	 * <p>
	 * Requests to look up the specified interface through
	 * {@link ServiceLoader#load(java.lang.Class) } should result in this
	 * implementation.
	 *
	 * @return The service interface or abstract class.
	 */
	Class<?> service();

}
