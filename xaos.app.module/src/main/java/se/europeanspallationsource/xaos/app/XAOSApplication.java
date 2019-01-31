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


import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ErrorNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import se.europeanspallationsource.xaos.core.util.ThreadPools;

import static java.util.logging.Level.SEVERE;
import static javafx.application.Preloader.StateChangeNotification.Type.BEFORE_START;
import static se.europeanspallationsource.xaos.app.impl.Constants.LOGGER;


/**
 * This is the basic class from which any JavaFX applications based on the XAOS
 * framework should inherit from.
 * <p>
 * <b>Note:</b> {@link #init()}, {@link #start(javafx.stage.Stage)}, and
 * {@link #stop()} should never be overridden!</p>
 *
 * @author claudio.rosati#esss,se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class XAOSApplication extends Application {

	private BooleanProperty ready = new SimpleBooleanProperty(false);
	private Scene scene;
	private Stage stage;

	public XAOSApplication() {
	}

	/**
	 * @return This application's {@link Scene} or {@code null} if the
	 *         application is not yet started.
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * @return This application's {@link Stage} or {@code null} if the
	 *         application is not yet started.
	 */
	public Stage getStage() {
		return stage;
	}

	@Override
	public final void init() throws Exception {
		initApplication();
	}

	@Override
	public final void start( Stage stage ) throws Exception {

		this.stage = stage;
		this.scene = new Scene(new BorderPane(), 1024, 768);

		stage.setScene(scene);

		ThreadPools.workStealingThreadPool().execute(() -> {

			try {
				//	Call the startApplication() method.
				startApplication((BorderPane) scene.getRoot());
			} catch ( Exception ex ) {
				LOGGER.log(SEVERE, "Exception when starting the application.", ex);
				notifyPreloader(new ErrorNotification(
					"XAOSApplication#startApplication",
					"Exception when starting the application.",
					ex
				));
				return;
			}

			//	After init is ready, the app is ready to be shown.
			//	Do this before hiding the preloader stage to prevent the
			//	application from exiting prematurely.
			ready.setValue(Boolean.TRUE);

			//	Tell the application is displaying.
			notifyPreloader(new StateChangeNotification(BEFORE_START));

		});

		//	After the app is ready, show the stage.
		ready.addListener(( ObservableValue<? extends Boolean> ov, Boolean oldv, Boolean newv ) -> {
			if ( Boolean.TRUE.equals(newv) ) {
				Platform.runLater(stage::show);
				ready = null;
			}
		});

	}

	@Override
	public final void stop() throws Exception {
		stopApplication();
	}

	/**
	 * The application initialization method.This method is called immediately
	 * after the {@link XAOSApplication} class is loaded and constructed. An
	 * application may override this method to perform initialization prior to
	 * the actual starting of the application.
	 * <p>
	 * The implementation of this method provided by the {@link XAOSApplication}
	 * class does nothing.</p>
	 * <p>
	 * <b>Note:</b> This method is not called on the JavaFX {@link Application}
	 * Thread. An application must not construct a {@link Scene} or a
	 * {@link Stage} in this method. An application may construct other JavaFX
	 * objects in this method.</p>
	 *
	 * @throws java.lang.Exception If something goes wrong.
	 */
	protected void initApplication() throws Exception {
		//	No-op method.
	}

	/**
	 * The main entry point for a XAOS application. The {@code startApplication}
	 * method is called after the {@link #initApplication()} method has returned,
	 * and after the system is ready for the application to begin running.
	 * <p>
	 * The implementation of this method provided by the {@link XAOSApplication}
	 * class does nothing.</p>
	 * <p>
	 * <b>Note:</b> Contrarily to the {@link Application#start(Stage)} method,
	 * this one is not called on the JavaFX Application Thread.</p>
	 *
	 * @param sceneRoot The {@link BorderPane} root container pre-installed in
	 *                  the application's {@link Scene}.
	 * @throws java.lang.Exception If something goes wrong.
	 */
	protected void startApplication( final BorderPane sceneRoot ) throws Exception {
		//	no-op method.
	}

	/**
	 * This method is called when the application should stop, and provides a
	 * convenient place to prepare for application exit and destroy resources.
	 * <p>
	 * The implementation of this method provided by the {@link XAOSApplication}
	 * class does nothing.</p>
	 * <p>
	 * <b>Note:</b> This method is called on the JavaFX {@link Application}
	 * Thread.
	 *
	 * @throws java.lang.Exception If something goes wrong.
	 */
	protected void stopApplication() throws Exception {
		//	no-op method.
	}

}
