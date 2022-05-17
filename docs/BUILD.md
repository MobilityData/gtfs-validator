# Build Setup

## Prerequisites for Gradle
1. Clone this repository
1. Install [Java Development Kit (JDK 8+)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## Building from the command line
To build the application, run `./gradlew clean build` from the command line at the root of the project.

## Building from IntelliJ IDE
To build the application simply click the hammer in the header section

![build from IntelliJ](https://user-images.githubusercontent.com/35747326/101071800-7a0b3c80-3573-11eb-80f5-afded385b117.png)

## Running unit tests
1. Run the following command at the root of the project to run Java tests:
```
$ ./gradlew test
```

## Packaging JAR with all dependencies

To build a JAR that can run stand-alone without any additional classes on the classpath (sometimes called an "uber" or "fat" JAR), run:

```
$ ./gradlew shadowJar 
./gradlew shadowJar
```

## Packaging as Installable Application

**NOTE:** The installable application is under active development.  It currently
works best on Windows.

To build an installable application package appropriate for your operating system
(e.g. Windows, Mac OS, Linux), first make sure you have a recent version of the
JDK installed (ver >= 15) that includes `jlink` and `jpackage`.  If you intend
to redistribute the built application publicly, make sure it's an OpenJDK
distribution (likely a recent build linked from https://openjdk.java.net/install/,
ok if it's built by Oracle) and *NOT* an Oracle *commercial* JDK, where license
and redistribution terms are murkier.

If building on Windows, have https://wixtoolset.org/ installed on your path for
Windows Installer support.  When installing WiX, if you get an error like:

```
WiX Toolset requires .NET Framework 3.5.1...
```

you can navigate to "Control Panel > All Control Panel Items > Programs and Features"
and enable the Windows Features for .NET framework
([stackoverflow](https://stackoverflow.com/a/57820594/937715)).

To build the app and installer, run:

```
./gradlew clean jpackage
```

and look for the resulting application artifacts in:

```
./app/pkg/build/jpackage/
```

## Generating Javadocs

To generate Javadocs for the project, run:

```
$ ./gradlew aggregateJavadocs
```
