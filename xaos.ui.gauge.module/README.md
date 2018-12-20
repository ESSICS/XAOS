# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS – Core

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.ui.gauge.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.ui.gauge/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.ui.gauge.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.ui.gauge)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.ui.gauge.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.ui.gauge)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS Gauge_ module (`xaos.ui.gauge`) provides the JavaFX-based kit of
controls and tools for building gauges, meters, knobs, etc.


## Using XAOS Gauge


### Maven

```maven
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.ui.gauge</artifactId>
  <version>0.3.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.ui.gauge` module:

![xaos.ui.gauge dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.ui.gauge.module/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.ui.gauge;
  ...
}
```

