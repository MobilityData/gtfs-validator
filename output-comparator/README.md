# output-validator

A tool to compare validation reports produced by the [gtfs-validator](htttps://www.github.com/MobilityData/gtfs-validator).

# Introduction
This is a command-line tool written in Java that performs the following steps:
1. loads validation reports from disk;
1. compares pairs of validation reports produced by two versions f the validator;  
1. generates a report that quantifies the new errors introduced by a new implementation. 

# Run the app via command line
### Setup
1. Install [Java 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download [output-comparator-v3.0.0_cli.jar](https://github.com/MobilityData/gtfs-validator/releases/download/v3.0.0/output-comparator-v3.0.0_cli.jar)

### Run it
To validate a GTFS dataset on your computer:

`java -jar output-validator-v3.0.0.jar --report_directory /path/to/validation/reports --new_error_threshold 1 --percent_invalid_datasets_threshold 40 --reference_report_name report.json --latest_report_name latest.json --output_base` 

where:
* *(Required)* `--report_directory` or `-d`: path to the validation reports     
* *(Required)* `--new_error_threshold` or `-n`: the number of new errors after which a dataset is considered invalid   
* *(Required)* `--percent_invalid_datasets_threshold` or `-p`: threshold for the percentage of newly invalid datasets out of total datasets in the mobility archives. If the observed percentage is greater than or equal to this threshold, the acceptance test will fail.
* *(Required)* `--output_base` or `-o`:  the path where the validation report will be stored (e.g., `output`)   
* *(Optional)* `--reference_report_name` or `-r`: the name of the validation report generated by the source of truth   
* *(Optional)* `--latest_report_name` or `-l`: the name of the validation report generated by the snapshot version of the gtfs-validator   

More detailed instructions are on our [acceptance test](../docs/ACCEPTANCE_TESTS.md) documentation.

# Build the code
We suggest using [IntelliJ](https://www.jetbrains.com/idea/download/) to [import](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html), build, and run this project.

Instructions to build the project from the command-line using [Gradle](https://gradle.org/) are available in our [Build documentation](../docs/BUILD.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).