# APPLICATION-FRAMEWORK

[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

A JavaFX application framework based on [JacpFX](http://jacpfx.org) (Java Asynchronous Client Platform)
made initially to create accelerator physics applications based on [Open XAL](https://github.com/openxal/openxal),
an open source development environment used for creating accelerator physics applications, scripts and services.


## Application Layout

The framework will allow applications having a generic layout with the following components:

- A toolbar in the top area, with 3 groups of buttons:

  - On the left, the buttons controlling the display of the left and bottom views;
  - On the right, the button controlling the display of the right view;
  - On the center, application specific buttons.

  If the application will not use outer views, the corresponding buttons will not be displayed.
  Moreover, if no application specific buttons are added, the whole toolbar will not be displayed.

- A status bar in the bottom area, where status message and other informations are displayed.
  The status bar can be omitted it the application doesn't need it.

- Peripheral views (`browser`, `navigator/overview`, `console/messages`, `palette`, and `inspector/properties`),
  where the application can register content to be displayed. If some of the views are not used then
  will not be displayed. When displayed, the splitters will allow to resize them.

- The `main` view area used by the application to display its main content.

![layout-01](https://github.com/ESSICS/APPLICATION-FRAMEWORK/blob/master/doc/layout-01.png)


### Outer View Areas

The complete application is made of 6 view areas, where the `main` one is always visible.

A view areas is visible only if at least one view is registered. The only exception is
the `main` area, where views can be added dynamically, and no initial view is required.


#### Browser View Area

The `browser` view should be used to navigate high-level structures in order to
find elements to be opened in dedicated views inside the `main` area.

#### Navigator/Overview View Area

This view area should be used to navigate the content of _selected_
entities, or display an overview of a more detailed zone displayed in
the currently visible `main` view.

...

### Main View Area
