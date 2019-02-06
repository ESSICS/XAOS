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


import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import se.europeanspallationsource.xaos.tools.lang.Reflections;

import static se.europeanspallationsource.xaos.ui.impl.Constants.LOGGER;


/**
 * An extension of {@link TreeItem} with the possibility to filter its children. To
 * enable filtering it is necessary to set the {@link TreeItemPredicate}. If a
 * predicate is set, then the tree item will also use this predicate to filter its
 * children (if they are of the type FilterableTreeItem).
 *
 * A tree item that has children will not be filtered. The predicate will only be
 * evaluated, if the tree item is a leaf. Since the predicate is also set for the
 * child tree items, the tree item in question can turn into a leaf if all its
 * children are filtered.
 *
 * This class extends {@link CheckBoxTreeItem} so it can, but does not need to be,
 * used in conjunction with {@link CheckBoxTreeCell} cells.
 *
 * @param <T> The type of the value returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="http://www.kware.net/?p=204">Filtering a JavaFX TreeView</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class FilterableTreeItem<T> extends CheckBoxTreeItem<T> {

	final private FilteredList<TreeItem<T>> filteredList;
	final private ObservableList<TreeItem<T>> unfilteredList;

	/**
	 * Creates a new {@link FilterableTreeItem} with filtered children. To enable
	 * filtering sorting it is necessary to set the {@link TreeItemPredicate}. If
	 * no predicate is set, then the tree item will attempt so bind itself to the
	 * predicate of its parent.
	 */
	public FilterableTreeItem() {
		this((T) null, null, false, false);
	}

	/**
	 * Creates a new {@link FilterableTreeItem} with filtered children. To enable
	 * filtering sorting it is necessary to set the {@link TreeItemPredicate}. If 
	 * no predicate is set, then the tree item will attempt so bind itself to the
	 * predicate of its parent.
	 *
	 * @param value The value of the tree item.
	 */
	public FilterableTreeItem( T value ) {
		this(value, null, false, false);
	}

	/**
	 * Creates a new {@link FilterableTreeItem} with filtered children.To enable
	 * filtering sorting it is necessary to set the {@link TreeItemPredicate}.If 
	 * no predicate is set, then the tree item will attempt so bind itself to the
	 * predicate of its parent.
	 *
	 * @param value   The value of the tree item.
	 * @param graphic The {@link Node} to show in the {@link TreeView} next to
	 *                this tree item.
	 */
	public FilterableTreeItem( T value, Node graphic ) {
		this(value, graphic, false, false);
	}

	/**
	 * Creates a new {@link FilterableTreeItem} with filtered children.To enable
	 * filtering sorting it is necessary to set the {@link TreeItemPredicate}.If 
	 * no predicate is set, then the tree item will attempt so bind itself to the
	 * predicate of its parent.
	 *
	 * @param value    The value of the tree item.
	 * @param graphic  The {@link Node} to show in the {@link TreeView} next to
	 *                 this tree item.
	 * @param selected The initial value of the {@link #selectedProperty()}.
	 */
	public FilterableTreeItem( T value, Node graphic, boolean selected ) {
		this(value, graphic, selected, false);
	}

	/**
	 * Creates a new {@link FilterableTreeItem} with filtered children.To enable
	 * filtering sorting it is necessary to set the {@link TreeItemPredicate}.If 
	 * no predicate is set, then the tree item will attempt so bind itself to the
	 * predicate of its parent.
	 *
	 * @param value       The value of the tree item.
	 * @param graphic     The {@link Node} to show in the {@link TreeView} next 
	 *                    to this tree item.
	 * @param selected    The initial value of the {@link #selectedProperty()}.
	 * @param independent The initial value of the {@link #independentProperty()}.
	 */
	public FilterableTreeItem( T value, Node graphic, boolean selected, boolean independent ) {

		super(value, graphic, selected, independent);

		unfilteredList = FXCollections.observableArrayList();
		filteredList = new FilteredList<>(this.unfilteredList);

		filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {

			Predicate<TreeItem<T>> p = child -> {

				//	It should never happen...
				if ( child == null ) {
					LOGGER.warning("Predicate invoked with a null parameter.");
					return false;
				}

				//	Set the predicate of child items to force filtering.
				if ( child instanceof FilterableTreeItem ) {
					( (FilterableTreeItem<T>) child ).setPredicate(getPredicate());
				}

				//	If there is no predicate, keep this tree item.
				if ( getPredicate() == null ) {
					return true;
				}

				//	If child is not a leaf (usually meaning it has children),
				//	keep this tree item.
				if ( !child.isLeaf() ) {
					return true;
				}

				//	Otherwise ask the TreeItemPredicate.
				return getPredicate().test(this, child.getValue());

			};

			return p;

		},
			this.predicate)
		);

		setHiddenFieldChildren(this.filteredList);

	}

	/*
	 * ---- predicate ----------------------------------------------------------
	 */
	private ObjectProperty<TreeItemPredicate<T>> predicate = new SimpleObjectProperty<>();

	public final ObjectProperty<TreeItemPredicate<T>> predicateProperty() {
		return this.predicate;
	}

	public final TreeItemPredicate<T> getPredicate() {
		return this.predicate.get();
	}

	public final void setPredicate( TreeItemPredicate<T> predicate ) {
		this.predicate.set(predicate);
	}

    /* **** END OF JAVAFX PROPERTIES  ****************************************** */

	/**
	 * Returns the list of children that is backing the filtered list.
	 *
	 * @return The underlying list of children.
	 */
	@SuppressWarnings( "ReturnOfCollectionOrArrayField" )
	public ObservableList<TreeItem<T>> getUnfilteredChildren() {
		return this.unfilteredList;
	}

	/**
	 * Set the hidden private field {@link TreeItem#children} through reflection and
	 * hook the hidden {@link ListChangeListener} in {@link TreeItem#childrenListener}
	 * to the list.
	 *
	 * @param list the list to set
	 */
	protected final void setHiddenFieldChildren( ObservableList<TreeItem<T>> list ) {

		Reflections.setFieldValue(this, "children", list);

		@SuppressWarnings( "unchecked" )
		ListChangeListener<? super TreeItem<T>> childrenListener = (ListChangeListener<? super TreeItem<T>>) Reflections.getFieldValue(this, "childrenListener");

		list.addListener(childrenListener);

	}

}
