# gtfs-validator [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

[![Test Package Document](https://github.com/MobilityData/gtfs-validator/workflows/Test%20Package%20Document/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Test+Package+Document%22) [![CircleCI](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master.svg?style=svg)](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master)

A GTFS Schedule (static) [General Transit Feed Specification (GTFS)](https://gtfs.mobilitydata.org/spec/gtfs-schedule) feed validator

# Introduction

This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks files integrity, numeric type parsing and ranges as well as string format according to the [GTFS Schedule specification](https://gtfs.mobilitydata.org/spec/gtfs-schedule#h.hc443y62gb8c)
1. Performs basic GTFS business rule validation
1. Performs advanced GTFS business rule validation *(work-in-progress)*

# Build Setup
The main IDE used when designing this project is IntelliJ which can be downloaded on [Jetbrains' web page](https://www.jetbrains.com/idea/download/?gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WGaq62QEFq2fzTUWswRwp4KKFcJ1GEIxeVMS4puzHwuCIYYBHS1DqwaAuWTEALw_wcB#section=mac).
This IDE is a powerful tool that allows building and running this project.
Instructions to build the project locally are available in our [Build Documentation page](/BUILD.md).

# How to run the app locally 
Instructions to run the application locally are available in our [Usage page](/USAGE.md).

⚠ ️The following command line parameters are required to run the application:
* `--input` or `-i`: the path to the GTFS archive
* `--output` or `-o`: the path to the validation report
* `--feed_name` or `-f`: the name of the feed as 

⚠ CLI parameter `--thread` or `-t` (the number of threads to use) is optional

# Architecture
The architecture of the `gtfs-validator` is described in our [Architecture page](/ARCHITECTURE.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

# Contributing
We welcome contributions to the project! Please check out our [Contribution guidelines](/CONTRIBUTION.md) for details.
