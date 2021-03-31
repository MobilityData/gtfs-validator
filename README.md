# gtfs-validator [![Test Package Document](https://github.com/MobilityData/gtfs-validator/workflows/Test%20Package%20Document/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Test+Package+Document%22) ![End to end](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end/badge.svg) ![End to end big](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end%20big/badge.svg) ![End to end 100](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end%20100/badge.svg) [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)![Docker image](https://github.com/MobilityData/gtfs-validator/actions/workflows/docker.yml/badge.svg)

A GTFS Schedule (static) [General Transit Feed Specification (GTFS)](https://gtfs.mobilitydata.org/spec/gtfs-schedule) feed validator

# Introduction
This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks file integrity, numeric type parsing and ranges as well as string format according to the [GTFS Schedule specification](https://gtfs.mobilitydata.org/spec/gtfs-schedule#h.hc443y62gb8c)
1. Performs GTFS [business rule validation](/RULES.md)

# Run the app via command line
### Setup
1. Install [Java 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download [gtfs-validator-v2.0.0_cli.jar](https://github.com/MobilityData/gtfs-validator/releases/download/v2.0.0/gtfs-validator-v2.0.0_cli.jar)

### Run it
To validate a GTFS dataset on your computer:

`java -jar gtfs-validator-v2.0.0_cli.jar -i /myDirectory/gtfs.zip -o output -f ca-myFeedName` 

To download and validate a GTFS dataset from a URL:

`java -jar gtfs-validator-v2.0.0_cli.jar -u https://www.abc.com/gtfs.zip -o output -f ca-myFeedName`

where:
* `--input` or `-i`: the path to the GTFS file (e.g., `/myDirectory/gtfs.zip`)
* `--url` or `-u`: the fully qualified URL to the GTFS file (e.g., `https://www.abc.com/gtfs.zip`)
* `--output` or `-o`: the path where the validation report will be stored (e.g., `output`)
* `--feed_name` or `-f`: the name of the feed as a valid [ISO two letter country code](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2), followed by `-`, followed by a user-defined name for the feed. (e.g., `ca-myFeedName`, `us-myFeedName`)
* *(Optional)* `--thread` or `-t`: the number of Java threads to use

More detailed instructions are on our ["Usage"](/docs/USAGE.md) page.

# Run the app using Docker
### Setup
1. Download and install [Docker](https://docs.docker.com/get-started/)
1. Pull the [latest Docker image for this project](https://github.com/orgs/MobilityData/packages/container/package/gtfs-validator)

### Run it
To run the Docker image in a new container:
`docker run -v /myDirectory:/theContainerDirectory -it ghcr.io/mobilitydata/gtfs-validator:v2.0.0`

where:
* `-v /myDirectory:/theContainerDirectory`: syntax to share directories and data between the container and the host
With the above command, any files that you place in `/myDirectory` on the host will show up in `/theContainerDirectory` inside the container and visa versa.

The validator can then be executed via bash commands. See [preceeding instructions for command line usage](#run-the-app-via-command-line).

# Validation rules
* [Implemented rules](/RULES.md)
* [Possible future rules](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3A%22new+rule%22)

Have a suggestion for a new rule? Open [an issue](https://github.com/MobilityData/gtfs-validator/issues/new/choose). You can see the complete process for adding new rules on the ["Adding new rules"](/docs/NEW_RULES.md) page.

# Build the code
We suggest using [IntelliJ](https://www.jetbrains.com/idea/download/) to [import](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html), build, and run this project.

Instructions to build the project from the command-line using [Gradle](https://gradle.org/) are available in our [Build documentation](/docs/BUILD.md).

# Architecture
The architecture of the `gtfs-validator` is described on our [Architecture page](/docs/ARCHITECTURE.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

# Contributing
We welcome contributions to the project! Please check out our [Contribution guidelines](/docs/CONTRIBUTING.md) for details. 
