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
package se.europeanspallationsource.xaos.tools.io;


import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;


/**
 * Implementation of an {@link InitiatorAsynchronousIO} that will update
 * a {@link DirectoryModel} on each operation.
 *
 * @param <I> Type of the <i>initiator</i> of the I/O operation.
 * @author claudio.rosati@esss.se
 * @see <a href="https://github.com/ESSICS/LiveDirsFX">LiveDirsFX:org.fxmisc.livedirs.LiveDirsIO</a>
 */
public class AsynchronousIOProvider<I> implements InitiatorAsynchronousIO<I> {

    private final Executor clientThreadExecutor;
	private final DirectoryWatcher directoryWatcher;

	public AsynchronousIOProvider(
		DirectoryWatcher directoryWatcher,
//		LiveDirsModel<I, ?> model,
		Executor clientThreadExecutor
	) {
		this.directoryWatcher = directoryWatcher;
//		this.model = model;
		this.clientThreadExecutor = clientThreadExecutor;
	}

	@Override
	public CompletionStage<Void> createDirectories( Path dir, I initiator, FileAttribute<?>... attrs ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> createDirectory( Path dir, I initiator, FileAttribute<?>... attrs ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> createFile( Path file, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> delete( Path path, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> deleteTree( Path root, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<byte[]> readBinaryFile( Path file, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<String> readTextFile( Path file, Charset charset, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> writeBinaryFile( Path file, byte[] content, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompletionStage<Void> writeTextFile( Path file, String content, Charset charset, I initiator ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
