# Instructions to run the application locally
 
 *If you're running a [`v1.x` release JAR file](https://github.com/MobilityData/gtfs-validator/releases) you'll need Java 11, and can follow [these instructions](https://github.com/MobilityData/gtfs-validator/tree/v1.4.0#via-java-on-your-local-computer). The below instructions are for the master branch, which will be v2.0.*

1. Install [Java 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
1. Download the latest gtfs-validator JAR file from our [Releases page](https://github.com/MobilityData/gtfs-validator/releases) or snapshot artifact from [GitHub Actions](https://github.com/MobilityData/gtfs-validator/actions?query=branch%3Amaster) or [Circle-CI Pipelines](https://app.circleci.com/pipelines/github/MobilityData/gtfs-validator?branch=master)

## via cli-app
### on a local GTFS zip file
Sample usage:

``` 
java -jar gtfs-validator-v2.0.jar --input relative/path/to/dataset.zip --output relative/output/path --feed_name <name_of_the_feed> --threads <number_of_threads_to_use> 
```

...which will:
 1. Search for a GTFS dataset located at `relative/path/to/dataset.zip`
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. 
 1. Export the validation report to `JSON` by default. This folder will contain a single `.json` file with information related to the validation process. The validation report will be named as `report.json`. 

### on a hosted GTFS zip file at a URL
Sample usage:

``` 
java -jar gtfs-validator-v2.0.jar --url https://url/to/dataset.zip --output relative/output/path --feed_name <name_of_the_feed> --threads <number_of_threads_to_use> --storage_directory input.zip
```

...which will:
 1. Download the GTFS feed at the URL `https://url/to/dataset.zip` and name it `input.zip`  
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. Validation results are exported to JSON by default.
Please note that since downloading will take time, we recommend validating repeatedly on a local file.

## via GitHub Actions - Run the validator on any gtfs archive available on a public url

1. [Fork this repository](/docs/REPRODUCE_ERRORS.md#fork-this-repository-detailed-instructions)
1. Open a PR on master within it
1. Edit the file `.github/workflows/end_to_end.yml` following instructions on lines 5, 43-45 and **push** on your PR branch (see detailed instructions [here](/docs/REPRODUCE_ERRORS.md##create-a-pull-request-pr))
1. Name your branch from the agency/authority/publisher of the feed you are testing

You should now see the workflow `End to end / run-on-data` start automatically in your PR checks, running the validator on the dataset you just added. The validation report is collected as a run artifact in the Actions tab of your fork repository on GitHub.

If the workflow run crashes or something doesn't look right in the validation report json file, **please see the [Contributing page](/docs/CONTRIBUTING.md) section, we may be able to help!**
