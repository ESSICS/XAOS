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
package se.europeanspallationsource.xaos.ui.util;


import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;


/**
 * Various utilities for the FX platform.
 *
 * @author claudiorosati
 */
public class FXUtils {

	/**
	 * Execute the given {@code task} on the JavaFX event thread via
	 * {@link Platform#runLater(Runnable)} and await for termination.
	 *
	 * @param task The {@link Runnable} to be executed.
	 * @throws InterruptedException If the current thread is interrupted while
	 *                              waiting.
	 */
	private static void runOnFXThreadAndWait( Runnable task ) throws InterruptedException {

		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			task.run();
			latch.countDown();
		});

		latch.await();

	}

	private FXUtils() {
		//	Nothing to do.
	}

}
