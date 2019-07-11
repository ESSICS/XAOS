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


import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;


/**
 * Lazy evaluation value container. {@link #set(Object)} can be used to force a
 * new value to the {@link Lazy} container. If called before {@link #get()}, it
 * will inhibit the lazy evaluation.
 * <p>
 * This class is thread-safe.</p>
 * <p>
 * <b>Usage:</b> instead of creating a (instance) variable of type T, create if
 * of type Lazy&lt;T&gt; passing the supplier for the lazy evaluation. Use the
 * get() method on your (instance) variable the get the actual value.</p>
 * <p>
 * <b>Note:</b> don't rely in possible collateral effects from the provided
 * {@link Supplier}. If {@link #set(Object)} is called before {@link #get()},
 * then the lazy evaluation will never occur.</p>
 *
 * @author claudio.rosati@esss.se
 * @param <T> The type of the returned lazy evaluation.
 * @see <a href="https://dzone.com/articles/lazy-assignment-in-java">Lazy Assignment in Java</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class Lazy<T> implements Supplier<T> {

	/**
	 * Creates and returns a new instance of {@link Lazy} to be used for a
	 * late evaluation of the given {@link Supplier}.
	 *
	 * @param <T>      The type of the returned lazy evaluation.
	 * @param supplier The {@link Supplier} of the evaluated value.
	 * @return A new instance of {@link Lazy} to be used for a late evaluation
	 *         of the given {@link Supplier}.
	 */
	public static <T> Lazy<T> of( Supplier<T> supplier ) {
		return new Lazy<>(supplier);
	}

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private boolean supplied = false;
	private final Supplier<T> supplier;
	private T value;

	protected Lazy( Supplier<T> supplier ) {
		this.supplier = supplier;
	}

	/**
	 * Returns the current value of this lazy container. If {@link #set(Object)}
	 * was not yet called, then the first time {@link #get()} is executed it 
	 * will perform the lazy evaluation of the initially provided
	 * {@link Supplier}.
	 *
	 * @return The current value of this lazy container.
	 */
	@Override
	public T get() {

		lock.readLock().lock();

		if ( !supplied ) {

			lock.readLock().unlock();
			lock.writeLock().lock();

			try {

 	 			if ( !supplied ) {
					value = supplier.get();
					supplied = true;
				}

				lock.readLock().lock();

			} finally {
				lock.writeLock().unlock();
			}

		}

		try {
			return value;
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Sets a new value for this value container. If this method is called
	 * before the {@link #get()} one, then the lazy evaluation of the initially
	 * provided {@link Supplier} will be inhibited.
	 *
	 * @param newValue The new value for this value container.
	 */
	public void set ( T newValue ) {

		lock.writeLock().lock();

		try {
			value = newValue;
			supplied = true;
		} finally {
			lock.writeLock().unlock();
		}

	}

}
