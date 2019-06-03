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
package se.europeanspallationsource.xaos.tests.tools.bundles.p1;


import se.europeanspallationsource.xaos.tools.annotation.Bundle;


/**
 * @author claudio.rosati@esss.se
 */
@Bundle( "Messages" )
public class ClassB {

	private static String staticFieldB1 = "";
	private boolean fieldB2 = false;

	public void methodBa() {

	}

	public void methodBb( int p1, boolean p2, String p3 ) {

	}

	public static class ClassBB {

		private String fieldBB1 = "";
		private boolean fieldBB2 = false;

		public void methodBBa() {

		}

		public void methodBBb( int p1, boolean p2, String p3 ) {

		}

	}

	public interface InterfaceBA {

		public void methodBAa();

		public void methodBAb( int p1, boolean p2, String p3 );

	}

}
