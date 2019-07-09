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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;


/**
 * Some system utilities.
 *
 * @author claudio.rosati@esss.se
 */
public class SystemUtils {

	private static final Logger LOGGER = Logger.getLogger(SystemUtils.class.getName());

	/**
	 * Execute the given {@code command} and return its output as a string.
	 *
	 * @param command The command (and parameters) to be executed.
	 * @return The output from the executed process.
	 */
	public static String execute( String... command ) {
		return execute(null, command);
	}

	/**
	 * Execute the given {@code command} and return its output as a string.
	 *
	 * @param workingDirectory The directory where the execution must be
	 *                         performed. Can be {@code null} to inherit the
	 *                         working directory from the current process.
	 * @param command          The command (and parameters) to be executed.
	 * @return The output from the executed process, or {@code null} if an
	 *         exception was thrown (and logged).
	 */
	@SuppressWarnings( "NestedAssignment" )
	public static String execute( File workingDirectory, String... command ) {

		try {

			String line;
			StringBuilder builder = new StringBuilder(64);
			Process proc = Runtime.getRuntime().exec(command, null, workingDirectory);

			try ( BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream())) ) {
				while ( ( line = reader.readLine() ) != null ) {
					builder.append(line);
					builder.append("\n");
				}
			}

			if ( builder.length() > 0 ) {
				builder.deleteCharAt(builder.length() - 1);
			}

			return builder.toString();

		} catch ( IOException ex ) {

			LogUtils.log(LOGGER, WARNING, ex);

			return null;

		}

	}

	/**
	 * Returns the percentage of available space to this virtual machine on the
	 * partition named by the given abstract pathname.
	 *
	 * @param path A file identifying the partition to be queried.
	 * @return The percentage of available space to this virtual machine, as a
	 *         {@code double} in the {@code [0.0, 1.0]} range.
	 */
	public static double freeDiskSpace( File path ) {

		if ( path == null ) {
			return 0;
		}

		return path.getUsableSpace() / (double) path.getTotalSpace();

	}

	private SystemUtils() {
		//	Nothing to do.
	}

}
