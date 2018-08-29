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


import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class FilterableTreeItemTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- FilterableTreeItemTest ------------------------------------");
	}

	public FilterableTreeItemTest() {
	}

	/**
	 * Test of setPredicate method, of class FilterableTreeItemTest.
	 * <pre>
	 *   Root
	 *     Parent A
	 *       Parent AA
	 *         Leaf AA1
	 *       Parent AB
	 *       Parent AC
	 *         Parent ACA
	 *           Leaf ACA1
	 *       Leaf A1
	 *       Leaf A2
	 *     Parent B
	 *       Parent BA
	 *         Leaf BA1
	 *       Parent BB
	 *         Leaf BB1
	 *     Parent C
	 *       Leaf C1
	 *       Leaf C2
	 *       Leaf C3
	 *       Leaf C4
	 *     Leaf 1
	 *     Leaf 2
	 * </pre>
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testPredicate() {

		System.out.println("  Testing ''setPredicate''...");

		FilterableTreeItem<String> root = new FilterableTreeItem<>("Root");
		FilterableTreeItem<String> parentA = new FilterableTreeItem<>("Parent A");
		FilterableTreeItem<String> parentB = new FilterableTreeItem<>("Parent B");
		FilterableTreeItem<String> parentC = new FilterableTreeItem<>("Parent C");
		FilterableTreeItem<String> leaf1 = new FilterableTreeItem<>("Leaf 1");
		FilterableTreeItem<String> leaf2 = new FilterableTreeItem<>("Leaf 2");

		root.getUnfilteredChildren().addAll(parentA, parentB, parentC, leaf1, leaf2);

			FilterableTreeItem<String> parentAA = new FilterableTreeItem<>("Parent AA");
			FilterableTreeItem<String> parentAB = new FilterableTreeItem<>("Parent AB");
			FilterableTreeItem<String> parentAC = new FilterableTreeItem<>("Parent AC");
			FilterableTreeItem<String> leafA1 = new FilterableTreeItem<>("Leaf A1");
			FilterableTreeItem<String> leafA2 = new FilterableTreeItem<>("Leaf A2");

			parentA.getUnfilteredChildren().addAll(parentAA, parentAB, parentAC, leafA1, leafA2);

				FilterableTreeItem<String> leafAA1 = new FilterableTreeItem<>("Leaf AA1");

				parentAA.getUnfilteredChildren().addAll(leafAA1);

				FilterableTreeItem<String> parentACA = new FilterableTreeItem<>("Parent ACA");

				parentAC.getUnfilteredChildren().addAll(parentACA);

					FilterableTreeItem<String> leafACA1 = new FilterableTreeItem<>("Leaf ACA1");

					parentACA.getUnfilteredChildren().addAll(leafACA1);

			FilterableTreeItem<String> parentBA = new FilterableTreeItem<>("Parent BA");
			FilterableTreeItem<String> parentBB = new FilterableTreeItem<>("Parent BB");

			parentB.getUnfilteredChildren().addAll(parentBA, parentBB);

				FilterableTreeItem<String> leafBA1 = new FilterableTreeItem<>("Leaf BA1");

				parentBA.getUnfilteredChildren().addAll(leafBA1);

				FilterableTreeItem<String> leafBB1 = new FilterableTreeItem<>("Leaf BB1");

				parentBB.getUnfilteredChildren().addAll(leafBB1);

			FilterableTreeItem<String> leafC1 = new FilterableTreeItem<>("Leaf C1");
			FilterableTreeItem<String> leafC2 = new FilterableTreeItem<>("Leaf C2");
			FilterableTreeItem<String> leafC3 = new FilterableTreeItem<>("Leaf C3");
			FilterableTreeItem<String> leafC4 = new FilterableTreeItem<>("Leaf C4");

			parentC.getUnfilteredChildren().addAll(leafC1, leafC2, leafC3, leafC4);

		//	Check starting situation (no predicate)
		//		Root
		//			Parent A
		//				Parent AA
		//					Leaf AA1
		//				Parent AB
		//				Parent AC
		//					Parent ACA
		//						Leaf ACA1
		//				Leaf A1
		//				Leaf A2
		//			Parent B
		//				Parent BA
		//					Leaf BA1
		//				Parent BB
		//					Leaf BB1
		//			Parent C
		//				Leaf C1
		//				Leaf C2
		//				Leaf C3
		//				Leaf C4
		//			Leaf 1
		//			Leaf 2
		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(5);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent A");
		assertThat(root.getChildren().get(0).getChildren()).hasSize(5);
		assertThat(root.getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent AA");
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf AA1");
		assertThat(root.getChildren().get(0).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent AB");
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(0).getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Parent AC");
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent ACA");
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf ACA1");
		assertThat(root.getChildren().get(0).getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf A1");
		assertThat(root.getChildren().get(0).getChildren().get(4)).hasFieldOrPropertyWithValue("value", "Leaf A2");
		assertThat(root.getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent B");
		assertThat(root.getChildren().get(1).getChildren()).hasSize(2);
		assertThat(root.getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent BA");
		assertThat(root.getChildren().get(1).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(1).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf BA1");
		assertThat(root.getChildren().get(1).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent BB");
		assertThat(root.getChildren().get(1).getChildren().get(1).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(1).getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf BB1");
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Parent C");
		assertThat(root.getChildren().get(2).getChildren()).hasSize(4);
		assertThat(root.getChildren().get(2).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf C1");
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Leaf C2");
		assertThat(root.getChildren().get(2).getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Leaf C3");
		assertThat(root.getChildren().get(2).getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf C4");
		assertThat(root.getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf 1");
		assertThat(root.getChildren().get(3).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(4)).hasFieldOrPropertyWithValue("value", "Leaf 2");
		assertThat(root.getChildren().get(4).getChildren()).hasSize(0);

		//	Predicate: "Root"
		root.setPredicate(( p, v ) -> v != null && v.startsWith("Root"));

		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(0);

		//	Predicate: "Leaf"
		//		Root
		//			Parent A
		//				Parent AA
		//					Leaf AA1
		//	****			Parent AB			****
		//				Parent AC
		//					Parent ACA
		//						Leaf ACA1
		//				Leaf A1
		//				Leaf A2
		//			Parent B
		//				Parent BA
		//					Leaf BA1
		//				Parent BB
		//					Leaf BB1
		//			Parent C
		//				Leaf C1
		//				Leaf C2
		//				Leaf C3
		//				Leaf C4
		//			Leaf 1
		//			Leaf 2
		root.setPredicate(( p, v ) -> v != null && v.startsWith("Leaf"));

		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(5);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent A");
		assertThat(root.getChildren().get(0).getChildren()).hasSize(4);
		assertThat(root.getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent AA");
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf AA1");
		assertThat(root.getChildren().get(0).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent AC");
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent ACA");
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf ACA1");
		assertThat(root.getChildren().get(0).getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Leaf A1");
		assertThat(root.getChildren().get(0).getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf A2");
		assertThat(root.getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent B");
		assertThat(root.getChildren().get(1).getChildren()).hasSize(2);
		assertThat(root.getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent BA");
		assertThat(root.getChildren().get(1).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(1).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf BA1");
		assertThat(root.getChildren().get(1).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent BB");
		assertThat(root.getChildren().get(1).getChildren().get(1).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(1).getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf BB1");
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Parent C");
		assertThat(root.getChildren().get(2).getChildren()).hasSize(4);
		assertThat(root.getChildren().get(2).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf C1");
		assertThat(root.getChildren().get(2).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Leaf C2");
		assertThat(root.getChildren().get(2).getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Leaf C3");
		assertThat(root.getChildren().get(2).getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf C4");
		assertThat(root.getChildren().get(3)).hasFieldOrPropertyWithValue("value", "Leaf 1");
		assertThat(root.getChildren().get(3).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(4)).hasFieldOrPropertyWithValue("value", "Leaf 2");
		assertThat(root.getChildren().get(4).getChildren()).hasSize(0);

		//	Predicate: "Parent"
		//		Root
		//			Parent A
		//				Parent AA
		//	****				Leaf AA1			****
		//				Parent AB
		//				Parent AC
		//					Parent ACA
		//	****					Leaf ACA1	****
		//	****			Leaf A1				****
		//	****			Leaf A2				****
		//			Parent B
		//				Parent BA
		//	****				Leaf BA1			****
		//				Parent BB
		//	****				Leaf BB1			****
		//			Parent C
		//	****			Leaf C1				****
		//	****			Leaf C2				****
		//	****			Leaf C3				****
		//	****			Leaf C4				****
		//	****		Leaf 1					****
		//	****		Leaf 2					****
		root.setPredicate(( p, v ) -> v != null && v.startsWith("Parent"));

		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(3);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent A");
		assertThat(root.getChildren().get(0).getChildren()).hasSize(3);
		assertThat(root.getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent AA");
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(0).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent AB");
		assertThat(root.getChildren().get(0).getChildren().get(1).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(0).getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Parent AC");
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent ACA");
		assertThat(root.getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent B");
		assertThat(root.getChildren().get(1).getChildren()).hasSize(2);
		assertThat(root.getChildren().get(1).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent BA");
		assertThat(root.getChildren().get(1).getChildren().get(0).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(1).getChildren().get(1)).hasFieldOrPropertyWithValue("value", "Parent BB");
		assertThat(root.getChildren().get(1).getChildren().get(1).getChildren()).hasSize(0);
		assertThat(root.getChildren().get(2)).hasFieldOrPropertyWithValue("value", "Parent C");
		assertThat(root.getChildren().get(2).getChildren()).hasSize(0);

		//	Predicate: "BB1"
		//		Root
		//	****		Parent A					****
		//	****			Parent AA			****
		//	****				Leaf AA1			****
		//	****			Parent AB			****
		//	****			Parent AC			****
		//	****				Parent ACA		****
		//	****					Leaf ACA1	****
		//	****			Leaf A1				****
		//	****			Leaf A2				****
		//			Parent B
		//	****			Parent BA			****
		//	****				Leaf BA1			****
		//				Parent BB
		//					Leaf BB1
		//	****		Parent C					****
		//	****			Leaf C1				****
		//	****			Leaf C2				****
		//	****			Leaf C3				****
		//	****			Leaf C4				****
		//	****		Leaf 1					****
		//	****		Leaf 2					****
		root.setPredicate(( p, v ) -> v != null && v.contains("BB1"));

		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent B");
		assertThat(root.getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Parent BB");
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren()).hasSize(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0)).hasFieldOrPropertyWithValue("value", "Leaf BB1");

		//	Predicate: "FUFFA"

		root.setPredicate(( p, v ) -> v != null && v.contains("FUFFA"));

		assertThat(root).hasFieldOrPropertyWithValue("value", "Root");
		assertThat(root.getChildren()).hasSize(0);

	}

}
