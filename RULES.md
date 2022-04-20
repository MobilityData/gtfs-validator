# Implemented rules
- Notices related to file parsing and data types are defined in the [core](/core/src/main/java/org/mobilitydata/gtfsvalidator/notice) 
- Notices related to GTFS semantics/business logic are encapsulated within the related validation rule class see the following example in [`TripUsageValidator`](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator/TripUsageValidator.java):
```java
/**
 * Validates that every trip in "trips.txt" is used by some stop from "stop_times.txt"
 *
 * <p>Generated notice: {@link UnusedTripNotice}.
 */
@GtfsValidator
public class TripUsageValidator extends FileValidator {
  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripUsageValidator(GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Do not report the same trip_id multiple times.
    Set<String> reportedTrips = new HashSet<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (reportedTrips.add(tripId) && stopTimeTable.byTripId(tripId).isEmpty()) {
        noticeContainer.addValidationNotice(new UnusedTripNotice(tripId, trip.csvRowNumber()));
      }
    }
  }
  /**
   * A {@code GtfsTrip} should be referred to at least once in {@code GtfsStopTimeTableContainer}
   * station).
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class UnusedTripNotice extends ValidationNotice {
    UnusedTripNotice(String tripId, long csvRowNumber) {
      super(
          ImmutableMap.of(
              "tripId", tripId,
              "csvRowNumber", csvRowNumber),
          SeverityLevel.WARNING);
    }
  }
}
```  
 
Note that the notice ID naming conventions changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](/docs/MIGRATION_V1_V2.md) for a mapping between v1 and v2 rules.

<a name="definitions"/>

