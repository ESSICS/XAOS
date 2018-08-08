/*
 * Copyright 2018 European Spallation Source ERIC.
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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


/**
 * A {@link FileVisitor} that will delete a file tree. Each directory will be
 * deleted after the entries in the directory are deleted.
 * <p>
 * Usage:
 * </p>
 * <pre>
 *   Path root = ...
 *
 *   Files.walkFileTree(root, new DeleteFileVisitor());</pre>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class DeleteFileVisitor extends SimpleFileVisitor<Path> {

	@Override
	public FileVisitResult postVisitDirectory( Path dir, IOException e ) throws IOException {

		if ( e == null ) {

			Files.delete(dir);

			return FileVisitResult.CONTINUE;

		} else {
			// directory iteration failed
			throw e;
		}

	}

	@Override
	public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {

		Files.delete(file);

		return FileVisitResult.CONTINUE;

	}

}
