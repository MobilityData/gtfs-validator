# Rule acceptance tests 

_Definitions_
- **The reference validator** is defined as the latest version of the validator available on the ([master branch](https://github.com/MobilityData/gtfs-validator/tree/master)) of this repository.
- **The proposed validator** is defined as the version of the validator that results from the changes introduced in the pull request that is proposed.
- **The acceptance criteria** (mentioned in the diagram below) is defined as the impact that a pull request has on datasets: does this pull request disrupt a large quantities of datasets? If yes, the pull request should be flagged as introducing breaking changes or rejected, if no then the pull request can be safely merged to the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master).

## Goal

Because GTFS data consumers and producers rely on the validator it is important to know if a pull request introduces a breaking change (i.e. the proposed validator declares existing valid datasets invalid).
If this step is skipped, newly declared invalid datasets could be rejected by GTFS data consumers (e.g. Transit App, Google Maps) which could lead to public transit systems disappearing from their interface which means that riders would no longer be able to access the trip information they are used to getting on these platforms.   
   
## Process description

For the latest version of all GTFS datasets from the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page), the validation report from both the proposed and the reference validator are compared. An acceptance test report is generated: it quantifies for each agency/dataset the number of new errors (as defined [here](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#definitions)) that have been introduced.
![steps](https://user-images.githubusercontent.com/35747326/139877746-fd047437-38b3-44fa-aeb8-37d925c289e8.png)

## Github Actions

The logic for this process is defined in [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml).

This workflow:
1. packages the `output-comparator` module;
1. packages the proposed version of the validator;
1. downloads the version of the reference validator that is on the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master);
1. defines a matrix of urls (fetched from the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page)) that will be used in the further validation process; 

On each of these urls:
1. the reference version of the validator is executed and the validation report is output as JSON (under `reference.json`);
1. the proposed version of the validator is executed and the validation report is output as JSON (under `latest.json`).

At the end of execution of the two aforementioned steps for every url in the matrix, all the validation reports are gathered in a single folder (`output`) and compared - the percentage of newly invalid datasets is output to the console.
The final acceptance test report is saved by as a workflow artifact (under `acceptance_report.json`). This file keeps the count of new error types introduced by the proposed version for each agency/dataset.

Sample output:
```json
{
  "newErrors": [
    {
      "first_notice_code": {
        "affectedSourcesCount": 2,
        "affectedSources": [
          {
            "source-id-1": "url to the latest version of the dataset issued by source-id-1"
          },
          {
            "source-id-2": "url to the latest version of the dataset issued by source-id-2"
          }
        ],
        "countPerSource": [
          {
            "source-id-1": 4
          },
          {
            "source-id-2": 6
          }
        ]
      }
    },
    {
      "fourth_notice_code": {
        "affectedSourcesCount": 1,
        "affectedSources": [
          {
            "source-id-5": "url to the latest version of the dataset issued by source-id-5"
          }
        ],
        "countPerSource": [
          {
            "source-id-5": 5
          }
        ]
      }
    },
    {
      "second_notice_code": {
        "affectedSourcesCount": 1,
        "affectedSources": [
          {
            "source-id-2": "url to the latest version of the dataset issued by source-id-2"
          }
        ],
        "countPerSource": [
          {
            "source-id-2": 40
          }
        ]
      }
    },
    {
      "third_notice_code": {
        "affectedSourcesCount": 3,
        "affectedSources": [
          {
            "source-id-1": "url to the latest version of the dataset issued by source-id-1"
          },
          {
            "source-id-3": "url to the latest version of the dataset issued by source-id-3"
          },
          {
            "source-id-5": "url to the latest version of the dataset issued by source-id-5"
          }
        ],
        "countPerSource": [
          {
            "source-id-1": 40
          },
          {
            "source-id-3": 15
          },
          {
            "source-id-5": 2
          }
        ]
      }
    }
  ]
}
```

Where each source id value come from the MobilityDatabase: they are a unique [property](http://mobilitydatabase.org/wiki/Property:P33) used to identify each source of data.

The source id can be used to find all datasets versions of a source on the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page) for the sakes of debugging or exploration.
