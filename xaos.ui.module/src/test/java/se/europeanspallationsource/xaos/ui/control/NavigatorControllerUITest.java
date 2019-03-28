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
package se.europeanspallationsource.xaos.ui.control;


import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;


/**
 * @author claudio.rosati@esss.se
 */
public class NavigatorControllerUITest extends ApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- NavigatorControllerUITest ---------------------------------");
	}

	private AnchorPane root;
	private NavigatorController controller;

	@Override
	public void start( Stage stage ) throws IOException {

		root = new AnchorPane();
		controller = new NavigatorController();

//		controller.set


//		monitor = TreeDirectoryMonitor.build(EXTERNAL);
//		rootItem = monitor.model().getRoot();
//
//		rootItem.setExpanded(true);
//
//		view = new TreeView<>(rootItem);
//
//		view.setId("tree");
//		view.setShowRoot(false);
//		view.setCellFactory(TreeItems.defaultTreePathCellFactory());
//
//		stage.setOnCloseRequest(event -> monitor.dispose());
		stage.setScene(new Scene(root, 140, 180));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void test() {

		System.out.println("  Testing ''NavigatorController''...");

	}

}
