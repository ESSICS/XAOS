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

/**
 * @author claudio.rosati@esss.se
 */
module xaos.tests.tools {

	requires xaos.tools;

	uses se.europeanspallationsource.xaos.tests.tools.services.BasicUsageInterface;
	uses se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface1;
	uses se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface2;
	uses se.europeanspallationsource.xaos.tests.tools.services.OrderedInterface;

	provides se.europeanspallationsource.xaos.tests.tools.services.BasicUsageInterface
		with se.europeanspallationsource.xaos.tests.tools.services.BasicUsageImplementation;
	provides se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface1
		with se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsImpl;
	provides se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsInterface2
		with se.europeanspallationsource.xaos.tests.tools.services.MultipleRegistrationsImpl;
	provides se.europeanspallationsource.xaos.tests.tools.services.OrderedInterface
		with se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl1,
			 se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl2,
			 se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl3,
			 se.europeanspallationsource.xaos.tests.tools.services.OrderedImpl4;

	opens se.europeanspallationsource.xaos.tests.tools.bundles to xaos.tools;
	opens se.europeanspallationsource.xaos.tests.tools.bundles.p1 to xaos.tools;
	opens se.europeanspallationsource.xaos.tests.tools.services to xaos.tools;

}
