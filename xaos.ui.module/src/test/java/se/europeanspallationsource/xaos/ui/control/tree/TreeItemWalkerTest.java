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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.TreeItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeItemWalkerTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeItemWalkerTest ----------------------------------------");
	}

	private TreeItem<Integer> a;
	private TreeItem<Integer> aa;
	private TreeItem<Integer> ab;
	private TreeItem<Integer> ac;
	private TreeItem<Integer> ad;
	private TreeItem<Integer> b;
	private TreeItem<Integer> c;
	private TreeItem<Integer> ca;
	private TreeItem<Integer> cb;
	private TreeItem<Integer> cba;
	private TreeItem<Integer> cbb;
	private TreeItem<Integer> cbc;
	private TreeItem<Integer> cbd;
	private Map<TreeItem<Integer>, Integer> depthMap;
	private TreeItem<Integer> r;
	private Map<Integer, TreeItem<Integer>> valueMap;
	private int walkingIndex;
	private List<TreeItem<Integer>> walkingList;

	@Before
	@SuppressWarnings( { "NestedAssignment", "unchecked" } )
	public void setUp() throws IOException {

		r = new TreeItem<>(1000);				//	depth: 0

		r.getChildren().addAll(
			a = new TreeItem<>(100),			//	depth: 1
			b = new TreeItem<>(200),
			c = new TreeItem<>(300)
		);
			a.getChildren().addAll(
				aa = new TreeItem<>(10),		//	depth: 2
				ab = new TreeItem<>(20),
				ac = new TreeItem<>(30),
				ad = new TreeItem<>(40)
			);
			c.getChildren().addAll(
				ca = new TreeItem<>(50),		//	depth: 2
				cb = new TreeItem<>(60)
			);
				cb.getChildren().addAll(
					cba = new TreeItem<>(1),	//	depth: 3
					cbb = new TreeItem<>(2),
					cbc = new TreeItem<>(3),
					cbd = new TreeItem<>(4)
				);

		depthMap = new HashMap<>(14);

		depthMap.put(r,   0);
		depthMap.put(a,   1);
		depthMap.put(b,   1);
		depthMap.put(c,   1);
		depthMap.put(aa,  2);
		depthMap.put(ab,  2);
		depthMap.put(ac,  2);
		depthMap.put(ad,  2);
		depthMap.put(ca,  2);
		depthMap.put(cb,  2);
		depthMap.put(cba, 3);
		depthMap.put(cbb, 3);
		depthMap.put(cbc, 3);
		depthMap.put(cbd, 3);

		walkingIndex = 0;
		walkingList = new ArrayList<>(14);

		walkingList.add(r);
		walkingList.add(a);
		walkingList.add(aa);
		walkingList.add(ab);
		walkingList.add(ac);
		walkingList.add(ad);
		walkingList.add(b);
		walkingList.add(c);
		walkingList.add(ca);
		walkingList.add(cb);
		walkingList.add(cba);
		walkingList.add(cbb);
		walkingList.add(cbc);
		walkingList.add(cbd);

		valueMap = new HashMap<>(14);

		walkingList.forEach(ti -> {
			valueMap.put(ti.getValue(), ti);
		});

	}

	/**
	 * Test of getDepth method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testGetDepth() throws InterruptedException {

		System.out.println("  Testing 'getDepth'...");

		TreeItemWalker<Integer> walker = TreeItemWalker.build(r);

		while ( walker.hasNext() ) {
			//	walker.next() must be called before walker.getDepth().
			assertThat(depthMap.get(walker.next()).intValue()).isEqualTo(walker.getDepth());
		}

	}

	/**
	 * Test of stream method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testStream() throws InterruptedException {

		System.out.println("  Testing 'stream'...");

		CountDownLatch latch1 = new CountDownLatch(14);
		TreeItemWalker<Integer> walker = TreeItemWalker.build(r);

		walker.stream().forEach(ti -> latch1.countDown());

		if ( !latch1.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				14,
				latch1.getCount()
			));
		}

		//	Walk partially, then stream.
		CountDownLatch latch2 = new CountDownLatch(11);
		walker = new TreeItemWalker<>(r);

		walker.next();
		walker.next();
		walker.next();

		walker.stream().forEach(ti -> latch2.countDown());

		if ( !latch2.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				11,
				latch1.getCount()
			));
		}

		CountDownLatch latch3 = new CountDownLatch(3);

		walker = new TreeItemWalker<>(r);

		walker
			.stream()
			.filter(ti -> ti.getValue() >= 100 && ti.getValue() < 1000)
			.forEach(ti -> latch3.countDown());

		if ( !latch3.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				3,
				latch1.getCount()
			));
		}

	}

	/**
	 * Test of visit method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( "ValueOfIncrementOrDecrementUsed" )
	public void testVisit_Consumer() throws InterruptedException {

		System.out.println("  Testing 'visit(TreeNode, Consumer)'...");

		CountDownLatch latch = new CountDownLatch(14);

		TreeItemWalker.visit(r, ti -> {
			latch.countDown();
			assertThat(walkingList.indexOf(ti)).isEqualTo(walkingIndex++);
		});

		if ( !latch.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				14,
				latch.getCount()
			));
		}

	}

	/**
	 * Test of visit method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( "ValueOfIncrementOrDecrementUsed" )
	public void testVisit_BiConsumer() throws InterruptedException {

		System.out.println("  Testing 'visit(TreeNode, BiConsumer)'...");

		CountDownLatch latch = new CountDownLatch(14);

		TreeItemWalker.visit(r, ( ti, d ) -> {

			assertThat(walkingList.indexOf(ti)).isEqualTo(walkingIndex++);

			if ( depthMap.get(ti).equals(d) ) {
				latch.countDown();
			}

		});

		if ( !latch.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				14,
				latch.getCount()
			));
		}

	}

	/**
	 * Test of visitValue method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( { "NestedAssignment", "ValueOfIncrementOrDecrementUsed" } )
	public void testVisitValue_Consumer() throws InterruptedException {

		System.out.println("  Testing 'visitValue(TreeNode, Consumer)'...");

		final int[] sum = new int[] { 0 };

		TreeItemWalker.visitValue(r, v -> {

			sum[0] += v;

			assertThat(walkingList.indexOf(valueMap.get(v))).isEqualTo(walkingIndex++);

		});

		assertThat(sum[0]).isEqualTo(1820);

	}

	/**
	 * Test of visitValue method, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( { "NestedAssignment", "ValueOfIncrementOrDecrementUsed" } )
	public void testVisitValue_BiConsumer() throws InterruptedException {

		System.out.println("  Testing 'visitValue(TreeNode, BiConsumer)'...");

		final int[] sum = new int[] { 0 };

		TreeItemWalker.visitValue(r, ( v, d ) -> {

			sum[0] += v;

			TreeItem<Integer> ti = valueMap.get(v);

			assertThat(walkingList.indexOf(ti)).isEqualTo(walkingIndex++);
			assertThat(depthMap.get(ti)).isEqualTo(d);

		});

		assertThat(sum[0]).isEqualTo(1820);

	}

	/**
	 * Test of hasNext/next methods, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	@SuppressWarnings( "ValueOfIncrementOrDecrementUsed" )
	public void testWalking() throws InterruptedException {

		System.out.println("  Testing 'hasNext/next'...");

		CountDownLatch latch = new CountDownLatch(14);
		TreeItemWalker<Integer> walker = new TreeItemWalker<>(r);

		while ( walker.hasNext() ) {

			latch.countDown();

			TreeItem<Integer> item = walker.next();

			assertThat(item).isNotNull().isInstanceOf(TreeItem.class);
			assertThat(walkingList.indexOf(item)).isEqualTo(walkingIndex++);

		}

		if ( !latch.await(15, TimeUnit.SECONDS) ) {
			fail(MessageFormat.format(
				"Tree walking not completed in 1 minute [expected: {0}, current: {1}].",
				14,
				latch.getCount()
			));
		}

	}

}
