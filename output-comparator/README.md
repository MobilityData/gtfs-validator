# output-validator

A tool to compare validation reports produced by the [gtfs-validator](htttps://www.github.com/MobilityData/gtfs-validator).

# Introduction
This is a command-line tool written in Java that performs the following steps:
1. Loads validation reports from disk
1. Compares pairs of validation reports produced by a snapshot version of the validator and the source of truth  
1. Generates a report that details the number of new errors introduced by the snapshot version of the validator

# Run the app via command line
### Setup
1. Install [Java 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download [output-comparator-v2.0.0_cli.jar](https://github.com/MobilityData/gtfs-validator/releases/download/v2.0.0/output-comparator-v2.0.0_cli.jar)

### Run it
To validate a GTFS dataset on your computer:

`java -jar output-validator-v2.0.0.jar --report_directory /path/to/validation/reports --new_error_threshold 1 --percent_invalid_datasets_threshold 40 --reference_report_name report.json --latest_report_name latest.json` 

where:
* *(Required)* `--report_directory` or `-d`: path to the validation reports     
* *(Required)* `--new_error_threshold` or `-n`: the number of new errors after which a dataset is considered invalid   
* *(Required)* `--percent_invalid_datasets_threshold` or `-p`: threshold for the percentage of newly invalid datasets out of total datasets in the mobility archives. If the observed percentage is greater than or equal to this threshold, the acceptance test will fail.
* *(Optional)* `--reference_report_name` or `-r`: the name of the validation report generated by the source of truth   
* *(Optional)* `--latest_report_name` or `-l`: the name of the validation report generated by the snapshot version of the gtfs-validator   

More detailed instructions are on our [acceptance test](../docs/ACCEPTANCE_TESTS.md) documentation.

# Build the code
We suggest using [IntelliJ](https://www.jetbrains.com/idea/download/) to [import](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html), build, and run this project.

Instructions to build the project from the command-line using [Gradle](https://gradle.org/) are available in our [Build documentation](../docs/BUILD.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).