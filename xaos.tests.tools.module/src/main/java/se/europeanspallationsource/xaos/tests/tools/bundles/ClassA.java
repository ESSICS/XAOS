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
package se.europeanspallationsource.xaos.tests.tools.bundles;


import se.europeanspallationsource.xaos.tools.annotation.BundleItem;
import se.europeanspallationsource.xaos.tools.annotation.BundleItems;


/**
 * @author claudio.rosati@esss.se
 */
public class ClassA {

	@BundleItem(
		key = "fieldA1.default",
		message = "Some initial A1 value."
	)
	private String fieldA1 = "";
	private boolean fieldA2 = false;

	@BundleItems({
		@BundleItem(key="methodAa.1", message="First message of method Aa."),
		@BundleItem(
			key = "methodAa.1",
			comment = "Message thrown when a connection attempt falied."
					+ "{0} Value of field A1.\n"
					+ "{1} Value of field A2.",
			message = "fieldA1: {0}, fieldA2: {1}"
		)
	})
	public void methodAa() {

	}

	public void methodAb( int p1, boolean p2, String p3 ) {

	}

	public class ClassAA {

		private String fieldAA1 = "";
		private boolean fieldAA2 = false;

		public void methodAAa() {

		}

		public void methodAAb( int p1, boolean p2, String p3 ) {

		}

	}

}
