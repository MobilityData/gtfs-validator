# gtfs-validator [![Java CI](https://github.com/MobilityData/gtfs-validator/workflows/Java%20CI/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Java+CI%22)

A GTFS static feed validator

# Introduction

Java code to parse and validate a GTFS feed Zip archive

# Usage

`--help` command available

Sample usage `java -jar gtfs-validator.jar -u https://transitfeeds.com/p/mbta/64/latest/download -z input.zip -i input -o output`

# Tests
* To run tests : `./gradlew check`

# License

Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
