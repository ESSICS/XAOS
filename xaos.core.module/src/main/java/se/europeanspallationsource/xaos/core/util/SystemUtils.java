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
import java.util.ArrayList;
import java.util.List;
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
	 * Creates a {@link ProcessBuilder} ready to execute the given class (that
	 * needs to have a main method) as a detached sub-process. Current
	 * class-path will be used to find the given class and its dependencies.
	 *
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The {@link ProcessBuilder} ready to run the given class.
	 * @see <a href="https://dzone.com/articles/running-a-java-class-as-a-subprocess">Running a Java Class as a Subprocess</a>
	 */
	public static ProcessBuilder build( Class<?> clazz, List<String> jvmArgs, List<String> args ) {

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = clazz.getName();
		List<String> command = new ArrayList<>(1 + jvmArgs.size() + 3 + args.size());

		command.add(javaBin);
		command.addAll(jvmArgs);
		command.add("--class-path");
		command.add(classpath);
		command.add(className);
		command.addAll(args);

		return new ProcessBuilder(command);

	}

	/**
	 * Creates a {@link ProcessBuilder} ready to execute the given class (that
	 * needs to have a main method) as a detached sub-process. Current
	 * module-path will be used to find the given module and class and its
	 * dependencies.
	 *
	 * @param module  The name of the module containing the class to be executed.
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The {@link ProcessBuilder} ready to run the given class.
	 */
	public static ProcessBuilder build( String module, Class<?> clazz, List<String> jvmArgs, List<String> args ) {

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String modulepath = System.getProperty("jdk.module.path");
		String className = clazz.getName();
		List<String> command = new ArrayList<>(1 + jvmArgs.size() + 4 + args.size());

		command.add(javaBin);
		command.addAll(jvmArgs);
		command.add("--module-path");
		command.add(modulepath);
		command.add("--module");
		command.add(module + "/" + className);
		command.addAll(args);

		return new ProcessBuilder(command);

	}

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
	 * Executes the given class (that needs to have a main method) as a detached
	 * sub-process, returning its exit value. Current class-path will be used to
	 * find the given class and its dependencies. Standard input and output will
	 * be the same of the calling application.
	 *
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The class execution exist status.
	 * @throws java.io.IOException            If an I/O error occurs.
	 * @throws java.lang.InterruptedException If the execution is interrupted.
	 * @see <a href="https://dzone.com/articles/running-a-java-class-as-a-subprocess">Running a Java Class as a Subprocess</a>
	 */
	public static int execute( Class<?> clazz, List<String> jvmArgs, List<String> args )
		throws IOException, InterruptedException
	{

		ProcessBuilder builder = build(clazz, jvmArgs, args);
		Process process = builder.inheritIO().start();

		process.waitFor();

		return process.exitValue();

	}

	/**
	 * Executes the given class (that needs to have a main method) as a detached
	 * sub-process, returning its exit value. Current module-path will be used
	 * to find the given module and class and its dependencies. Standard input
	 * and output will be the same of the calling application.
	 *
	 * @param module  The name of the module containing the class to be executed.
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The class execution exist status.
	 * @throws java.io.IOException            If an I/O error occurs.
	 * @throws java.lang.InterruptedException If the execution is interrupted.
	 */
	public static int execute( String module, Class<?> clazz, List<String> jvmArgs, List<String> args )
		throws IOException, InterruptedException
	{

		ProcessBuilder builder = build(module, clazz, jvmArgs, args);
		Process process = builder.inheritIO().start();

		process.waitFor();

		return process.exitValue();

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
