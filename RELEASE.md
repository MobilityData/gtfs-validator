# Release instructions

We create releases from the command-line using the [shadow Gradle plugin](https://github.com/johnrengelman/shadow), which creates a JAR file including all necessary dependencies.
This task runs automatically on every change in GitHub.

### 0. Open a release branch + PR

The following changes should happen in a specific PR

### 1. Prepare for release
Change `version` in the various `build.gradle` files to remove the `-SNAPSHOT` qualifier. 

For example, if the current version is `1.1.0-SNAPSHOT`, change the `version` to `1.1.0`. 

Do the same in GitHub CI [config file](https://github.com/MobilityData/gtfs-validator/blob/master/.github/workflows/gradle.yml) for
both `Run validator on MBTA data` and `upload artifacts` steps

Do the same in Circle-CI [config file](https://github.com/MobilityData/gtfs-validator/blob/master/.circleci/config.yml)

**!! Be wary of preserving postfixes like `_cli` or `_web` in some names !!**

### 2. Do the release

Commit and push those changes to your release preparation PR. 
Locate the .jar file artifact produced by the corresponding GitHub [workflow](https://github.com/MobilityData/gtfs-validator/actions)

This file can then be run from the command-line with the normal Java conventions:

```
java -jar gtfs-validator-v1.1.0.jar -u https://transitfeeds.com/p/mbta/64/latest/download -i input.zip -e input -o output
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
  "input": "input.zip"
}
```

If everything looks ok, you can create the new release in GitHub. Tag the code in your PR branch.

### 3. Prepare for the next development cycle

Increment the `version` in the various `build.gradle` files and add the `-SNAPSHOT` qualifier. 

For example, if the version you just released is `1.1.0`, change the `version` to `1.1.1-SNAPSHOT`.

Update the GitHub CI [config file](https://github.com/MobilityData/gtfs-validator/blob/master/.github/workflows/gradle.yml) to point to the new `SNAPSHOT` version.
Both `Run validator on MBTA data` and `upload artifacts` steps

For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).

