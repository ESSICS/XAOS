/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018 by European Spallation Source ERIC.
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
package se.europeanspallationsource.xaos.ui.control.tree;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.util.Pair;


/**
 * Walks a {@link TreeItem}s tree depth-first.It supports streaming, visitor
 * pattern and iterator pattern.
 * <p>
 * <b>Important Note #1:</b> Once used, a {@link TreeItemWalker} cannot be reused.
 * A new instance has to be created instead.</p>
 * <p>
 * <b>Important Note #2:</b> {@link TreeItem}s added to the tree being walked
 * will be walked only if they are part of the sub-tree not yet visited.
 * Generally speaking no changes should be done to the tree structure during a
 * walk.</p>
 * <p>
 * <b>Important Note #3:</b> This implementation is not synchronized. Calling
 * {@link #getDepth()}, {@link #hasNext()}, {@link #next()} and/or
 * {@link #stream()}'s methods from different threads concurrently can produce
 * odd results. Call {@code buildSynchronized} to get a synchronized version of
 * the walker.
 * </p>
 *
 * @author claudio.rosati@esss.se
 * @param <T> The type of the {@link TreeItem}s.
 * @see <a href="https://stackoverflow.com/questions/28342309/iterate-treeview-nodes">Iterate TreeView nodes</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeItemWalker<T> implements Iterator<TreeItem<T>>, Iterable<TreeItem<T>> {

	/**
	 * Returns a walker initialized with the given {@code root} item.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param root The {@link TreeItem} being the root to be walked depth-first.
	 *             Can be {@code null}, in which case an empty walker will be
	 *             created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code root} parameter.
	 */
	public static <T> TreeItemWalker<T> build( TreeItem<T> root ) {
		return new TreeItemWalker<>(root);
	}

	/**
	 * Returns a walker initialized with the {@link TreeView#getRoot()} from the
	 * given {@code view}.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param view The {@link TreeView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code view} parameter.
	 */
	public static <T> TreeItemWalker<T> build( TreeView<T> view ) {
		return new TreeItemWalker<>(view);
	}

	/**
	 * Returns a walker initialized with the {@link TreeTableView#getRoot()}
	 * from the given {@code view}.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param view The {@link TreeTableView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code view} parameter.
	 */
	public static <T> TreeItemWalker<T> build( TreeTableView<T> view ) {
		return new TreeItemWalker<>(view);
	}

	/**
	 * Returns a synchronized walker initialized with the given {@code root}
	 * item.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param root The {@link TreeItem} being the root to be walked depth-first.
	 *             Can be {@code null}, in which case an empty walker will be
	 *             created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code root} parameter.
	 */
	public static <T> TreeItemWalker<T> buildSynchronized( TreeItem<T> root ) {
		return new SynchronizedWalker<>(root);
	}

	/**
	 * Returns a synchronized walker initialized with the
	 * {@link TreeView#getRoot()} from the given {@code view}.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param view The {@link TreeView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code view} parameter.
	 */
	public static <T> TreeItemWalker<T> buildSynchronized( TreeView<T> view ) {
		return new SynchronizedWalker<>(view);
	}

	/**
	 * Returns a synchronized walker initialized with the
	 * {@link TreeTableView#getRoot()} from the given {@code view}.
	 *
	 * @param <T>  The type of the {@link TreeItem}s.
	 * @param view The {@link TreeTableView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 * @return A new instance of {@link TreeItemWalker} initialized with the
	 *         given {@code view} parameter.
	 */
	public static <T> TreeItemWalker<T> buildSynchronized( TreeTableView<T> view ) {
		return new SynchronizedWalker<>(view);
	}

	/**
	 * Walks over the given tree {@code root} and calls the consumer for each
	 * tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param root    The root {@link TreeItem} to visit.
	 * @param visitor The visitor receiving the visited {@link TreeItem} during
	 *                the tree walk.
	 */
	public static <T> void visit( TreeItem<T> root, Consumer<TreeItem<T>> visitor ) {

		TreeItemWalker<T> walker = build(root);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next());
		}

	}

	/**
	 * Walks over the given tree {@code root} and calls the consumer for each
	 * tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param root    The root {@link TreeItem} to visit.
	 * @param visitor The visitor receiving the visited {@link TreeItem} and the
	 *                current walking depth during the tree walk.
	 */
	public static <T> void visit( TreeItem<T> root, BiConsumer<TreeItem<T>, Integer> visitor ) {

		TreeItemWalker<T> walker = build(root);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next(), walker.getDepth());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem} during
	 *                the tree walk.
	 */
	public static <T> void visit( TreeView<T> tree, Consumer<TreeItem<T>> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem} and the
	 *                current walking depth during the tree walk.
	 */
	public static <T> void visit( TreeView<T> tree, BiConsumer<TreeItem<T>, Integer> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next(), walker.getDepth());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeTableView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem} during
	 *                the tree walk.
	 */
	public static <T> void visit( TreeTableView<T> tree, Consumer<TreeItem<T>> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeTableView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem} and the
	 *                current walking depth during the tree walk.
	 */
	public static <T> void visit( TreeTableView<T> tree, BiConsumer<TreeItem<T>, Integer> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next(), walker.getDepth());
		}

	}

	/**
	 * Walks over the given tree {@code root} and calls the consumer for each
	 * tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param root    The root {@link TreeItem} to visit.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                during the tree walk.
	 */
	public static <T> void visitValue( TreeItem<T> root, Consumer<T> visitor ) {

		TreeItemWalker<T> walker = build(root);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue());
		}

	}

	/**
	 * Walks over the given tree {@code root} and calls the consumer for each
	 * tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param root    The root {@link TreeItem} to visit.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                and the current walking depth during the tree walk.
	 */
	public static <T> void visitValue( TreeItem<T> root, BiConsumer<T, Integer> visitor ) {

		TreeItemWalker<T> walker = build(root);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue(), walker.getDepth());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                during the tree walk.
	 */
	public static <T> void visitValue( TreeView<T> tree, Consumer<T> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                and the current walking depth during the tree walk.
	 */
	public static <T> void visitValue( TreeView<T> tree, BiConsumer<T, Integer> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue(), walker.getDepth());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeTableView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                during the tree walk.
	 */
	public static <T> void visitValue( TreeTableView<T> tree, Consumer<T> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue());
		}

	}

	/**
	 * Walks over the given root of the given {@code tree} and calls the consumer
	 * for each tree item's value.
	 *
	 * @param <T>     The type of the {@link TreeItem}s.
	 * @param tree    The {@link TreeTableView} whose root has to be visited.
	 * @param visitor The visitor receiving the visited {@link TreeItem}'s value
	 *                and the current walking depth during the tree walk.
	 */
	public static <T> void visitValue( TreeTableView<T> tree, BiConsumer<T, Integer> visitor ) {

		TreeItemWalker<T> walker = build(tree);

		while ( walker.hasNext() ) {
			visitor.accept(walker.next().getValue(), walker.getDepth());
		}

	}

	/**
	 * The current tree depth.
	 */
	private int depth = 0;

	/**
	 * The depth offset for the next step.
	 */
	private int offset = 0;

	/**
	 * The walk state stack.
	 */
	private final Deque<Pair<TreeItem<T>, Integer>> stack = new ArrayDeque<>(8);


	/**
	 * Initialize the walker with the given {@code root} item.
	 *
	 * @param root The {@link TreeItem} being the root to be walked depth-first.
	 *             Can be {@code null}, in which case an empty walker will be
	 *             created.
	 */
	protected TreeItemWalker( TreeItem<T> root ) {
		if ( root != null ) {
			stack.push(new Pair<>(root, -1));
		}
	}

	/**
	 * Initialize the walker with the {@link TreeView#getRoot()} from the given
	 * {@code view}.
	 *
	 * @param view The {@link TreeView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 */
	protected TreeItemWalker( TreeView<T> view ) {
		this(view != null ? view.getRoot() : null);
	}

	/**
	 * Initialize the walker with the {@link TreeTableView#getRoot()} from the
	 * given {@code view}.
	 *
	 * @param view The {@link TreeTableView} whose root element has to be walked
	 *             depth-first. Can be {@code null}, or having a {@code null}
	 *             root element, in which case an empty walker will be created.
	 */
	protected TreeItemWalker( TreeTableView<T> view ) {
		this(view != null ? view.getRoot() : null);
	}

	/**
	 * @return The current depth level. {@code 0} means "root item" (the one
	 * used to build this walker). {@code 1} means "root's child" and so on.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @return {@code true} if the walker still has unserved items.
	 */
	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public Iterator<TreeItem<T>> iterator() {
		return this;
	}

	/**
	 * @return The next tree item in depth-first walk order: the parent is
	 *         returned before any of its children.
	 */
	@Override
	public TreeItem<T> next() {

		if ( !hasNext() ) {
			throw new IllegalStateException("Walking stack is empty.");
		}

		TreeItem<T> next = stack.peek().getKey();

		updateStack();

//	----------------------------------------------------------------------------
//	The following is a possible alternative that seems easier and more direct, 
//	allowing to get rid of the Pair class. Nevertheless, the current one has a
//	better memory footprint, keeping the stack size at minimum, even if a node
//	has a lot of children, because only one at time is in the stack.
//	----------------------------------------------------------------------------
//		TreeItem<T> next = stack.pop().getKey();
//
//		ObservableList<TreeItem<T>> children = next.getChildren();
//
//		if ( !children.isEmpty() ) {
//			for ( int i = children.size() - 1; i >= 0; i-- ) {
//				stack.push(new Pair<>(children.get(i), -1));
//			}
//		}
//	----------------------------------------------------------------------------

		depth += offset;

		Pair<TreeItem<T>, Integer> lookAhead = stack.peek();

		if ( lookAhead != null ) {

			TreeItem<T> lookAheadKey = lookAhead.getKey();

			if ( lookAheadKey != null ) {
				if ( Objects.equals(next, lookAheadKey.getParent()) ) {
					offset = 1;
				} else if ( Objects.equals(next.getParent(), lookAheadKey.getParent()) ) {
					offset = 0;
				} else {
					offset = -1;
				}
			}

		}

		return next;

	}

	@Override
	public Spliterator<TreeItem<T>> spliterator() {
		return stream().spliterator();
	}

	/**
	 * @return A {@link Stream} of all (remaining) items. Note that the walker
	 *         can traverse only once over items. So if {@link #hasNext()}/{@link #next()}
	 *         where used to partially walk the tree, a following call to {@link #stream()}
	 *         will return the remaining items.
	 */
	public Stream<TreeItem<T>> stream() {

		if ( !hasNext() ) {
			throw new IllegalStateException("Walking stack is empty.");
		}

		return createStream();

	}

	protected Stream<TreeItem<T>> createStream() {
		return StreamSupport.stream(new Spliterator<TreeItem<T>>() {

			@Override
			public int characteristics() {
				return 0;
			}

			@Override
			public long estimateSize() {
				return Long.MAX_VALUE;
			}

			@Override
			public boolean tryAdvance( Consumer<? super TreeItem<T>> action ) {

				if ( hasNext() ) {
					action.accept(next());
					return true;
				}

				return false;

			}

			@Override
			public Spliterator<TreeItem<T>> trySplit() {
				return null;
			}

		}, false);
	}

	/**
	 * The value of the top stack {@link Pair} contains
	 */
	private void updateStack() {

		Pair<TreeItem<T>, Integer> topStackElement = stack.pop();
		TreeItem<T> treeItem = topStackElement.getKey();
		ObservableList<TreeItem<T>> children = treeItem.getChildren();
		int index = topStackElement.getValue() + 1;

		if ( index >= children.size() ) {
			//	All children of "treeItem" were processed, move on.
			if ( !stack.isEmpty() ) {
				updateStack();
			}
		} else {
			// The "idndex"-th child of "treeItem" has to be the next one to be
			// returned, followed by "treeItem" itself with the pair value
			// updated to "index".
			stack.push(new Pair<>(treeItem, index));
			stack.push(new Pair<>(children.get(index), -1));
		}

	}

	private static class SynchronizedWalker<T> extends TreeItemWalker<T> {

		SynchronizedWalker( TreeItem<T> root ) {
			super(root);
		}

		SynchronizedWalker( TreeView<T> view ) {
			super(view);
		}

		SynchronizedWalker( TreeTableView<T> view ) {
			super(view);
		}

		@Override
		public synchronized int getDepth() {
			return super.getDepth();
		}

		@Override
		public synchronized boolean hasNext() {
			return super.hasNext();
		}

		@Override
		public synchronized TreeItem<T> next() {
			return super.next();
		}

		@Override
		protected Stream<TreeItem<T>> createStream() {
			return StreamSupport.stream(new Spliterator<TreeItem<T>>() {

				@Override
				public int characteristics() {
					return 0;
				}

				@Override
				public long estimateSize() {
					return Long.MAX_VALUE;
				}

				@Override
				public boolean tryAdvance( Consumer<? super TreeItem<T>> action ) {

					boolean hn;
					TreeItem<T> n = null;

					synchronized ( SynchronizedWalker.this ) {

						hn = hasNext();

						if ( hn  ) {
							n = next();
						}

					}

					if ( hn ) {
						action.accept(n);
						return true;
					}

					return false;

				}

				@Override
				public Spliterator<TreeItem<T>> trySplit() {
					return null;
				}

			}, false);
		}

	}

}
