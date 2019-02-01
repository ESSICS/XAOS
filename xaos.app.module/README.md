# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS –App

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.app.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.app/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.app.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.app)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.app.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.app)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS App_ module (`xaos.app`) provides a JavaFX-based application
framework for building UI-based applications with menus, toolbars, etc.


## Application Types


TODO:CR
**** EXPLAIN THE DIFFERENT TYPES OF APPLICATIONS ****



## Application Layout

The framework allows applications having a generic layout with the following components:

- An optional toolbar in the top area, with 3 groups of buttons:

  - On the left, the buttons controlling the display of the left and bottom views;
  - On the right, the button controlling the display of the right view;
  - On the center, application specific buttons.

  If the application will not use outer views, the corresponding buttons will not be displayed.
  Moreover, if no application specific buttons are added, the whole toolbar will not be displayed.

- A status bar in the bottom area, where status messages and other information are displayed.
  The status bar will be omitted it the application doesn't need it.

- Peripheral views (`browser`, `navigator/overview`, `console/messages`, `palette`, and `inspector/properties`),
  where the application can register content to be displayed. If some of the views are not used they
  will not be displayed. When displayed, the splitters will allow to resize them.

- The `main` view area used by the application to display its main content.

![layout-01](https://github.com/ESSICS/XAOS/blob/master/doc/layout-01.png)


### Outer View Areas

The complete application is made of 6 view areas, where the `main` one is always visible.

A view area is visible only if at least one view is registered. The only exception is
the `main` area, where views can be added dynamically, and no initial view is required.

Outer view areas' content can be static (i.e. always visible), or depending on what is the
active view inside the `main` area.


#### Browser View Area

The `browser` view should be used to navigate high-level structures in order to
find elements to be opened in dedicated views inside the `main` area.


#### Navigator/Overview View Area

This view area should be used to navigate the content of _selected_
entities, or display an overview of a more detailed zone displayed in
the currently visible `main` view.


#### Console/Messages View Area

The `console` view should be used to display messages from the application to the users,
mirroring, for example, the standard System.out and System.err streams.


#### Palette View Area

The `palette` view area is where to display a set of (draggable or selectable)
elements frequently used by the user.


#### Inspector/Properties View Area

This area should be used to display details of the element selected in the
currently visible `main` view. Usually a table of name/value pairs is used to
list the properties of the selected element, but other visualizations are
possible too.


### Main View Area

The `main` view area contains one or more views (not necessarily "document-based") 
showing the data the use will act upon. The views in the `main` area can be opened 
from user's interaction in the `browser` and/or the `navigation` area, or 
programmatically.


### How View Are Displayed

Inside the `main` and `console` view areas, views are displayed in a tab container.
In the remaining view areas, the alternative between tab container or accordion can
be chosen.


## Using XAOS App


### Maven

```maven
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.app</artifactId>
  <version>0.4.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.app` module:

![xaos.app dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.app.module/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.app;
  ...
}
```

