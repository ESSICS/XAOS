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
package se.europeanspallationsource.xaos.app;


import java.text.MessageFormat;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.ErrorNotification;
import javafx.application.Preloader.PreloaderNotification;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.core.util.ThreadPools;

import static java.util.logging.Level.SEVERE;
import static javafx.application.Preloader.StateChangeNotification.Type.BEFORE_START;
import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;


/**
 * This is the basic class from which any JavaFX applications based on the XAOS
 * framework should inherit from.
 * <p>
 * <b>Note:</b> {@link #init()}, {@link #start(javafx.stage.Stage)}, and
 * {@link #stop()} should never be overridden!</p>
 *
 * @author claudio.rosati@esss.se
 */
public class XAOSApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(XAOSApplication.class.getName());

	private volatile int currentStep = -1;
	private volatile int currentSubStep = -1;
	private BooleanProperty ready = new SimpleBooleanProperty(false);
	private Scene scene;
	private Stage stage;
	private volatile double steps = -1;
	private volatile double subSteps = -1;

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
		//	TODO:CR set the appropriate title.
		stage.setTitle(getClass().getSimpleName());

		ThreadPools.workStealingThreadPool().execute(() -> {

			try {
				//	Call the startApplication() method.
				startApplication((BorderPane) scene.getRoot());
			} catch ( Exception ex ) {
				LogUtils.log(LOGGER, SEVERE, ex, "Exception when starting the application.");
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
	 * @return The current step.
	 */
	int getCurrentStep() {
		return currentStep;
	}

	/**
	 * @return The current sub-step.
	 */
	int getCurrentSubStep() {
		return currentSubStep;
	}

	/**
	 * @return The steps.
	 */
	double getSteps() {
		return steps;
	}

	/**
	 * @return The sub-steps.
	 */
	double getSubSteps() {
		return subSteps;
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
	 * Inside this methods {@link Application#notifyPreloader(PreloaderNotification)}
	 * can be invoked to inform the {@link Preloader} of the current startup
	 * progress:</p>
	 * <pre>
	 *   notifyPreloader(new ProgressNotification(progress));</pre>
	 * <p>
	 * where {@code progress} is a number between 0 (started) to 1 (completed).</p>
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
	 * Tells the {@link Preloader} the application startup process is about to
	 * start the next step of progress. It must be call at the beginning of the
	 * step to be performed.
	 * <p>
	 * For example:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step();
	 *     ...
	 *   }
	 *   stepsComplete();</pre>
	 *
	 * @throws IllegalStateException If this method is called before the
	 *                               {@link #stepsBegin(int)} one, or after
	 *                               {@link #stepsComplete()}, or more than the
	 *                               number of steps stated in {@link #stepsBegin(int)}.
	 */
	protected void step() throws IllegalStateException {

		if ( steps <= 0 ) {
			throw new IllegalStateException("Steps progress not begun.");
		} else if ( currentStep == Integer.MAX_VALUE ) {
			throw new IllegalStateException("Step called after completion.");
		} else if ( currentStep >= steps - 1 ) {
			throw new IllegalStateException("Step called more then declared steps.");
		}

		subSteps = -1;
		currentSubStep = -1;
		currentStep++;

		notifyPreloader(new ProgressNotification(currentStep / steps));

	}

	/**
	 * Tells the {@link Preloader} the application startup process is about to
	 * start the next step of progress, which is itself split in the given
	 * number of sub-steps. It must be call at the beginning of the step to be
	 * performed.
	 * <p>
	 * For example:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step(5);
	 *     for ( int  j = 0; j &lt; 5; j++ ) {
	 *       subStep()
	 *       ...
	 *     }
	 *   }
	 *   stepsComplete();</pre>
	 *
	 * @param subSteps The number of sub-steps to be performed. It must be
	 *                 greater than or equal to 2. If no sub-steps must be
	 *                 performed then call {@link #step()} instead.
	 * @throws IllegalArgumentException If {@code subSteps} is not greater than
	 *                                  or equal to 2.
	 * @throws IllegalStateException    If this method is called before the
	 *                                  {@link #stepsBegin(int)} one, or after
	 *                                  {@link #stepsComplete()}.
	 */
	protected void step( int subSteps ) throws IllegalArgumentException, IllegalStateException {

		if ( subSteps <= 1 ) {
			throw new IllegalArgumentException(MessageFormat.format("Illegal ''subSteps'' value [{0}].", steps));
		} else {

			//	Must be called before the other statements because it sets
			//	subSteps to -1.
			step();

			this.subSteps = subSteps;
			this.currentSubStep = -1;

		}

	}

	/**
	 * Tells the {@link Preloader} the application startup process is about to
	 * begin. The total number of {@code steps} is given, and the first one is
	 * about to start.
	 * <p>
	 * For example:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step();
	 *     ...
	 *   }
	 *   stepsComplete();</pre>
	 *
	 * @param steps The number of steps to be performed. Cannot be 0. If no
	 *              steps must be performed then call {@link #stepsIndeterminate()}
	 *              instead, and then {@link #stepsComplete()} after the startup
	 *              is completed.
	 * @throws IllegalArgumentException If {@code steps} is not a positive number.
	 * @throws IllegalStateException    If this method is called more than once.
	 */
	protected void stepsBegin( int steps ) throws IllegalArgumentException, IllegalStateException {

		if ( this.steps > 0 ) {
			throw new IllegalStateException("Steps progress already begun.");
		} else if ( steps <= 0 ) {
			throw new IllegalArgumentException(MessageFormat.format("Illegal ''steps'' value [{0}].", steps));
		} else {

			this.steps = steps;

			notifyPreloader(new ProgressNotification(0.0));

		}

	}

	/**
	 * Tells the {@link Preloader} the application startup process is completed
	 * and the application's UI is about to be displayed.
	 * <p>
	 * For example:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step();
	 *     ...
	 *   }
	 *   stepsComplete();</pre>
	 */
	protected void stepsComplete() {

		steps = Double.MAX_VALUE;
		currentStep = Integer.MAX_VALUE;
		subSteps = Double.MAX_VALUE;
		currentSubStep = Integer.MAX_VALUE;

		notifyPreloader(new ProgressNotification(1.0));

	}

	/**
	 * Tells the {@link Preloader} the application startup progress is
	 * indeterminate.
	 * <p>
	 * It could be called instead of {@link #stepsBegin(int)} if the whole
	 * startup process cannot be decomposed into a determinate number of steps:</p>
	 * <pre>
	 *   stepsIndeterminate();
	 *   ...
	 *   stepsComplete();
	 * </pre>
	 * <p>
	 * Otherwise can be intermixed with {@link #step()} when some steps require
	 * an indeterminate amount of time to be completed:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step();
	 *     ...
	 *     if ( longTimeRequired ) {
	 *       stepsIndeterminate();
	 *     }
	 *     ...
	 *   }
	 *   stepsComplete();</pre>
	 */
	protected void stepsIndeterminate() {
		notifyPreloader(new ProgressNotification(INDETERMINATE_PROGRESS));
	}

	/**
	 * This method is called when the application should stop, and provides a
	 * convenient place to prepare for application exit and destroy resources.
	 * <p>
	 * The implementation of this method provided by the {@link XAOSApplication}
	 * class does nothing.</p>
	 * <p>
	 * <b>Note:</b> This method is called on the JavaFX {@link Application}
	 * Thread.</p>
	 *
	 * @throws java.lang.Exception If something goes wrong.
	 */
	protected void stopApplication() throws Exception {
		//	no-op method.
	}

	/**
	 * Tells the {@link Preloader} the application startup process is about to
	 * start the next sub-step of progress. It must be call at the beginning of
	 * the sub-step to be performed.
	 * <p>
	 * For example:</p>
	 * <pre>
	 *   stepsBegin(10);
	 *   for ( int  i = 0; i &lt; 10; i++ ) {
	 *     step(5);
	 *     for ( int  j = 0; j &lt; 5; j++ ) {
	 *       subStep()
	 *       ...
	 *     }
	 *   }
	 *   stepsComplete();</pre>
	 *
	 * @throws IllegalStateException If this method is called before the
	 *                               {@link #step(int)} one, or after
	 *                               {@link #stepsComplete()}, or more than the
	 *                               number of sub-steps stated in {@link #step(int)}.
	 */
	protected void subStep() throws IllegalStateException {

		if ( this.subSteps <= 0 ) {
			throw new IllegalStateException("Sub-steps progress not begun.");
		} else if ( currentSubStep == Integer.MAX_VALUE ) {
			throw new IllegalStateException("Sub-step called after completion.");
		} else if ( currentSubStep >= subSteps - 1 ) {
			throw new IllegalStateException("Sub-step called more then declared sub-steps.");
		}

		currentSubStep++;

		notifyPreloader(new ProgressNotification(( currentStep + currentSubStep / subSteps ) / steps));

	}

}
