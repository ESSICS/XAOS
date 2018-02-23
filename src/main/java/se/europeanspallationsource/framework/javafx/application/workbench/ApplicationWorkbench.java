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
package se.europeanspallationsource.framework.javafx.application.workbench;


import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.menuBar.JACPMenuBar;
import org.jacpfx.rcp.workbench.FXWorkbench;
import se.europeanspallationsource.framework.javafx.application.utilities.Bundles;

import static se.europeanspallationsource.framework.javafx.application.workbench.Constants.PERSPECTIVE_MAIN;


/**
 * The application workbench, where perspectives are registered and installed.
 *
 * @author claudio.rosati@esss.se
 */
@Workbench(
     id = "application.workbench",
     perspectives = { PERSPECTIVE_MAIN }
)
public class ApplicationWorkbench implements FXWorkbench {

    @Override
    public void handleInitialLayout( Message<Event, Object> action, WorkbenchLayout<Node> layout, Stage stage ) {

//  TODO: CR: Initial size should be declared through the ApplicationLauncher
//            constructor's parameters, or using an @ApplicationInfo annotation.
//            This values should then be saved into application properties, and
//            updated when the application closes.
        layout.setWorkbenchXYSize(1024, 768);
        layout.registerToolBar(ToolbarPosition.NORTH);
        layout.setStyle(StageStyle.DECORATED);
        layout.setMenuEnabled(true);
        
    }

    @Override
    public void postHandle( FXComponentLayout layout ) {

        final JACPMenuBar menu = layout.getMenu();

//  TODO:CR menus should be loaded dynamically, using a service.
        Menu fileMenu = new Menu(Bundles.getLocalizedStrings().getString("menubar.file"));

        menu.getMenus().addAll(fileMenu);

    }

}
