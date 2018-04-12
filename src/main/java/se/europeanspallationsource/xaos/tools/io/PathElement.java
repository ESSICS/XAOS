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
package se.europeanspallationsource.xaos.tools.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


/**
 * An element of a tree representation of a file system.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class PathElement {

	private static final Comparator<Path> PATH_COMPARATOR = ( p, q ) -> {

		boolean pd = Files.isDirectory(p);
		boolean qd = Files.isDirectory(q);

		if ( pd && !qd ) {
			return -1;
		} else if ( !pd && qd ) {
			return 1;
		} else {
			return p.getFileName().toString().compareToIgnoreCase(q.getFileName().toString());
		}

	};

	/**
	 * Factory method returning a tree structure of {@link PathElement} instances
	 * representing the content of the given {@code root} directory.
	 *
	 * @param root The pathname of the tree's root directory.
	 * @return The {@link PathElement} of the tree rooted at the given {@link Path}.
	 * @throws IOException If an I/O error occurs.
	 */
	public static PathElement tree( Path root ) throws IOException {

		if ( Files.isDirectory(root) ) {

			Path[] childPaths;

			try ( Stream<Path> dirStream = Files.list(root) ) {
				childPaths = dirStream.sorted(PATH_COMPARATOR).toArray(Path[]::new);
			}

			List<PathElement> children = new ArrayList<>(childPaths.length);

			for ( Path p : childPaths ) {
				children.add(tree(p));
			}

			return directory(root, children);

		} else {
			return file(root, Files.getLastModifiedTime(root));
		}

	}

	/**
	 * Factory method returning a {@link PathElement} instance representing a
	 * directory.
	 *
	 * @param path     The directory pathname.
	 * @param children The directory's children.
	 * @return A{@link PathElement} instance representing a directory.
	 */
	static PathElement directory( Path path, List<PathElement> children ) {
		return new PathElement(path, true, children, null);
	}

	/**
	 * Factory method returning a {@link PathElement) instance representing a
	 * file.
	 *
	 * @param path         The file pathname.
	 * @param lastModified The date of last modification of the file.
	 * @return A {@link PathElement) instance representing a file.
	 */
	static PathElement file( Path path, FileTime lastModified ) {
		return new PathElement(path, false, Collections.emptyList(), lastModified);
	}

	private final List<PathElement> children;
	private final boolean directory;
	private final FileTime lastModified;
	private final Path path;

	private PathElement( Path path, boolean directory, List<PathElement> children, FileTime lastModified ) {
		this.path = path;
		this.directory = directory;
		this.children = children;
		this.lastModified = lastModified;
	}

	public List<PathElement> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public FileTime getLastModified() {
		return lastModified;
	}

	public Path getPath() {
		return path;
	}

	public boolean isDirectory() {
		return directory;
	}

}
