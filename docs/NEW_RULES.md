# Adding new rules

We will want to add new rules to this validator as the static [GTFS specification](http://gtfs.org/reference/static) evolves. This page outlines the process of adding new rules to this tool.
Note that:
- Notices related to file parsing and data types are defined in the [core](/core/src/main/java/org/mobilitydata/gtfsvalidator/notice)
- Notices related to GTFS semantics/business logic are encapsulated within the related validation rule class. See the example below in [`TripUsageValidator`](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator/TripUsageValidator.java)

## 0. Prepare for implementation 

- Check the [current rules](../RULES.md) to make sure the rule doesn't already exist.
- Check the [list of possible future rules](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3A%22new+rule%22) to see if an issue already exists for the proposed rule.
  - If no existing issue exists, open [a new issue](https://github.com/MobilityData/gtfs-validator/issues/new/choose).
- Discuss the rule with the community via the Github issue and come to a consensus on the exact logic, and if it should be an `ERROR`, a `WARNING` or an `INFO`. See [definitions for severities](../RULES.md#the-severity-of-a-notice).
- Please note that [`ValidationNotices`](core/src/main/java/org/mobilitydata/gtfsvalidator/notice/ValidationNotice.java) should be distinguished from [`SystemErrors`](core/src/main/java/org/mobilitydata/gtfsvalidator/notice/SystemError.java): while `ValidationNotices` give information about the data quality, `SystemErrors` are not semantic errors, they give information about things that may have gone wrong during the validation process such as an impossibility to unzip a GTFS archive.

- Implement new rule using the process below

## 1. Implement the new rule

Let's look at an example to check that all trips in "trips.txt" have at least two stops in `stop_times.txt`. If a trip has less than 2 records in `stop_times.txt`, a `WARNING` should be generated.

tl;dr - here's what the complete rule looks like:

```java
/**
 * Validates that every trip in "trips.txt" is used by at least two stops from "stop_times.txt"
 *
 * <p>Generated notice: {@link UnusableTripNotice}.
 */
@GtfsValidator
public class TripUsabilityValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripUsabilityValidator(
      GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (stopTimeTable.byTripId(tripId).size() <= 1) {
        noticeContainer.addValidationNotice(new UnusableTripNotice(trip.csvRowNumber(), tripId));
      }
    }
  }

  /**
   * Trips must have more than one stop to be usable.                                                
   *
   * A trip must visit more than one stop in `stop_times.txt` to be usable by passengers for boarding and alighting.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsTripSchema.class}),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class UnusableTripNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's id. */
    private final String tripId;

    UnusableTripNotice(int csvRowNumber, String tripId) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
}
```

The above `TripUsabilityValidator.java` file is located in the [`/main/src/main/java/org/mobilitydata/gtfsvalidator/validator`](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator) folder, where all new validation rules are stored. All files annotated with `@GtfsValidator` will automatically be queued for processing - you don't need to manually tell the validator to execute this rule by editing another file.

The following steps explain how to implement the above rule.

If you want to take a look at a complete set of changes that implement this new rule (including tests and documentation) before diving into the instructions, see [this commit on Github](https://github.com/MobilityData/gtfs-validator/commit/db42f75b319c9d110bf333d41463c33660d76648#diff-9b25966a77f317daf1d785c2964ed5c9cf4f636f3bbfdb71883541f5c1d7dc06).

### a. Determine which `...Validator.java` class the new rule should extend
You'll notice that the above validation rule class `...extends FileValidator` - this means that the rule needs to examine more than one record in a GTFS file or cross-reference more than one file.

Alternately, if the rule only needs to look at a single record at a time (e.g., to make sure each record's start date comes before the end date), you should use `...extends SingleEntityValidator` instead - this is preferred for performance reasons.

For efficiency, multiple rules related to similar fields can be implemented in the same `...Validator.java` class (e.g., to avoid iterating through all rows from GTFS file `stop_times.txt` for each rule), so take a look at the existing validators in the [`/main/src/main/java/org/mobilitydata/gtfsvalidator/validator`](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator) folder to see if there is already a related rule.

Note that some validators are automatically generated based on annotations in the [GTFS table schema classes](/main/src/main/java/org/mobilitydata/gtfsvalidator/table), so you'll never need to implement these manually:
* `...ForeignKeyValidator` - Checks if valid primary key values exist for all foreign key values (for all fields with `@ForeignKey`). For example, if there is a record in `stop_times.txt` with `trip_id = 5`, this validator will generate an error if there isn't a record in `trips.txt` with `trip_id = 5`. 
* `...EndRangeValidator` - Checks if time or date ranges for a record are in order (for all fields with `@EndRange`).
* `...TableHeaderValidator` - Checks if `@Required` fields exist in a file and outputs `INFO` notices for any unknown fields (e.g., to help catch typos in field names).

### b. Add the new notice

The `UnusableTripNotice` is the container for information that will be exported to JSON when this rule detects a problem and is also where we declare if this notice is a [`WARNING` or `ERROR`](/RULES.md#definitions).

```java
  @GtfsValidationNotice(severity = WARNING, ...)
  static class UnusableTripNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;
    
    /** The faulty record's id. */
    private final String tripId;

    UnusableTripNotice(int csvRowNumber, String tripId) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
```
In this case, because the GTFS spec doesn't explictly say that each trip requires at least two stops, we can't say it's an `ERROR`. But it's still suspicious (riders need to board and exit the vehicle), so we set this as a `WARNING`.

You can set up the notice constructor `UnusableTripNotice(int csvRowNumber, String tripId)` to take in whatever variables you want to pass from the validator to the notice, and then include them in the `ImmutableMap.of(` section to write them to the JSON output.

For example, this notice will appear in JSON output as:

```json
{
   "notices":[
      {
         "code":"unusable_trip",
         "severity":"WARNING",
         "totalNotices":1,
         "notices":[
            {
               "tripId":"3362144",
               "csvRowNumber":40150
            },
            ...
        ]
     }
   ] 
} 
```

Values for `tripId` and `csvRowNumber` will be different for each generated notice.

### c. Implement the validation rule logic (`FileValidator`)

<!--suppress ALL -->

<a name="examples"/>

Here's the fun part - writing the rule. Because this rule `...extends FileValidator`, we need to define what GTFS files we want - in this case the `trips.txt` and `stop_times.txt` tables.

We do that by declaring the two variables at the top of the class `GtfsTripTableContainer tripTable` and `GtfsStopTimeTableContainer stopTimeTable` (similar `...TableContainer` classes exist for all GTFS files).

We also need to include the `@Inject` annotation on the `TripUsabilityValidator(...)` constructor and assign the variables - this is how the validator gives you access to these files.

```java
@GtfsValidator
public class TripUsabilityValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripUsabilityValidator(
      GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (stopTimeTable.byTripId(tripId).size() <= 1) {
        noticeContainer.addValidationNotice(new UnusableTripNotice(trip.csvRowNumber(), tripId));
      }
    }
  }
  ...
}
```
The `validate()` method contains the logic to loop through the trips table, and for each `trip_id` it checks if the size of the list of stop_times for that `trip_id` is less than or equal to 1.

If so, it adds a notice to the `noticeContainer` with the info needed to troubleshoot this error in the GTFS data - this notice will then be output to JSON.

That's it for the main rule logic!

### d. Implement the validation rule logic (`SingleEntityValidator`)

Before we talk about documentation and testing for our new rule, let's look at how this rule would be implemented differently if it was looking at a single record at a time instead of multiple records and files.

Below is an example of a rule `FeedServiceDateValidator` that checks that if a start date has been provided in `feed_info.txt` an end date was also provided (and vice versa). 

Because the start and end date are within a single feed info record, we use `...extends SingleEntityValidator<GtfsFeedInfo>` instead: 

```java
/**
 * Validates that if one of {@code (start_date, end_date)} fields is provided for {@code
 * feed_info.txt}, then the second field is also provided.
 *
 * <p>Generated notice: {@link MissingFeedInfoDateNotice}.
 */
@GtfsValidator
public class FeedServiceDateValidator extends SingleEntityValidator<GtfsFeedInfo> {

  @Override
  public void validate(GtfsFeedInfo feedInfo, NoticeContainer noticeContainer) {
    if (feedInfo.hasFeedStartDate() && !feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_end_date"));
    } else if (!feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_start_date"));
    }
  }

  /**
   * Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is
   * provided the second one should also be provided.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class MissingFeedInfoDateNotice extends ValidationNotice {
    MissingFeedInfoDateNotice(int csvRowNumber, String fieldName) {
      super(
          ImmutableMap.of("csvRowNumber", csvRowNumber, "fieldName", fieldName),
          SeverityLevel.WARNING);
    }
  }
}
```

The `validate()` now takes an additional parameters now: 
* `GtfsFeedInfo feedInfo` - The GTFS record type to validate. The type should always match the type you use in `...extends SingleEntityValidator<X>`.
* `NoticeContainer` - same as before, you add your notices here to export to JSON.

Note that we don't need to define the GTFS tables as local variables and we can also omit the constructor. The notice subclass is declared the same as before.

## 2. Document the new rule

Notices are documented directly in source code via comments.

Coming back to our example:

```java
  /**
   * Trips must have more than one stop to be usable.
   *
   * A trip must visit more than one stop in `stop_times.txt` to be usable by passengers for boarding and alighting.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsTripSchema.class}),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class UnusableTripNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's id. */
    private final String tripId;

    UnusableTripNotice(int csvRowNumber, String tripId) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
```

Each Notice needs a class-level comment of the following form:

```java
/**
 * Short text describing the notice in a single line (required).
 *
 * Additional text further describing the notice on new lines (optional).
 */
```

The short description should generally describe the invalid condition found in the feed (e.g. "A recommended file is missing.") as oppossed to describing the expected condition (e.g. "All recommended files should be present.").

Additional text describing the notice is allowed on lines separate from the short description.  Markdown syntax is allowed and should be used instead of Javadoc syntax (e.g. prefer `value` over {@code value}).  Unfortunately, our code formatter will still try to enforce Javadoc formatting to a certain extent, so if you need more complex Markdown formatting (e.g. a list or table), wrap it in a &lt;pre&gt; block:

```java
/**
 * Invalid data.
 *
 * Here's a list of things to check:
 * <pre>
 * - a
 * - b
 * - c
 * </pre>
```

If you would like to link to the GTFS reference or best-practices in a generic way, add a documentation reference to the `@GtfsValidationNotice` so that documentation can be generated in a consistent way.

Each field of the Notice must also be documented with a field-level `/** comment */` (note: not //).


## 3. Test the newly added to rule
`gtfs-validator` tests use [`JUnit 4`](https://junit.org/junit4/) and [`Google Truth`](https://github.com/google/truth).

Generally, you'll want to add test cases to make sure a notice isn't being generated for good data (check for false positive) and that a notice is being generated for bad data (check for false negative).

The following sections describe implementing tests for validators that extend both `FileValidator` and `SingleEntityValidator`. 

### Test a `FileValidator`

tl;dr - The core of the test will end up looking like this:

```java
public class TripUsabilityValidatorTest {
  ...

  @Test
  public void tripServingMoreThanOneStopShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createTrip(1, "route id value", "service id value", "t0"),
                    createTrip(3, "route id value", "service id value", "t1")),
                ImmutableList.of(
                    createStopTime(0, "t0", "s0", 2),
                    createStopTime(2, "t0", "s1", 3),
                    createStopTime(0, "t1", "s3", 5),
                    createStopTime(2, "t1", "s4", 9))))
        .isEmpty();
  }

  @Test
  public void tripServingOneStopShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createTrip(1, "route id value", "service id value", "t0"),
                    createTrip(3, "route id value", "service id value", "t1")),
                ImmutableList.of(
                    createStopTime(0, "t0", "s0", 2),
                    createStopTime(0, "t1", "s3", 5),
                    createStopTime(2, "t1", "s4", 9))))
        .containsExactly(new UnusableTripNotice(1, "t0"));
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripUsabilityValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

}
```

See a full example [here](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/TripUsabilityValidatorTest.java).

#### Detailed steps
1️⃣ Create an instance of the validator to test
```
    TripUsabilityValidator tripUsabilityValidator = new TripUsabilityValidator();
```

2️⃣ Create the relevant [`GtfsTableContainers`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsTableContainer.java) and inject them in the validator
```
    tripUsabilityValidator.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(
                createTrip(1, "route id value", "service id value", "t0"),
                createTrip(3, "route id value", "service id value", "t1")));
    tripUsabilityValidator.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(0, "t0", "s0", 2),
                createStopTime(2, "t0", "s1", 3),
                createStopTime(0, "t1", "s3", 5),
                createStopTime(2, "t1", "s4", 9)));
```

3️⃣ Execute the validator `.validate()` method
```
    underTest.validate(noticeContainer);
```

4️⃣ Verify the content of `NoticeContainer`.
```
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
```

### Test a `SingleEntityValidator`
Similar to the rule itself, implementing a unit test for a `SingleEntityValidator` is slightly different.

tl;dr - here's the core of what it looks like:

```java
public class FeedServiceDateValidatorTest {

  ...
  public void noStartDateShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "url value", Locale.CANADA, null, GtfsDate.fromEpochDay(450))))
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_start_date"));
  }

  @Test
  public void bothDatesCanBeBlank() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "https://www.mobilitydata.org", Locale.CANADA, null, null)))
        .isEmpty();
  }
  ...

  private static List<ValidationNotice> generateNotices(GtfsFeedInfo feedInfo) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FeedServiceDateValidator().validate(feedInfo, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  public static GtfsFeedInfo createFeedInfo(
      int csvRowNumber,
      String feedPublisherName,
      String feedPublisherUrl,
      Locale feedLang,
      GtfsDate feedStartDate,
      GtfsDate feedEndDate) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFeedPublisherName(feedPublisherName)
        .setFeedPublisherUrl(feedPublisherUrl)
        .setFeedLang(feedLang)
        .setDefaultLang(null)
        .setFeedStartDate(feedStartDate)
        .setFeedEndDate(feedEndDate)
        .setFeedVersion(null)
        .setFeedContactEmail(null)
        .setFeedContactUrl(null)
        .build();
  }


}
```
 
See a full example [here](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/FeedServiceDateValidatorTest.java).

#### Detailed steps 
1️⃣ Create a [`GtfsEntity`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsEntity.java) via an annex private method: 
```
  public static GtfsFeedInfo createFeedInfo(
      int csvRowNumber,
      String feedPublisherName,
      String feedPublisherUrl,
      Locale feedLang,
      GtfsDate feedStartDate,
      GtfsDate feedEndDate) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFeedPublisherName(feedPublisherName)
        .setFeedPublisherUrl(feedPublisherUrl)
        .setFeedLang(feedLang)
        .setDefaultLang(null)
        .setFeedStartDate(feedStartDate)
        .setFeedEndDate(feedEndDate)
        .setFeedVersion(null)
        .setFeedContactEmail(null)
        .setFeedContactUrl(null)
        .build();
  }
```

2️⃣ Create a [`NoticeContainer`](../core/src/main/java/org/mobilitydata/gtfsvalidator/notice/NoticeContainer.java):
```
NoticeContainer container = new NoticeContainer();
```

3️⃣ Execute the validator one the previously defined parameters (`GtfsEntity` and `NoticeContainer`).
```
new FeedServiceDateValidator().validate(feedInfo, noticeContainer);
```
4️⃣ Verify the content of `NoticeContainer`: 
```
  @Test
  public void noStartDateShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "url value", Locale.CANADA, null, GtfsDate.fromEpochDay(450))))
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_start_date"));
  }
```
 
