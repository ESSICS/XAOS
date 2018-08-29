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
package se.europeanspallationsource.xaos.core.util;


import java.util.Objects;
import java.util.function.Function;


/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 * <p>
 * This is a <i>functional interface</i> whose functional method is
 * {@link #apply(Object, Object, Object)}.
 *
 * @param <T> The type of the first argument to the function.
 * @param <U> The type of the second argument to the function.
 * @param <V> The type of the third argument to the function.
 * @param <R> The type of the result of the function.
 *
 * @author claudio.rosati@esss.se
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t The first function argument.
	 * @param u The second function argument.
	 * @param v The third function argument.
	 * @return the function result
	 */
	R apply( T t, U u, V v );

	/**
	 * Returns a composed function that first applies this function to
	 * its input, and then applies the {@code after} function to the result.
	 * If evaluation of either function throws an exception, it is relayed to
	 * the caller of the composed function.
	 *
	 * @param <W>   The type of output of the {@code after} function, and of the
	 *              composed function.
	 * @param after The function to apply after this function is applied.
	 * @return A composed function that first applies this function and then
	 *         applies the {@code after} function.
	 * @throws NullPointerException If after is {@code null}.
	 */
	default <W> TriFunction<T, U, V, W> andThen( Function<? super R, ? extends W> after ) {

		Objects.requireNonNull(after);

		return ( T t, U u, V v ) -> after.apply(apply(t, u, v));

	}

}
