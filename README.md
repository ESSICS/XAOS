# XAOS (ΧΑΟΣ – the primordial state of existence)

<!--[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos/)-->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos)
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

![layout-01](https://github.com/ESSICS/XAOS/blob/master/doc/layout-01.png)


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

The `main` view area will contains one or more views (not necessarily "document-based") 
showing the data the use will act upon. The views in the `main` area can be opened 
from user's interaction in the `browser` and/or the `navigation` area, or 
programmatically.


### How View Are Displayed

Inside the `main` and `console` view areas, views are displayed in a tab container.
In the remaining view areas, the alternative between tab container or accordion can
be chosen.


## Tools and Utilities


### Service Providers

The _ServiceProvider_ annotation will simplify providing service implementations,
taking care of publishing them in the proper file inside the `META-INF/service`
folder.

```java
package my.module;
import my.module.spi.SomeService;
import se.europeanspallationsource.framework.annotation.ServiceProvider;

@ServiceProvider(service=SomeService.class)
public class MyService implements SomeService {
  ...
}
```

Moreover, the _ServiceLoaderUtilities_ class will complement the
`java.util.ServiceLoader` one with few more methods.


### SVG Support

XAOS supports SVG natively converting them in a JavaFX components graph, thus
providing true vectorial rendering.

The following simple test explains how to display SVGs with XAOS:

```java
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;

public class SVGFromURLTest extends ApplicationTest {

  private SVGContent svgContent;

  @Override
  public void start( Stage stage ) throws IOException, XMLStreamException {

    svgContent = SVGLoader.load(SVGFromURLTest.class.getResource("/svg/duke.svg"));

    svgContent.setId("Loaded SVG Image");

    stage.setScene(new Scene(svgContent));
    stage.show();

  }

  @After
  public void tearDown() throws TimeoutException {
    FxToolkit.cleanupStages();
  }

  @Test
  public void testLoadDuke() {
    assertThat(svgContent).hasAnyChild();
  }

}
```


## Using XAOS


### Maven

```maven
<dependency>
	<groupId>se.europeanspallationsource</groupId>
	<artifactId>xaos</artifactId>
	<version>0.2.3</version>
	<scope>compile</scope>
</dependency>
```


## Contributing XAOS


The XAOS project on GitHub is using [Gitflow](https://blog.axosoft.com/gitflow/),
development model introduced by [Vincent Driessen](http://nvie.com/posts/a-successful-git-branching-model/),
here summarized:

![Gitflow](http://nvie.com/img/git-model@2x.png)

Who wants to contribute this projects must adopt the Gitflow model and
[tools](https://github.com/nvie/gitflow).

