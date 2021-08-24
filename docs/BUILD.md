# Build Setup

## Prerequisites for Gradle
1. Clone this repository
1. Install [Java Development Kit (JDK 8+)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## Building from the command line
To build the application, run `./gradlew clean build` from the command line at the root of the project.

This command will build a jar file for each submodule of the project: `main`, `core`, `processor` and `springboot`.

Said jar files can be found under `build/libs/gtfs-validator*.jar` for each sub-module. E.g. `core/build/libs/gtfs-validator*.jar` is the path to access the jar file that results from the build of `core` sub-module.
Each one of these jar only include the dependencies the module they are related to should build.  

It is possible to only build sub-modules of the project. For example:
 - running `./gradlew clean build :main:build` from the command line at the root of the project will only build the CLI application (this command will exclude the `springboot` sub-module).
 - running `./gradlew clean build :processor:build` from the command line at the root of the project will build the jars for `core` and `processor` sub-modules since `core` depends on `processor`. 

## Building from IntelliJ IDE
To build the application simply click the hammer in the header section

![build from IntelliJ](https://user-images.githubusercontent.com/35747326/101071800-7a0b3c80-3573-11eb-80f5-afded385b117.png)

## Running unit tests
1. Run the following command at the root of the project to run Java tests:
```
$ ./gradlew test
```

