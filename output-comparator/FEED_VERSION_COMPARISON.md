# Feed version comparison API

The draft `feedversion` API compares validation counts for two publications of one feed under the
same validation method. It is proposed in [#2205](https://github.com/MobilityData/gtfs-validator/pull/2205)
for the Change Tracker [#2152](https://github.com/MobilityData/gtfs-validator/issues/2152).

## Load two captured runs

For each run, supply a `VerifiedReportLoader.InputSpec` containing the stable source identity,
observation time, provenance reference, validator version, reference date, country, complete
result-affecting option map, exit code, and six `FileRef` values:

1. GTFS ZIP;
2. validator JAR;
3. `report.json`;
4. `system_errors.json`;
5. captured stdout;
6. captured stderr.

Each `FileRef` pairs a local path with an expected SHA256 recorded by the trusted capture process.
Recomputing a digest from a file being loaded establishes its current digest but does not authenticate
its origin or link it to a particular invocation. The caller must establish those facts independently.
`sourceId` identifies the feed, rather than a signed download URL or a single publication.

```java
VerifiedReportLoader loader = new VerifiedReportLoader();
VerifiedReportLoader.LoadedRun before = loader.load(beforeInput);
VerifiedReportLoader.LoadedRun after = loader.load(afterInput);
FeedVersionDiff diff = new FeedVersionReportComparator().compare(
    before.report(), before.metadata(), after.report(), after.metadata());
String json = diff.toJson();
```

`beforeInput` and `afterInput` are caller-provided `InputSpec` instances. The
[executable contract examples](src/test/java/org/mobilitydata/gtfsvalidator/outputcomparator/feedversion/FeedVersionIntegrationContractTest.java)
construct them from small synthetic files. Those tests use a placeholder JAR and staged reports to
exercise loading and comparison; they do not run GTFS validation.

The loader checks hashes, strict raw JSON, method summary, counts and samples, system errors, exit
status, and captured logs. Missing or invalid files cause an exception before comparison. In the
v8.0.1 log format it requires `Validation took ` and rejects the known warning about validators skipped
after GTFS parsing failures. These checks do not prove that every validator ran, or authenticate a
caller's source identity, expected hashes, provenance, or exhaustive option map. The in-memory
comparator separately returns `unverified_input` for incomplete attestations and `invalid_input` for
invalid reports or run facts.

## Interpret the result

Counts come from `totalNotices`, grouped by `(code, severity)`. Samples can be truncated and row
numbers can move. A lower count is not evidence that a particular defect was repaired: records may
have been removed, severity may have changed, or a different condition may now trigger the rule.

| Captured runs | Status | Interpretation |
| --- | --- | --- |
| Different ZIPs, same source and method, counts 3 to 5 | `comparable` | A count change of +2; the cause is not established. |
| Changed GTFS values, same counts | `comparable` with empty `changes` | No count change; content may have changed. |
| Identical member bytes in differently packaged ZIPs, same counts | `comparable` with empty `changes` | ZIP inputs differ; content equivalence needs separate evidence. |
| Same ZIP and method, counts 3 to 5 | `same_feed` | Ordinary `changes` is empty; +2 appears in `diagnosticChanges`. Investigate repeatability or evidence linkage. |
| Different reference dates | `incompatible_method` | No ordinary or diagnostic changes are emitted. |
| Different result-affecting options | `incompatible_method` | No count comparison under a fixed method. |

`same_feed` means identical ZIP SHA256 after source and method checks pass. It is not a claim about
publisher version numbers or semantic content. Empty `changes` never establishes unchanged GTFS
values, unchanged service, or the absence of instance-level notice changes.

### Reference dates

Evaluating each publication on its own service start date answers how each publication was evaluated
at that date. This API instead holds the date fixed to compare the effect of changing feed input.
To obtain that result from two runs with different dates, revalidate both ZIPs at one chosen date and
compare the new runs. Preserve the original runs under their original dates; changing metadata to
make them match would misrepresent the method.

### Runtime context

The API compares the complete `validationOptions` maps by exact key/value equality. It does not
discover the runtime OS, architecture, Java version, locale, or validator profile. A caller whose
reproducibility policy requires those facts to match can include canonical context entries alongside
the effective options, for example:

```java
Map.of("-svu", "true", "context.os", "Linux", "context.arch", "x86_64")
```

The `context.*` names are an example convention for caller metadata, not additional validator CLI
flags. Use the same vocabulary on both sides, include effective defaults, and retain the facts at
capture time. Unknown environment facts cannot be recovered by adding the same guessed value to
both maps. The integration test proves that a changed context entry yields `incompatible_method`.
It does not claim that these two context fields suffice for every deployment.

## Connect a content or semantic layer

Retain publication IDs, capture provenance, and complete method metadata in the calling system.
The result's `before.feedSha256` and `after.feedSha256` bind the ordered pair to exact ZIP bytes.
A separate content/semantic result should reference those same digests, its own schema and engine
version, and its own evidence. A publication ID alone is insufficient if the publisher can replace
the bytes behind that ID.

For example, a caller may show the following facts together:

```text
publication A -> publication B
validation: comparable, no count changes
content comparison: routes.txt route_short_name changed from 10 to 11
```

Or it may have separate evidence that two differently packaged ZIPs contain equivalent values. That
does not change this API's exact-byte `same_feed` rule. CSV row reordering can also reveal validator
order dependence, so content equivalence alone does not prove identical validation behavior.

[GTFS-JP Monitor's content model](https://github.com/ttezer/gtfs-jp-monitor/blob/facb8e6ee05c85a0cdc7846d448aa10edbb2fe5d/docs/data-model.md#42-equivalent-publications)
is one external example of a separate content signature. The
[GTFS Diff specification](https://github.com/MobilityData/gtfs-diff/blob/main/spec/v2/specification.md)
provides a possible format for content-change evidence. This draft does not compute a content
signature, infer entity matches, or claim a causal connection between a file change and a count delta.

`FeedVersionDiff` stores only digest summaries of runs. Raw source IDs, provenance IDs, and option
maps can contain private URLs or headers and are deliberately absent from its JSON. Keep an internal
join from the ordered ZIP pair to publication records. If publishing a combined artifact, explicitly
select safe identifiers instead of serializing `RunMetadata` or `InputSpec`.

## Run the examples

From the repository root with JDK 17:

```sh
./gradlew :output-comparator:test --tests '*FeedVersionIntegrationContractTest'
```

The cases cover changed values with equal counts, repackaged ZIPs, differing reference dates,
caller-supplied runtime identity, and repeated-ZIP diagnostics. API placement, the `InputSpec`
boundary, and any future content-identity field remain open for maintainer review.
