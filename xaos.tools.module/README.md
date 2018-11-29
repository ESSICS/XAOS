# ![logo](https://github.com/ESSICS/XAOS/blob/feature/XAOS-11/doc/logo-small.png) XAOS – Tools
<!-- # ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS – Tools -->

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.tools.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.tools/)
<!--[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.tools.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.tools)-->
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.tools.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.tools)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS Tools_ module (`xaos.tools`) provides tools and utilities used by the
other XAOS modules (e.g. annotation processors) to simplify some programming
tasks.


## Service Providers

The _ServiceProvider_ annotation will simplify providing service implementations,
taking care of publishing them in the proper file inside the `META-INF/service`
folder.

```java
package my.module;
import my.module.spi.SomeService;
import se.europeanspallationsource.xaos.annotation.ServiceProvider;

@ServiceProvider(service=SomeService.class)
public class MyService implements SomeService {
  ...
}
```

Moreover, the _ServiceLoaderUtilities_ class will complement the
`java.util.ServiceLoader` one with few more methods.

**Note:** when using _ServiceProvider_ or _ServiceProviders_ `service` class
must be listed in the _module-info_ class inside a `uses` statement. Moreover a
`provides … with` statement must also be added to declare the annotated class as
provider for the parameter class.

**Note:** when using _ServiceLoaderUtilities_ the service provider interface
type must be listed in the _module-info_ class inside a `uses` statement.


## Java Language Tools

The _Reflections_ class provides few methods to handling fields and method
access using the Java Reflection API.

**Note:** when using the _Reflections_ class it is necessary to open the
_module/package_ being reflected to `xaos.tools`. This can be achieved with the
following flag being added to the command line launching the application:

```
  --add-opens module/package=xaos.tools
```


## Using XAOS Tools


### Maven

```xml
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.tools</artifactId>
  <version>0.3.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.tools` module:

![xaos.tools Maven dependencis](https://github.com/ESSICS/XAOS/blob/feature/XAOS-11/xaos.tools.module/doc/maven-dependencies.png)
<!--![xaos.tools dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.tools.module/doc/maven-dependencies.png)-->


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.tools;
  ...
}
```

Here the Java dependencies of `xaos.tools` module:

![xaos.tools Java dependencis](https://github.com/ESSICS/XAOS/blob/feature/XAOS-11/xaos.tools.module/doc/java-dependencies.png)
<!--![xaos.tools dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.tools.module/doc/java-dependencies.png)-->

