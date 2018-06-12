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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings({ "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" })
public class DeleteFileVisitorTest {

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private Path root;

	@Before
	public void setUp() throws IOException {

		root = Files.createTempDirectory("DFV_");
			dir_a = Files.createTempDirectory(root, "DFV_a_");
				file_a = Files.createTempFile(dir_a, "DFV_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "DFV_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "DFV_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "DFV_b_");
				file_b1 = Files.createTempFile(dir_b, "DFV_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "DFV_b2_", ".test");

		System.out.println(MessageFormat.format(
			"  Testing 'DeleteFileVisitor'\n"
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

	@Test
	public void testPostVisitDirectory() throws Exception {

		System.out.println("  Testing 'DeleteFileVisitor'...");

		assertTrue(Files.exists(file_b2));
		assertTrue(Files.exists(file_b1));
		assertTrue(Files.exists(dir_b));
		assertTrue(Files.exists(file_a_c));
		assertTrue(Files.exists(dir_a_c));
		assertTrue(Files.exists(file_a));
		assertTrue(Files.exists(dir_a));
		assertTrue(Files.exists(root));

		Files.walkFileTree(root, new DeleteFileVisitor());

		assertFalse(Files.exists(file_b2));
		assertFalse(Files.exists(file_b1));
		assertFalse(Files.exists(dir_b));
		assertFalse(Files.exists(file_a_c));
		assertFalse(Files.exists(dir_a_c));
		assertFalse(Files.exists(file_a));
		assertFalse(Files.exists(dir_a));
		assertFalse(Files.exists(root));

	}

}
