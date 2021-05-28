# Integration tests 

## Goal
Execute the validator against all datasets and verify that a new rule implementation does not make "a lot" of GTFS datasets suddenly invalid. 
The source of truth is defined as the last release of the validator (`v2.0` as of now). 

For the latest version of all GTFS datasets from the MobilityArchives, the validation report from the release candidate and the source of truth are compared: a new rule implementation is defined as valid if the percentage of newly invalid datasets does not exceed N/100 (**N to be defined**).
   
## Process description

![steps](https://user-images.githubusercontent.com/35747326/119565069-7c5a9080-bd77-11eb-86c9-3b02b0acc264.png)

## Github Actions
The logic for this process is defined in [`integration_test.yml`](../.github/workflows/integration_test.yml).
This workflow:
1. packages the `comparator` module;
1. downloads the latest release of the validator;
1. defines a matrix of 256 urls (fetched from the Mobility archives) that will be used in further validation process; 

On each of these urls:
1. the latest release of the validator is executed and the validation report is output in JSON (`report.json`);
1. the release candidate of the validator is executed and the validation report stored (as `latest.json`).

GitHub uses the defined matrix to execute these two steps in parallel for each url in the matrix. 
At the end of execution of the two aforementioned steps for all urls in the matrix, all validation reports are gathered in a single folder (`reports_all`) and compared - the percentage of newly invalid datasets is output to the console.
The final integration test report is saved by the workflow artifact as `integration_report.json`. This file keeps the count of new invalid datasets. 

⚠️ Note that this workflow is executed on a branch if the commit message contains the terms "integration test".
