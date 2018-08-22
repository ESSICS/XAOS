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


import java.util.function.Predicate;
import javafx.scene.control.TreeItem;


/**
 * Represents a predicate (boolean-valued function) of two arguments: the first
 * is the parent {@link TreeItem} of the element subject of the function call,
 * and the second the value of element itself.
 * <p>
 * This is a {@link FunctionalInterface} whose functional method is
 * {@link #test(TreeItem, Object)}.
 *
 * @param <T> The type of the {@link TreeItem#getValue() value} property within
 *            {@link TreeItem}.
 * @author claudio.rosati@esss.se
 * @see <a href="http://www.kware.net/?p=204">Filtering a JavaFX TreeView</a>
 */
@FunctionalInterface
public interface TreeItemPredicate<T> {

	/**
	 * Evaluates this predicate on the given arguments.
	 *
	 * @param parent The parent {@link TreeItem} of the element or {@code null} if
	 *               there is no parent.
	 * @param value  The value to be tested.
	 * @return {@code true} if the input argument matches the predicate.
	 */
	boolean test( TreeItem<T> parent, T value );

	/**
	 * Utility method to create a {@link TreeItemPredicate} from a given
	 * {@link Predicate}.
	 *
	 * @param <T>       The type of the value.
	 * @param predicate The {@link Predicate} to be wrapped.
	 * @return A new {@link TreeItemPredicate} wrapped around the given {@code predicate}.
	 */
	static <T> TreeItemPredicate<T> create( Predicate<T> predicate ) {
		return ( parent, value ) -> predicate.test(value);
	}

}
