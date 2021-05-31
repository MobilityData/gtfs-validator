# Integration tests 

## Goal
Execute the validator against all datasets and verify that a new rule implementation does not make "a lot" of GTFS datasets suddenly invalid. 
The source of truth is defined as the last release of the validator (`v2.0` as of now). 

For the latest version of all GTFS datasets from the MobilityArchives, the validation report from both the snapshot and the source of truth are compared: a new rule implementation is defined as valid if the percentage of newly invalid datasets does not exceed N/100 (**N to be defined**).
   
## Process description

![steps](https://user-images.githubusercontent.com/35747326/120213646-6fb7bb80-c201-11eb-8520-39c88e9753d0.png)

## Github Actions
The logic for this process is defined in [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml).
This workflow:
1. packages the `comparator` module;
1. downloads the latest release of the validator;
1. defines a matrix of urls (fetched from the Mobility archives) that will be used in further validation process; 

On each of these urls:
1. the latest release of the validator is executed and the validation report is output in JSON (`report.json`);
1. the snapshot version of the validator is executed and the validation report stored (as `latest.json`).

GitHub uses the defined matrix to execute these two steps in parallel for each url in the matrix. 
At the end of execution of the two aforementioned steps for all urls in the matrix, all validation reports are gathered in a single folder (`reports_all`) and compared - the percentage of newly invalid datasets is output to the console.
The final acceptance test report is saved by the workflow artifact as `acceptance_report.json`. This file keeps the count of new errors introduced by the snapshot version for each agency.

Sample example of said acceptance report:
```json
{
  "transperth": 1,
  "octa": 0,
  "thb": 0
}
```

⚠️ Note that this workflow is executed only if package `org.mobilitydata.gtfsvalidator.validator` has changed.
