# gtfs-validator [![Java CI](https://github.com/MobilityData/gtfs-validator/workflows/Java%20CI/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Java+CI%22) [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

A static [General Transit Feed Specification (GTFS)](http://gtfs.org/reference/static/) feed validator

# Introduction

This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks files integrity, numeric type parsing and ranges as well as string format according to the [GTFS specification](http://gtfs.org/reference/static/#field-types) using [this schema file](https://github.com/MobilityData/gtfs-validator/blob/v1.1.0/adapter/repository/in-memory-simple/src/main/resources/gtfs_spec.asciipb)
1. Performs basic GTFS business rule validation *(work-in-progress)*

# Prerequisites
1. Install [Java 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download the latest gtfs-validator JAR file from our [Releases page](https://github.com/MobilityData/gtfs-validator/releases)

# Usage

Sample usage:

``` 
java -jar gtfs-validator-v1.1.0.jar -u https://transitfeeds.com/p/mbta/64/latest/download -z input.zip -i input -o output
```

...which will:
 1. Download the GTFS feed at the URL `https://transitfeeds.com/p/mbta/64/latest/download` and name it `input.zip`
 1. Extract the `input.zip` contents to the directory `input`
 1. Validate the GTFS data and output the results to the directory `output`. Validation results are exported to JSON by default.

For a list of all available commands, use `--help`:

``` 
java -jar gtfs-validator-v1.1.0.jar --help
```

Execution parameters are configurable through command-line or via a configuration file `execution-parameters.json`. 
By default, if no command-line is provided the validation process will look for execution parameters in user configurable configuration file `execution-parameters.json`.
In the case said file could not be found or is incomplete, default values will be used.

As an example, the sample usage equivalent with configuration file 

``` 
java -jar gtfs-validator-v1.1.0.jar
```

With `execution-parameters.json` file located in the working directory:
 
```
{
  "extract": "input",
  "output": "output",
  "url": "https://transitfeeds.com/p/mbta/64/latest/download",
  "zipinput": "input.zip"
}
```

Note that you'll need to change the above JAR file name to whatever [release version](https://github.com/MobilityData/gtfs-validator/releases) you download.

# Architecture

We use [clean architecture principles](https://medium.com/slalom-build/clean-architecture-with-java-11-f78bba431041) to implement this validator, which modularizes the project.

Some important modules:
* [Domain](domain) - Entity classes
* [Use cases](usecase) - Business logic 
* [Adapters](adapter) - Convertors (e.g., parsers and exporters)
* [application/cli-app](application/cli-app) - The main command-line application

# Tests

To run tests: 

```
./gradlew check
```

# License

Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
