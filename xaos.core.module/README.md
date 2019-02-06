# ![logo](https://github.com/ESSICS/XAOS/blob/master/doc/logo-small.png) XAOS – Core

<!-- [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/se.europeanspallationsource/xaos.core.svg)](https://oss.sonatype.org/content/repositories/snapshots/se/europeanspallationsource/xaos.core/) -->
[![Maven Central](https://img.shields.io/maven-central/v/se.europeanspallationsource/xaos.core.svg)](https://repo1.maven.org/maven2/se/europeanspallationsource/xaos.core)
[![javadoc](https://www.javadoc.io/badge/se.europeanspallationsource/xaos.core.svg)](https://www.javadoc.io/doc/se.europeanspallationsource/xaos.core)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The _XAOS Core_ module (`xaos.core`) provides the non-UI classes part of the
framework. Any non-graphical application can use this module.


## IO Utilities


### Delete File Visitor

It allows to delete recursively a folder.

```java
Files.walkFileTree(rootDirectory, new DeleteFileVisitor());
```


#### Directory Watcher

Watches for changes in files and directories, and allows for standard operations
on both files and directories.

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
DirectoryWatcher watcher = build(executor);

watcher.events().subscribe(event -> {
  event.getEvents().stream().forEach(e -> {
	if ( StandardWatchEventKinds.ENTRY_CREATE.equals(e.kind()) ) {
	  ...
	} else if ( StandardWatchEventKinds.ENTRY_DELETE.equals(e.kind()) ) {
	  ...
	} else if ( StandardWatchEventKinds.ENTRY_MODIFY.equals(e.kind()) ) {
	  ...
	}
  });
});

Path root = ...

watcher.watch(root);
```


## Using XAOS Core


### Maven

```maven
<dependency>
  <groupId>se.europeanspallationsource</groupId>
  <artifactId>xaos.core</artifactId>
  <version>0.4.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

Here the Maven dependencies of `xaos.core` module:

![xaos.core dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.core.module/doc/maven-dependencies.png)


### Java `module-info`

Inside your application's `module-info.java` file the following dependency must
be added:

```java
module your.application {
  ...
  requires xaos.core;
  ...
}
```

Here the Java module dependencies of `xaos.core` module:

![xaos.core java dependencis](https://github.com/ESSICS/XAOS/blob/master/xaos.core.module/doc/module-dependencies.png)

