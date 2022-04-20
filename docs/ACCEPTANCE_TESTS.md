# Rule acceptance tests 

## Goal

Because GTFS data consumers and producers rely on the validator it is important to know if a pull request introduces a breaking change (i.e. the proposed validator declares existing valid datasets invalid).
If this step is skipped, newly declared invalid datasets could be rejected by GTFS data consumers (e.g. Transit App, Google Maps) which could lead to public transit systems disappearing from their interface which means that riders would no longer be able to access the trip information they are used to getting on these platforms.   

## Definitions
- **The reference validator** is defined as the latest version of the validator available on the ([master branch](https://github.com/MobilityData/gtfs-validator/tree/master)) of this repository.
- **The proposed validator** is defined as the version of the validator that results from the changes introduced in the pull request that is proposed.
- **The acceptance criteria** (mentioned in the diagram below) is defined as the impact that a pull request has on datasets: does this pull request disrupt a large quantities of datasets? If yes, the pull request should be flagged as introducing breaking changes or rejected, if no then the pull request can be safely merged to the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master).
   
## Process description

For the latest version of all GTFS datasets from the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page), the validation report from both the proposed and the reference validator are compared. An acceptance test report is generated: it quantifies for each agency/dataset the number of new errors (as defined [here](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#definitions)) that have been introduced.
![steps](https://user-images.githubusercontent.com/35747326/139877746-fd047437-38b3-44fa-aeb8-37d925c289e8.png)

## Github Actions

The logic for this process is defined in [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml).

This workflow:
1. packages the `output-comparator` module;
1. packages the proposed version of the validator;
1. downloads the version of the reference validator that is on the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master);
1. defines a matrix of urls (fetched from the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page)) that will be used in the further validation process; 

On each of these urls:
1. the reference version of the validator is executed and the validation report is output as JSON (under `reference.json`);
1. the proposed version of the validator is executed and the validation report is output as JSON (under `latest.json`).

At the end of execution of the two aforementioned steps for every url in the matrix, all the validation reports are gathered in a single folder (`output`) and compared - the percentage of newly invalid datasets is output to the console.
Two reports are saved as a workflow artifact: 
- the final acceptance test report (under `acceptance_report.json`): this file keeps the count of new error types introduced by the proposed version for each agency/dataset. 
- the corrupted sources report  (under `corrupted_sources_report.json`): this file keeps track of sources that could not be taken into account while generating the acceptance test report because of I/O errors, or missing file.  

To finish with, a comment that sums up the acceptance test result is issued on the PR.

Sample outputs:
- `acceptance_report.json`
```json
{
  "newErrors": [
    {
      "noticeCode": "first_notice_code",
      "affectedSourcesCount": 2,
      "affectedSources": [
        {
          "sourceId": "source-id-1",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-1",
          "count": 4
        },
        {
          "sourceId": "source-id-2",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-2",
          "count": 6
        }
      ]
    },
    {
      "noticeCode": "fourth_notice_code",
      "affectedSourcesCount": 1,
      "affectedSources": [
        {
          "sourceId": "source-id-5",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-5",
          "count": 5
        }
      ]
    },
    {
      "noticeCode": "second_notice_code",
      "affectedSourcesCount": 1,
      "affectedSources": [
        {
          "sourceId": "source-id-2",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-2",
          "count": 40
        }
      ]
    },
    {
      "noticeCode": "third_notice_code",
      "affectedSourcesCount": 3,
      "affectedSources": [
        {
          "sourceId": "source-id-1",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-1",
          "count": 40
        },
        {
          "sourceId": "source-id-3",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-3",
          "count": 15
        },
        {
          "sourceId": "source-id-5",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-5",
          "count": 2
        }
      ]
    }
  ]
}
```

- ` corrupted_sources_report.json`
```json
{
  "corruptedSources": [
    "source-id-1",
    "source-id-2"
  ],
  "sourceIdCount": 1245,
  "status": "valid",
  "corruptedSourcesCount": 2,
  "maxPercentageCorruptedSources": 2
} 
```
Where each source id value come from the MobilityDatabase: they are a unique [property](http://old.mobilitydatabase.org/wiki/Property:P33) used to identify each source of data.

The source id can be used to find all datasets versions of a source on the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page) for the sakes of debugging or exploration.

## Instructions to run the pipeline

1. Provide code changes by creating a new PR on the [GitHub repository](https://github.com/MobilityData/gtfs-validator);
2. The acceptance test pipeline will run each time code is pushed on the newly created branch; **except if** the keyword `[acceptance test skip]` is included in the commit message.

## Instructions to verify the execution of the pipeline

1. Download all validation reports from the artifact listed for the specific GitHub run;
2. One can verify that the count of validation report (1 per source) matches the number of sources announced by the GitHub PR comment
3. Select a sample of validation reports and compare them manually. MobilityData uses an internal tool to do so. We will open source it in the future.
