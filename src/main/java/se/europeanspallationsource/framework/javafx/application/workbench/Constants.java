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

/**
 * Constants used for configuring the application layout.
 * <p>
 * The complete application is made of 6 view areas, where the <i>main</i> one is
 * always visible.
 *
 * <pre>
 * ┌───────────────────┬───────────────────────────────────────┬──────────────────┐
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         B         │                                       │        P         │
 * │      browser      │                                       │     palette      │
 * │                   │                                       │                  │
 * │                   │                   M                   │                  │
 * │                   │                 main                  │                  │
 * │                   │                                       │                  │
 * ├───────────────────┤                                       ├──────────────────┤
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         N         │                                       │        I         │
 * │     navigator     ├───────────────────────────────────────┤    inspector     │
 * │     overview      │                                       │    properties    │
 * │                   │                   C                   │                  │
 * │                   │                console                │                  │
 * │                   │               messages                │                  │
 * │                   │                                       │                  │
 * └───────────────────┴───────────────────────────────────────┴──────────────────┘
 * </pre>
 *
 * <h3>Outer View Areas</h3>
 *
 * The complete application is made of 6 view areas, where the `main` one is
 * always visible.
 * <p>
 * A view area is visible only if at least one view is registered. The only
 * exception is the <i>main</i> area, where views can be added dynamically, and
 * no initial view is required.
 *
 * <h4>Browser View Area</h4>
 * 
 * The <i>browser</i> view should be used to navigate high-level structures in
 * order to find elements to be opened in dedicated views inside the <i>main</i>
 * area.
 *
 * <h4>Navigator/Overview View Area</h4>
 *
 * This view area should be used to navigate the content of <i>selected</i>
 * entities, or display an overview of a more detailed zone displayed in
 * the currently visible <i>main</i> view.
 *
 * ...
 *
 *
 * @author claudio.rosati@esss.se
 */
public interface Constants {

    String PERSPECTIVE_MAIN = "perspective.main";

}
