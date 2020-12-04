# Instructions to run the application locally
1. Install [Java 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download the latest gtfs-validator JAR file from our [Releases page](https://github.com/MobilityData/gtfs-validator/releases) or snapshot artifact from [GitHub Actions](https://github.com/MobilityData/gtfs-validator/actions?query=branch%3Amaster) or [Circle-CI Pipelines](https://app.circleci.com/pipelines/github/MobilityData/gtfs-validator?branch=master)

## cli-app usage
### Validate a locally stored GTFS dataset
Sample usage:

``` 
java -jar gtfs-validator-v2.0.jar --input relative/path/to/dataset --output relative/output/path --feed_name <name_of_the_feed> --threads <number_of_threads_to_use> 
```

...which will:
 1. Search for a GTFS dataset located at `relative/path/to/dataset`
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. 
 1. Export the validation report to `JSON` by default. This folder will contain a single `.json` file with information related to the validation process. The validation report will be named as `report.json`. 
