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
package se.europeanspallationsource.xaos.ui.control.tree;


import java.nio.file.Path;
import java.util.logging.Logger;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
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
	 * @param node   The node to be expanded or collapsed.
	 * @param expand If {@code true} the node and its children will be expanded,
	 *               if {@code false} they will be collapsed.
	 * @return The passed node.
	 */
	public static <T> TreeItem<T> expandAll( final TreeItem<T> node, final boolean expand ) {

		if ( node != null && !node.isLeaf() ) {
			node.setExpanded(expand);
			node.getChildren().forEach(n -> expandAll(n, expand));
		}

		return node;

	}

	private TreeItems() {
	}

}
