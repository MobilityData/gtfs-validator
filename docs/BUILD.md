# Build Setup

## Prerequisites for Gradle
1. Clone this repository
1. Install [Java Development Kit (JDK 8+)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## Building from the command line
To build the application, run `./gradlew clean build` from the command line at the root of the project.

By default, two JARs are built: one for the CLI application and the other one for the Spring Boot application. They are respectively located in `main/build/libs` and `springboot/build/libs`.
If you want to build the main CLI JAR without building Spring Boot, you can run the following command line at the root of the project:
```
./gradlew clean build :main:build
```

## Building from IntelliJ IDE
To build the application simply click the hammer in the header section

![build from IntelliJ](https://user-images.githubusercontent.com/35747326/101071800-7a0b3c80-3573-11eb-80f5-afded385b117.png)

## Running unit tests
1. Run the following command at the root of the project to run Java tests:
```
$ ./gradlew test
```

