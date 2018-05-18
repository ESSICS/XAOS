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
package se.europeanspallationsource.xaos.tools;

/**
 * Provides a methods to set headless JavaFX execution through the OpenJDK
 * Monocle library.
 *
 * @author claudio.rosati@esss.se
 * @see <a href="https://wiki.openjdk.java.net/display/OpenJFX/Monocle">OpenJDK Monocle</a>
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class HeadlessUtility {
	
	/**
	 * Set headless JavaFX execution only if the {@code xaos.headless} system
	 * property is set to {@code true}. It must be run before stage is created.
	 */
	public static void conditionallyHeadless() {
		if ( Boolean.getBoolean("xaos.headless") ) {
			headless();
		}		
	}

	/**
	 * * Set headless JavaFX execution. It must be run before stage is created.
	 */
	public static void headless() {
		System.setProperty("glass.platform", "Monocle");
		System.setProperty("monocle.platform", "Headless");
		System.setProperty("prism.order", "sw");
		System.setProperty("prism.text", "t2k");
		System.setProperty("java.awt.headless", "true");
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
	}

	private HeadlessUtility() {
	}

}
