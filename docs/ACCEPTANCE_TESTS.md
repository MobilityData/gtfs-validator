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

<img src="/docs/images/Acceptance-test-process.jpg" width="750">

## Performance metrics within the acceptance reports

The _Performance Assessment_ section of the PR comment compares the reference and proposed validators on every dataset. It is **not a blocker**, since timings vary with the GitHub runners, but large or unexpected changes should be investigated before approving a PR.

### Measurements

Each validation records its measurements in `report.json`, under `summary.performanceMetrics`. A validation has four phases: `input` (reading or downloading the feed), `loadTables`, `multiFileValidators` and `feedMetadata`. Report generation is not measured.

| Metric | What it measures | Flag threshold |
|---|---|---|
| Wall time | Elapsed time of all phases except `input`, so that download time does not count. | 5% |
| CPU time | CPU time of the validation thread and of the tasks it runs in the loader's thread pool. Excludes the garbage collector and JIT threads. | 5% |
| Allocated memory | Bytes allocated by those same threads. Nearly identical between runs of the same code, so small changes are real. | 1% |
| Peak heap after GC | Highest heap in use right after a garbage collection run by the JVM on its own. Empty when none ran, which is common on small feeds since CI starts with an 8 GB heap. | 5% |
| Peak live heap | Highest heap in use after a full garbage collection forced at the end of each phase. Measured on every feed. Enabled in CI with `-Dgtfsvalidator.performance.liveHeap=true`. | 2% |

When either validator does not report these metrics, only the validation time is compared.

The _Out of Memory Check_ lists the datasets whose validation ran out of memory (12 GB heap in CI). They are left out of every other check and statistic.

### How the comparison works

- Each metric is compared per dataset as latest / reference, so that every dataset weighs the same.
- Datasets are grouped by reference validation time: < 2 s, 2 to 20 s, 20 to 120 s and >= 120 s.
- For each group, the comment shows the median and the geometric mean of the changes, with a 95% bootstrap confidence interval.
- A change is flagged as _Regression_ or _Improvement_ only when the whole interval is beyond the metric's threshold and the group has at least 5 datasets.
- _Largest wall time changes per feed_ lists the 10 datasets of at least 2 s with the largest wall time change, with all their metrics.

### How to read it

- Start with the flagged lines, then check the group table: a group's median shows what a typical feed does, while the geometric mean can be pulled by a few feeds.
- Memory: _Peak live heap_ is what the validator keeps, which decides whether a feed fits in memory. _Allocated memory_ is what it creates, which costs garbage collection and CPU time. A change can lower one and raise the other.
- Memory against CPU. These often move in opposite directions:
  - Lower live heap, higher CPU and allocation: less data is kept and rows are rebuilt on each read, for example a table stored as compact columns.
  - Higher live heap, lower CPU: results are kept to avoid recomputing them, for example a per-trip cache.
  - _CPU time_ excludes the garbage collector, so a smaller heap's savings show in wall time, not CPU.

  A memory fix is expected to cost some CPU, and a speed fix some memory.
- Timings: a single feed's time can vary by more than 20% between runs. Trust a timing change when it shows across a group, or when CPU time or allocated memory move the same way.
- Expect changes in loading or validators to show mostly in the 2 to 20 s and 20 to 120 s groups. The >= 120 s group has few datasets and is rarely flagged.

## Instructions to run the pipeline

1. Provide code changes by creating a new PR on the [GitHub repository](https://github.com/MobilityData/gtfs-validator);
2. The acceptance test pipeline will run each time code is pushed on the newly created branch; **except if** the keyword `[acceptance test skip]` is included in the commit message.

## Instructions to verify the execution of the pipeline

1. Download all validation reports from the artifact listed for the specific GitHub run;
2. One can verify that the count of validation report (1 per source) matches the number of sources announced by the GitHub PR comment
3. Select a sample of validation reports and compare them manually. MobilityData uses an internal tool to do so. We will open source it in the future.
