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
package se.europeanspallationsource.xaos.components;


import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import javafx.scene.control.TreeItem;
import se.europeanspallationsource.xaos.util.io.DirectoryModel;
import se.europeanspallationsource.xaos.util.io.DirectoryWatcher;
import se.europeanspallationsource.xaos.util.io.InitiatorAsynchronousIO;

import static se.europeanspallationsource.xaos.util.DefaultExecutorCompletionStage.wrap;


/**
 * Implementation of an {@link InitiatorAsynchronousIO} that will update
 * a {@link DirectoryModel} on each operation.
 *
 * @param <I> Type of the <i>initiator</i> of the I/O operation.
 * @param <T> Type of the object returned by {@link TreeItem#getValue()}.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.LiveDirsIO</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class TreeDirectoryAsynchronousIO<I, T> implements InitiatorAsynchronousIO<I> {

	private final Executor clientThreadExecutor;
	private final DirectoryWatcher directoryWatcher;
	private final TreeDirectoryModel<I, T> model;

	public TreeDirectoryAsynchronousIO(
		DirectoryWatcher directoryWatcher,
		TreeDirectoryModel<I, T> model,
		Executor clientThreadExecutor
	) {
		this.directoryWatcher = directoryWatcher;
		this.model = model;
		this.clientThreadExecutor = clientThreadExecutor;
	}

	@Override
	public CompletionStage<Void> createDirectories( Path dir, I initiator, FileAttribute<?>... attrs ) {

		CompletableFuture<Void> created = new CompletableFuture<>();

		directoryWatcher.createDirectories(
			dir,
			path -> {
				if ( model.containsPrefixOf(path) ) {
					model.addDirectory(path, initiator);
					directoryWatcher.watchUpOrStreamError(path);
				}
				created.complete(null);
			},
			created::completeExceptionally,
			attrs
		);

		return wrap(created, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> createDirectory( Path dir, I initiator, FileAttribute<?>... attrs ) {

		CompletableFuture<Void> created = new CompletableFuture<>();

		directoryWatcher.createDirectory(
			dir,
			path -> {
				if ( model.containsPrefixOf(path) ) {
					model.addDirectory(path, initiator);
					directoryWatcher.watchOrStreamError(path);
				}
				created.complete(null);
			},
			created::completeExceptionally,
			attrs
		);

		return wrap(created, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> createFile( Path file, I initiator, FileAttribute<?>... attrs ) {

		CompletableFuture<Void> created = new CompletableFuture<>();

		directoryWatcher.createFile(
			file,
			lastModified -> {
				model.addFile(file, lastModified, initiator);
				created.complete(null);
			},
			created::completeExceptionally,
			attrs
		);

		return wrap(created, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> delete( Path path, I initiator ) {

		CompletableFuture<Void> deleted = new CompletableFuture<>();

		directoryWatcher.delete(
			path,
			done -> {

				if ( done ) {
					model.delete(path, initiator);
				}

				deleted.complete(null);

			},
			deleted::completeExceptionally
		);

		return wrap(deleted, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> deleteTree( Path root, I initiator ) {

		CompletableFuture<Void> deleted = new CompletableFuture<>();

		directoryWatcher.deleteTree(root,
			dummy -> {
				model.delete(root, initiator);
				deleted.complete(null);
			},
			deleted::completeExceptionally
		);

		return wrap(deleted, clientThreadExecutor);

	}

	@Override
	public CompletionStage<byte[]> readBinaryFile( Path file, I initiator ) {

		CompletableFuture<byte[]> read = new CompletableFuture<>();

		directoryWatcher.readBinaryFile(
			file,
			read::complete,
			read::completeExceptionally
		);

		return wrap(read, clientThreadExecutor);

	}

	@Override
	public CompletionStage<String> readTextFile( Path file, Charset charset, I initiator ) {

		CompletableFuture<String> read = new CompletableFuture<>();

		directoryWatcher.readTextFile(
			file,
			charset,
			read::complete,
			read::completeExceptionally
		);

		return wrap(read, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> writeBinaryFile( Path file, byte[] content, I initiator ) {

		CompletableFuture<Void> written = new CompletableFuture<>();

		directoryWatcher.writeBinaryFile(
			file,
			content,
			lastModified -> {
				model.updateModificationTime(file, lastModified, initiator);
				written.complete(null);
			},
			written::completeExceptionally
		);

		return wrap(written, clientThreadExecutor);

	}

	@Override
	public CompletionStage<Void> writeTextFile( Path file, String content, Charset charset, I initiator ) {

		CompletableFuture<Void> written = new CompletableFuture<>();

		directoryWatcher.writeTextFile(
			file,
			content,
			charset,
			lastModified -> {
				model.updateModificationTime(file, lastModified, initiator);
				written.complete(null);
			},
			written::completeExceptionally
		);

		return wrap(written, clientThreadExecutor);

	}

}
