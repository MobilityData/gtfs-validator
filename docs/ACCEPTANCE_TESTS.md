# Rule acceptance tests 

## Goal
Execute the validator against all datasets (latest version hosted in the MobilityDatabase) and quantify the effect of a code change on all of them.  
The source of truth is defined as the latest stable version of the validator the ([master branch](https://github.com/MobilityData/gtfs-validator/tree/master)) of this repository. 

For the latest version of all GTFS datasets from the MobilityArchives, the validation report from both the snapshot and the source of truth are compared. An acceptance test report is generated: it quantifies for each agency/dataset the number of new errors (as defined [here](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#definitions)) in introduced.
   
## Process description

![steps](https://user-images.githubusercontent.com/35747326/120213646-6fb7bb80-c201-11eb-8520-39c88e9753d0.png)

## Github Actions
The logic for this process is defined in [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml).
This workflow:
1. packages the `comparator` module;
1. downloads the latest release of the validator;
1. defines a matrix of urls (fetched from the Mobility archives) that will be used in further validation process; 

On each of these urls:
1. the latest stable version of the validator is executed and the validation report is output as JSON (under `reference.json`);
1. the snapshot version of the validator is executed and the validation report stored (as `report.json`).

At the end of execution of the two aforementioned steps for all urls in the matrix, all validation reports are gathered in a single folder (`output`) and compared - the percentage of newly invalid datasets is output to the console.
The final acceptance test report is saved by the workflow artifact as `acceptance_report.json`. This file keeps the count of new error types introduced by the snapshot version for each agency/dataset.

Sample output:
```json
{
  "newErrors": [
    {
      "first_notice_code": {
        "affectedDatasetsCount": 2,
        "affectedDatasets": [
          "dataset-id-1",
          "dataset-id-2"
        ],
        "countPerDataset": [
          {
            "dataset-id-1": 4
          },
          {
            "dataset-id-2": 6
          }
        ]
      }
    },
    {
      "fourth_notice_code": {
        "affectedDatasetsCount": 1,
        "affectedDatasets": [
          "dataset-id-5"
        ],
        "countPerDataset": [
          {
            "dataset-id-5": 5
          }
        ]
      }
    },
    {
      "second_notice_code": {
        "affectedDatasetsCount": 1,
        "affectedDatasets": [
          "dataset-id-2"
        ],
        "countPerDataset": [
          {
            "dataset-id-2": 40
          }
        ]
      }
    },
    {
      "third_notice_code": {
        "affectedDatasetsCount": 3,
        "affectedDatasets": [
          "dataset-id-1",
          "dataset-id-3",
          "dataset-id-5"
        ],
        "countPerDataset": [
          {
            "dataset-id-1": 40
          },
          {
            "dataset-id-3": 15
          },
          {
            "dataset-id-5": 2
          }
        ]
      }
    }
  ]
}
```
