# Rule acceptance tests 

## Goal

_Definitions_ 
- **The reference validator** is defined as the latest version of the validator available on the ([master branch](https://github.com/MobilityData/gtfs-validator/tree/master)) of this repository. 
- **The proposed validator** is defined as the version of the validator that results from the changes introduced in the pull request that is proposed.
- **The acceptance criteria** (mentioned in the diagram below) is defined as the impact that a pull request has on datasets: does this pull request disrupt a large quantities of datasets? If yes, the pull request should be flagged as introducing breaking changes or rejected, if no then the pull request can be safely merged to the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master).       

Execute the proposed validator against all datasets (latest version hosted in the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page)) and quantify the effect of a code change on all of them.  

For the latest version of all GTFS datasets from the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page), the validation report from both the proposed and the reference are compared. An acceptance test report is generated: it quantifies for each agency/dataset the number of new errors (as defined [here](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#definitions)) that have been introduced.
   
## Process description

![steps](https://user-images.githubusercontent.com/35747326/139877746-fd047437-38b3-44fa-aeb8-37d925c289e8.png)

## Github Actions

The logic for this process is defined in [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml).

This workflow:
1. packages the `comparator` module;
1. packages the proposed version of the validator;
1. downloads the version of the validator that is on the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master);
1. defines a matrix of urls (fetched from the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page)) that will be used in further validation process; 

On each of these urls:
1. the reference version of the validator is executed and the validation report is output as JSON (under `reference.json`);
1. the proposed version of the validator is executed and the validation report is output as JSON (under `latest.json`).

At the end of execution of the two aforementioned steps for all urls in the matrix, all validation reports are gathered in a single folder (`output`) and compared - the percentage of newly invalid datasets is output to the console.
The final acceptance test report is saved by as a workflow artifact (under `acceptance_report.json`). This file keeps the count of new error types introduced by the proposed version for each agency/dataset.

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
