/*
 * Copyright 2018 European Spallation Source ERIC.
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
package se.europeanspallationsource.javafx.framework.application;


import javafx.stage.Stage;
import org.jacpfx.minimal.launcher.JacpFXApplicationLauncher;
import org.jacpfx.rcp.workbench.FXWorkbench;
import se.europeanspallationsource.javafx.framework.application.workbench.ApplicationWorkbench;


/**
 * The main entry point of the framework.
 *
 * @author claudio.rosati@esss.se
 */
public class ApplicationLauncher extends JacpFXApplicationLauncher {

    public ApplicationLauncher( ) {
    }

    @Override
    protected final String[] getBasePackages() {
        return new String[] { "se.europeanspallationsource.javafx.framework.application.workbench" };
    }

    @Override
    protected final Class<? extends FXWorkbench> getWorkbenchClass() {
        return ApplicationWorkbench.class;
    }

    /**
     * Will be executed after JavaFX initialisation. Current implementation is
     * empty, and can be safely overridden.
     *
     * @param stage The JavaFX {@link Stage}.
     */
    @Override
    protected void postInit( Stage stage ) {
    }

}
