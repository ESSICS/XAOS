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
package se.europeanspallationsource.xaos.ui.control.tree;


import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.util.Callback;


/**
 * Provides methods to manipulate trees made of {@link TreeItem}s.
 *
 * @author claudio.rosati@esss.se
 */
public class TreeItems {

	private static final Logger LOGGER = Logger.getLogger(TreeItems.class.getName());

	/**
	 * Returns a cell factory to be used in {@link TreeView}s whose type
	 * parameter is {@link Path}.
	 * <p>
	 * The returned {@link TreeCell}s will show the full path name if the
	 * corresponding {@link TreeItem} is a direct child of the view's root node
	 * (the one returned by {@link TreeView#getRoot()}</p>, otherwise only the
	 * last portion of the path name is displayed (the one returned by
	 * {@link Path#getFileName()}.
	 *
	 * @return A cell factory to be used in {@link TreeView}s whose type
	 *         parameter is {@link Path}.
	 */
	public static Callback<TreeView<Path>, TreeCell<Path>> defaultTreePathCellFactory() {
		return treeView -> {
			return new TreeCell<Path>() {
				@Override
				protected void updateItem( Path item, boolean empty ) {

					super.updateItem(item, empty);

					if ( empty || item == null ) {
						setText(null);
						setGraphic(null);
					} else {

						TreeItem<Path> treeItem = getTreeItem();

						if ( treeItem.getParent() != getTreeView().getRoot() ) {
							setText(item.getFileName().toString());
						} else {
							setText(item.toString());
						}

						setGraphic(treeItem.getGraphic());

					}

				}
			};
		};
	}

	/**
	 * Expands/collapses the node and all its non-leaf children recursively.
	 * <p>
	 * This method is not thread safe, and should be called from the JavaFX
	 * application thread.</p>
	 *
	 * @param <T>    The type of the value returned by {@link TreeItem#getValue()}.
	 * @param node   The {@link TreeItem} to be expanded or collapsed.
	 * @param expand If {@code true} the node and its children will be expanded,
	 *               if {@code false} they will be collapsed.
	 * @return The passed {@link TreeItem} node.
	 */
	public static <T> TreeItem<T> expandAll( final TreeItem<T> node, final boolean expand ) {

		TreeItemWalker.visit(node, n -> n.setExpanded(expand));

		return node;

	}

	/**
	 * Expands/collapses the {@link TreeView} root node and all its non-leaf
	 * children recursively.
	 * <p>
	 * This method is not thread safe, and should be called from the JavaFX
	 * application thread.</p>
	 *
	 * @param <T>    The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree   The {@link TreeView} whose root node has to be expanded or
	 *               collapsed.
	 * @param expand If {@code true} the node and its children will be expanded,
	 *               if {@code false} they will be collapsed.
	 * @return The passed {@link TreeView}.
	 */
	public static <T> TreeView<T> expandAll( final TreeView<T> tree, final boolean expand ) {

		TreeItemWalker.visit(tree, n -> n.setExpanded(expand));

		return tree;

	}

	/**
	 * Expands/collapses the {@link TreeTableView} root node and all its non-leaf
	 * children recursively.
	 * <p>
	 * This method is not thread safe, and should be called from the JavaFX
	 * application thread.</p>
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param treeTable The {@link TreeTableView} whose root node has to be
	 *                  expanded or collapsed.
	 * @param expand    If {@code true} the node and its children will be expanded,
	 *                  if {@code false} they will be collapsed.
	 * @return The passed {@link TreeTableView}.
	 */
	public static <T> TreeTableView<T> expandAll( final TreeTableView<T> treeTable, final boolean expand ) {

		TreeItemWalker.visit(treeTable, n -> n.setExpanded(expand));

		return treeTable;

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree
	 * item from the tree rooted at the given root node, matching the given
	 * {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param root      The root {@link TreeItem} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> find ( final TreeItem<T> root, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(root);

		return walker.stream().filter(predicate).findFirst();

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree 
	 * item from the given {@code tree}, matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> find ( final TreeView<T> tree, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(predicate).findFirst();

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree
	 * item from the given {@code tree} table, matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeTableView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> find ( final TreeTableView<T> tree, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(predicate).findFirst();

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree
	 * item from the tree rooted at the given root node, whose value is matching
	 * the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param root      The root {@link TreeItem} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> findValue ( final TreeItem<T> root, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(root);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).findFirst();

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree
	 * item from the given {@code tree}, whose value is matching the given
	 * {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> findValue ( final TreeView<T> tree, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).findFirst();

	}

	/**
	 * Return an {@link Optional} object possibly containing the first tree
	 * item from the given {@code tree} table, whose value is matching the given
	 * {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeTableView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link Optional} object possibly containing the found tree items.
	 */
	public static <T> Optional<TreeItem<T>> findValue ( final TreeTableView<T> tree, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).findFirst();

	}

	/**
	 * Return a {@link List} of the tree items from the tree rooted at the given
	 * root node, matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param root      The root {@link TreeItem} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> search ( final TreeItem<T> root, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(root);

		return walker.stream().filter(predicate).collect(Collectors.toList());

	}

	/**
	 * Return a {@link List} of the tree items from the given {@code tree},
	 * matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> search ( final TreeView<T> tree, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(predicate).collect(Collectors.toList());

	}

	/**
	 * Return a {@link List} of the tree items from the given {@code tree} table,
	 * matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeTableView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> search ( final TreeTableView<T> tree, final Predicate<? super TreeItem<T>> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(predicate).collect(Collectors.toList());

	}

	/**
	 * Return a {@link List} of the tree items from the tree rooted at the given
	 * root node, whose value is matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param root      The root {@link TreeItem} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> searchValue ( final TreeItem<T> root, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(root);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).collect(Collectors.toList());

	}

	/**
	 * Return a {@link List} of the tree items from the given {@code tree},
	 * whose value is matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> searchValue ( final TreeView<T> tree, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).collect(Collectors.toList());

	}

	/**
	 * Return a {@link List} of the tree items from the given {@code tree} table,
	 * whose value is matching the given {@link Predicate}.
	 *
	 * @param <T>       The type of the value returned by {@link TreeItem#getValue()}.
	 * @param tree      The {@link TreeTableView} where the search is performed.
	 * @param predicate The predicate used to select the visited tree item.
	 * @return The {@link List} of the found tree items.
	 */
	public static <T> List<TreeItem<T>> searchValue ( final TreeTableView<T> tree, final Predicate<? super T> predicate ) {

		TreeItemWalker<T> walker = new TreeItemWalker<>(tree);

		return walker.stream().filter(ti -> predicate.test(ti.getValue())).collect(Collectors.toList());

	}

	private TreeItems() {
	}

}
