# gtfs-validator [![CircleCI](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master.svg?style=svg)](https://circleci.com/gh/MobilityData/gtfs-validator/tree/master) [![Java CI](https://github.com/MobilityData/gtfs-validator/workflows/Java%20CI/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Java+CI%22) [![Join the gtfs-validator chat](https://mobilitydata-io.herokuapp.com/badge.svg)](https://mobilitydata-io.herokuapp.com/)

A static [General Transit Feed Specification (GTFS)](http://gtfs.org/reference/static/) feed validator

# Introduction

This command-line tool written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk
1. Checks files integrity, numeric type parsing and ranges as well as string format according to the [GTFS specification](http://gtfs.org/reference/static/#field-types) using [this schema file](https://github.com/MobilityData/gtfs-validator/blob/v1.1.0/adapter/repository/in-memory-simple/src/main/resources/gtfs_spec.asciipb)
1. Performs basic GTFS business rule validation *(work-in-progress)*

# Prerequisites
1. Install [Java 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download the latest gtfs-validator JAR file from our [Releases page](https://github.com/MobilityData/gtfs-validator/releases) or snapshot artifact from [GitHub Actions](https://github.com/MobilityData/gtfs-validator/actions?query=branch%3Amaster) or [Circle-CI Pipelines](https://app.circleci.com/pipelines/github/MobilityData/gtfs-validator?branch=master)

# Usage

### Validate a locally stored GTFS dataset

Sample usage:

``` 
java -jar gtfs-validator-v1.1.0.jar -i relative/path/to/zipped_dataset -o relative/output/path -e relative/extraction/path -x enumeration_of_files_to_exclude_from_validation_process
```

...which will:
 1. Search for a zipped GTFS dataset located at `relative/path/to/zipped_dataset`
 1. Extract the zip content to a directory located at `relative/extraction/path`
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. Validation results are exported to JSON by default. The validation process will not be executed on the enumeration of files provided via option `-x` and the files that rely on them.

Note:
 - *export validation report as `.json` file*: After validating [MBTA's GTFS archive](https://cdn.mbta.com/MBTA_GTFS.zip) on 2020-10-20 at 09:07:48 (America/Montreal timezone), the validation report will be named as follows `MBTA__2020-10-20_09/07/48.442365.json`
 - *export validation report as `.pb` file*: after validating [MBTA's GTFS archive](https://cdn.mbta.com/MBTA_GTFS.zip) on 2020-10-20 at 09:07:48 (America/Montreal timezone), the validation reports will be named as follows 
   - `MBTA__2020-10-20_09/07/48.442365-1.pb` 
   - `MBTA__2020-10-20_09/07/48.442365-2.pb` 
   - ...
   - `MBTA__2020-10-20_09/07/48.442365-n.pb` 

**Those names come from concatenating the information found in `feed_info.feed_publisher_name` and the local time of execution seaprated with `__`
then replacing whitespace character by `_`**

In the case where GTFS file`feed_info.txt`  is not provided, the validation report name would be limited to: `__2020-10-20_09/07/48.442365.json` or `__2020-10-20_09/07/48.442365-1.pb`    

#### Example: Validate GTFS dataset while specifying extraction, input, and output directories

``` 
java -jar gtfs-validator-v1.1.0.jar -i gtfs-dataset.zip -o output_folder -e extraction_folder
```

In order, this command line will:
 1. Search for a zipped GTFS dataset name `gtfs-dataset.zip` located in the working directory
 1. Extract its content to a directory named `extraction_folder`
 1. Validate the GTFS data and output the results to the directory named `output_folder`. This folder will contain a single `.json` file with information related to the validation process. 


#### Example: Validate a subset of a GTFS dataset.

``` 
java -jar gtfs-validator-v1.1.0.jar -i gtfs-dataset.zip  -x fare_attributes.txt,attributions.txt
```

In order, this command line will:
 1. Search for a zipped GTFS dataset name `gtfs-dataset.zip` located in the working directory
 1. Create a directory named `input` 
 1. Extract the content of `gtfs-dataset.zip` to the directory created at step 2
 1. Create a directory names `output`
 1. Exclude files `fare_attributes.txt` and `attributions.txt` from the validation process. But also the files that rely on them: `translations.txt` and `fare_rules.txt`
 1. Validate the GTFS data and output the results to the directory created at step 4. This folder will contain a single `.json` file with information related to the validation process. 

### Validate a GTFS dataset stored on a remote server

Sample usage:

``` 
java -jar gtfs-validator-v1.1.0.jar -u url/to/dataset -o relative/output/path -e relative/extraction/path -i input.zip
```

...which will:
 1. Download the GTFS feed at the URL `url/to/dataset` and name it `input.zip`  
 1. Extract the `input.zip` content to the directory located at `relative/extraction/path`
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. Validation results are exported to JSON by default.

#### Example: Validate a GTFS dataset and export the validation result as proto files

``` 
java -jar gtfs-validator-v1.1.0.jar -u url/to/dataset -o output_folder -e extraction_folder -i local-dataset.zip -p
```

In order, this command line will:
 1. Download the GTFS feed at the URL `url/to/dataset` and name it `local-dataset.zip`
 1. Extract the `local-dataset.zip` content to the directory `extraction_folder`
 1. Validate the GTFS data and output the results to the directory `output_folder`. As option `-p` is provided, results will be exported as `.pb` files

#### Example: Validate a GTFS dataset without specifying command arguments or providing configuration file

``` 
java -jar gtfs-validator-v1.1.0.jar
```

In order, this command line will:
 1. Search for a zipped folder in the working directory
 1. Extract by default the content of the zipped GTFS dataset to directory `gtfs-validator/input/`  
 1. Validate the GTFS data and output the results to directory `gtfs-validator/output`. Validation results will be exported to JSON by default.


For a list of all available commands, use `--help`:

``` 
java -jar gtfs-validator-v1.1.0.jar --help
```

## Software configuration

Execution parameters are configurable through command-line or via a configuration file `execution-parameters.json`. 
By default, if no command-line is provided the validation process will look for execution parameters in user configurable configuration file `execution-parameters.json`.
In the case said file could not be found or is incomplete, default values will be used.

One should note that if both command-line options and configuration file are provided, the configuration file takes precedence over the command option.

Sample usage:

The two following sample usages are equivalent, provided that `execution-parameters.json` file is located in the working directory:

``` 
java -jar gtfs-validator-v1.1.0.jar -e relative/extraction/path -o relative/output/path -i relative/path/to/zipped_dataset -x agency.txt,routes.txt
```
```
{
  "extract": "relative/extraction/path",
  "output": "relative/output/path",
  "input": "relative/path/to/zipped_dataset",
  "exclude": "agency.txt,routes.txt"
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
