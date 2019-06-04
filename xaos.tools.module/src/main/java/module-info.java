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
module xaos.tools {

	//	The following are "automatic modules", whose name is derived from the
	//	JAR name.
	requires cache2k.api;

	requires java.compiler;
	requires java.logging;
	requires org.apache.commons.lang3;

	exports se.europeanspallationsource.xaos.tools.annotation;
	exports se.europeanspallationsource.xaos.tools.lang;

	uses javax.annotation.processing.Processor;

	provides javax.annotation.processing.Processor
		with se.europeanspallationsource.xaos.tools.annotation.impl.BundleProcessor,
			 se.europeanspallationsource.xaos.tools.annotation.impl.ServiceProviderProcessor;

}
