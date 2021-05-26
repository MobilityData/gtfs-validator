# Integration tests 

## Goal
Execute the validator against all datasets and verify that a new implementation does not make "a lot" of GTFS datasets suddenly invalid. 
The source of truth is defined as the last release of the validator (`v2.0` as of now). 

For the latest version of all GTFS dataset from the MobilityArchives, the validation report from the release candidate and the source of truth as compared: a new implementation is defined as valid if the percentage of new invalid datasets does not exceed N/100 (**N to be defined**).
   
## Process description

![steps](https://user-images.githubusercontent.com/35747326/119565069-7c5a9080-bd77-11eb-86c9-3b02b0acc264.png)

## Github Actions
The pipeline for this process is defined in [`integration_test.yaml`](../.github/workflows/integration_test.yml).
This workflow:
1. packages the `comparator` module;
1. downloads the latest release of the validator;
1. defines a matrix of 256 urls (fetched from the Mobility archives) that will be used in further validation process; 

On each of these urls:
1. the latest release of the validator  is executed and the validation report stored (as `report.json`);
1. the release candidate of the validator is executed and the validation report stored (as `latest.json`).

Thanks to the matrix definition, these two stepss are executed in parallel for each url found in the matrix. 
At the end of execution of the two aforementioned steps, all validation reports are gathered in a single folder (`reports_all`) and compared - the percentage of new invalid datasets is returned as a console output.
The final integration test report is saved in the workflow artefacts under `integration_report.json`. This file keeps the count of new invalid datasets for each url that was treated. 

⚠️ Note that this workflow is executed on a branch if the commit message contains the terms "integration test".
