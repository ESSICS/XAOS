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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Various utilities for processes.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://dzone.com/articles/running-a-java-class-as-a-subprocess">Running a Java Class as a Subprocess</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ProcessUtils {

	/**
	 * Creates a {@link ProcessBuilder} ready to execute the given class (that
	 * needs to have a main method) as a detached sub-process.
	 *
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The {@link ProcessBuilder} ready to run the given class.
	 */
	public static ProcessBuilder build( Class<?> clazz, List<String> jvmArgs, List<String> args ) {

		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = clazz.getName();
		List<String> command = new ArrayList<>(1 + jvmArgs.size() + 3 + args.size());

		command.add(javaBin);
		command.addAll(jvmArgs);
		command.add("-cp");
		command.add(classpath);
		command.add(className);
		command.addAll(args);

		return new ProcessBuilder(command);

	}

	/**
	 * Executes the given class (that needs to have a main method) as a detached
	 * sub-process, returning its exit value.
	 *
	 * @param clazz   The class to be executed.
	 * @param jvmArgs The JVM arguments.
	 * @param args    The class arguments.
	 * @return The class execution exist status.
	 * @throws java.io.IOException            If an I/O error occurs.
	 * @throws java.lang.InterruptedException If the execution is interrupted.
	 */
	public static int exec( Class<?> clazz, List<String> jvmArgs, List<String> args )
		throws IOException, InterruptedException
	{

		ProcessBuilder builder = build(clazz, jvmArgs, args);
		Process process = builder.inheritIO().start();

		process.waitFor();

		return process.exitValue();

	}

	private ProcessUtils() {
		//	Nothing to do.
	}

}
