# Instructions to run the application locally
*If you're running a [`v1.x` release JAR file](https://github.com/MobilityData/gtfs-validator/releases) you'll need Java 11, and can follow [these instructions](https://github.com/MobilityData/gtfs-validator/tree/v1.4.0#via-java-on-your-local-computer). The below instructions are for the master branch, which will be v2.0.*

1. Install [Java 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html).
1. Download the latest gtfs-validator JAR file from our [Releases page](https://github.com/MobilityData/gtfs-validator/releases) or snapshot artifact from [GitHub Actions](https://github.com/MobilityData/gtfs-validator/actions?query=branch%3Amaster).

## via cli-app
**Full list of command line parameters available**

| Short name | Long name                     | required?              | Description                                                                                                                                                                                                                                                   |
|------------|-------------------------------| ---------------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-i`       | `--input`                     | Conditionally required | The path to the GTFS file (e.g., `/myDirectory/gtfs.zip`). Required if `-u` or `--url` is not provided.                                                                                                                                                       |
| `-u`       | `--url`                       | Conditionally Required | `--url` or `-u`: the fully qualified URL to the GTFS file (e.g., `https://www.abc.com/gtfs.zip`). Required if `-i` or `--input` is not provided.                                                                                                              |
| `-o`       | `--output`                    | Required               | Path to where the validation report will be stored (e.g., `output`)                                                                                                                                                                                           |
| `-s`       | `--storage_directory`         | Optional               | Target path where to store the GTFS archive. Downloaded from network (if not provided, the ZIP will be stored in memory).                                                                                                                                     |
| `-c`       | `--country_code`              | Optional               | Country code of the feed, e.g., `nl`. It must be a two-letter country code (ISO 3166-1 alpha-2). (e.g., `ca`, `us`). It can be either lower or upper case (e.g. `FR` or `GP`). If the country code is provided, phone numbers will be validated based on it.  |
| `-h`       | `--help`                      | Optional               | Print help menu.                                                                                                                                                                                                                                              |
| `-t`       | `--threads`                   | Optional               | Number of threads to be used by Java to run the validator.                                                                                                                                                                                                    |
| `-v`       | `--validation_report_name`    | Optional               | Name of the validation report (including `.json` extension).                                                                                                                                                                                                  |
| `-r`       | `--html_report_name`          | Optional               | Name of the HTML validation report (including `.html` extension).                                                                                                                                                                                             |
| `-e`       | `--system_errors_report_name` | Optional               | Name of the system errors report (including `.json` extension).                                                                                                                                                                                               |
| `-n`       | `--export_notices_schema`     | Optional               | Export notice schema as a json file.                                                                                                                                                                                                                          |
| `-p`       | `--pretty`                    | Optional               | Pretty JSON validation report. If specified, the JSON validation report will be printed using JSON Pretty print. This does not impact data parsing.                                                                                                           |
| `-d`       | `--date`                      | Optional               | The date used to validate the feed for time-based rules, e.g feed_expiration_30_days, in ISO_LOCAL_DATE format like '2001-01-30'. By default, the current date is used.                                                                                       |
| `-svu`     | `--skip_validator_update`     | Optional               | Skip GTFS version validation update check. If specified, the GTFS version validation will be skipped. By default, the GTFS version validation will be performed.                                                                                              |                                              

⚠️ Note that exactly one of the following options must be provided: `--url` or `--input`.

⚠️ Note that `--storage_directory` must not be provided if `--url` is not provided.

### on a local GTFS zip file
Sample usage:

``` 
java -jar gtfs-validator-v2.0.jar -i relative/path/to/dataset.zip -o relative/output/path -c ca -t <number_of_threads_to_use> 
```

...which will:
 1. Search for a GTFS dataset located at `relative/path/to/dataset.zip`
 2. Validate the GTFS data and output the results to the directory located at `relative/output/path`. 
 3. Export validation and system errors reports to JSON by default. This folder will contain the `.json` file with information related to the validation process. The validation report will (by default) be named as `report.json` and the system errors report can be found under the name of `system_errors.json`.
 4. Export the HTML validation report, which is a standalone HTML file that can be opened in a web browser. This folder will contain the `.html` file with information related to the validation process. The HTML validation report will (by default) be named as `report.html`.
 
  ⚠️ Note that the name of the reports can be overridden by providing values to the respective CLI arguments mentioned above. These **should** include the correct extension, either `.json` or `.html`.

### on a hosted GTFS zip file at a URL
Sample usage:

``` 
java -jar gtfs-validator-v2.0.jar -u https://url/to/dataset.zip -o relative/output/path -c ca -t <number_of_threads_to_use> --storage_directory input.zip
```


...which will:
 1. Download the GTFS feed at the URL `https://url/to/dataset.zip` and name it `input.zip`  
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. Validation results are exported to JSON by default.
Please note that since downloading will take time, we recommend validating repeatedly on a local file.

## via GitHub Actions - Run the validator on any gtfs archive available on a public url

1. [Fork this repository](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo)
1. Open a PR on master within it
1. Edit the file `.github/workflows/end_to_end.yml` following instructions on lines 5, 43-45 and **push** on your PR branch (see detailed instructions [here](/docs/REPRODUCE_ERRORS.md))
1. Name your branch from the agency/authority/publisher of the feed you are testing

You should now see the workflow `End to end / run-on-data` start automatically in your PR checks, running the validator on the dataset you just added. The validation report is collected as a run artifact in the Actions tab of your fork repository on GitHub.

If the workflow run crashes or something doesn't look right in the validation report json file, **please see the [guide to reproduce](/docs/REPRODUCE_ERRORS.md) section.**

## Export notice schema

Sample usage:

### Without file validation
``` 
java -jar gtfs-validator-SNAPSHOT.jar --export_notices_schema
```

...which will:
 1. Generate and export all validation notices as a json file. 
 
### With file validation
``` 
java -jar gtfs-validator-SNAPSHOT.jar --export_notices_schema --url https://url/to/dataset.zip --output relative/output/path --country_code <country_code> --threads <number_of_threads_to_use> --storage_directory input.zip 
```

...which will:
 1. Generate and export all validation notices as a json file. 
 1. Download the GTFS feed at the URL `https://url/to/dataset.zip` and name it `input.zip`  
 1. Validate the GTFS data and output the results to the directory located at `relative/output/path`. Validation results are exported to JSON by default.
Please note that since downloading will take time, we recommend validating repeatedly on a local file.
