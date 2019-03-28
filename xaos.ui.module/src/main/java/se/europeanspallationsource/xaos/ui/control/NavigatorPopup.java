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


import javafx.scene.control.PopupControl;


/**
 * A popup window that shows 9 buttons to perform the following operation: 
 * zoom in/out/100%, pan up/right/down/left, forward/backward.
 * <p>
 * The implementation of the actions associated with the popup is left to 
 * the client code, that must add the corresponding callbacks.</p>
 * <p>Typical usage is the following:</P>
 * <pre>
 * NavigatorPopup popup = new NavigatorPopup();
 *
 * popup.setOn...
 * ...
 * popup.setOn...
 * popup.show(ownerNode, cursorScreenX, cursorScreenY);</pre>
 *
 * @author claudiorosati
 */
public class NavigatorPopup extends PopupControl {

}
