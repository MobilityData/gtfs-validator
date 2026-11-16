# Proposal: faster, cheaper PR checks

## Problem

The acceptance test and end-to-end workflows validate real GTFS feeds on paid `ubuntu-latest-4-cores` machines. The runs are long and expensive. When to trigger them stays as it is; the goal is to make each run take less time and cost less.

Example: on [MobilityData/gtfs-validator PR 2256](https://github.com/MobilityData/gtfs-validator/pull/2256), acceptance ran 60 validation jobs, each feed validated with both the master and PR versions of the validator, and end-to-end ran 23 more. Almost all of each job's time was spent validating feeds; setup and downloads took seconds. Job times ranged from about 2 minutes to about 20 minutes, and the whole run waits for the slowest job.

## Decision 1: group feeds by size

| Situation | Before | After |
|---|---|---|
| How feeds are split across jobs | Without regard to size, so one job can get several large feeds | Spread by size, so each job gets a similar amount of work |
| Total run time | Set by the slowest job (~20 min in PR 2256) | Closer to the average job (~5 to 7 min) |
| Validation results | Unchanged | Unchanged |

Tradeoff: needs feed sizes when the job list is built, either from Mobility Database metadata or a quick size check before grouping.

## Decision 2: use free GitHub runners as much as possible, based on feed size

| Situation | Before | After |
|---|---|---|
| Jobs with small feeds | Paid `ubuntu-latest-4-cores` | Free `ubuntu-24.04` |
| Jobs with large feeds | Paid `ubuntu-latest-4-cores` | Paid `ubuntu-latest-4-cores` (unchanged) |
| End-to-end `fetch-urls` job (only builds the feed list) | Paid `ubuntu-latest-4-cores` | Free `ubuntu-24.04` |
| Validation results | Unchanged | Unchanged |

This builds on decision 1: once feeds are grouped by size, each group can be sent to the cheapest runner that can handle it. The job matrix carries the runner name per group, and `runs-on` reads it.

Settled:

- **Runner image.** Free runners use `ubuntu-24.04`, not `ubuntu-latest`, to avoid surprises from an image change; move to `-latest` once everything works. The paid 4-core runner is already set to 24.04 in the organization settings.
- **Size threshold.** 50 MB zipped, adjustable in the workflows. Measured on 2026-11-16: 1,004 feeds, about 10.6 GB in total; 43 feeds are above 50 MB and make up 69% of the bytes; the largest is about 2 GB.
- **Memory settings.** Unchanged (`-Xms8G -Xmx12G`), so out-of-memory behavior stays the same.

Still to confirm with real runs: for public repositories, GitHub lists the free runner as 4 vCPU and 16 GB RAM, apparently the same CPU and memory as the paid 4-core runner (which has more disk). If that holds, the threshold could go up and more feeds could move to free runners. How many free-runner jobs can run at once also depends on the organization's plan.

## Decision 3: reuse master's results

| Situation | Before | After |
|---|---|---|
| First acceptance run on a PR | Each feed validated with the master and PR versions | Same; master's report for each feed is saved |
| Later pushes to the same PR, master unchanged | Master version re-validates every feed | Master's saved report is reused when master's commit and the feed file are unchanged; only the PR version runs |
| Validation work on later pushes | 2 runs per feed | Up to half |

The saved report is keyed on master's commit and a hash of the feed file, so a new master commit or a republished feed triggers a fresh master run. All of master's reports for a master commit are kept in one GitHub Actions cache entry (about 50 MB at most, judging by the size of a full run's reports), so reuse doesn't depend on which job a feed lands in. GitHub only shares a PR's cache with later runs of that same PR.

Tradeoff: acceptance also compares performance between the two versions. A reused master report was timed on a different machine, so that comparison becomes noisier.

## Decision 4: remove the end-to-end check from PRs

| Situation | Before | After |
|---|---|---|
| PR that changes validator code | Acceptance validates all feeds with the PR version, and end-to-end validates a sample of them again | Acceptance only |
| Push to master | End-to-end runs | End-to-end runs (unchanged for now) |
| Large-machine jobs per PR | Acceptance jobs + ~23 end-to-end jobs | Acceptance jobs only |

End-to-end's sample is already covered by acceptance's full set, so PRs lose no feed coverage. On master pushes, end-to-end keeps running, now with the same size-based runner choice as acceptance.

## Estimated savings

Rough estimate made on 2026-11-13, before any change, to compare against real runs once the decisions are in place.

Baseline: PR checks take 30 to 45 minutes, on paid `ubuntu-latest-4-cores` machines.

Baseline cost: gtfs-validator's "Actions Linux 4-core" usage, from the MobilityData IO organization's metered usage page (price $0.012 per minute):

| Period | Minutes | Gross amount | Billed amount |
|---|---|---|---|
| 2026 to date (Jan 1 to Nov 13) | 111,291 | $1,335.49 | $1,317.47 |
| October 2026 | 18,738 | $224.86 | $211.73 |
| November 2026 (Nov 1 to 13) | 16,817 | $201.80 | $183.78 |

Basis: one acceptance run (PR 2256, 60 validation jobs), of which 8 job timings were sampled; validation jobs ranged from about 2 to 20 minutes.

| Decision | Estimated effect on time | Estimated effect on cost |
|---|---|---|
| 1. Group feeds by size | Biggest time gain: the slowest job (~20 min) should come down near the average job (~5 to 7 min) | Small; same total work, spread more evenly |
| 2. Free runners by feed size | Probably none; could add waiting time if the free runners can't run all jobs at once under MobilityData's plan | Large; free runners cost nothing for public repos, so paid minutes could drop to a small fraction if most feeds move |
| 3. Reuse master's results | Validation roughly halved on later pushes to the same PR | Roughly halves validation minutes on later pushes |
| 4. Remove end-to-end | Little; it runs alongside acceptance | About 23 fewer paid jobs per PR, roughly 30 to 40 paid minutes |

Overall estimate:

- **Time:** about 10 to 15 minutes on a PR's first run and about 6 to 10 minutes on later pushes, down from 30 to 45.
- **Cost:** around half per PR from decisions 1, 3 and 4 alone; much lower if most feeds can move to free runners.

Actual results, measured on [MobilityData/gtfs-validator PR 2259](https://github.com/MobilityData/gtfs-validator/pull/2259) on 2026-11-16. Paid minutes are billed per job, rounded up to the minute, from the GitHub Actions jobs API. The before figures come from PR 2256: 234 paid minutes for acceptance plus 26 for end-to-end, 260 in total.

| Measure | Estimated | Actual |
|---|---|---|
| First run on a PR (master's results not cached yet) | 10 to 15 min | About 17 min; mdb-2014 (about 2 GB) alone took about 15 min, all other jobs finished within 5 to 7 min |
| Later runs on the same PR (master unchanged) | 6 to 10 min | 9 to 12 min; master's results reused for 1,002 of 1,004 feeds, mdb-2014 down to 8 min 24 s |
| Paid runner minutes per PR | About half of today, lower with free runners | 95 on a first run (down 63% from 260); 54 to 57 on later runs (down about 79%); 23 of 60 validation jobs on paid runners, plus 99 to 145 free-runner minutes |
| Linux 4-core billed per month (October 2026: $211.73) | About half, lower with free runners | Not measured yet; check the December 2026 usage page |

Issue found while testing: re-running a run made the report merge also pick up the previous attempt's merged reports, corrupting files at random. This bug predates the change and is fixed in the same PR.

## Rejected

These are reasonable ideas that fall outside the current focus on run time and cost. Triggers stay as they are.

### Run only when relevant files change

Both workflows use `paths-ignore` ("run unless every changed file is on this list"). Entries like `docker.yml` only match a file at the repo root, not `.github/workflows/docker.yml`, so workflow-only PRs still start the full run. Any folder nobody thought to list, such as `.github/actions/`, also starts it.

| Situation | Before | After |
|---|---|---|
| PR changes only workflows, docs, `web/` or other unrelated files | Full acceptance and end-to-end runs | Neither runs |
| PR changes validator code, build files or the feed scripts | Full runs | Full runs (unchanged) |
| New folder added to the repo | Starts the full runs by default | Ignored until added to the list |

The change would replace `paths-ignore` with a `paths` allowlist in `acceptance_test.yml` and `end_to_end.yml`:

```yaml
paths:
  - 'core/**'
  - 'main/**'
  - 'model/**'
  - 'processor/**'
  - 'cli/**'
  - 'output-comparator/**'      # acceptance only
  - 'build.gradle'
  - 'settings.gradle'
  - 'gradle/**'
  - 'scripts/*.sh'
  - 'scripts/mobility-database-harvester/**'
  - '.github/workflows/acceptance_test.yml'   # or end_to_end.yml
  - '!**/src/test/**'
  - '!**.md'
```

Tradeoff: the list must be updated when a new module that affects validation is added. The only checks master requires before merging are `test`, `package` and `doc`, so skipped workflows would never leave a PR stuck waiting.

### Skip the acceptance builds when the run is skipped

| Situation | Before | After |
|---|---|---|
| Draft PR | Both versions of the validator (PR and master) are built (~1 min each), then thrown away | Neither is built |
| Last commit contains `[acceptance test skip]` | Both versions are built, then thrown away | Neither is built |
| Normal PR, ready for review | Both versions are built right away | Both are built ~10 s later, after the draft check (`pre_ci`); total time unchanged |

The change would add two lines to `pack-snapshot` and `pack-master` in `acceptance_test.yml`:

```yaml
needs: pre_ci
if: "!contains(needs.pre_ci.outputs.commit_message, '[acceptance test skip]')"
```

Build coverage would not be lost: the required `package` check in `test_pack_doc.yml` builds the same validator file on every PR, drafts included.

### Cancel an end-to-end run when a newer commit replaces it

| Situation | Before | After |
|---|---|---|
| Commit A, then commit B pushed to the same PR before A's run finishes | Both runs finish, so ~23 feed jobs on large machines run twice and A's result is discarded | A's run is cancelled when B's starts |
| Push to master | Every push runs to the end | Every push still runs to the end |

The change would add to `end_to_end.yml`:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.head_ref || github.run_id }}
  cancel-in-progress: true
```

Decision 4 removes end-to-end from PRs, which makes this moot.
