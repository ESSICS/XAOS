# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS (ΧΑΟΣ – the primordial state of existence)

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

A JavaFX application framework made initially to create accelerator physics
applications based on [Open XAL](https://github.com/openxal/openxal), an open
source development environment used for creating accelerator physics
applications, scripts and services.


## Modules

XAOS is written using the Java Platform Module System (JPMS) available since
Java 9. The Maven project is also organized in modules maintaining a 1-to-1
mapping with the JPMS ones. A Maven module's name is made up by the JPMS module
one plus the `.module` suffix.

### [xaos.app](https://github.com/ESSICS/XAOS/tree/master/xaos.app.module)

The _XAOS App_ module (`xaos.tools`) provides a JavaFX-based application
framework for building UI-based applications with menus, toolbars, etc.

### [xaos.core](https://github.com/ESSICS/XAOS/tree/master/xaos.core.module)

The _XAOS Core_ module (`xaos.core`) provides the non-UI classes part of the
framework. Any non-graphical application can use this module.

### [xaos.tools](https://github.com/ESSICS/XAOS/tree/master/xaos.tools.module)

The _XAOS Tools_ module (`xaos.tool`) provides tools and utilities used by the
other XAOS modules (e.g. annotation processors) to simplify some programming
tasks.

### [xaos.ui](https://github.com/ESSICS/XAOS/tree/master/xaos.ui.module)

The _XAOS UI_ module (`xaos.ui`) provides JavaFX-based controls and tools
suitable for other projects too.

### [xaos.ui.gauge](https://github.com/ESSICS/XAOS/tree/master/xaos.ui.gauge.module)

The _XAOS Gauge_ module (`xaos.ui.gauge`) provides the JavaFX-based kit of
controls and tools for creating gauges.

### [xaos.ui.plot](https://github.com/ESSICS/XAOS/tree/master/xaos.ui.plot.module)

The _XAOS Plot_ module (`xaos.ui.plot`) provides the JavaFX-based controls and
tools for plotting data into a graph.

### Test Modules

From the introduction of JPMS in Java 9, some tests cannot anymore performed
from inside the module to be tested. for this reasons there are some specific
JPMS/Maven modules built to test others:

* [xaos.tests.tools](https://github.com/ESSICS/XAOS/tree/master/xaos.tests.tools.module).

## Using XAOS


### Maven

Each XAOS module needed to build your application require a dependency in the
application's `pom.xml` file like the following one:

```xml
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.module</artifactId>
  <version>0.4.2-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

where _module_ is the name of the XAOS module needed. Look at the `README.md`
file of the module you need for more examples and information.

Here the Maven dependencies of `xaos` project:

![xaos dependencis](https://github.com/ESSICS/XAOS/blob/master/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.module;
  ...
}
```

where _module_ is the name of the XAOS module needed. Look at the `README.md`
file of the module you need for more examples and information.


### System Properties

XAOS defines the following system properties that can be set before launching
an application build on XAOS (using `java -D<name>=<value> ...` ):

Property | Type | Default | Description
-------- | ---- | ------- | -----------
xaos.test.verbose | boolean | `false` | Some tests will be more verbose if set to `true`.

### Running [TextFX](https://github.com/TestFX/TestFX) Tests

When XAOS is built JUnit tests and UI ones (based on [TextFX](https://github.com/TestFX/TestFX))
are automatically performed.

On macOS since version 10.14 Mojave it is necessary to explicitly allow the IDE
or the Terminal application performing the build to control the computer. This
can be done in the Privacy tab of the Security & Privacy system preferences, by
selecting the Accessibility category, the adding and enabling the applications.


## Contributing XAOS


The XAOS project on GitHub is using [Gitflow](https://blog.axosoft.com/gitflow/),
development model introduced by [Vincent Driessen](http://nvie.com/posts/a-successful-git-branching-model/),
here summarized:

![Gitflow](http://nvie.com/img/git-model@2x.png)

Who wants to contribute this projects must adopt the Gitflow model and
[tools](https://github.com/nvie/gitflow).
