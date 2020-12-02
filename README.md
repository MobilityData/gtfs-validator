# gtfs-validator [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

[![Test Package Document](https://github.com/MobilityData/gtfs-validator/workflows/Test%20Package%20Document/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Test+Package+Document%22) [![Docker image](https://github.com/MobilityData/gtfs-validator/workflows/Docker%20image/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Docker+image%22) [![End to end](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22End+to+end%22) [![CircleCI](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master.svg?style=svg)](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master)


A GTFS Schedule (static) [General Transit Feed Specification (GTFS)](https://gtfs.mobilitydata.org/spec/gtfs-schedule) feed validator

# Introduction

This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks files integrity, numeric type parsing and ranges as well as string format according to the [GTFS Schedule specification](https://gtfs.mobilitydata.org/spec/gtfs-schedule#h.hc443y62gb8c)
1. Performs basic GTFS business rule validation
1. Performs advanced GTFS business rule validation *(work-in-progress)*
