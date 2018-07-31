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
package se.europeanspallationsource.xaos.ui;


import java.util.logging.Logger;
import javafx.scene.control.TreeItem;


/**
 * Provides methods to manipulate trees made of {@link TreeItem}s.
 *
 * @author claudio.rosati@esss.se
 */
public class TreeItems {

	private static final Logger LOGGER = Logger.getLogger(TreeItems.class.getName());

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
