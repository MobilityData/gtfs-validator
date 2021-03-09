# Adding new rules

We will want to add new rules to this validator as the static [GTFS specification](http://gtfs.org/reference/static) evolves. This page outlines the process of adding new rules to this tool.

## 0. Prepare fr implementation 

- Check the [list of currently implemented rules](../RULES.md) to make sure the rule doesn't already exist.
- Check the [list of planned future rules](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+milestone%3A%22Future+work%22) to see if an issue already exists for the proposed rule.
  - If no existing issue exists, open a new issue with the "new rule" label.
- Discuss the rule with the community via the Github issue and come to a general consensus on the exact logic, and if it should be an `ERROR` or a `WARNING`. See [definitions for ERROR and WARNING](../RULES.md#definitions).
- Implement new rule using the process below

For the below example, let's look the steps required to implementing existing the rule that makes sure each trip in "trips.txt" is used by at least two stops from `stop_times.txt`. If a trip is used by 0 or 1 stop from `stop_times.txt` a `WARNING` should be issued.

If you want to take a look at a complete set of changes that implement this new rule before diving into the instructions, see [this commit on Github](https://github.com/MobilityData/gtfs-validator/commit/db42f75b319c9d110bf333d41463c33660d76648#diff-9b25966a77f317daf1d785c2964ed5c9cf4f636f3bbfdb71883541f5c1d7dc06).

## 1. Implement the new rule
### a. Add the new notice in package `org.mobilitydata.gtfsvalidator.notice`
Create a new class and make it extend [`ValidationNotice`](../core/src/main/java/org/mobilitydata/gtfsvalidator/notice/ValidationNotice.java) as follows: 
```
public class UnusableTripNotice extends ValidationNotice {
  public UnusableTripNotice(long csvRowNumber, String tripId) {
    super(
        ImmutableMap.of(
            "csvRowNumber", csvRowNumber,
            "tripId", tripId),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "unusable_trip";
  }
}
```

Notices must specify the severity level: `ERROR`, `WARNING`, or `INFO` (see definitions [here](/RULES.md#definitions). `trip_id` being unique, notices are set up so that the final warning message in the validation report should look like: 
```
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
            }
        ]
     }
   ] 
} 
```

Values for `tripId` and `csvRowNumber` will be different for each generated notice.

### b. Determine which `*Validator.java` class the new rule should extend
All classes that implement rules should use a name that fits the `*Validator.java` format and must extend either the [`SingleEntityValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/SingleEntityValidator.java), or the [`FileValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/FileValidator.java) class.
For efficiency of implementation, multiple rules related to similar fields can be implemented in the same `*Validator.java` class (e.g., to avoid iterating through all rows from GTFS file `stop_times.txt` for each rule).

Here are the currently implemented `*Validator.java` classes (all defined in the package `org.mobilitydata.gtfsvalidator.validator`):
* [`SingleEntityValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/SingleEntityValidator.java) - Examines GTFS files supposed to contain an unique (e.g `feed_info.txt`)
* [`FileValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/FileValidator.java) -  Examines one or multiple GTFS files 

### c. Implement the validation rule logic
This exact process will differ for each rule, but first let's cover some of the basics that are the same across any rule implementation in the `*Validator.validate()` method.
⚠ Note that javadocs should be included in all new files, or updated if modifying existing files.

### [`SingleEntityValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/SingleEntityValidator.java)
```
  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
  // execute some code
  }
```
The `*Validator.validate()` takes two parameters: 
* the `GtfsEntity` to validate
* the `NoticeContainer` that will store notices generated during the validation process

### [`FileValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/FileValidator.java)
```
@GtfsValidator
public class TripUsabilityValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (stopTimeTable.byTripId(tripId).size() <= 1) {
        noticeContainer.addValidationNotice(new UnusableTripNotice(trip.csvRowNumber(), tripId));
      }
    }
  }
}
```
The `*Validator.validate()` takes only one parameter: 
* the `NoticeContainer` that will store notices generated during the validation process

All tables used during the validation process should be injected using `@Inject` annotation.

## 2. Document the new rule in [`RULES.md`](../RULES.md)
Add the rule [`RULES.md`](../RULES.md) keeping the alphabetical order of the table: 
```
| [NewRuleRelatedToStops](#NewRuleRelatedToStops) | new rule short description | 
```
...and add a definition of that rule in the errors or warnings section (still keeping the alphabetical order):

```
<a name="NewRuleRelatedToStops"/>

### NewRuleRelatedToStops

New rule long description

#### References:
* [stops.txt specification](http://gtfs.org/reference/static#stopstxt)
```
When the user clicks on the error code in the validator web interface, they are directed to this section of the rules page so they can find out more information about the rule. So, any information that might help an agency or data consumer fix the problem should be included here.

## 3. Test the newly added to rule
`gtfs-validator` tests rely on [`JUnit 4`](https://junit.org/junit4/), and [`Google Truth`](https://github.com/google/truth).

Validators are tested against data samples via [unit tests](https://en.wikipedia.org/wiki/Unit_testing).

### Test a `SingleEntityValidator` 
1️⃣ Create a [`GtfsEntity`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsEntity.java) via an annex private method: 
```
private GtfsFeedInfo createFeedInfo(GtfsDate feedEndDate) {
  return new GtfsFeedInfo.Builder()
      .setCsvRowNumber(1)
      .setFeedPublisherName("feed publisher name value")
      .setFeedPublisherUrl("https://www.mobilitydata.org")
      .setFeedLang(Locale.CANADA)
      .setFeedEndDate(feedEndDate)
      .build();
}
```

2️⃣ Create a [`NoticeContainer`](../core/src/main/java/org/mobilitydata/gtfsvalidator/notice/NoticeContainer.java):
```
NoticeContainer container = new NoticeContainer();
```

3️⃣ Execute the validator one the previously defined parameters (`GtfsEntity` and `NoticeContainer`).
```
validateFeedInfo(createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7))))
```
4️⃣ Verify the content of `NoticeContainer`: 
```
assertThat(
        validateFeedInfo(createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7)))))
    .containsExactly(
        new FeedExpirationDateNotice(
            1,
            GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
            GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7)),
            GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30))));
```

One can also refer to [`this example`](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/FeedExpirationDateValidatorTest.java).
 
### Test a `FileValidator`
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


One can also refer to [`this example`](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/TripUsabilityValidatorTest.java).
