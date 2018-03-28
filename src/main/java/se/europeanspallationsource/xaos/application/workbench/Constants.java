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
package se.europeanspallationsource.xaos.application.workbench;

/**
 * Constants used for configuring the application layout.
 *
 * <pre>
 * ┌───────────────────┬───────────────────────────────────────┬──────────────────┐
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         B         │                                       │                  │
 * │      browser      │                                       │                  │
 * │                   │                                       │        P         │
 * │                   │                                       │     palette      │
 * │                   │                   M                   │                  │
 * ├───────────────────┤                 main                  │                  │
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │                   │                                       ├──────────────────┤
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         N         │                                       │                  │
 * │     navigator     │                                       │        I         │
 * │     overview      ├───────────────────────────────────────┤    inspector     │
 * │                   │                                       │    properties    │
 * │                   │                   C                   │                  │
 * │                   │                console                │                  │
 * │                   │               messages                │                  │
 * │                   │                                       │                  │
 * └───────────────────┴───────────────────────────────────────┴──────────────────┘</pre>
 *
 * <p>
 * As explained in {@link ApplicationWorkbench}, perspective name is made of
 * 8 character consisting of the first character of the registered View areas
 * name, or "-" when not registered.
 * </p>
 *
 * <pre>
 *    ┌────────────────────────────┐
 *    │      ┌─────────────────────│───────────┐
 *    │      │      ┌──────────────│───────────│───────────┐
 * ┌─────┐   │      │              │           │           │
 * │     ├───────┐  │              │           │           │
 * │  B  │       ├─────┐           │           │           │
 * │     │       │     │           │           │           │
 * ├─────┤   M   │  P  │           ▼           ▼           ▼
 * │     │       │     │        ┌─────┬─────┬─────┬─────┬─────┬─────┐
 * │     │       ├─────┤        │ B/- │ N/- │ M/- │ C/- │ P/- │ I/- │ Perspective Name
 * │     │       │     │        └─────┴─────┴─────┴─────┴─────┴─────┘
 * │  N  ├───────┤  I  │                 ▲           ▲           ▲
 * │     │   C   │     │                 │           │           │
 * │     │       ├─────┘                 │           │           │
 * │     ├───────┘  │                    │           │           │
 * └─────┘   │      │                    │           │           │
 *    │      │      └────────────────────│───────────│───────────┘
 *    │      └───────────────────────────│───────────┘
 *    └──────────────────────────────────┘</pre>
 *
 * @author claudio.rosati@esss.se
 */
public interface Constants {

	String ID_WORKBENCH = "application.workbench";

	String VIEW_BROWSER     = "B";
	String VIEW_CONSOLE     = "C";
	String VIEW_INSPECTOR   = "I";
	String VIEW_MAIN        = "M";
	String VIEW_MESSAGES    = "C";
	String VIEW_NAVIGATOR   = "N";
	String VIEW_NOT_PRESENT = "-";
	String VIEW_OVERVIEW    = "N";
	String VIEW_PALETTE     = "P";
	String VIEW_PROPERTIES  = "I";

}
