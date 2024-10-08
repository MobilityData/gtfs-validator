# Rule acceptance tests 

## Goal

Because GTFS data consumers and producers rely on the validator it is important to know if a pull request introduces a breaking change (i.e. the proposed validator declares existing valid datasets invalid).
If this step is skipped, newly declared invalid datasets could be rejected by GTFS data consumers (e.g. Transit App, Google Maps) which could lead to public transit systems disappearing from their interface which means that riders would no longer be able to access the trip information they are used to getting on these platforms.   

## Definitions
- **The reference validator** is defined as the latest version of the validator available on the ([master branch](https://github.com/MobilityData/gtfs-validator/tree/master)) of this repository.
- **The proposed validator** is defined as the version of the validator that results from the changes introduced in the pull request that is proposed.
- **The acceptance criteria** (mentioned in the diagram below) is defined as the impact that a pull request has on datasets: does this pull request disrupt a large quantities of datasets? If yes, the pull request should be flagged as introducing breaking changes or rejected, if no then the pull request can be safely merged to the [`master` branch](https://github.com/MobilityData/gtfs-validator/tree/master).
   
## Process description

For the latest version of all GTFS datasets from the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page), the validation report from both the proposed and the reference validator are compared. An acceptance test report is generated: it quantifies for each agency/dataset the number of new notice (as defined [here](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#definitions)) that have been introduced.
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

At the end of execution of the two aforementioned steps for every url in the matrix, all the validation
reports are gathered in a single folder (`output`) and compared - the percentage of newly invalid datasets
is output to the console.  The final acceptance test report is output at `acceptance_report.json`.
It includes a summary of both new notice types and dropped notice types.  It also contains a list of
"corrupted" sources: sources that could not be taken into account while generating the acceptance test 
report because of I/O errors, or missing file.  

To finish with, a comment that sums up the acceptance test result is issued on the PR.

Example output:
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
          "noticeCount": 4
        },
        {
          "sourceId": "source-id-2",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-2",
          "noticeCount": 6
        }
      ]
    },
    {
      "noticeCode": "second_notice_code",
      "affectedSourcesCount": 1,
      "affectedSources": [
        {
          "sourceId": "source-id-5",
          "sourceUrl": "url to the latest version of the dataset issued by source-id-5",
          "noticeCount": 5
        }
      ]
    },
  ],
  "droppedErrors": [
    # Same schema as `newErrors`
  "newWarnings": [
    # Same schema as `newErrors`
  "droppedWarnings": [
    # Same schema as `newErrors`
  "newInfo": [
    # Same schema as `newErrors`
  "droppedInfo": [
    # Same schema as `newErrors`
  ],
  "corruptedSources": {
    "corruptedSources": [
      "source-id-1",
      "source-id-2"
    ],
    "sourceIdCount": 1245,
    "aboveThreshold": false,
    "corruptedSourcesCount": 2,
    "maxPercentageCorruptedSources": 2
  }
}
```

Where each source id value come from the MobilityDatabase: they are a unique [property](http://old.mobilitydatabase.org/wiki/Property:P33) used to identify each source of data.

The source id can be used to find all datasets versions of a source on the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page) for the sakes of debugging or exploration.

## What do we do with the results?
We follow this process:

<img src="/docs/Acceptance-test-process.jpg" width="750">

## Performance metrics within the acceptance reports

There are two main metrics added to the acceptance report comment at the PR level, _Validation Time_ and _Memory Consumption_.
The performance metrics are **not a blocker** as performance might vary due to external factors including GitHub infrastructure performance.
However, large jumps in performance values should be investigated before approving a PR.

### Validation Time
The validation time consists in general metrics like average, median, standard deviation, minimums and maximums.
This metrics can be affected by addition of new validators than introduce a penalty in processing time.

### Memory Consumption
The memory consumption section contains three tables.
- The first, list the first 25 datasets that the difference increased memory comparing with the main branch.
- The second, list the first 25 datasets that the difference decreased memory comparing with the main branch.
- The third, list(not always visible) the first 25 datasets that were not available for comparison as the main branch didn't contain the memory usage information.

Memory usage is collected in critical points and persists in the JSON report. The added snapshot points are:
- _GtfsFeedLoader.loadTables_: This is taken after the validator loads all files.
- _GtfsFeedLoader.executeMultiFileValidators_: This is taken after the validator executed all multi-file validators
- _org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader.loadAndValidate_: This is taken for the complete load and validation method.
- _ValidationRunner.run_: This is taken for the complete run of the validator, excluding report generation

## Instructions to run the pipeline

1. Provide code changes by creating a new PR on the [GitHub repository](https://github.com/MobilityData/gtfs-validator);
2. The acceptance test pipeline will run each time code is pushed on the newly created branch; **except if** the keyword `[acceptance test skip]` is included in the commit message.

## Instructions to verify the execution of the pipeline

1. Download all validation reports from the artifact listed for the specific GitHub run;
2. One can verify that the count of validation report (1 per source) matches the number of sources announced by the GitHub PR comment
3. Select a sample of validation reports and compare them manually. MobilityData uses an internal tool to do so. We will open source it in the future.
