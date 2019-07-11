# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS – UI

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.ui.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.ui/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.ui.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.ui)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.ui.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.ui)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS UI_ module (`xaos.ui`) provides JavaFX-based controls and tools
suitable for other projects too.


## SVG Support

XAOS supports SVG natively converting them in a JavaFX components graph, thus
providing true vectorial rendering.

The following simple test explains how to display SVGs with XAOS:

```java
package se.europeanspallationsource.xaos.ui.components;


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

  private SVG svg;

  @Override
  public void start( Stage stage ) throws IOException, XMLStreamException {

    svg = SVG.load(SVGFromURLTest.class.getResource("/svg/duke.svg"));

    svg.setId("Loaded SVG Image");

    stage.setScene(new Scene(svg));
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


## Tree Controls and Utilities


### Tree Directory Monitor

_TreeDirectoryMonitor_ combines a DirectoryWatcher, a TreeDirectoryModel, and a
TreeDirectoryAsynchronousIO. The added value of this combination is that the
tree model is updated automatically to reflect the current state of the
file-system, and the application can distinguish file-system changes made via
the I/O facility (TreeDirectoryAsynchronousIO) from external ones.

The directory model can be used directly as a model for TreeViews.


### Filterable Tree Items

_FilterableTreeItem_ is an extension of TreeItem with the possibility to filter
its children.


### Tree Item Walker

_TreeItemWalker_ walks a TreeItems tree depth-first. It supports streaming,
visitor pattern and iterator pattern.


### Tree Item Utilities

_TreeItems_ provides methods to manipulate trees made of TreeItems.


## Using XAOS UI


### Maven

```maven
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.ui</artifactId>
  <version>0.4.2</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.ui` module:

![xaos.ui dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.ui.module/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.ui;
  ...
}
```

Here the Java module dependencies of `xaos.ui` module:

![xaos.ui java dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.ui.module/doc/module-dependencies.png)

