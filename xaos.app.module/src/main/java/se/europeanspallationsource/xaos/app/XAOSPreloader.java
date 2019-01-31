/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018 by European Spallation Source ERIC.
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
package se.europeanspallationsource.xaos.app;


import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.application.Preloader.StateChangeNotification.Type.BEFORE_START;
import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;


/**
 * {@link Preloader} showing a splash screen and progress controls.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class XAOSPreloader extends Preloader {

	private final boolean fadeOut;
	private boolean noLoadingProgress = true;
	private ProgressBar progressBar;
	private Stage stage;

	/**
	 * Creates a new instance of {@link XAOSPreloader} where the splash-screen
	 * fading-out is set to {@code true}.
	 */
	public XAOSPreloader() {
		this(true);
	}

	/**
	 * Creates a new instance of {@link XAOSPreloader} where the splash-screen
	 * fading-out is specified in the given parameter.
	 *
	 * @param fadeOut {@code true} if the splash-screen must disappear gradually.
	 */
	public XAOSPreloader( boolean fadeOut ) {
		this.fadeOut = fadeOut;
	}

	@Override
	public void handleApplicationNotification( PreloaderNotification pn ) {

		if ( pn instanceof ProgressNotification ) {

			//	Expect application to send us progress notifications
			//	with progress ranging from 0 to 1.0.
			double progress = ( (ProgressNotification) pn ).getProgress();

			if ( !noLoadingProgress ) {
				//	If we were receiving loading progress notifications
				//	then progress is already at 50%.
				//	Rescale application progress to start from 50%.
				progress = 0.5 + progress / 2;
			}

			progressBar.setProgress(progress);

		} else if ( pn instanceof StateChangeNotification && ((StateChangeNotification) pn).getType() == BEFORE_START ) {
			if ( fadeOut ) {

				//	Fade out, hide stage at the end of animation.
				final Stage finalStage = stage;
				FadeTransition ft = new FadeTransition(
					Duration.millis(1000),
					stage.getScene().getRoot()
				);

				ft.setOnFinished(e -> finalStage.hide());
				ft.setFromValue(1.0);
				ft.setToValue(0.0);
				ft.play();

			} else {
				//	Hide after get any state update from application.
				stage.hide();
			}
		}

	}

	@Override
	public boolean handleErrorNotification( ErrorNotification en ) {
		//	TODO:CR Show a message and then call Platform.exit().
		return super.handleErrorNotification(en); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void handleProgressNotification( ProgressNotification pn ) {

		progressBar.setProgress(pn.getProgress());

		//	Application loading progress is rescaled to be first 50%.
		//	Even if there is nothing to load 0% and 100% events can be
		//	delivered.
		if ( pn.getProgress() != 1.0 || !noLoadingProgress ) {

			progressBar.setProgress(pn.getProgress() / 2);

			if ( pn.getProgress() > 0 ) {
				noLoadingProgress = false;
			}

		}

	}

	@Override
	public void handleStateChangeNotification( StateChangeNotification scn ) {
		//	Ignore, hide after application signals it is ready.
		//if ( scn.getType() == BEFORE_START ) {
		//	stage.hide();
		//}
	}

	@Override
	public void start( Stage stage ) throws Exception {

		this.stage = stage;

		stage.setScene(createPreloaderScene());
		stage.show();

	}

	private Scene createPreloaderScene() {

		progressBar = new ProgressBar();

		progressBar.setProgress(INDETERMINATE_PROGRESS);

		BorderPane borderPane = new BorderPane();

		borderPane.setCenter(progressBar);

		return new Scene(borderPane, 300, 150);

	}

}
