# Release instructions

We create releases from the command-line using the [shadow Gradle plugin](https://github.com/johnrengelman/shadow), which creates a JAR file including all necessary dependencies.

### 1. Prepare for release
Change `version` in the various `build.gradle` files to remove the `-SNAPSHOT` qualifier. 

For example, if the current version is `1.1.0-SNAPSHOT`, change the `version` to `1.1.0`. 

### 2. Do the release

```
./gradlew shadowJar
```

The command line output will tell you where the compiled JAR file is located - for example:

>Successfully built the gtfs-validator command-line app: C:\git-projects\gtfs-validator\application\cli-app\build\libs\gtfs-validator-v1.1.0.jar

This file can then be run from the command-line with the normal Java conventions:

```
java -jar gtfs-validator-v1.1.0.jar -u https://transitfeeds.com/p/mbta/64/latest/download -z input.zip -e input -o output
```

This file can also be be run from the command-line with execution parameters coming from file `execution-parameters.json` located in the working directory:

```
java -jar gtfs-validator-v1.1.0.jar
```

With file `execution-parameters.json` content: 

```
{
  "extract": "input",
  "output": "output",
  "url": "https://transitfeeds.com/p/mbta/64/latest/download",
  "zipinput": "input.zip"
}
```

### 3. Prepare for the next development cycle

Increment the `version` in the various `build.gradle` files and add the `-SNAPSHOT` qualifier. 

For example, if the version you just released is `1.1.0`, change the `version` to `1.1.1-SNAPSHOT`.

Update the GitHub CI [config file](https://github.com/MobilityData/gtfs-validator/blob/master/.github/workflows/gradle.yml) to point to the new `SNAPSHOT` version.

For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).


