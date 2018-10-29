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
	private TreeItem<Integer> r;

	@Before
	@SuppressWarnings( { "NestedAssignment", "unchecked" } )
	public void setUp() throws IOException {

		r = new TreeItem<>(1000);

		r.getChildren().addAll(
			a = new TreeItem<>(100),
			b = new TreeItem<>(200),
			c = new TreeItem<>(300)
		);
			a.getChildren().addAll(
				aa = new TreeItem<>(10),
				ab = new TreeItem<>(20),
				ac = new TreeItem<>(30),
				ad = new TreeItem<>(40)
			);
			c.getChildren().addAll(
				ca = new TreeItem<>(50),
				cb = new TreeItem<>(60)
			);
				cb.getChildren().addAll(
					cba = new TreeItem<>(1),
					cbb = new TreeItem<>(2),
					cbc = new TreeItem<>(3),
					cbd = new TreeItem<>(4)
				);

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
		TreeItemWalker<Integer> walker = new TreeItemWalker<>(r);

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
	public void testVisit() throws InterruptedException {

		System.out.println("  Testing 'visit(TreeNode)'...");

		CountDownLatch latch = new CountDownLatch(14);

		TreeItemWalker.visit(r, ti -> latch.countDown());

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
	@SuppressWarnings( "NestedAssignment" )
	public void testVisitValue() throws InterruptedException {

		System.out.println("  Testing 'visitValue(TreeNode)'...");

		final int[] sum = new int[] { 0 };

		TreeItemWalker.visitValue(r, v -> sum[0] += v);

		assertThat(sum[0]).isEqualTo(1820);

	}

	/**
	 * Test of hasNext/next methods, of class TreeItemWalker.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testWalking() throws InterruptedException {

		System.out.println("  Testing 'hasNext/next'...");

		CountDownLatch latch = new CountDownLatch(14);
		TreeItemWalker<Integer> walker = new TreeItemWalker<>(r);

		while ( walker.hasNext() ) {
			latch.countDown();
			assertThat(walker.next()).isNotNull().isInstanceOf(TreeItem.class);
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
