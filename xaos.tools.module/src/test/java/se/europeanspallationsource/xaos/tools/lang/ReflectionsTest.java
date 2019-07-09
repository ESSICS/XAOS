/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
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
package se.europeanspallationsource.xaos.tools.lang;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.tools.lang.Reflections.getField;
import static se.europeanspallationsource.xaos.tools.lang.Reflections.getFieldValue;
import static se.europeanspallationsource.xaos.tools.lang.Reflections.getMethod;
import static se.europeanspallationsource.xaos.tools.lang.Reflections.invokeMethod;
import static se.europeanspallationsource.xaos.tools.lang.Reflections.setFieldValue;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class ReflectionsTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- ReflectionsTest -------------------------------------------");
	}

	public ReflectionsTest() {
	}

	/**
	 * Test of getField method, of class Reflections.
	 *
	 * @throws java.lang.NoSuchFieldException
	 */
	@Test
	@SuppressWarnings( { "UnusedAssignment", "BroadCatchBlock", "TooBroadCatch" } )
	public void testGetField() throws NoSuchFieldException {

		System.out.println("  Testing 'getField'...");

		assertThat(getField(PublicInnerClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicInnerClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicInnerClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicInnerClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicInnerSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicInnerSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicInnerSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicInnerSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicInnerSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicInnerSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicInnerSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicInnerSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicInnerSubClass3.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicInnerSubClass3.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicInnerSubClass3.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicInnerSubClass3.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicInnerSubClass4.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicInnerSubClass4.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicInnerSubClass4.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicInnerSubClass4.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageInnerClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageInnerClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageInnerClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageInnerClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageInnerSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageInnerSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageInnerSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageInnerSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageInnerSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageInnerSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageInnerSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageInnerSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageInnerSubClass3.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageInnerSubClass3.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageInnerSubClass3.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageInnerSubClass3.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageInnerSubClass4.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageInnerSubClass4.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageInnerSubClass4.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageInnerSubClass4.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(ProtectedInnerClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(ProtectedInnerClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(ProtectedInnerClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(ProtectedInnerClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(ProtectedInnerSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(ProtectedInnerSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(ProtectedInnerSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(ProtectedInnerSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(ProtectedInnerSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(ProtectedInnerSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(ProtectedInnerSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(ProtectedInnerSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(ProtectedInnerSubClass3.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(ProtectedInnerSubClass3.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(ProtectedInnerSubClass3.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(ProtectedInnerSubClass3.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(ProtectedInnerSubClass4.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(ProtectedInnerSubClass4.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(ProtectedInnerSubClass4.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(ProtectedInnerSubClass4.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PrivateInnerClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PrivateInnerClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PrivateInnerClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PrivateInnerClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PrivateInnerSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PrivateInnerSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PrivateInnerSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PrivateInnerSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PrivateInnerSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PrivateInnerSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PrivateInnerSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PrivateInnerSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PrivateInnerSubClass3.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PrivateInnerSubClass3.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PrivateInnerSubClass3.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PrivateInnerSubClass3.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PrivateInnerSubClass4.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PrivateInnerSubClass4.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PrivateInnerSubClass4.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PrivateInnerSubClass4.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicOuterClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicOuterClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicOuterClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicOuterClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicOuterSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicOuterSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicOuterSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicOuterSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PublicOuterSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PublicOuterSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PublicOuterSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PublicOuterSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageOuterClass.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageOuterClass.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageOuterClass.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageOuterClass.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageOuterSubClass1.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageOuterSubClass1.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageOuterSubClass1.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageOuterSubClass1.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		assertThat(getField(PackageOuterSubClass2.class, "publicField")).isNotNull().hasFieldOrPropertyWithValue("name", "publicField");
		assertThat(getField(PackageOuterSubClass2.class, "packageField")).isNotNull().hasFieldOrPropertyWithValue("name", "packageField");
		assertThat(getField(PackageOuterSubClass2.class, "protectedField")).isNotNull().hasFieldOrPropertyWithValue("name", "protectedField");
		assertThat(getField(PackageOuterSubClass2.class, "privateField")).isNotNull().hasFieldOrPropertyWithValue("name", "privateField");

		Field field = null;

		try {
			field = getField(PublicInnerClass.class, "nonExistingField");
			fail("No exception for a non existing field.");
		} catch ( NoSuchFieldException nsfex ) {
			System.out.println(MessageFormat.format(
				"    Expected NoSuchFieldException: {0}",
				nsfex.getMessage()
			));
			assertThat(field).isNull();
		} catch ( Exception ex ) {
			System.out.println(MessageFormat.format(
				"    Unexpected exception: {0} [{1}].",
				ex.getClass().getSimpleName(),
				ex.getMessage()
			));
			fail("Unexpected exception for a non existing field.");
		}

	}

	/**
	 * Test of getFieldValue method, of class Reflections.
	 */
	@Test
	@SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch" } )
	public void testGetFieldValue() {

		System.out.println("  Testing 'getFieldValue'...");

		assertThat(getFieldValue(new PublicInnerClass(), "publicField")).isEqualTo(1);
		assertThat(getFieldValue(new PublicInnerClass(), "packageField")).isEqualTo(2);
		assertThat(getFieldValue(new PublicInnerClass(), "protectedField")).isEqualTo(3);
		assertThat(getFieldValue(new PublicInnerClass(), "privateField")).isEqualTo(4);

		assertThat(getFieldValue(new PublicInnerSubClass1(), "publicField")).isEqualTo(1);
		assertThat(getFieldValue(new PublicInnerSubClass1(), "packageField")).isEqualTo(2);
		assertThat(getFieldValue(new PublicInnerSubClass1(), "protectedField")).isEqualTo(3);
		assertThat(getFieldValue(new PublicInnerSubClass1(), "privateField")).isEqualTo(4);

		assertThat(getFieldValue(new PublicInnerSubClass2(), "publicField")).isEqualTo(11);
		assertThat(getFieldValue(new PublicInnerSubClass2(), "packageField")).isEqualTo(12);
		assertThat(getFieldValue(new PublicInnerSubClass2(), "protectedField")).isEqualTo(13);
		assertThat(getFieldValue(new PublicInnerSubClass2(), "privateField")).isEqualTo(14);

		assertThat(getFieldValue(new PublicInnerSubClass3(), "publicField")).isEqualTo(21);
		assertThat(getFieldValue(new PublicInnerSubClass3(), "packageField")).isEqualTo(22);
		assertThat(getFieldValue(new PublicInnerSubClass3(), "protectedField")).isEqualTo(23);
		assertThat(getFieldValue(new PublicInnerSubClass3(), "privateField")).isEqualTo(24);

		assertThat(getFieldValue(new PublicInnerSubClass4(), "publicField")).isEqualTo(31);
		assertThat(getFieldValue(new PublicInnerSubClass4(), "packageField")).isEqualTo(32);
		assertThat(getFieldValue(new PublicInnerSubClass4(), "protectedField")).isEqualTo(33);
		assertThat(getFieldValue(new PublicInnerSubClass4(), "privateField")).isEqualTo(34);

		assertThat(getFieldValue(new PackageInnerClass(), "publicField")).isEqualTo(11);
		assertThat(getFieldValue(new PackageInnerClass(), "packageField")).isEqualTo(12);
		assertThat(getFieldValue(new PackageInnerClass(), "protectedField")).isEqualTo(13);
		assertThat(getFieldValue(new PackageInnerClass(), "privateField")).isEqualTo(14);

		assertThat(getFieldValue(new PackageInnerSubClass1(), "publicField")).isEqualTo(1);
		assertThat(getFieldValue(new PackageInnerSubClass1(), "packageField")).isEqualTo(2);
		assertThat(getFieldValue(new PackageInnerSubClass1(), "protectedField")).isEqualTo(3);
		assertThat(getFieldValue(new PackageInnerSubClass1(), "privateField")).isEqualTo(4);

		assertThat(getFieldValue(new PackageInnerSubClass2(), "publicField")).isEqualTo(11);
		assertThat(getFieldValue(new PackageInnerSubClass2(), "packageField")).isEqualTo(12);
		assertThat(getFieldValue(new PackageInnerSubClass2(), "protectedField")).isEqualTo(13);
		assertThat(getFieldValue(new PackageInnerSubClass2(), "privateField")).isEqualTo(14);

		assertThat(getFieldValue(new PackageInnerSubClass3(), "publicField")).isEqualTo(21);
		assertThat(getFieldValue(new PackageInnerSubClass3(), "packageField")).isEqualTo(22);
		assertThat(getFieldValue(new PackageInnerSubClass3(), "protectedField")).isEqualTo(23);
		assertThat(getFieldValue(new PackageInnerSubClass3(), "privateField")).isEqualTo(24);

		assertThat(getFieldValue(new PackageInnerSubClass4(), "publicField")).isEqualTo(31);
		assertThat(getFieldValue(new PackageInnerSubClass4(), "packageField")).isEqualTo(32);
		assertThat(getFieldValue(new PackageInnerSubClass4(), "protectedField")).isEqualTo(33);
		assertThat(getFieldValue(new PackageInnerSubClass4(), "privateField")).isEqualTo(34);

		assertThat(getFieldValue(new ProtectedInnerClass(), "publicField")).isEqualTo(21);
		assertThat(getFieldValue(new ProtectedInnerClass(), "packageField")).isEqualTo(22);
		assertThat(getFieldValue(new ProtectedInnerClass(), "protectedField")).isEqualTo(23);
		assertThat(getFieldValue(new ProtectedInnerClass(), "privateField")).isEqualTo(24);

		assertThat(getFieldValue(new ProtectedInnerSubClass1(), "publicField")).isEqualTo(1);
		assertThat(getFieldValue(new ProtectedInnerSubClass1(), "packageField")).isEqualTo(2);
		assertThat(getFieldValue(new ProtectedInnerSubClass1(), "protectedField")).isEqualTo(3);
		assertThat(getFieldValue(new ProtectedInnerSubClass1(), "privateField")).isEqualTo(4);

		assertThat(getFieldValue(new ProtectedInnerSubClass2(), "publicField")).isEqualTo(11);
		assertThat(getFieldValue(new ProtectedInnerSubClass2(), "packageField")).isEqualTo(12);
		assertThat(getFieldValue(new ProtectedInnerSubClass2(), "protectedField")).isEqualTo(13);
		assertThat(getFieldValue(new ProtectedInnerSubClass2(), "privateField")).isEqualTo(14);

		assertThat(getFieldValue(new ProtectedInnerSubClass3(), "publicField")).isEqualTo(21);
		assertThat(getFieldValue(new ProtectedInnerSubClass3(), "packageField")).isEqualTo(22);
		assertThat(getFieldValue(new ProtectedInnerSubClass3(), "protectedField")).isEqualTo(23);
		assertThat(getFieldValue(new ProtectedInnerSubClass3(), "privateField")).isEqualTo(24);

		assertThat(getFieldValue(new ProtectedInnerSubClass4(), "publicField")).isEqualTo(31);
		assertThat(getFieldValue(new ProtectedInnerSubClass4(), "packageField")).isEqualTo(32);
		assertThat(getFieldValue(new ProtectedInnerSubClass4(), "protectedField")).isEqualTo(33);
		assertThat(getFieldValue(new ProtectedInnerSubClass4(), "privateField")).isEqualTo(34);

		assertThat(getFieldValue(new PrivateInnerClass(), "publicField")).isEqualTo(31);
		assertThat(getFieldValue(new PrivateInnerClass(), "packageField")).isEqualTo(32);
		assertThat(getFieldValue(new PrivateInnerClass(), "protectedField")).isEqualTo(33);
		assertThat(getFieldValue(new PrivateInnerClass(), "privateField")).isEqualTo(34);

		assertThat(getFieldValue(new PrivateInnerSubClass1(), "publicField")).isEqualTo(1);
		assertThat(getFieldValue(new PrivateInnerSubClass1(), "packageField")).isEqualTo(2);
		assertThat(getFieldValue(new PrivateInnerSubClass1(), "protectedField")).isEqualTo(3);
		assertThat(getFieldValue(new PrivateInnerSubClass1(), "privateField")).isEqualTo(4);

		assertThat(getFieldValue(new PrivateInnerSubClass2(), "publicField")).isEqualTo(11);
		assertThat(getFieldValue(new PrivateInnerSubClass2(), "packageField")).isEqualTo(12);
		assertThat(getFieldValue(new PrivateInnerSubClass2(), "protectedField")).isEqualTo(13);
		assertThat(getFieldValue(new PrivateInnerSubClass2(), "privateField")).isEqualTo(14);

		assertThat(getFieldValue(new PrivateInnerSubClass3(), "publicField")).isEqualTo(21);
		assertThat(getFieldValue(new PrivateInnerSubClass3(), "packageField")).isEqualTo(22);
		assertThat(getFieldValue(new PrivateInnerSubClass3(), "protectedField")).isEqualTo(23);
		assertThat(getFieldValue(new PrivateInnerSubClass3(), "privateField")).isEqualTo(24);

		assertThat(getFieldValue(new PrivateInnerSubClass4(), "publicField")).isEqualTo(31);
		assertThat(getFieldValue(new PrivateInnerSubClass4(), "packageField")).isEqualTo(32);
		assertThat(getFieldValue(new PrivateInnerSubClass4(), "protectedField")).isEqualTo(33);
		assertThat(getFieldValue(new PrivateInnerSubClass4(), "privateField")).isEqualTo(34);

		assertThat(getFieldValue(new PublicOuterClass(), "publicField")).isEqualTo(101);
		assertThat(getFieldValue(new PublicOuterClass(), "packageField")).isEqualTo(102);
		assertThat(getFieldValue(new PublicOuterClass(), "protectedField")).isEqualTo(103);
		assertThat(getFieldValue(new PublicOuterClass(), "privateField")).isEqualTo(104);

		assertThat(getFieldValue(new PublicOuterSubClass1(), "publicField")).isEqualTo(101);
		assertThat(getFieldValue(new PublicOuterSubClass1(), "packageField")).isEqualTo(102);
		assertThat(getFieldValue(new PublicOuterSubClass1(), "protectedField")).isEqualTo(103);
		assertThat(getFieldValue(new PublicOuterSubClass1(), "privateField")).isEqualTo(104);

		assertThat(getFieldValue(new PublicOuterSubClass2(), "publicField")).isEqualTo(111);
		assertThat(getFieldValue(new PublicOuterSubClass2(), "packageField")).isEqualTo(112);
		assertThat(getFieldValue(new PublicOuterSubClass2(), "protectedField")).isEqualTo(113);
		assertThat(getFieldValue(new PublicOuterSubClass2(), "privateField")).isEqualTo(114);

		assertThat(getFieldValue(new PackageOuterClass(), "publicField")).isEqualTo(111);
		assertThat(getFieldValue(new PackageOuterClass(), "packageField")).isEqualTo(112);
		assertThat(getFieldValue(new PackageOuterClass(), "protectedField")).isEqualTo(113);
		assertThat(getFieldValue(new PackageOuterClass(), "privateField")).isEqualTo(114);

		assertThat(getFieldValue(new PackageOuterSubClass1(), "publicField")).isEqualTo(101);
		assertThat(getFieldValue(new PackageOuterSubClass1(), "packageField")).isEqualTo(102);
		assertThat(getFieldValue(new PackageOuterSubClass1(), "protectedField")).isEqualTo(103);
		assertThat(getFieldValue(new PackageOuterSubClass1(), "privateField")).isEqualTo(104);

		assertThat(getFieldValue(new PackageOuterSubClass2(), "publicField")).isEqualTo(111);
		assertThat(getFieldValue(new PackageOuterSubClass2(), "packageField")).isEqualTo(112);
		assertThat(getFieldValue(new PackageOuterSubClass2(), "protectedField")).isEqualTo(113);
		assertThat(getFieldValue(new PackageOuterSubClass2(), "privateField")).isEqualTo(114);

		Object value = null;

		try {
			value = getFieldValue(new PublicInnerClass(), "nonExistingField");
			fail("No exception for a non existing field value.");
		} catch ( RuntimeException rex ) {
			System.out.println(MessageFormat.format(
				"    Expected RuntimeException: {0} [{1}].",
				rex.getCause().getClass().getSimpleName(),
				rex.getMessage()
			));
			assertThat(value).isNull();
		} catch ( Exception ex ) {
			System.out.println(MessageFormat.format(
				"    Unexpected exception: {0} [{1}].",
				ex.getClass().getSimpleName(),
				ex.getMessage()
			));
			fail("Unexpected exception for a non existing field value.");
		}

	}

	/**
	 * Test of getMethod method, of class Reflections.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	@SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch", "UnusedAssignment" } )
	public void testGetMethod() throws Exception {

		System.out.println("  Testing 'getMethod'...");

		assertThat(getMethod(PublicInnerClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicInnerClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicInnerClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicInnerClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicInnerSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicInnerSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicInnerSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicInnerSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicInnerSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicInnerSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicInnerSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicInnerSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicInnerSubClass3.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicInnerSubClass3.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicInnerSubClass3.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicInnerSubClass3.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicInnerSubClass4.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicInnerSubClass4.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicInnerSubClass4.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicInnerSubClass4.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageInnerClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageInnerClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageInnerClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageInnerClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageInnerSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageInnerSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageInnerSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageInnerSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageInnerSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageInnerSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageInnerSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageInnerSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageInnerSubClass3.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageInnerSubClass3.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageInnerSubClass3.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageInnerSubClass3.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageInnerSubClass4.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageInnerSubClass4.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageInnerSubClass4.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageInnerSubClass4.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(ProtectedInnerClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(ProtectedInnerClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(ProtectedInnerClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(ProtectedInnerClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(ProtectedInnerSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(ProtectedInnerSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(ProtectedInnerSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(ProtectedInnerSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(ProtectedInnerSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(ProtectedInnerSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(ProtectedInnerSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(ProtectedInnerSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(ProtectedInnerSubClass3.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(ProtectedInnerSubClass3.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(ProtectedInnerSubClass3.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(ProtectedInnerSubClass3.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(ProtectedInnerSubClass4.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(ProtectedInnerSubClass4.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(ProtectedInnerSubClass4.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(ProtectedInnerSubClass4.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PrivateInnerClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PrivateInnerClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PrivateInnerClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PrivateInnerClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PrivateInnerSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PrivateInnerSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PrivateInnerSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PrivateInnerSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PrivateInnerSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PrivateInnerSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PrivateInnerSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PrivateInnerSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PrivateInnerSubClass3.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PrivateInnerSubClass3.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PrivateInnerSubClass3.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PrivateInnerSubClass3.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PrivateInnerSubClass4.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PrivateInnerSubClass4.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PrivateInnerSubClass4.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PrivateInnerSubClass4.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicOuterClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicOuterClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicOuterClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicOuterClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicOuterSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicOuterSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicOuterSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicOuterSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PublicOuterSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PublicOuterSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PublicOuterSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PublicOuterSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageOuterClass.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageOuterClass.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageOuterClass.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageOuterClass.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageOuterSubClass1.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageOuterSubClass1.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageOuterSubClass1.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageOuterSubClass1.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		assertThat(getMethod(PackageOuterSubClass2.class, "setPublicField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPublicField");
		assertThat(getMethod(PackageOuterSubClass2.class, "setPackageField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPackageField");
		assertThat(getMethod(PackageOuterSubClass2.class, "setProtectedField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setProtectedField");
		assertThat(getMethod(PackageOuterSubClass2.class, "setPrivateField", int.class)).isNotNull().hasFieldOrPropertyWithValue("name", "setPrivateField");

		Method method = null;

		try {
			method = getMethod(PublicInnerClass.class, "nonExistingMethod");
			fail("No exception for a non existing field.");
		} catch ( NoSuchMethodException nsmex ) {
			System.out.println(MessageFormat.format(
				"    Expected NoSuchMethodException: {0}",
				nsmex.getMessage()
			));
			assertThat(method).isNull();
		} catch ( Exception ex ) {
			System.out.println(MessageFormat.format(
				"    Unexpected exception: {0} [{1}].",
				ex.getClass().getSimpleName(),
				ex.getMessage()
			));
			fail("Unexpected exception for a non existing method.");
		}

	}

	/**
	 * Test of invokeMethod method, of class Reflections.
	 *
	 * @throws java.lang.NoSuchMethodException
	 */
	@Test
	@SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch" } )
	public void testInvokeMethod() throws NoSuchMethodException {

		System.out.println("  Testing 'invokeMethod'...");

		assertThat(invokeMethod(new PublicInnerClass(), "getPublicField")).isEqualTo(1);
		assertThat(invokeMethod(new PublicInnerClass(), "getPackageField")).isEqualTo(2);
		assertThat(invokeMethod(new PublicInnerClass(), "getProtectedField")).isEqualTo(3);
		assertThat(invokeMethod(new PublicInnerClass(), "getPrivateField")).isEqualTo(4);

		assertThat(invokeMethod(new PublicInnerSubClass1(), "getPublicField")).isEqualTo(1);
		assertThat(invokeMethod(new PublicInnerSubClass1(), "getPackageField")).isEqualTo(2);
		assertThat(invokeMethod(new PublicInnerSubClass1(), "getProtectedField")).isEqualTo(3);
		assertThat(invokeMethod(new PublicInnerSubClass1(), "getPrivateField")).isEqualTo(4);

		assertThat(invokeMethod(new PublicInnerSubClass2(), "getPublicField")).isEqualTo(11);
		assertThat(invokeMethod(new PublicInnerSubClass2(), "getPackageField")).isEqualTo(12);
		assertThat(invokeMethod(new PublicInnerSubClass2(), "getProtectedField")).isEqualTo(13);
		assertThat(invokeMethod(new PublicInnerSubClass2(), "getPrivateField")).isEqualTo(14);

		assertThat(invokeMethod(new PublicInnerSubClass3(), "getPublicField")).isEqualTo(21);
		assertThat(invokeMethod(new PublicInnerSubClass3(), "getPackageField")).isEqualTo(22);
		assertThat(invokeMethod(new PublicInnerSubClass3(), "getProtectedField")).isEqualTo(23);
		assertThat(invokeMethod(new PublicInnerSubClass3(), "getPrivateField")).isEqualTo(24);

		assertThat(invokeMethod(new PublicInnerSubClass4(), "getPublicField")).isEqualTo(31);
		assertThat(invokeMethod(new PublicInnerSubClass4(), "getPackageField")).isEqualTo(32);
		assertThat(invokeMethod(new PublicInnerSubClass4(), "getProtectedField")).isEqualTo(33);
		assertThat(invokeMethod(new PublicInnerSubClass4(), "getPrivateField")).isEqualTo(34);

		assertThat(invokeMethod(new PackageInnerClass(), "getPublicField")).isEqualTo(11);
		assertThat(invokeMethod(new PackageInnerClass(), "getPackageField")).isEqualTo(12);
		assertThat(invokeMethod(new PackageInnerClass(), "getProtectedField")).isEqualTo(13);
		assertThat(invokeMethod(new PackageInnerClass(), "getPrivateField")).isEqualTo(14);

		assertThat(invokeMethod(new PackageInnerSubClass1(), "getPublicField")).isEqualTo(1);
		assertThat(invokeMethod(new PackageInnerSubClass1(), "getPackageField")).isEqualTo(2);
		assertThat(invokeMethod(new PackageInnerSubClass1(), "getProtectedField")).isEqualTo(3);
		assertThat(invokeMethod(new PackageInnerSubClass1(), "getPrivateField")).isEqualTo(4);

		assertThat(invokeMethod(new PackageInnerSubClass2(), "getPublicField")).isEqualTo(11);
		assertThat(invokeMethod(new PackageInnerSubClass2(), "getPackageField")).isEqualTo(12);
		assertThat(invokeMethod(new PackageInnerSubClass2(), "getProtectedField")).isEqualTo(13);
		assertThat(invokeMethod(new PackageInnerSubClass2(), "getPrivateField")).isEqualTo(14);

		assertThat(invokeMethod(new PackageInnerSubClass3(), "getPublicField")).isEqualTo(21);
		assertThat(invokeMethod(new PackageInnerSubClass3(), "getPackageField")).isEqualTo(22);
		assertThat(invokeMethod(new PackageInnerSubClass3(), "getProtectedField")).isEqualTo(23);
		assertThat(invokeMethod(new PackageInnerSubClass3(), "getPrivateField")).isEqualTo(24);

		assertThat(invokeMethod(new PackageInnerSubClass4(), "getPublicField")).isEqualTo(31);
		assertThat(invokeMethod(new PackageInnerSubClass4(), "getPackageField")).isEqualTo(32);
		assertThat(invokeMethod(new PackageInnerSubClass4(), "getProtectedField")).isEqualTo(33);
		assertThat(invokeMethod(new PackageInnerSubClass4(), "getPrivateField")).isEqualTo(34);

		assertThat(invokeMethod(new ProtectedInnerClass(), "getPublicField")).isEqualTo(21);
		assertThat(invokeMethod(new ProtectedInnerClass(), "getPackageField")).isEqualTo(22);
		assertThat(invokeMethod(new ProtectedInnerClass(), "getProtectedField")).isEqualTo(23);
		assertThat(invokeMethod(new ProtectedInnerClass(), "getPrivateField")).isEqualTo(24);

		assertThat(invokeMethod(new ProtectedInnerSubClass1(), "getPublicField")).isEqualTo(1);
		assertThat(invokeMethod(new ProtectedInnerSubClass1(), "getPackageField")).isEqualTo(2);
		assertThat(invokeMethod(new ProtectedInnerSubClass1(), "getProtectedField")).isEqualTo(3);
		assertThat(invokeMethod(new ProtectedInnerSubClass1(), "getPrivateField")).isEqualTo(4);

		assertThat(invokeMethod(new ProtectedInnerSubClass2(), "getPublicField")).isEqualTo(11);
		assertThat(invokeMethod(new ProtectedInnerSubClass2(), "getPackageField")).isEqualTo(12);
		assertThat(invokeMethod(new ProtectedInnerSubClass2(), "getProtectedField")).isEqualTo(13);
		assertThat(invokeMethod(new ProtectedInnerSubClass2(), "getPrivateField")).isEqualTo(14);

		assertThat(invokeMethod(new ProtectedInnerSubClass3(), "getPublicField")).isEqualTo(21);
		assertThat(invokeMethod(new ProtectedInnerSubClass3(), "getPackageField")).isEqualTo(22);
		assertThat(invokeMethod(new ProtectedInnerSubClass3(), "getProtectedField")).isEqualTo(23);
		assertThat(invokeMethod(new ProtectedInnerSubClass3(), "getPrivateField")).isEqualTo(24);

		assertThat(invokeMethod(new ProtectedInnerSubClass4(), "getPublicField")).isEqualTo(31);
		assertThat(invokeMethod(new ProtectedInnerSubClass4(), "getPackageField")).isEqualTo(32);
		assertThat(invokeMethod(new ProtectedInnerSubClass4(), "getProtectedField")).isEqualTo(33);
		assertThat(invokeMethod(new ProtectedInnerSubClass4(), "getPrivateField")).isEqualTo(34);

		assertThat(invokeMethod(new PrivateInnerClass(), "getPublicField")).isEqualTo(31);
		assertThat(invokeMethod(new PrivateInnerClass(), "getPackageField")).isEqualTo(32);
		assertThat(invokeMethod(new PrivateInnerClass(), "getProtectedField")).isEqualTo(33);
		assertThat(invokeMethod(new PrivateInnerClass(), "getPrivateField")).isEqualTo(34);

		assertThat(invokeMethod(new PrivateInnerSubClass1(), "getPublicField")).isEqualTo(1);
		assertThat(invokeMethod(new PrivateInnerSubClass1(), "getPackageField")).isEqualTo(2);
		assertThat(invokeMethod(new PrivateInnerSubClass1(), "getProtectedField")).isEqualTo(3);
		assertThat(invokeMethod(new PrivateInnerSubClass1(), "getPrivateField")).isEqualTo(4);

		assertThat(invokeMethod(new PrivateInnerSubClass2(), "getPublicField")).isEqualTo(11);
		assertThat(invokeMethod(new PrivateInnerSubClass2(), "getPackageField")).isEqualTo(12);
		assertThat(invokeMethod(new PrivateInnerSubClass2(), "getProtectedField")).isEqualTo(13);
		assertThat(invokeMethod(new PrivateInnerSubClass2(), "getPrivateField")).isEqualTo(14);

		assertThat(invokeMethod(new PrivateInnerSubClass3(), "getPublicField")).isEqualTo(21);
		assertThat(invokeMethod(new PrivateInnerSubClass3(), "getPackageField")).isEqualTo(22);
		assertThat(invokeMethod(new PrivateInnerSubClass3(), "getProtectedField")).isEqualTo(23);
		assertThat(invokeMethod(new PrivateInnerSubClass3(), "getPrivateField")).isEqualTo(24);

		assertThat(invokeMethod(new PrivateInnerSubClass4(), "getPublicField")).isEqualTo(31);
		assertThat(invokeMethod(new PrivateInnerSubClass4(), "getPackageField")).isEqualTo(32);
		assertThat(invokeMethod(new PrivateInnerSubClass4(), "getProtectedField")).isEqualTo(33);
		assertThat(invokeMethod(new PrivateInnerSubClass4(), "getPrivateField")).isEqualTo(34);

		assertThat(invokeMethod(new PublicOuterClass(), "getPublicField")).isEqualTo(101);
		assertThat(invokeMethod(new PublicOuterClass(), "getPackageField")).isEqualTo(102);
		assertThat(invokeMethod(new PublicOuterClass(), "getProtectedField")).isEqualTo(103);
		assertThat(invokeMethod(new PublicOuterClass(), "getPrivateField")).isEqualTo(104);

		assertThat(invokeMethod(new PublicOuterSubClass1(), "getPublicField")).isEqualTo(101);
		assertThat(invokeMethod(new PublicOuterSubClass1(), "getPackageField")).isEqualTo(102);
		assertThat(invokeMethod(new PublicOuterSubClass1(), "getProtectedField")).isEqualTo(103);
		assertThat(invokeMethod(new PublicOuterSubClass1(), "getPrivateField")).isEqualTo(104);

		assertThat(invokeMethod(new PublicOuterSubClass2(), "getPublicField")).isEqualTo(111);
		assertThat(invokeMethod(new PublicOuterSubClass2(), "getPackageField")).isEqualTo(112);
		assertThat(invokeMethod(new PublicOuterSubClass2(), "getProtectedField")).isEqualTo(113);
		assertThat(invokeMethod(new PublicOuterSubClass2(), "getPrivateField")).isEqualTo(114);

		assertThat(invokeMethod(new PackageOuterClass(), "getPublicField")).isEqualTo(111);
		assertThat(invokeMethod(new PackageOuterClass(), "getPackageField")).isEqualTo(112);
		assertThat(invokeMethod(new PackageOuterClass(), "getProtectedField")).isEqualTo(113);
		assertThat(invokeMethod(new PackageOuterClass(), "getPrivateField")).isEqualTo(114);

		assertThat(invokeMethod(new PackageOuterSubClass1(), "getPublicField")).isEqualTo(101);
		assertThat(invokeMethod(new PackageOuterSubClass1(), "getPackageField")).isEqualTo(102);
		assertThat(invokeMethod(new PackageOuterSubClass1(), "getProtectedField")).isEqualTo(103);
		assertThat(invokeMethod(new PackageOuterSubClass1(), "getPrivateField")).isEqualTo(104);

		assertThat(invokeMethod(new PackageOuterSubClass2(), "getPublicField")).isEqualTo(111);
		assertThat(invokeMethod(new PackageOuterSubClass2(), "getPackageField")).isEqualTo(112);
		assertThat(invokeMethod(new PackageOuterSubClass2(), "getProtectedField")).isEqualTo(113);
		assertThat(invokeMethod(new PackageOuterSubClass2(), "getPrivateField")).isEqualTo(114);

		Object value = null;

		try {
			value = invokeMethod(new PublicInnerClass(), "getNonExistingField");
			fail("No exception for a non existing method.");
		} catch ( RuntimeException rex ) {
			System.out.println(MessageFormat.format(
				"    Expected RuntimeException: {0} [{1}].",
				rex.getCause().getClass().getSimpleName(),
				rex.getMessage()
			));
			assertThat(value).isNull();
		} catch ( Exception ex ) {
			System.out.println(MessageFormat.format(
				"    Unexpected exception: {0} [{1}].",
				ex.getClass().getSimpleName(),
				ex.getMessage()
			));
			fail("Unexpected exception for a non existing method.");
		}

	}

	/**
	 * Test of setFieldValue method, of class Reflections.
	 */
	@Test
	public void testSetFieldValue() {

		System.out.println("  Testing 'setFieldValue'...");

		assertThat(setAndReadBack(new PublicInnerClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicInnerSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicInnerSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass2(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicInnerSubClass3(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass3(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass3(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass3(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicInnerSubClass4(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass4(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass4(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicInnerSubClass4(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageInnerClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageInnerSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageInnerSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass2(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageInnerSubClass3(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass3(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass3(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass3(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageInnerSubClass4(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass4(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass4(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageInnerSubClass4(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new ProtectedInnerClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new ProtectedInnerSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new ProtectedInnerSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass2(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new ProtectedInnerSubClass3(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass3(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass3(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass3(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new ProtectedInnerSubClass4(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass4(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass4(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new ProtectedInnerSubClass4(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PrivateInnerClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PrivateInnerSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PrivateInnerSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass2(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PrivateInnerSubClass3(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass3(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass3(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass3(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PrivateInnerSubClass4(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass4(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass4(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PrivateInnerSubClass4(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicOuterClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicOuterSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PublicOuterSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PublicOuterSubClass2(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageOuterClass(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterClass(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterClass(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterClass(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageOuterSubClass1(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass1(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass1(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass1(), "privateField", 12345)).isEqualTo(12345);

		assertThat(setAndReadBack(new PackageOuterSubClass2(), "publicField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass2(), "packageField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass2(), "protectedField", 12345)).isEqualTo(12345);
		assertThat(setAndReadBack(new PackageOuterSubClass2(), "privateField", 12345)).isEqualTo(12345);

		Object value = null;

		try {
			value = setAndReadBack(new PublicInnerClass(), "nonExistingField", 12345);
			fail("No exception for setting a non existing field.");
		} catch ( RuntimeException rex ) {
			System.out.println(MessageFormat.format(
				"    Expected RuntimeException: {0} [{1}].",
				rex.getCause().getClass().getSimpleName(),
				rex.getMessage()
			));
			assertThat(value).isNull();
		} catch ( Exception ex ) {
			System.out.println(MessageFormat.format(
				"    Unexpected exception: {0} [{1}].",
				ex.getClass().getSimpleName(),
				ex.getMessage()
			));
			fail("Unexpected exception for setting a non existing field.");
		}

	}

	private Object setAndReadBack( Object object, String name, Object value ) {

		setFieldValue(object, name, value);

		return getFieldValue(object, name);

	}

	@SuppressWarnings( "PublicInnerClass" )
	public class PublicInnerClass {

		@SuppressWarnings( "PublicField" )
		public int publicField = 1;
		@SuppressWarnings( "PackageVisibleField" )
		int packageField = 2;
		@SuppressWarnings( "ProtectedField" )
		protected int protectedField = 3;
		private int privateField = 4;

		public int getPublicField() {
			return publicField;
		}

		public void setPublicField( int publicField ) {
			this.publicField = publicField;
		}

		int getPackageField() {
			return packageField;
		}

		void setPackageField( int packageField ) {
			this.packageField = packageField;
		}

		protected int getProtectedField() {
			return protectedField;
		}

		protected void setProtectedField( int protectedField ) {
			this.protectedField = protectedField;
		}

		private int getPrivateField() {
			return privateField;
		}

		private void setPrivateField( int privateField ) {
			this.privateField = privateField;
		}

	}

	@SuppressWarnings( "PublicInnerClass" )
	public class PublicInnerSubClass1 extends PublicInnerClass {
	}

	@SuppressWarnings( "PublicInnerClass" )
	public class PublicInnerSubClass2 extends PackageInnerClass {
	}

	@SuppressWarnings( "PublicInnerClass" )
	public class PublicInnerSubClass3 extends ProtectedInnerClass {
	}

	@SuppressWarnings( "PublicInnerClass" )
	public class PublicInnerSubClass4 extends PrivateInnerClass {
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	class PackageInnerClass {

		@SuppressWarnings( "PackageVisibleField" )
		public int publicField = 11;
		@SuppressWarnings( "PackageVisibleField" )
		int packageField = 12;
		@SuppressWarnings( "PackageVisibleField" )
		protected int protectedField = 13;
		private int privateField = 14;

		public int getPublicField() {
			return publicField;
		}

		public void setPublicField( int publicField ) {
			this.publicField = publicField;
		}

		int getPackageField() {
			return packageField;
		}

		void setPackageField( int packageField ) {
			this.packageField = packageField;
		}

		protected int getProtectedField() {
			return protectedField;
		}

		protected void setProtectedField( int protectedField ) {
			this.protectedField = protectedField;
		}

		private int getPrivateField() {
			return privateField;
		}

		private void setPrivateField( int privateField ) {
			this.privateField = privateField;
		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	class PackageInnerSubClass1 extends PublicInnerClass {
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	class PackageInnerSubClass2 extends PackageInnerClass {
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	class PackageInnerSubClass3 extends ProtectedInnerClass {
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	class PackageInnerSubClass4 extends PrivateInnerClass {
	}

	@SuppressWarnings( "ProtectedInnerClass" )
	protected class ProtectedInnerClass {

		@SuppressWarnings( "ProtectedField" )
		public int publicField = 21;
		@SuppressWarnings( "PackageVisibleField" )
		int packageField = 22;
		@SuppressWarnings( "ProtectedField" )
		protected int protectedField = 23;
		private int privateField = 24;

		public int getPublicField() {
			return publicField;
		}

		public void setPublicField( int publicField ) {
			this.publicField = publicField;
		}

		int getPackageField() {
			return packageField;
		}

		void setPackageField( int packageField ) {
			this.packageField = packageField;
		}

		protected int getProtectedField() {
			return protectedField;
		}

		protected void setProtectedField( int protectedField ) {
			this.protectedField = protectedField;
		}

		private int getPrivateField() {
			return privateField;
		}

		private void setPrivateField( int privateField ) {
			this.privateField = privateField;
		}

	}

	@SuppressWarnings( "ProtectedInnerClass" )
	protected class ProtectedInnerSubClass1 extends PublicInnerClass {
	}

	@SuppressWarnings( "ProtectedInnerClass" )
	protected class ProtectedInnerSubClass2 extends PackageInnerClass {
	}

	@SuppressWarnings( "ProtectedInnerClass" )
	protected class ProtectedInnerSubClass3 extends ProtectedInnerClass {
	}

	@SuppressWarnings( "ProtectedInnerClass" )
	protected class ProtectedInnerSubClass4 extends PrivateInnerClass {
	}

	private class PrivateInnerClass {

		@SuppressWarnings( "PublicField" )
		public int publicField = 31;
		@SuppressWarnings( "PackageVisibleField" )
		int packageField = 32;
		@SuppressWarnings( "ProtectedField" )
		protected int protectedField = 33;
		private int privateField = 34;

		public int getPublicField() {
			return publicField;
		}

		public void setPublicField( int publicField ) {
			this.publicField = publicField;
		}

		int getPackageField() {
			return packageField;
		}

		void setPackageField( int packageField ) {
			this.packageField = packageField;
		}

		protected int getProtectedField() {
			return protectedField;
		}

		protected void setProtectedField( int protectedField ) {
			this.protectedField = protectedField;
		}

		private int getPrivateField() {
			return privateField;
		}

		private void setPrivateField( int privateField ) {
			this.privateField = privateField;
		}

	}

	private class PrivateInnerSubClass1 extends PublicInnerClass {
	}

	private class PrivateInnerSubClass2 extends PackageInnerClass {
	}

	private class PrivateInnerSubClass3 extends ProtectedInnerClass {
	}

	private class PrivateInnerSubClass4 extends PrivateInnerClass {
	}

}
