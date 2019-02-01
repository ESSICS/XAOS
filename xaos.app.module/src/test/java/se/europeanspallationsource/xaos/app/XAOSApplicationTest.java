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
package se.europeanspallationsource.xaos.app;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class XAOSApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- XAOSApplicationTest ---------------------------------------");
	}

	private final XAOSApplication application = new XAOSApplication();

	/**
	 * Test of step method, of class XAOSApplication.
	 */
	@Test
	public void testStep() {

		System.out.println("  Testing 'step'...");

		application.stepsBegin(3);
		assertThat(application.getSteps()).isEqualTo(3);
		assertThat(application.getCurrentStep()).isEqualTo(-1);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

		application.step();
		assertThat(application.getSteps()).isEqualTo(3);
		assertThat(application.getCurrentStep()).isEqualTo(0);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

		application.step();
		assertThat(application.getSteps()).isEqualTo(3);
		assertThat(application.getCurrentStep()).isEqualTo(1);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

		application.step();
		assertThat(application.getSteps()).isEqualTo(3);
		assertThat(application.getCurrentStep()).isEqualTo(2);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

		application.stepsComplete();
		assertThat(application.getSteps()).isEqualTo(Double.MIN_VALUE);
		assertThat(application.getCurrentStep()).isEqualTo(Integer.MAX_VALUE);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

	}

	/**
	 * Test of step method called after completion, of class XAOSApplication.
	 */
	@Test( expected = IllegalStateException.class )
	public void testStepIllegalStateAfterCompletion() throws IllegalStateException {

		System.out.println("  Testing 'step' called after completion...");

		application.stepsComplete();
		application.step();

	}

	/**
	 * Test of step method called before begin, of class XAOSApplication.
	 */
	@Test( expected = IllegalStateException.class )
	public void testStepIllegalStateBeforeBegin() throws IllegalStateException {

		System.out.println("  Testing 'step' called before begin...");

		application.step();

	}

	/**
	 * Test of step method called too much, of class XAOSApplication.
	 */
	@Test( expected = IllegalStateException.class )
	public void testStepIllegalStateCalledTooMuch() throws IllegalStateException {

		System.out.println("  Testing 'step' called too much...");

		application.stepsBegin(3);
		application.step();
		application.step();
		application.step();

		application.step();	//	Called too much.

	}

	/**
	 * Test of stepsBegin method, of class XAOSApplication.
	 */
	@Test
	public void testStepsBegin() {

		System.out.println("  Testing 'stepsBegin'...");

		application.stepsBegin(11);
		assertThat(application.getSteps()).isEqualTo(11);
		assertThat(application.getCurrentStep()).isEqualTo(-1);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

	}

	/**
	 * Test of stepsBegin method with illegal parameter, of class XAOSApplication.
	 */
	@Test( expected = IllegalArgumentException.class )
	public void testStepsBeginIllegalArgument() throws IllegalArgumentException {

		System.out.println("  Testing 'stepsBegin' with illegal parameter...");

		application.stepsBegin(-1);

	}

	/**
	 * Test of stepsBegin method called twice, of class XAOSApplication.
	 */
	@Test( expected = IllegalStateException.class )
	public void testStepsBeginIllegalState() throws IllegalStateException {

		System.out.println("  Testing 'stepsBegin' called twice...");

		application.stepsBegin(10);
		application.stepsBegin(-1);

	}

	/**
	 * Test of stepsComplete method, of class XAOSApplication.
	 */
	@Test
	public void testStepsComplete() {

		System.out.println("  Testing 'stepsComplete'...");

		application.stepsComplete();
		assertThat(application.getSteps()).isEqualTo(Double.MIN_VALUE);
		assertThat(application.getCurrentStep()).isEqualTo(Integer.MAX_VALUE);
		assertThat(application.getSubSteps()).isEqualTo(-1);
		assertThat(application.getCurrentSubStep()).isEqualTo(-1);

	}

}
