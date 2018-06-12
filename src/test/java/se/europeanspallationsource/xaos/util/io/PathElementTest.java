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
package se.europeanspallationsource.xaos.util.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings({ "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" })
public class PathElementTest {

	private static Path dir_a;
	private static Path dir_a_c;
	private static Path dir_b;
	private static Path file_a;
	private static Path file_a_c;
	private static Path file_b1;
	private static Path file_b2;
	private static Path root;

	@BeforeClass
	public static void setUpClass() throws IOException {

		root = Files.createTempDirectory("PE_");
			dir_a = Files.createTempDirectory(root, "PE_a_");
				file_a = Files.createTempFile(dir_a, "PE_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "PE_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "PE_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "PE_b_");
				file_b1 = Files.createTempFile(dir_b, "PE_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "PE_b2_", ".test");

		System.out.println(MessageFormat.format(
			"  Testing 'PathElement'\n"
			+ "    created directories:\n"
			+ "      {0}\n"
			+ "      {1}\n"
			+ "      {2}\n"
			+ "      {3}\n"
			+ "    created files:\n"
			+ "      {4}\n"
			+ "      {5}\n"
			+ "      {6}\n"
			+ "      {7}",
			root,
			dir_a,
			dir_a_c,
			dir_b,
			file_a,
			file_a_c,
			file_b1,
			file_b2
		));

	}

	@AfterClass
	public static void tearDownClass() throws IOException {
		Files.walkFileTree(root, new DeleteFileVisitor());
	}

	/**
	 * Test of directory method, of class PathElement.
	 */
	@Test
	public void testDirectory() {

		System.out.println("  Testing 'directory'...");

		PathElement pe_root = PathElement.directory(root, Collections.emptyList());

		assertEquals(root, pe_root.getPath());
		assertEquals(Collections.emptyList(), pe_root.getChildren());
		assertNull(pe_root.getLastModified());
		assertTrue(pe_root.isDirectory());

		PathElement pe_a = PathElement.directory(dir_a, Collections.emptyList());

		assertEquals(dir_a, pe_a.getPath());
		assertEquals(Collections.emptyList(), pe_a.getChildren());
		assertNull(pe_a.getLastModified());
		assertTrue(pe_a.isDirectory());

		PathElement pe_a_c = PathElement.directory(dir_a_c, Collections.emptyList());

		assertEquals(dir_a_c, pe_a_c.getPath());
		assertEquals(Collections.emptyList(), pe_a_c.getChildren());
		assertNull(pe_a_c.getLastModified());
		assertTrue(pe_a_c.isDirectory());

	}

	/**
	 * Test of file method, of class PathElement.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testFile() throws IOException {

		System.out.println("  Testing 'file'...");

		PathElement pe_file_a = PathElement.file(file_a, Files.getLastModifiedTime(file_a));

		assertEquals(file_a, pe_file_a.getPath());
		assertEquals(Collections.emptyList(), pe_file_a.getChildren());
		assertEquals(Files.getLastModifiedTime(file_a), pe_file_a.getLastModified());
		assertFalse(pe_file_a.isDirectory());

		PathElement pe_file_a_c = PathElement.file(file_a_c, Files.getLastModifiedTime(file_a_c));

		assertEquals(file_a_c, pe_file_a_c.getPath());
		assertEquals(Collections.emptyList(), pe_file_a_c.getChildren());
		assertEquals(Files.getLastModifiedTime(file_a_c), pe_file_a_c.getLastModified());
		assertFalse(pe_file_a_c.isDirectory());

		PathElement pe_file_b1 = PathElement.file(file_b1, Files.getLastModifiedTime(file_b1));

		assertEquals(file_b1, pe_file_b1.getPath());
		assertEquals(Collections.emptyList(), pe_file_b1.getChildren());
		assertEquals(Files.getLastModifiedTime(file_b1), pe_file_b1.getLastModified());
		assertFalse(pe_file_b1.isDirectory());

		PathElement pe_file_b2 = PathElement.file(file_b2, Files.getLastModifiedTime(file_b2));

		assertEquals(file_b2, pe_file_b2.getPath());
		assertEquals(Collections.emptyList(), pe_file_b2.getChildren());
		assertEquals(Files.getLastModifiedTime(file_b2), pe_file_b2.getLastModified());
		assertFalse(pe_file_b2.isDirectory());

	}

	/**
	 * Test of tree method, of class PathElement.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testTree() throws IOException {

		System.out.println("  Testing 'tree'...");

		PathElement pe_root = PathElement.tree(root);
		List<PathElement> root_children = pe_root.getChildren();

		assertEquals(root, pe_root.getPath());
		assertEquals(2, root_children.size());
		assertNull(pe_root.getLastModified());
		assertTrue(pe_root.isDirectory());

			PathElement pe_a = root_children.get(0);
			List<PathElement> a_children = pe_a.getChildren();

			assertEquals(dir_a, pe_a.getPath());
			assertEquals(2, a_children.size());
			assertNull(pe_a.getLastModified());
			assertTrue(pe_a.isDirectory());

				PathElement pe_a_c = a_children.get(0);
				List<PathElement> a_c_children = pe_a_c.getChildren();

				assertEquals(dir_a_c, pe_a_c.getPath());
				assertEquals(1, a_c_children.size());
				assertNull(pe_a_c.getLastModified());
				assertTrue(pe_a_c.isDirectory());

					PathElement pe_file_a_c = a_c_children.get(0);

					assertEquals(file_a_c, pe_file_a_c.getPath());
					assertEquals(Collections.emptyList(), pe_file_a_c.getChildren());
					assertEquals(Files.getLastModifiedTime(file_a_c), pe_file_a_c.getLastModified());
					assertFalse(pe_file_a_c.isDirectory());

				PathElement pe_file_a = a_children.get(1);

				assertEquals(file_a, pe_file_a.getPath());
				assertEquals(Collections.emptyList(), pe_file_a.getChildren());
				assertEquals(Files.getLastModifiedTime(file_a), pe_file_a.getLastModified());
				assertFalse(pe_file_a.isDirectory());

			PathElement pe_b = root_children.get(1);
			List<PathElement> b_children = pe_b.getChildren();

			assertEquals(dir_b, pe_b.getPath());
			assertEquals(2, b_children.size());
			assertNull(pe_b.getLastModified());
			assertTrue(pe_b.isDirectory());

				PathElement pe_file_b1 = b_children.get(0);

				assertEquals(file_b1, pe_file_b1.getPath());
				assertEquals(Collections.emptyList(), pe_file_b1.getChildren());
				assertEquals(Files.getLastModifiedTime(file_b1), pe_file_b1.getLastModified());
				assertFalse(pe_file_b1.isDirectory());

				PathElement pe_file_b2 = b_children.get(1);

				assertEquals(file_b2, pe_file_b2.getPath());
				assertEquals(Collections.emptyList(), pe_file_b2.getChildren());
				assertEquals(Files.getLastModifiedTime(file_b2), pe_file_b2.getLastModified());
				assertFalse(pe_file_b2.isDirectory());

	}

}
