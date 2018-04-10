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


import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;


/**
 * API for asynchronous file-system operations.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX</a>
 */
public interface AsynchronousIOFacility {

	/**
	 * Creates a directory. If the directory already exists, or its parent
	 * directory does not exist, or another I/O error occurs, the returned
	 * completion stage is completed exceptionally with the encountered error.
	 *
	 * @param dir The pathname of the directory to be created.
	 * @return An exceptionally completed {@link CompletionStage} in case the
	 *         directory already exists, or its parent directory does not exist,
	 *         or an exception is thrown.
	 */
	CompletionStage<Void> createDirectory( Path dir );

	/**
	 * Creates an empty file. If file already exists or an I/O error occurs,
	 * the returned completion stage is completed exceptionally with the
	 * encountered error.
	 *
	 * @param file The pathname of the file to be created.
	 * @return An exceptionally completed {@link CompletionStage} in case the
	 *         file already exists, or an exception is thrown.
	 */
	CompletionStage<Void> createFile( Path file );

	/**
	 * Deletes a file or an empty directory. If an I/O error occurs, the returned
	 * completion stage is completed exceptionally with the encountered error.
	 *
	 * @param path Pathname of the file or directory to be deleted.
	 * @return An exceptionally completed {@link CompletionStage} in case an
	 *         exception is thrown.
	 */
	CompletionStage<Void> delete( Path path );

	/**
	 * Deletes a file tree toothed at the given path. If an I/O error occurs,
	 * the returned completion stage is completed exceptionally with the
	 * encountered error.
	 *
	 * @param root The path to the file tree root to be deleted.
	 * @return An exceptionally completed {@link CompletionStage} in case an
	 *         exception is thrown.
	 */
	CompletionStage<Void> deleteTree( Path root );

	/**
	 * Reads the contents of a binary file. The returned completion stage will
	 * contain the read content as a byte array or, if an I/O error occurs, it
	 * will be completed exceptionally with the encountered error.
	 *
	 * @param file The pathname of the file to be read.
	 * @return A {@link CompletionStage} containing the read content as a byte
	 *         array or, if an I/O error occurs, the encountered error.
	 */
	CompletionStage<byte[]> readBinaryFile( Path file );

	/**
	 * Reads the contents of a text file. The returned completion stage will
	 * contain the read content as a string or, if an I/O error occurs, it will
	 * be completed exceptionally with the encountered error.
	 *
	 * @param file    The pathname of the file to be read.
	 * @param charset The {@link Charset} used.
	 * @return A {@link CompletionStage} containing the read content as a string
	 *         or, if an I/O error occurs, the encountered error.
	 */
	CompletionStage<String> readTextFile( Path file, Charset charset );

	/**
	 * Reads the contents of an UTF8-encoded file. The returned completion stage
	 * will contain the read content as a string or, if an I/O error occurs, it
	 * will be completed exceptionally with the encountered error.
	 *
	 * @param file The pathname of the file to be read.
	 * @return A {@link CompletionStage} containing the read content as a string
	 *         or, if an I/O error occurs, the encountered error.
	 */
	default CompletionStage<String> readUTF8File( Path file ) {
		return readTextFile(file, Charset.forName("UTF-8"));
	}

	/**
	 * Writes binary file to disk. If an I/O error occurs, the returned
	 * completion stage is completed exceptionally with the encountered error.
	 *
	 * @param file    The pathname of the file to be created.
	 * @param content The bytes to be written into the file.
	 * @return An exceptionally completed {@link CompletionStage} in case an
	 *         exception is thrown.
	 */
	CompletionStage<Void> writeBinaryFile( Path file, byte[] content );

	/**
	 * Writes the given string in a text file to disk. If an I/O error occurs,
	 * the returned completion stage is completed exceptionally with the
	 * encountered error.
	 *
	 * @param file    The pathname of the file to be created.
	 * @param content The String to be written into the file.
	 * @param charset The {@link Charset} used.
	 * @return An exceptionally completed {@link CompletionStage} in case an
	 *         exception is thrown.
	 */
	CompletionStage<Void> writeTextFile( Path file, String content, Charset charset );

	/**
	 * Writes UTF8-encoded text to disk. If an I/O error occurs, the returned
	 * completion stage is completed exceptionally with the encountered error.
	 *
	 * @param file    The pathname of the file to be created.
	 * @param content The String to be written into the file.
	 * @return An exceptionally completed {@link CompletionStage} in case an
	 *         exception is thrown.
	 */
	default CompletionStage<Void> writeUTF8File( Path file, String content ) {
		return writeTextFile(file, content, Charset.forName("UTF-8"));
	}

}
