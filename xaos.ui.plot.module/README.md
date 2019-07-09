# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS – Core

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.ui.plot.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.ui.plot/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.ui.plot.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.ui.plot)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.ui.plot.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.ui.plot)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS Plot_ module (`xaos.ui.plot`) provides the JavaFX-based controls and
tools for plotting data into a graph.


## Using XAOS Plot


### Maven

```maven
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.ui.plot</artifactId>
  <version>0.4.1</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.ui.plot` module:

![xaos.ui.plot dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.ui.plot.module/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.ui.plot;
  ...
}
```

Here the Java module dependencies of `xaos.ui.plot` module:

![xaos.ui.plot java dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.ui.plot.module/doc/module-dependencies.png)