## Definitions
Notices are split into three categories: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for items that the [GTFS reference specification](https://github.com/google/transit/tree/master/gtfs/spec/en) explicitly requires or prohibits (e.g., using the language "must"). The validator uses [RFC2119](https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec.
  * ⚠️ for this particular level of severity, [`ValidationNotices`](core/src/main/java/org/mobilitydata/gtfsvalidator/notice/ValidationNotice.java) should be distinguished from [`SystemErrors`](core/src/main/java/org/mobilitydata/gtfsvalidator/notice/SystemError.java): while `ValidationNotices` give information about the data quality, `SystemErrors` are not semantic errors, they give information about things that may have gone wrong during the validation process such as an impossibility to unzip a GTFS archive. 
* `WARNING` notices are for items that will affect the quality of GTFS datasets but the GTFS spec does expressly require or prohibit. For example, these might be items recommended using the language "should" or "should not" in the GTFS spec, or items recommended in the MobilityData [GTFS Best Practices](https://gtfs.org/best-practices/).
* `INFO` notices are for items that do not affect the feed's quality, such as unknown files or unknown fields.

Additional details regarding the notices' context is provided in [`NOTICES.md`](/docs/NOTICES.md).

<!--suppress ALL -->

<a name="ERRORS"/>

## Table of errors

| Name                                                                                                            | Description                                                                                                                                            |
|-----------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         | Block trips with overlapping stop times.                                                                                                               |
| [`CsvParsingFailedNotice`](#CsvParsingFailedNotice)                                                             | Parsing of a CSV file failed.                                                                                                                          |
| [`DecreasingShapeDistanceNotice`](#DecreasingShapeDistanceNotice)                                               | Decreasing `shape_dist_traveled` in `shapes.txt`.                                                                                                      |
| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           | Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                         |
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             | Duplicated column in CSV.                                                                                                                              |
| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   | Duplicate rows from `fare_rules.txt` based on `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id`. |
| [`DuplicateKeyNotice`](#DuplicateKeyNotice)                                                                     | Duplicated entity.                                                                                                                                     |
| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                                            	  | A column name is empty.                                                                                                                                |
| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           | A CSV file is empty.                                                                                                                                   |
| [`EqualShapeDistanceDiffCoordinatesNotice`](#EqualShapeDistanceDiffCoordinatesNotice)                           | Two consecutive points have equal `shape_dist_traveled` and different lat/lon coordinates in `shapes.txt`.                                             |
| [`ForeignKeyViolationNotice`](#ForeignKeyViolationNotice)                                                       | Wrong foreign key.                                                                                                                                     |
| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)                                         | Inconsistent Timezone among agencies.                                                                                                                  |
| [`InvalidColorNotice`](#InvalidColorNotice)                                                                     | A field contains an invalid color value.                                                                                                               |
| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)                                                               | A field contains a wrong currency code.                                                                                                                |
| [`InvalidDateNotice`](#InvalidDateNotice)                                                                       | A field cannot be parsed as date.                                                                                                                      |
| [`InvalidEmailNotice`](#InvalidEmailNotice)                                                                     | A field contains a malformed email address.                                                                                                            |
| [`InvalidFloatNotice`](#InvalidFloatNotice)                                                                     | A field cannot be parsed as a floating point number.                                                                                                   |
| [`InvalidIntegerNotice`](#InvalidIntegerNotice)                                                                 | A field cannot be parsed as an integer.                                                                                                                |
| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)                                                       | A field contains a wrong language code.                                                                                                                |
| [`InvalidPhoneNumberNotice`](#InvalidPhoneNumberNotice)                                                         | A field contains a malformed phone number.                                                                                                             |
| [`InvalidRowLengthNotice`](#InvalidRowLengthNotice)                                                             | Invalid csv row length.                                                                                                                                |
| [`InvalidTimeNotice`](#InvalidTimeNotice)                                                                       | A field cannot be parsed as time.                                                                                                                      |
| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)                                                               | A field cannot be parsed as a timezone.                                                                                                                |
| [`InvalidUrlNotice`](#InvalidUrlNotice)                                                                         | A field contains a malformed URL.                                                                                                                      |
| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     | A location that must have `parent_station` field does not have it.                                                                                     |
| [`LocationWithUnexpectedStopTimeNotice`](#LocationWithUnexpectedStopTimeNotice)                                 | A location in `stops.txt` that is not a stop is referenced by some `stop_times.stop_id`.                                                               |
| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       | Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                            |
| [`MissingLevelIdNotice`](#MissingLevelIdNotice)       	                                                      | `stops.level_id` is conditionally required.                                                                                                            |
| [`MissingRequiredColumnNotice`](#MissingRequiredColumnNotice)                                                   | A required column is missing in the input file.                                                                                                        |
| [`MissingRequiredFieldNotice`](#MissingRequiredFieldNotice)                                                     | A required field is missing.                                                                                                                           |
| [`MissingRequiredFileNotice`](#MissingRequiredFileNotice)                                                       | A required file is missing.                                                                                                                            |
| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               | Missing trip edge `arrival_time` or `departure_time`.                                                                                                  |
| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                 | New line or carriage return in a value in CSV file.                                                                                                    |
| [`NumberOutOfRangeNotice`](#NumberOutOfRangeNotice)                                                             | Out of range value.                                                                                                                                    |
| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     | Trip frequencies overlap.                                                                                                                              |
| [`PathwayToPlatformWithBoardingAreasNotice`](#PathwayToPlatformWithBoardingAreasNotice)                         | A pathway has an endpoint that is a platform which has boarding areas.                                                                                 |
| [`PathwayToWrongLocationTypeNotice`](#PathwayToWrongLocationTypeNotice)                                             | A pathway has an endpoint that is a station.                                                                                                           |
| [`PathwayUnreachableLocationNotice`](#PathwayUnreachableLocationNotice)                                         | A location is not reachable at least in one direction: from the entrances or to the exits.                                                             |
| [`PointNearOriginNotice`](#PointNearOriginNotice)                                                               | A point is too close to origin `(0, 0)`.                                                                                                               |
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             | Missing route short name and long name.                                                                                                                |
| [`StartAndEndRangeEqualNotice`](#StartAndEndRangeEqualNotice)                                                   | Two date or time fields are equal.                                                                                                                     |
| [`StartAndEndRangeOutOfOrderNotice`](#StartAndEndRangeOutOfOrderNotice)                                         | Two date or time fields are out of order.                                                                                                              |
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             | A station has `parent_station` field set.                                                                                                              |
| [`StopTimeTimepointWithoutTimesNotice`](#StopTimeTimepointWithoutTimesNotice)     	                          | `arrival_time` or `departure_time` not specified for timepoint.                                                                                        |
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) | Backwards time travel between stops in `stop_times.txt`                                                                                                |
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 | Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                      |
| [`StopWithoutZoneIdNotice`](#StopWithoutZoneIdNotice)                                                           | Stop without value for `stops.zone_id`.                                                                                                                |
| [`TranslationForeignKeyViolationNotice`](#TranslationForeignKeyViolationNotice)                                 | An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.                                                      |
| [`TranslationUnexpectedValueNotice`](#TranslationUnexpectedValueNotice)                                         | A field in a translations row has value but must be empty.                                                                                             |
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               | Incorrect type of the parent location.                                                                                                                 |

<a name="WARNINGS"/>

## Table of warnings

| Name                                                                              	| Description                                                                                                                                                 	|
|-----------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [`AttributionWithoutRoleNotice`](#AttributionWithoutRoleNotice)                   	| Attribution with no role.                                                                                                                                   	|
| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	| Two distinct routes have either the same `route_short_name`, the same `route_long_name`, or the same combination of `route_short_name` and `route_long_name`. | 
| [`EmptyRowNotice`](#EmptyRowNotice)                                               	| A row in the input file has only spaces.                                                                                                                      |
| [`EqualShapeDistanceSameCoordinatesNotice`](#EqualShapeDistanceSameCoordinatesNotice) | Two consecutive points have equal `shape_dist_traveled` and the same lat/lon coordinates in `shapes.txt`.                                                     |
| [`FastTravelBetweenConsecutiveStopsNotice`](#FastTravelBetweenConsecutiveStopsNotice) | A transit vehicle moves too fast between two consecutive stops.                                                                            	                |
| [`FastTravelBetweenFarStopsNotice`](#FastTravelBetweenFarStopsNotice)                 | A transit vehicle moves too fast between two far stops.                                                                            	                        |
| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	| Dataset should be valid for at least the next 7 days. Dataset should cover at least the next 30 days of service.                                            	|
| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	| Mismatching feed and agency language fields.                                                                                                                	|
| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	| Inconsistent language among agencies.                                                                                                                       	|
| [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)         | The value in CSV file has leading or trailing whitespaces.                                                                                                  	|
| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	| `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                   	|
| [`MissingTimepointColumnNotice`](#MissingTimepointColumnNotice)                         	        | `timepoint` column is missing for a dataset.                                                                                                        	        |
| [`MissingTimepointValueNotice`](#MissingTimepointValueNotice)                         	        | `stop_times.timepoint` value is missing for a record.                                                                                                        	|
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	| More than one row in CSV.                                                                                                                                   	|
| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	| Non ascii or non printable char in  `id`.                                                                                                                   	|
| [`PathwayDanglingGenericNodeNotice`](#PathwayDanglingGenericNodeNotice)           	| A generic node has only one incident location in a pathway graph.                                                                                             |
| [`PathwayLoopNotice`](#PathwayLoopNotice)                                         	| A pathway starts and ends at the same location.                                                                                                               |
| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	| A platform has no `parent_station` field set.                                                                                                               	|
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	| Insufficient route color contrast.                                                                                                                          	|
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	| `route_short_name` and `route_long_name` are equal for a single route.                                                                                        |
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	| Short name of a route is too long (more than 12 characters).                                                                                                	|
| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)     | Same name and description for route.                                                                                                                        	|
| [`SameNameAndDescriptionForStopNotice`](#SameNameAndDescriptionForStopNotice)       | Same name and description for stop.                                                                                                                      	    |
| [`SameRouteAndAgencyUrlNotice`](#SameRouteAndAgencyUrlNotice)                       | Same `routes.route_url` and `agency.agency_url`.                                                                                                  	        |
| [`SameStopAndAgencyUrlNotice`](#SameStopAndAgencyUrlNotice)                         | Same `stops.stop_url` and `agency.agency_url`.                                                                                                  	            |
| [`SameStopAndRouteUrlNotice`](#SameStopAndRouteUrlNotice)                          	| Same `stops.stop_url` and `routes.route_url`.                                                                                                  	            |
| [`StopHasTooManyMatchesForShapeNotice`](#StopHasTooManyMatchesForShapeNotice)     	| Stop entry that has many potential matches to the trip's path of travel.                                                                                       |
| [`StopsMatchShapeOutOfOrderNotice`](#StopsMatchShapeOutOfOrderNotice)     	        | Two stop entries are different than their arrival-departure order defined by the shapes.txt                                                                   |
| [`StopTooFarFromShapeNotice`](#StopTooFarFromShapeNotice)                 	        | Stop too far from trip shape.                                                                                                                               	|
| [`StopTooFarFromShapeUsingUserDistanceNotice`](#StopTooFarFromShapeUsingUserDistanceNotice)| Stop time too far from shape.                                                                                                                     |
| [`StopWithoutStopTimeNotice`](#StopWithoutStopTimeNotice)                             | A stop in `stops.txt` is not referenced by any `stop_times.stop_id`.                                                                                          |
| [`TranslationUnknownTableNameNotice`](#TranslationUnknownTableNameNotice)             | A translation references an unknown or missing GTFS table.                                                                                                    |
| [`UnexpectedEnumValueNotice`](#UnexpectedEnumValueNotice)                         	| An enum has an unexpected value.                                                                                                                            	|
| [`UnusableTripNotice`](#UnusableTripNotice)                                       	| Trips must have more than one stop to be usable.                                                                                                            	|
| [`UnusedShapeNotice`](#UnusedShapeNotice)                                         	| Shape is not used in GTFS file `trips.txt`.                                                                                                                 	|
| [`UnusedTripNotice`](#UnusedTripNotice)                                           	| Trip is not be used in `stop_times.txt`                                                                                                                     	|

<a name="INFOS"/>

## Table of info

| Name                                          	| Description               	|
|-----------------------------------------------	|---------------------------	|
| [`UnknownColumnNotice`](#UnknownColumnNotice) 	| A column name is unknown. 	|
| [`UnknownFileNotice`](#UnknownFileNotice)     	| A file is unknown.        	|

<a name="SYSTEM_ERRORS"/>

## Table of system errors

| Name                                                                    	| Description                                            	|
|-------------------------------------------------------------------------	|--------------------------------------------------------	|
| [`IOError`](#IOError)                                                   	| Error in IO operation.                                 	|
| [`RuntimeExceptionInLoaderError`](#RuntimeExceptionInLoaderError)       	| RuntimeException while loading GTFS dataset in memory. 	|
| [`RuntimeExceptionInValidatorError`](#RuntimeExceptionInValidatorError) 	| RuntimeException while validating GTFS archive.        	|
| [`ThreadExecutionError`](#ThreadExecutionError)                         	| ExecutionException during multithreaded validation     	|
| [`URISyntaxError`](#URISyntaxError)                                     	| A string could not be parsed as a URI reference.       	|

## Notices

### Errors

#### BlockTripsWithOverlappingStopTimesNotice

Trips with the same block id have overlapping stop times.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="CsvParsingFailedNotice"/>

#### CsvParsingFailedNotice

Parsing of a CSV file failed. One common case of the problem is when a cell value contains more than 4096 characters.

<a name="DecreasingShapeDistanceNotice"/>

#### DecreasingShapeDistanceNotice

When sorted by `shape.shape_pt_sequence`, two consecutive shape points must not have decreasing values for `shape_dist_traveled`.  

##### References:
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

<a name="DecreasingOrEqualStopTimeDistanceNotice"/>

#### DecreasingOrEqualStopTimeDistanceNotice

When sorted by `stop_times.stop_pt_sequence`, two consecutive stop times in a trip should have increasing distance. If the values are equal, this is considered as an error.  

##### References:
* [stops.txt specification](https://gtfs.org/reference/static#stopstxt)

<a name="DuplicatedColumnNotice"/>

#### DuplicatedColumnNotice

The input file CSV header has the same column name repeated.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="DuplicateFareRuleZoneIdFieldsNotice"/>

#### DuplicateFareRuleZoneIdFieldsNotice

The combination of `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id` fields should be unique in GTFS file `fare_rules.txt`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="DuplicateKeyNotice"/>

#### DuplicateKeyNotice

The values of the given key and rows are duplicates.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="EmptyColumnNameNotice"/>

#### EmptyColumnNameNotice

A column name has not been provided. Such columns are skipped by the validator.

##### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

<a name="EmptyFileNotice"/>

#### EmptyFileNotice

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.

##### References:
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)

#### EqualShapeDistanceDiffCoordinatesNotice

<a name="EqualShapeDistanceDiffCoordinatesNotice"/>

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and different coordinates indicate an error.

##### References:
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

<a name="ForeignKeyViolationNotice"/>

#### ForeignKeyViolationNotice

The values of the given key and rows of one table cannot be found a values of the given key in another table. The Foreign keys are defined in the specification under "Type" for each file.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="InconsistentAgencyTimezoneNotice"/>

#### InconsistentAgencyTimezoneNotice

Agencies from GTFS `agency.txt` have been found to have different timezones.

##### References:
* [GTFS agency.txt specification](https://gtfs.org/reference/static/#agencytxt)

<a name="InvalidColorNotice"/>

#### InvalidColorNotice

Value of field with type `color` is not valid. A color must be encoded as a six-digit hexadecimal number. The leading "#" is not included.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidCurrencyNotice"/>

#### InvalidCurrencyNotice

Value of field with type `currency` is not valid. Currency code must follow <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidDateNotice"/>

#### InvalidDateNotice

Value of field with type `date` is not valid. Dates must have the YYYYMMDD format.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidEmailNotice"/>

#### InvalidEmailNotice

Value of field with type `email` is not valid. Definitions for valid emails are quite vague. We perform strict validation in the upstream using the Apache Commons EmailValidator.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons EmailValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/EmailValidator.html)
 
<a name="InvalidFloatNotice"/>

#### InvalidFloatNotice

Value of field with type `float` is not valid. 

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
 
<a name="InvalidIntegerNotice"/>

#### InvalidIntegerNotice

Value of field with type `integer` is not valid. 

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidLanguageCodeNotice"/>

#### InvalidLanguageCodeNotice

Value of field with type `language` is not valid. Language codes must follow <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">IETF BCP 47</a>.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidPhoneNumberNotice"/>

#### InvalidPhoneNumberNotice

Value of field with type `phone number` is not valid. This rule uses the [PhoneNumberUtil](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.4.1/com/google/i18n/phonenumbers/PhoneNumberUtil.html) class to validate a phone number based on a country code. If no country code is provided in the parameters used to run the validator, this notice won't be emitted. 

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidRowLengthNotice"/>

#### InvalidRowLengthNotice

A row in the input file has a different number of values than specified by the CSV header.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="InvalidTimeNotice"/>

#### InvalidTimeNotice

Value of field with type `time` is not valid. Time must be in the `H:MM:SS`, `HH:MM:SS` or `HHH:MM:SS` format.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidTimezoneNotice"/>

#### InvalidTimezoneNotice

Value of field with type `timezone` is not valid.Timezones are defined at <a href="https://www.iana.org/time-zones">www.iana.org</a>. Timezone names never contain the space character but may contain an underscore. Refer to <a href="http://en.wikipedia.org/wiki/List_of_tz_zones">Wikipedia</a> for a list of valid values.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidUrlNotice"/>

#### InvalidUrlNotice

Value of field with type `url` is not valid. Definitions for valid URLs are quite vague. We perform strict validation in the upstream using the Apache Commons UrlValidator.

##### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons UrlValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)

<a name="LocationWithoutParentStationNotice"/>

#### LocationWithoutParentStationNotice

A location that must have `parent_station` field does not have it. The following location types must have `parent_station`: entrance, generic node, boarding_area.

##### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="LocationWithUnexpectedStopTimeNotice"/>

#### LocationWithUnexpectedStopTimeNotice

Referenced locations (using `stop_times.stop_id`) must be stops/platforms, i.e. their `stops.location_type` value must be 0 or empty.

##### References:
* [stop_times.txt GTFS specification](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stoptimestxt)

<a name="MissingCalendarAndCalendarDateFilesNotice"/>

#### MissingCalendarAndCalendarDateFilesNotice

Both files calendar_dates.txt and calendar.txt are missing from the GTFS archive. At least one of the files must be provided.

##### References:
* [calendar.txt specification](http://gtfs.org/reference/static/#calendartxt)
* [calendar_dates.txt specification](http://gtfs.org/reference/static/#calendar_datestxt)

<a name="MissingLevelIdNotice"/>

#### MissingLevelIdNotice

GTFS file `levels.txt` is required for elevator (`pathway_mode=5`). A row from `stops.txt` linked to an elevator pathway has no value for `stops.level_id`.

##### References:
* [levels.txt specification](http://gtfs.org/reference/static/#levelstxt)

<a name="MissingRequiredColumnNotice"/>

#### MissingRequiredColumnNotice

A required column is missing in the input file.

##### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingRequiredFieldNotice"/>

#### MissingRequiredFieldNotice

The given field has no value in some input row, even though values are required.

##### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingRequiredFileNotice"/>

#### MissingRequiredFileNotice

A required file is missing.

##### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingTripEdgeNotice"/>

#### MissingTripEdgeNotice

First and last stop of a trip must define both `arrival_time` and `departure_time` fields.

##### References:
* [stop_times.txt specification](https://gtfs.org/reference/static/#stop_timestxt)

<a name="NewLineInValueNotice"/>

#### NewLineInValueNotice

A value in CSV file has a new line or carriage return.

##### References:
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)

<a name="NumberOutOfRangeNotice"/>

#### NumberOutOfRangeNotice

The values in the given column of the input rows are out of range.

##### References:
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS field types](http://gtfs.org/reference/static/#field-types)

<a name="OverlappingFrequencyNotice"/>

#### OverlappingFrequencyNotice

Trip frequencies must not overlap in time

##### References:
* [frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)

<a name="PathwayToPlatformWithBoardingAreasNotice"/>

#### PathwayToPlatformWithBoardingAreasNotice

A pathway has an endpoint that is a platform which has boarding areas. A platform that has boarding
areas is treated as a parent object, not a point. In such cases, the platform must not have pathways
assigned - instead, pathways must be assigned to its boarding areas.

##### References:
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)

<a name="RouteBothShortAndLongNameMissingNotice"/>

<a name="PathwayToWrongLocationTypeNotice"/>

#### PathwayToWrongLocationTypeNotice

A pathway has an endpoint that is a station. Pathways endpoints must be platforms (stops),
entrances/exits, generic nodes or boarding areas.

##### References:
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)

<a name="PathwayUnreachableLocationNotice"/>

#### PathwayUnreachableLocationNotice

A location belongs to a station that has pathways and is not reachable at least in one direction:
from the entrances or to the exits.

Notices are reported for platforms, boarding areas and generic nodes but not for entrances or
stations.

Notices are not reported for platforms that have boarding areas since such platforms may not
have incident pathways. Instead, notices are reported for the boarding areas.

##### References:
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
 
<a name="PointNearOriginNotice"/>

#### PointNearOriginNotice

A point is too close to origin `(0, 0)`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

#### RouteBothShortAndLongNameMissingNotice

Both short_name and long_name are missing for a route.

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="StartAndEndRangeEqualNotice"/>

#### StartAndEndRangeEqualNotice

The fields `frequencies.start_date` and `frequencies.end_date` have been found equal in `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StartAndEndRangeOutOfOrderNotice"/>

#### StartAndEndRangeOutOfOrderNotice

Date or time fields have been found out of order in `calendar.txt`, `feed_info.txt` and `stop_times.txt`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StationWithParentStationNotice"/>

#### StationWithParentStationNotice

Field `parent_station` must be empty when `location_type` is 1.

##### References:
[stop.txt](http://gtfs.org/reference/static/#stopstxt)

<a name="StopTimeTimepointWithoutTimesNotice"/>

#### StopTimeTimepointWithoutTimesNotice

Any records with `stop_times.timepoint` set to 1 must define a value for `stop_times.arrival_time` and `stop_times.departure_time` fields.

##### References:
* [GTFS stop_times.txt specification](https://gtfs.org/reference/static#stoptimestxt)

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

#### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence in `stop_times.txt`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

#### StopTimeWithOnlyArrivalOrDepartureTimeNotice

Missing `stop_time.arrival_time` or `stop_time.departure_time`

##### References:
* [stop_times.txt specification](http://gtfs.org/reference/static/#stop_timestxt)

<a name="StopWithoutZoneIdNotice"/>

#### StopWithoutZoneIdNotice

If `fare_rules.txt` is provided, and `fare_rules.txt` uses at least one column among `origin_id`, `destination_id`, and `contains_id`, then all stops and platforms (location_type = 0) must have `stops.zone_id` assigned. 

##### References:
* [GTFS stops.txt specification](https://gtfs.org/reference/static#stopstxt)

<a name="TranslationForeignKeyViolationNotice"/>

#### TranslationForeignKeyViolationNotice

An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.

##### References:
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)

<a name="TranslationUnexpectedValueNotice"/>

#### TranslationUnexpectedValueNotice

A field in a translations row has value but must be empty.

##### References:
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)

<a name="WrongParentLocationTypeNotice"/>

#### WrongParentLocationTypeNotice

Value of field `location_type` of parent found in field `parent_station` is invalid.

According to spec
- _Stop/platform_ can only have _Station_ as parent
- _Station_ can NOT have a parent
- _Entrance/exit_ or _generic node_ can only have _Station_ as parent
- _Boarding Area_ can only have _Platform_ as parent 

Any other combination raise this error.

##### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

### Warnings

<a name="AttributionWithoutRoleNotice"/>

#### AttributionWithoutRoleNotice

At least one of the fields `is_producer`, `is_operator`, or `is_authority` should be set to 1.

##### References:
* [attributions.txt specification](https://gtfs.org/reference/static#attributionstxt)

<a name="DuplicateRouteNameNotice"/>

#### DuplicateRouteNameNotice

All routes of the same `route_type` with the same `agency_id` should have unique combinations of `route_short_name` and `route_long_name`.

Note that there may be valid cases where routes have the same short and long name, e.g., if they serve different areas. However, different directions must be modeled as the same route.

Example of bad data:
| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| U1                 	| Southern          	|
| route2     	| U1                 	| Southern          	|

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [routes.txt best practices](http://gtfs.org/best-practices/#routestxt)

<a name="EmptyRowNotice"/>

#### EmptyRowNotice

A row in the input file has only spaces.

##### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

#### EqualShapeDistanceSameCoordinatesNotice

<a name="EqualShapeDistanceSameCoordinatesNotice"/>

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and the same coordinates indicate a duplicative shape point.

##### References:
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

<a name="FastTravelBetweenConsecutiveStopsNotice"/>

#### FastTravelBetweenConsecutiveStopsNotice

A transit vehicle moves too fast between two consecutive stops. The speed threshold depends on route type.

##### Speed thresholds

| Route type | Description | Threshold, km/h |
|------------|-------------|-----------------|
| 0          | Light rail  | 100             |
| 1          | Subway      | 150             |
| 2          | Rail        | 500             |
| 3          | Bus         | 150             |
| 4          | Ferry       |  80             |
| 5          | Cable tram  |  30             |
| 6          | Aerial lift |  50             |
| 7          | Funicular   |  50             |
| 11         | Trolleybus  | 150             |
| 12         | Monorail    | 150             |
| -          | Unknown     | 200             |

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

#### FastTravelBetweenFarStopsNotice

A transit vehicle moves too fast between far consecutive stops (more than in 10 km apart). 
This normally indicates a more serious problem than too fast travel between consecutive stops.
The speed threshold depends on route type.

##### Speed thresholds

Same as for [`FastTravelBetweenConsecutiveStopsNotice`](#FastTravelBetweenConsecutiveStopsNotice).

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="FeedExpirationDateNotice"/>

#### FeedExpirationDateNotice

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.
If possible, the GTFS dataset should cover at least the next 30 days of service.

##### References:
* [General Publishing & General Practices](https://gtfs.org/best-practices/#dataset-publishing--general-practices)

<a name="FeedInfoLangAndAgencyLangMismatchNotice"/>

#### FeedInfoLangAndAgencyLangMismatchNotice
1. Files `agency.txt` and `feed_info.txt` should define matching `agency.agency_lang` and `feed_info.feed_lang`.
  The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
  * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
  * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
  * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

##### References:
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)

<a name="InconsistentAgencyLangNotice"/>

#### InconsistentAgencyLangNotice

Agencies from GTFS `agency.txt` have been found to have different languages.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="LeadingOrTrailingWhitespacesNotice"/>

#### LeadingOrTrailingWhitespacesNotice

The value in CSV file has leading or trailing whitespaces.

##### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

<a name="MissingFeedInfoDateNotice"/>

#### MissingFeedInfoDateNotice

Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided.

##### References:
* [feed_info.txt Best practices](http://gtfs.org/best-practices/#feed_infotxt)
 
<a name="MissingTimepointValueNotice"/>

#### MissingTimepointValueNotice

Even though the column `timepoint` is optional in `stop_times.txt` according to the specification, `stop_times.timepoint` should not be empty when provided. 

##### References:
* [stop_times.txt specification](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)

<a name="MissingTimepointColumnNotice"/>

#### MissingTimepointColumnNotice

The `timepoint` column should be provided.

##### References:
* [stop_times.txt bets practices](https://github.com/MobilityData/GTFS_Schedule_Best-Practices/blob/master/en/stop_times.md)

<a name="MoreThanOneEntityNotice"/>

#### MoreThanOneEntityNotice

The file is expected to have a single entity but has more (e.g., "feed_info.txt").

##### References:
* [GTFS field definition](http://gtfs.org/reference/static#field-definitions)

<a name="NonAsciiOrNonPrintableCharNotice"/>

#### NonAsciiOrNonPrintableCharNotice

A value of a field with type `id` contains non ASCII or non printable characters. This is not recommended.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="PathwayDanglingGenericNodeNotice"/>

#### PathwayDanglingGenericNodeNotice

A generic node has only one incident location in a pathway graph. Such generic node is useless
because there is no benefit in visiting it.

##### References:
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
* 
<a name="PathwayLoopNotice"/>

#### PathwayLoopNotice

A pathway should not have same values for `from_stop_id` and `to_stop_id`.

<a name="PlatformWithoutParentStationNotice"/>

#### PlatformWithoutParentStationNotice

A platform has no `parent_station` field set.

##### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="RouteColorContrastNotice"/>

#### RouteColorContrastNotice

A route's color and `route_text_color` should be contrasting.

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="RouteShortAndLongNameEqualNotice"/>

#### RouteShortAndLongNameEqualNotice

A single route has the same values for `route_short_name` and `route_long_name`.

Example of bad data:

| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| L1                 	| L1                	|

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="RouteShortNameTooLongNotice"/>

#### RouteShortNameTooLongNotice

Short name of a route is too long (more than 12 characters).

##### References:
* [routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="SameNameAndDescriptionForRouteNotice"/>

#### SameNameAndDescriptionForRouteNotice

The GTFS spec defines `routes.txt` [route_desc](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_desc` fields.

##### References:
[routes.txt specification](http://gtfs.org/reference/static/#routestxt)
[routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="SameNameAndDescriptionForStopNotice"/>

#### SameNameAndDescriptionForStopNotice

The GTFS spec defines `stops.txt` [stop_description](https://gtfs.org/reference/static/#stopstxt) as:

> Description of the location that provides useful, quality information. Do not simply duplicate the name of the location.

##### References:
[stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="SameRouteAndAgencyUrlNotice"/>

#### SameRouteAndAgencyUrlNotice

A route should not have the same `routes.route_url` as a record from `agency.txt`.

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="SameStopAndAgencyUrlNotice"/>

#### SameStopAndAgencyUrlNotice

A stop should not have the same `stops.stop_url` as a record from `agency.txt`.

##### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="SameStopAndRouteUrlNotice"/>

#### SameStopAndRouteUrlNotice

A stop should not have the same `stop.stop_url` as a record from `routes.txt`.

##### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
 
<a name="StopHasTooManyMatchesForShapeNotice"/>

#### StopHasTooManyMatchesForShapeNotice

A stop entry that has many potential matches to the trip's path of travel, as defined  by the shape entry in `shapes.txt`.

<a name="StopsMatchShapeOutOfOrderNotice"/>

#### StopsMatchShapeOutOfOrderNotice

Two stop entries in `stop_times.txt` are different than their arrival-departure order as defined by the shape in the `shapes.txt` file.

<a name="StopTooFarFromShapeNotice"/>

#### StopTooFarFromShapeNotice

Per GTFS Best Practices, route alignments (in `shapes.txt`) should be within 100 meters of stop locations which a trip serves.

##### References:
* [GTFS Best Practices shapes.txt](https://gtfs.org/best-practices/#shapestxt)
 
<a name="StopTooFarFromShapeUsingUserDistanceNotice"/>

#### StopTooFarFromShapeUsingUserDistanceNotice

A stop time entry that is a large distance away from the location of the shape in `shapes.txt` as defined by `shape_dist_traveled` values.

<a name="StopWithoutStopTimeNotice"/>

#### StopWithoutStopTimeNotice

A stop in `stops.txt` is not referenced by any `stop_times.stop_id`, so it is not used by any trip.
Such stops normally do not provide user value. This notice may indicate a typo in `stop_times.txt`.

<a name="TranslationUnknownTableNameNotice"/>

#### TranslationUnknownTableNameNotice

A translation references an unknown or missing GTFS table.

##### References:
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)

<a name="UnexpectedEnumValueNotice"/>

#### UnexpectedEnumValueNotice

An enum has an unexpected value.

##### References:
* [GTFs field definitions](http://gtfs.org/reference/static/#field-definitions)

<a name="UnusableTripNotice"/>

#### UnusableTripNotice

A trip must visit more than one stop in stop_times.txt to be usable by passengers for boarding and alighting.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnusedShapeNotice"/>

#### UnusedShapeNotice

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnusedTripNotice"/>

#### UnusedTripNotice

Trips should be referred to at least once in `stop_times.txt`.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

### Info

<a name="UnknownColumnNotice"/>

#### UnknownColumnNotice

A column is unknown.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnknownFileNotice"/>

#### UnknownFileNotice

A file is unknown.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

### System errors

<a name="IOError"/>

#### IOError

Error in IO operation.

<a name="RuntimeExceptionInLoaderError"/>

#### RuntimeExceptionInLoaderError

A [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) occurred while loading a table. This normally indicates a bug in validator.

<a name="RuntimeExceptionInValidatorError"/>

#### RuntimeExceptionInValidatorError

A [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) occurred during validation. This normally indicates a bug in validator code, e.g., in a custom validator class.

<a name="ThreadExecutionError"/>

#### ThreadExecutionError

An [ExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutionException.html) occurred during multithreaded validation.

<a name="URISyntaxError"/>

#### URISyntaxError

A string could not be parsed as a URI reference.
