# gtfs-validator [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

[![Test Package Document](https://github.com/MobilityData/gtfs-validator/workflows/Test%20Package%20Document/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Test+Package+Document%22) 
[![CircleCI](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master.svg?style=svg)](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master) 
![End to end](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end/badge.svg)

A GTFS Schedule (static) [General Transit Feed Specification (GTFS)](https://gtfs.mobilitydata.org/spec/gtfs-schedule) feed validator

# Introduction

This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks file integrity, numeric type parsing and ranges as well as string format according to the [GTFS Schedule specification](https://gtfs.mobilitydata.org/spec/gtfs-schedule#h.hc443y62gb8c)
1. Performs GTFS business rule validation *(work-in-progress)*

# Build the code
We suggest using [IntelliJ](https://www.jetbrains.com/idea/download/) to [import](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html), build, and run this project.

Instructions to build the project from the command-line using [Gradle](https://gradle.org/) are available in our [Build documentation](/docs/BUILD.md).

# Run the app
*(Instructions to run a pre-built JAR file are coming soon)*

From IntelliJ set up a [run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration.html) with the following program arguments: 

`-i /myDirectory/gtfs.zip -o output -f ca-myFeedName`

where:
* `--input` or `-i`: the path to the GTFS archive (e.g., `/myDirectory/gtfs.zip`)
* `--output` or `-o`: the path to the validation report (e.g., `output`)
* `--feed_name` or `-f`: the name of the feed as a valid [ISO two letter country code](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2), followed by `-`, followed by a user-defined name for the feed. (e.g., `ca-myFeedName`, `us-myFeedName`)
* *(Optional)* `--thread` or `-t`: the number of Java threads to use

More detailed instructions to run the application locally are available in our [Usage page](/docs/USAGE.md).

# Architecture
The architecture of the `gtfs-validator` is described on our [Architecture page](/docs/ARCHITECTURE.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

# Contributing
We welcome contributions to the project! Please check out our [Contribution guidelines](/docs/CONTRIBUTING.md) for details. 
