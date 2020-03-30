# gtfs-validator [![Java CI](https://github.com/MobilityData/gtfs-validator/workflows/Java%20CI/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Java+CI%22) [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

A GTFS static feed validator

# Introduction

Java code to parse and validate a GTFS feed Zip archive

# Usage

Loads input GTFS feed from url or disk.
 Checks files integrity, numeric type parsing and ranges as well as string
format according to [GTFS specification](http://gtfs.org/reference/static)

Schema file can be found [here](https://github.com/MobilityData/gtfs-validator/blob/v1.1.0/README.md)
 
 For a list of all available commands, use `--help`
              
 Validation results are exported to JSON file by default


Sample usage `java -jar gtfs-validator.jar -u https://transitfeeds.com/p/mbta/64/latest/download -z input.zip -i input -o output`

# Tests
* To run tests : `./gradlew check`

# License

Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
