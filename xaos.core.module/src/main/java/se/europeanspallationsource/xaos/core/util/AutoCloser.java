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
package se.europeanspallationsource.xaos.core.util;


import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * This utility class allows to use non-{@link AutoCloseable} classes in
 * auto-closeable contexts.
 * <p>
 * For example, imagine to have the following class:</p>
 * <pre>
 *   public class NotAutoclosable {
 *     public NotAutoclosable() { ... }
 *     public void dispose() { ...to be called to free resources... }
 *     ...
 *   }</pre>
 * <p>
 * This class can be used in the following way:</p>
 * <pre>
 *   NotAutoclosable nac = new NotAutoclosable();
 *
 *   try {
 *     ...use nac.xxx methods...
 *   } finally {
 *     nac.dispose();
 *   }</pre>
 * <p>
 * {@link AutoCloser} simplify things without requiring to modify
 * {@code NotAutoclosable} to implement {@link AutoCloseable}:</p>
 * <pre>
 *   try { var nacs = AutoCloser.get(new NotAutoclosable()).using(sp -> sp.get().dispose()) ) {
 *     ...use nacs.get().xxx methods...
 *	 }
 * </pre>
 *
 * @author Peter Verhas
 * @author claudio.rosati@esss.se
 * @param <T> The type of the object wrapped by this class.
 * @see <a href="https://javax0.wordpress.com/2019/05/22/box-old-objects-to-be-autoclosable/">Box old objects to be autoclosable</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AutoCloser<T> {

	/**
	 * Returns an instance of this class wrapping 
	 * @param <T>
	 * @param resource
	 * @return
	 */
	public static <T> AutoCloser<T> get( T resource ) {
		return new AutoCloser<>(resource);
	}

	private final T resource;

	private AutoCloser( T resource ) {
		this.resource = resource;
	}

	public AutoClosableSupplier using( Consumer<Supplier<T>> closer ) {
		return new AutoClosableSupplier(closer);
	}

	@SuppressWarnings( "PublicInnerClass" )
	public class AutoClosableSupplier implements Supplier<T>, AutoCloseable {

		private final Consumer<Supplier<T>> closer;

		private AutoClosableSupplier( Consumer<Supplier<T>> closer ) {
			this.closer = closer;
		}

		@Override
		public void close() {
			closer.accept(this);
		}

		@Override
		public T get() {
			return resource;
		}

	}

}
