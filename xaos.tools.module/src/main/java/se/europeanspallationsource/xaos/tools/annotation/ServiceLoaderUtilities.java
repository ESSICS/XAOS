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


import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Provides few more methods to {@link ServiceLoader}.
 * <p>
 * <b>Note:</b> when using {@link ServiceLoaderUtilities} the service provider
 * interface type must be listed in the {@code module-info} class inside a
 * <b>uses</b> statement.
 * </p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ServiceLoaderUtilities {

	/**
	 * Returns the {@link List} of classes implementing the given service
	 * provider interface ({@code spi}). A {@link ServiceLoader} is created for
	 * the given {@code service} type, using the current thread's context class
	 * loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service)
	 *     .map(Provider::type)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @return The {@link List} of found service provider classes.
	 */
	public static <S> List<Class<? extends S>> classesOf( Class<S> service ) {
		return stream(service)
			.map(Provider::type)
			.collect(Collectors.toList());
	}

	/**
	 * Returns the {@link List} of classes implementing the given service
	 * provider interface ({@code spi}). A {@link ServiceLoader} is created for
	 * the given {@code service} type, using the given class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service, loader)
	 *     .map(Provider::type)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or {@code null} if the system
	 *                class loader (or, failing that, the bootstrap class
	 *                loader) is to be used.
	 * @return The {@link List} of found service provider classes.
	 */
	public static <S> List<Class<? extends S>> classesOf( Class<S> service, ClassLoader loader ) {
		return stream(service, loader)
			.map(Provider::type)
			.collect(Collectors.toList());
	}

	/**
	 * Load the first available service provider of the given service provider
	 * interface ({@code spi}). A {@link ServiceLoader} is created for the given
	 * {@code service} type, using the current thread's context class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service)
	 *     .map(Provider::get)
	 *     .findFirst();</pre><p>
	 * The following example loads the first available service provider. If no
	 * service providers are located then it uses a default implementation.</p><pre>
	 *   CodecFactory factory = ServiceLoaderUtilities.findFirst(CodecFactory.class)
	 *     .orElse(DEFAULT_CODECSET_FACTORY);</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @return The first service provider or empty {@link Optional} if no
	 *         service providers are located.
	 */
	public static <S> Optional<S> findFirst( Class<S> service ) {
		return stream(service)
			.map(Provider::get)
			.findFirst();
	}

	/**
	 * Load the first available service provider of the given service provider
	 * interface ({@code spi}). A {@link ServiceLoader} is created for the given
	 * {@code service} type, using the given class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service, loader)
	 *     .map(Provider::get)
	 *     .findFirst();</pre><p>
	 * The following example loads the first available service provider. If no
	 * service providers are located then it uses a default implementation.</p><pre>
	 *   CodecFactory factory = ServiceLoaderUtilities.findFirst(CodecFactory.class)
	 *     .orElse(DEFAULT_CODECSET_FACTORY);</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or {@code null} if the system
	 *                class loader (or, failing that, the bootstrap class
	 *                loader) is to be used.
	 * @return The first service provider or empty {@link Optional} if no
	 *         service providers are located.
	 */
	public static <S> Optional<S> findFirst( Class<S> service, ClassLoader loader ) {
		return stream(service, loader)
			.map(Provider::get)
			.findFirst();
	}

	/**
	 * Returns the {@link List} of implementers of the given service provider
	 * interface ({@code spi}). A {@link ServiceLoader} is created for the given
	 * {@code service} type, using the current thread's context class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service)
	 *     .map(Provider::get)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @return The {@link List} of found service provider implementers.
	 */
	public static <S> List<S> of( Class<S> service ) {
		return stream(service)
			.map(Provider::get)
			.collect(Collectors.toList());
	}

	/**
	 * Returns the {@link List} of implementers of the given service provider
	 * interface ({@code spi}). A {@link ServiceLoader} is created for the given
	 * {@code service} type, using the given class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service, loader)
	 *     .map(Provider::get)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or {@code null} if the system
	 *                class loader (or, failing that, the bootstrap class
	 *                loader) is to be used.
	 * @return The {@link List} of found service provider implementers.
	 */
	public static <S> List<S> of( Class<S> service, ClassLoader loader ) {
		return stream(service, loader)
			.map(Provider::get)
			.collect(Collectors.toList());
	}

	/**
	 * Returns the {@link List} of {@link Provider}s implementing the given
	 * service provider interface ({@code spi}). A {@link ServiceLoader} is
	 * created for the given {@code service} type, using the current thread's
	 * context class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @return The {@link List} of found service {@link Provider}s.
	 */
	public static <S> List<Provider<S>> providersOf( Class<S> service ) {
		return stream(service)
			.collect(Collectors.toList());
	}

	/**
	 * Returns the {@link List} of {@link Provider}s implementing the given
	 * service provider interface ({@code spi}). A {@link ServiceLoader} is
	 * created for the given {@code service} type, using the given class loader.
	 * <p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(service, loader)
	 *     .collect(Collectors.toList());</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The meta-class representing the service provider interface.
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or {@code null} if the system
	 *                class loader (or, failing that, the bootstrap class
	 *                loader) is to be used.
	 * @return The {@link List} of found service {@link Provider}s.
	 */
	public static <S> List<Provider<S>> providersOf( Class<S> service, ClassLoader loader ) {
		return stream(service, loader)
			.collect(Collectors.toList());
	}

	/**
	 * Returns a sorted stream to lazily load available providers of the given
	 * {@code loader}'s service. The stream elements are of type
	 * {@link Provider}. The {@link Provider}'s get method must be invoked to
	 * get or instantiate the provider.
	 * <p>
	 * If the provider class is annotated with {@link ServiceProvider}, and a
	 * specific <i>order</i> number is given, then it will be used to sort the
	 * elements of the returned stream (low order first). Non annotated
	 * providers and annotated ones whose <i>order</i> is not explicitly set
	 * will be the last ones in the returned stream.
	 * </p><p>
	 * This is equivalent to call
	 * </p><pre>
	 *   loader
	 *     .stream()
	 *     .sorted(( p1, p2 ) -&gt; {
	 *       Class&lt;? extends S&gt; t1 = p1.type();
	 *       int o1 = t1.isAnnotationPresent(ServiceProvider.class)
	 *              ? t1.getAnnotation(ServiceProvider.class).order()
	 *              : Integer.MAX_VALUE;
	 *       Class&lt;? extends S&gt; t2 = p2.type();
	 *       int o2 = t2.isAnnotationPresent(ServiceProvider.class)
	 *              ? t2.getAnnotation(ServiceProvider.class).order()
	 *              : Integer.MAX_VALUE;
	 *     return o1 - o2;
	 *   });</pre>
	 *
	 * @param <S>    The service provider interface type.
	 * @param loader The {@link ServiceLoader} for which a sorted {@link Stream}
	 *               must be returned.
	 * @return A sorted stream that lazily loads providers for this loader's
	 *         service.
	 * @see ServiceLoader#stream()
	 */
	public static <S> Stream<Provider<S>> stream( ServiceLoader<S> loader ) {
		return loader
			.stream()
			.sorted(( p1, p2 ) -> {

				Class<? extends S> t1 = p1.type();
				int o1 = t1.isAnnotationPresent(ServiceProvider.class)
						 ? t1.getAnnotation(ServiceProvider.class).order()
						 : Integer.MAX_VALUE;
				Class<? extends S> t2 = p2.type();
				int o2 = t2.isAnnotationPresent(ServiceProvider.class)
						 ? t2.getAnnotation(ServiceProvider.class).order()
						 : Integer.MAX_VALUE;

				return o1 - o2;

			});
	}

	/**
	 * Returns a sorted stream to lazily load available providers of the
	 * {@link ServiceLoader} created for the given {@code service} type, using
	 * the current thread's context class loader. The stream elements are of
	 * type {@link Provider}. The {@link Provider}'s get method must be invoked
	 * to get or instantiate the provider.
	 * <p>
	 * If the provider class is annotated with {@link ServiceProvider}, and a
	 * specific <i>order</i> number is given, then it will be used to sort the
	 * elements of the returned stream (low order first). Non annotated
	 * providers and annotated ones whose <i>order</i> is not explicitly set
	 * will be the last ones in the returned stream.
	 * </p><p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(ServiceLoader.load(service));</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The interface or abstract class representing the service.
	 * @return A sorted stream that lazily loads providers for this loader's
	 *         service.
	 * @see ServiceLoader#stream()
	 * @see ServiceLoader#load(Class)
	 */
	public static <S> Stream<Provider<S>> stream( Class<S> service ) {
		return stream(ServiceLoader.load(service));
	}

	/**
	 * Returns a sorted stream to lazily load available providers of the
	 * {@link ServiceLoader} created for the given {@code service} type, using
	 * the given class loader. The stream elements are of type {@link Provider}.
	 * The {@link Provider}'s get method must be invoked to get or instantiate
	 * the provider.
	 * <p>
	 * If the provider class is annotated with {@link ServiceProvider}, and a
	 * specific <i>order</i> number is given, then it will be used to sort the
	 * elements of the returned stream (low order first). Non annotated
	 * providers and annotated ones whose <i>order</i> is not explicitly set
	 * will be the last ones in the returned stream.
	 * </p><p>
	 * This is equivalent to call
	 * </p><pre>
	 *   ServiceLoaderUtilities.stream(ServiceLoader.load(service, loader));</pre>
	 *
	 * @param <S>     The service provider interface type.
	 * @param service The interface or abstract class representing the service.
	 * @param loader  The class loader to be used to load provider-configuration
	 *                files and provider classes, or {@code null} if the system
	 *                class loader (or, failing that, the bootstrap class
	 *                loader) is to be used.
	 * @return A sorted stream that lazily loads providers for this loader's
	 *         service.
	 * @see ServiceLoader#stream()
	 * @see ServiceLoader#load(Class, ClassLoader)
	 */
	public static <S> Stream<Provider<S>> stream( Class<S> service, ClassLoader loader ) {
		return stream(ServiceLoader.load(service, loader));
	}

	private ServiceLoaderUtilities() {
	}

}
