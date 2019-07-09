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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Completion stage wrapper that uses the provided executor as both the default
 * execution facility and the default asynchronous execution facility.
 *
 * A {@link CompletionStage} returned from any of this completion stage's
 * methods inherits this stage's execution facilities.
 *
 * This means, for example, that
 *
 * <pre>
 * this.thenApply(f).thenAcceptAsync(g).thenRun(h)</pre>
 *
 * is equivalent to
 *
 * <pre>
 * this.thenApplyAsync(f, executor).thenAcceptAsync(g, executor).thenRunAsync(h, executor)</pre>
 *
 * where {@code executor} is the executor this stage was created with.
 *
 * @param <T> The type passed to the consumers of this {@link CompletionStage}
 *            methods.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/TomasMikula/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.CompletionStageWithDefaultExecutor</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class DefaultExecutorCompletionStage<T> implements CompletionStage<T> {

	/**
	 * Wrap the {@code original} {@link CompletionStage} into a
	 * {@link DefaultExecutorCompletionStage} using the given {@code executor}.
	 *
	 * @param <U>             The type of the {@code original} {@link CompletionStage}.
	 * @param original        The {@link CompletionStage} to be wrapped.
	 * @param defaultExecutor The {@link Executor} to be used by the wrapper.
	 * @return Instance of this class wrapping the given {@code original}
	 *         {@link CompletionStage}.
	 */
	public static <U> CompletionStage<U> wrap( CompletionStage<U> original, Executor defaultExecutor ) {
		return new DefaultExecutorCompletionStage<>(original, defaultExecutor);
	}

	private final Executor defaultExecutor;
	private final CompletionStage<T> original;

	private DefaultExecutorCompletionStage( CompletionStage<T> original, Executor defaultExecutor ) {
		this.original = original;
		this.defaultExecutor = defaultExecutor;
	}

	@Override
	public CompletionStage<Void> acceptEither( CompletionStage<? extends T> other, Consumer<? super T> action ) {
		return wrap(original.acceptEitherAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync( CompletionStage<? extends T> other, Consumer<? super T> action ) {
		return wrap(original.acceptEitherAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync( CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor ) {
		return wrap(original.acceptEitherAsync(other, action, executor));
	}

	@Override
	public <U> CompletionStage<U> applyToEither( CompletionStage<? extends T> other, Function<? super T, U> fn ) {
		return wrap(original.applyToEitherAsync(other, fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync( CompletionStage<? extends T> other, Function<? super T, U> fn ) {
		return wrap(original.applyToEitherAsync(other, fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync( CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor ) {
		return wrap(original.applyToEitherAsync(other, fn, executor));
	}

	@Override
	public CompletionStage<T> exceptionally( Function<Throwable, ? extends T> fn ) {
		return wrap(original.exceptionally(fn));
	}

	@Override
	public <U> CompletionStage<U> handle( BiFunction<? super T, Throwable, ? extends U> fn ) {
		return wrap(original.handleAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> handleAsync( BiFunction<? super T, Throwable, ? extends U> fn ) {
		return wrap(original.handleAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> handleAsync( BiFunction<? super T, Throwable, ? extends U> fn, Executor executor ) {
		return wrap(original.handleAsync(fn, executor));
	}

	@Override
	public CompletionStage<Void> runAfterBoth( CompletionStage<?> other, Runnable action ) {
		return wrap(original.runAfterBothAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync( CompletionStage<?> other, Runnable action ) {
		return wrap(original.runAfterBothAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync( CompletionStage<?> other, Runnable action, Executor executor ) {
		return wrap(original.runAfterBothAsync(other, action, executor));
	}

	@Override
	public CompletionStage<Void> runAfterEither( CompletionStage<?> other, Runnable action ) {
		return wrap(original.runAfterEitherAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync( CompletionStage<?> other, Runnable action ) {
		return wrap(original.runAfterEitherAsync(other, action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync( CompletionStage<?> other, Runnable action, Executor executor ) {
		return wrap(original.runAfterEitherAsync(other, action, executor));
	}

	@Override
	public CompletionStage<Void> thenAccept( Consumer<? super T> action ) {
		return wrap(original.thenAcceptAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync( Consumer<? super T> action ) {
		return wrap(original.thenAcceptAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync( Consumer<? super T> action, Executor executor ) {
		return wrap(original.thenAcceptAsync(action, executor));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBoth( CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action ) {
		return wrap(original.thenAcceptBothAsync(other, action, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync( CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action ) {
		return wrap(original.thenAcceptBothAsync(other, action, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync( CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor ) {
		return wrap(original.thenAcceptBothAsync(other, action, executor));
	}

	@Override
	public <U> CompletionStage<U> thenApply( Function<? super T, ? extends U> fn ) {
		return wrap(original.thenApplyAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync( Function<? super T, ? extends U> fn ) {
		return wrap(original.thenApplyAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync( Function<? super T, ? extends U> fn, Executor executor ) {
		return wrap(original.thenApplyAsync(fn, executor));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombine( CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn ) {
		return wrap(original.thenCombineAsync(other, fn, defaultExecutor));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync( CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn ) {
		return wrap(original.thenCombineAsync(other, fn, defaultExecutor));
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync( CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor ) {
		return wrap(original.thenCombineAsync(other, fn, executor));
	}

	@Override
	public <U> CompletionStage<U> thenCompose( Function<? super T, ? extends CompletionStage<U>> fn ) {
		return wrap(original.thenComposeAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync( Function<? super T, ? extends CompletionStage<U>> fn ) {
		return wrap(original.thenComposeAsync(fn, defaultExecutor));
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync( Function<? super T, ? extends CompletionStage<U>> fn, Executor executor ) {
		return wrap(original.thenComposeAsync(fn, executor));
	}

	@Override
	public CompletionStage<Void> thenRun( Runnable action ) {
		return wrap(original.thenRunAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> thenRunAsync( Runnable action ) {
		return wrap(original.thenRunAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<Void> thenRunAsync( Runnable action, Executor executor ) {
		return wrap(original.thenRunAsync(action, executor));
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		if ( original instanceof CompletableFuture ) {
			return (CompletableFuture<T>) original;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public CompletionStage<T> whenComplete( BiConsumer<? super T, ? super Throwable> action ) {
		return wrap(original.whenCompleteAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<T> whenCompleteAsync( BiConsumer<? super T, ? super Throwable> action ) {
		return wrap(original.whenCompleteAsync(action, defaultExecutor));
	}

	@Override
	public CompletionStage<T> whenCompleteAsync( BiConsumer<? super T, ? super Throwable> action, Executor executor ) {
		return wrap(original.whenCompleteAsync(action, executor));
	}

	private <U> CompletionStage<U> wrap( CompletionStage<U> original ) {
		return wrap(original, defaultExecutor);
	}

}
