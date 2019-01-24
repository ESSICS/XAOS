/*
 * Copyright 2019 claudiorosati.
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
package se.europeanspallationsource.xaos.demos.simple;


import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author claudiorosati
 */
public class NewFXPreloader extends Preloader {

	ProgressBar bar;
	Stage stage;

	private Scene createPreloaderScene() {
		bar = new ProgressBar();
		BorderPane p = new BorderPane();
		p.setCenter(bar);
		return new Scene(p, 300, 150);
	}

	@Override
	public void start( Stage stage ) throws Exception {
		this.stage = stage;
		stage.setScene(createPreloaderScene());
		stage.show();
	}

	@Override
	public void handleStateChangeNotification( StateChangeNotification scn ) {
		if ( scn.getType() == StateChangeNotification.Type.BEFORE_START ) {
			stage.hide();
		}
	}

	@Override
	public void handleProgressNotification( ProgressNotification pn ) {
		bar.setProgress(pn.getProgress());
	}

}
