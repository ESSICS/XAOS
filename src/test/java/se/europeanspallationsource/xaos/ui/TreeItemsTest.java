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


import java.io.IOException;
import javafx.scene.control.TreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeItemsTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeItemsTest ---------------------------------------------");
	}

	private TreeItem<String> root;

	@Before
	@SuppressWarnings( "unchecked" )
	public void setUp() throws IOException {

		root = new TreeItem<>("root");

		root.setExpanded(true);
		root.getChildren().addAll(
			new TreeItem<>("node A"),
			new TreeItem<>("node B"),
			new TreeItem<>("node C")
		);
			root.getChildren().get(0).getChildren().addAll(
				new TreeItem<>("node AA"),
				new TreeItem<>("node AB"),
				new TreeItem<>("node AC"),
				new TreeItem<>("node AD")
			);
			root.getChildren().get(2).getChildren().addAll(
				new TreeItem<>("node CA"),
				new TreeItem<>("node CB")
			);
				root.getChildren().get(2).getChildren().get(1).getChildren().addAll(
					new TreeItem<>("node CBA"),
					new TreeItem<>("node CBB"),
					new TreeItem<>("node CBC"),
					new TreeItem<>("node CBD")
				);

		root.getChildren().get(0).setExpanded(true);
		root.getChildren().get(1).setExpanded(false);
		root.getChildren().get(2).setExpanded(false);
		root.getChildren().get(2).getChildren().get(1).setExpanded(true);

	}

	/**
	 * Test of expandAll method, of class TreeItems.
	 */
	@Test
	public void testExpandAll() {

		System.out.println("  Testing ''expandAll''...");

		assertThat(root).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", true);

		TreeItems.expandAll(root, true);

		assertThat(root).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", true);

		TreeItems.expandAll(root, false);

		assertThat(root).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", false);

		root.setExpanded(true);

		assertThat(root).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", false);

		TreeItems.expandAll(root.getChildren().get(2), true);

		assertThat(root).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", true);

		TreeItems.expandAll(root, true);
		TreeItems.expandAll(root.getChildren().get(2), false);

		assertThat(root).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("expanded", true);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("expanded", false);
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("expanded", false);

	}

}
