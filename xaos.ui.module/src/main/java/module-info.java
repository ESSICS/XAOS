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
module xaos.ui {

	requires java.logging;
	requires java.xml;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.media;
	requires javafx.swing;
	requires javafx.web;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;
	requires org.kordamp.iconli.core;
	requires org.kordamp.ikonli.fontawesome;
	requires org.kordamp.ikonli.javafx;
	requires transitive xaos.core;
	requires transitive xaos.tools;

	uses se.europeanspallationsource.xaos.ui.spi.ClassIconProvider;
	uses se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider;
	uses se.europeanspallationsource.xaos.ui.spi.IconProvider;
	uses se.europeanspallationsource.xaos.ui.spi.MIMETypeIconProvider;

	provides se.europeanspallationsource.xaos.ui.spi.ClassIconProvider
		with se.europeanspallationsource.xaos.ui.spi.impl.DefaultJavaFXClassIconProvider;
	provides se.europeanspallationsource.xaos.ui.spi.FileExtensionIconProvider
		with se.europeanspallationsource.xaos.ui.spi.impl.DefaultFileExtensionIconProvider;
	provides se.europeanspallationsource.xaos.ui.spi.IconProvider
		with se.europeanspallationsource.xaos.ui.spi.impl.DefaultCommonIconProvider;
	provides se.europeanspallationsource.xaos.ui.spi.MIMETypeIconProvider
		with se.europeanspallationsource.xaos.ui.spi.impl.DefaultMIMETypeIconProvider;

	exports se.europeanspallationsource.xaos.ui.control;
	exports se.europeanspallationsource.xaos.ui.control.svg;
	exports se.europeanspallationsource.xaos.ui.control.tree;
	exports se.europeanspallationsource.xaos.ui.control.tree.directory;
	exports se.europeanspallationsource.xaos.ui.spi;
	exports se.europeanspallationsource.xaos.ui.util;

	opens se.europeanspallationsource.xaos.ui.control to javafx.fxml;

}
