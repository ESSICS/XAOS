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
package se.europeanspallationsource.xaos.core.util;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Various utilities for threads.
 *
 * @author claudiorosati
 */
public class ThreadUtils {

	private static final Logger LOGGER = Logger.getLogger(ThreadUtils.class.getName());

	/**
	 * Invokes {@link Thread#sleep(long)} taking care of possible exceptions.
	 *
	 * @param millis The sleep time in milliseconds.
	 * @return {@code true} if an {@link InterruptedException} occurred.
	 */
	public static boolean sleep( long millis ) {
		try {
			Thread.sleep(millis);
			return false;
		} catch ( InterruptedException ex ) {
			LOGGER.log(Level.WARNING, "Sleep interrupted.", ex);
			return true;
		}
	}

	/**
	 * Invokes {@link Thread#sleep(long)} taking care of possible exceptions.
	 *
	 * @param millis The sleep time in milliseconds.
	 * @param nanos  0-999999 additional nanoseconds to sleep.
	 * @return {@code true} if an {@link InterruptedException} occurred.
	 */
	public static boolean sleep( long millis, int nanos ) {
		try {
			Thread.sleep(millis, nanos);
			return false;
		} catch ( InterruptedException ex ) {
			LOGGER.log(Level.WARNING, "Sleep interrupted.", ex);
			return true;
		}
	}

	private ThreadUtils() {
		//	Nothing to do.
	}

}
