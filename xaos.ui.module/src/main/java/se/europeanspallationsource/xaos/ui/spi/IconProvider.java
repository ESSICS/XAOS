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
package se.europeanspallationsource.xaos.ui.spi;


import javafx.scene.Node;


/**
 * Provides icons (i.e. {@link Node}s) for a given key object.
 *
 * @author claudio.rosati@esss.se
 */
public interface IconProvider {

	/**
	 * Return an icon (i.e. a {@link Node} for the given {@code key} object.
	 *
	 * @param key The object for which a graphical representation is needed.
	 * @return An icon as a {@link Node} instance, or {@code null}.
	 */
	public Node iconFor( Object key );

}
