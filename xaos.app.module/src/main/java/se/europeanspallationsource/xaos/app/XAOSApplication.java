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


import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * This is the basic class from which any JavaFX applications based on the XAOS
 * framework should inherit from.
 * <p>
 * <b>Note:</b> {@link #init()}, {@link #start(javafx.stage.Stage)}, and
 * {@link #stop()} should never be overridden!</p>
 *
 * @author claudio.rosati#esss,se
 */
public class XAOSApplication extends Application {

	private static final Logger LOGGER = Logger.getLogger(XAOSApplication.class.getName());

	public XAOSApplication() {
	}

	@Override
	public void init() throws Exception {
		super.init();
		initApplication();
	}

	@Override
	public void start( Stage stage ) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void stop() throws Exception {
		stopApplication();
		super.stop();
	}

	protected void initApplication() {
		//	No-op method.
	}

	private void stopApplication() {
		//	no-op method.
	}

}
