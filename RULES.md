# Implemented rules

Rules are declared in the `Notice` modules: 
- [Notices related to file parsing and data types](https://github.com/MobilityData/gtfs-validator/tree/master/core/src/java/org/mobilitydata/gtfsvalidator/notice) 
- [Notices related to GTFS semantics/business logic](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/notice). 
 
Note that the notice ID naming conventions changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](/docs/MIGRATION_V1_V2.md) for a mapping between v1 and v2 rules.

## Definitions
Notices are split into three categories: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for items that the [GTFS reference specification](https://github.com/google/transit/tree/master/gtfs/spec/en) explicitly requires or prohibits (e.g., using the language "must"). The validator uses [RFC2119](https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec.
* `WARNING` notices are for items that will affect the quality of GTFS datasets but the GTFS spec does expressly require or prohibit. For example, these might be items recommended using the language "should" or "should not" in the GTFS spec, or items recommended in the MobilityData [GTFS Best Practices](https://gtfs.org/best-practices/).
* `INFO` notices are for items that do not affect the feed's quality, such as unknown files or unknown fields.

<!--suppress ALL -->

<a name="ERRORS"/>

## Table of errors

| Name                                                                                                            	| Description                                                                                                                                                 	|
|-----------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         	| Block trips with overlapping stop times.                                                                                                                    	|
| [`DecreasingOrEqualShapeDistanceNotice`](#DecreasingOrEqualShapeDistanceNotice)                                 	| Decreasing or equal `shape_dist_traveled` in `shapes.txt`.                                                                                                  	|
| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           	| Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                              	|
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             	| Duplicated column in CSV.                                                                                                                                   	|
| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   	| Duplicate rows rows from `fare_rules.txt` based on `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id`. 	|
| [`DuplicateKeyError`](#DuplicateKeyError)                                                                       	| Duplicated entity.                                                                                                                                          	|
| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           	| A CSV file is empty.                                                                                                                                        	|
| [`ForeignKeyError`](#ForeignKeyError)                                                                           	| Wrong foreign key.                                                                                                                                          	|
| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)                                         	| Inconsistent Timezone among agencies.                                                                                                                       	|
| [`InvalidColorNotice`](#InvalidColorNotice)                                                                     	| A field contains an invalid color value.                                                                                                                    	|
| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)                                                               	| A field contains a wrong currency code.                                                                                                                     	|
| [`InvalidDateNotice`](#InvalidDateNotice)                                                                       	| A field cannot be parsed as date.                                                                                                                           	|
| [`InvalidEmailNotice`](#InvalidEmailNotice)                                                                     	| A field contains a malformed email address.                                                                                                                 	|
| [`InvalidFloatNotice`](#InvalidFloatNotice)                                                                     	| A field cannot be parsed as a floating point number.                                                                                                        	|
| [`InvalidIntegerNotice`](#InvalidIntegerNotice)                                                                 	| A field cannot be parsed as an integer.                                                                                                                     	|
| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)                                                       	| A field contains a wrong language code.                                                                                                                     	|
| [`InvalidPhoneNumberNotice`](#InvalidPhoneNumberNotice)                                                         	| A field contains a malformed phone number.                                                                                                                  	|
| [`InvalidRowLengthError`](#InvalidRowLengthError)                                                               	| Invalid csv row length.                                                                                                                                     	|
| [`InvalidTimeNotice`](#InvalidTimeNotice)                                                                       	| A field cannot be parsed as time.                                                                                                                           	|
| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)                                                               	| A field cannot be parsed as a timezone.                                                                                                                     	|
| [`InvalidUrlNotice`](#InvalidUrlNotice)                                                                         	| A field contains a malformed URL.                                                                                                                           	|
| [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)                                     	| The value in CSV file has leading or trailing whitespaces.                                                                                                  	|
| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     	| A location that must have `parent_station` field does not have it.                                                                                          	|
| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       	| Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                                 	|
| [`MissingRequiredColumnError`](#MissingRequiredColumnError)                                                     	| A required column is missing in the input file.                                                                                                             	|
| [`MissingRequiredFieldError`](#MissingRequiredFieldError)                                                       	| A required field is missing.                                                                                                                                	|
| [`MissingRequiredFileError`](#MissingRequiredFileError)                                                         	| A required file is missing.                                                                                                                                 	|
| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               	| Missing trip edge `arrival_time` or `departure_time`.                                                                                                       	|
| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                 	| New line or carriage return in a value in CSV file.                                                                                                         	|
| [`NumberOutOfRangeError`](#NumberOutOfRangeError)                                                               	| Out of range value.                                                                                                                                         	|
| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     	| Trip frequencies overlap.                                                                                                                                   	|
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             	| Missing route short name and long name.                                                                                                                     	|
| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)                                 	| Same name and description for route.                                                                                                                        	|
| [`StartAndEndDateOutOfOrderNotice`](#StartAndEndDateOutOfOrderNotice)                                           	| Two date fields are out of order.                                                                                                                           	|
| [`StartAndEndTimeOutOfOrderNotice`](#StartAndEndTimeOutOfOrderNotice)                                           	| Two time fields are out of order.                                                                                                                           	|
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             	| A station has `parent_station` field set.                                                                                                                   	|
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) 	| Backwards time travel between stops in `stop_times.txt`                                                                                                     	|
| [`StopTimeWithDepartureBeforeArrivalTimeNotice`](#StopTimeWithDepartureBeforeArrivalTimeNotice)                 	| Two time fields are out of order.                                                                                                                           	|
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 	| Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                           	|
| [`URISyntaxError`](#URISyntaxError)                                                                             	| A string could not be parsed as a URI reference.                                                                                                            	|
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               	| Incorrect type of the parent location.                                                                                                                      	|

<a name="WARNINGS"/>

## Table of warnings

| Name                                                                              	| Description                                                                                                                                                 	|
|-----------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [`AttributionWithoutRoleNotice`](#AttributionWithoutRoleNotice)                   	| Attribution with no role.                                                                                                                                   	|
| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	| Duplicate  `routes.route_long_name`. Duplicate `routes.route_short_name`. Duplicate combination of fields `route_long_name`  and `routes.route_short_name`. 	|
| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                 	| A column name is empty.                                                                                                                                     	|
| [`EmptyRowNotice`](#EmptyRowNotice)                                               	| A file is unknown.                                                                                                                                          	|
| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	| Dataset should be valid for at least the next 7 days. Dataset should cover at least the next 30 days of service.                                            	|
| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	| Mismatching feed and agency language fields.                                                                                                                	|
| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	| Inconsistent language among agencies.                                                                                                                       	|
| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	| `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                   	|
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	| More than one row in CSV.                                                                                                                                   	|
| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	| Non ascii or non printable char in  `id`.                                                                                                                   	|
| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	| A platform has no `parent_station` field set.                                                                                                               	|
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	| Insufficient route color contrast.                                                                                                                          	|
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	| Short and long name are equal for a route.                                                                                                                  	|
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	| Short name of a route is too long (more than 12 characters).                                                                                                	|
| [`StartAndEndTimeEqualNotice`](#StartAndEndTimeEqualNotice)                       	| Equal `frequencies.start_time` and `frequencies.end_time`.                                                                                                  	|
| [`StopTimeTimepointWithoutTimesNotice`](#StopTimeTimepointWithoutTimesNotice)     	| `arrival_time` or `departure_time` not specified for timepoint.                                                                                             	|
| [`StopTooFarFromTripShapeNotice`](#StopTooFarFromTripShapeNotice)                 	| Stop too far from trip shape.                                                                                                                               	|
| [`TooFastTravelNotice`](#TooFastTravelNotice)                                     	| Fast travel between stops in `stop_times.txt`.                                                                                                              	|
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

## Notices

### Errors

### BlockTripsWithOverlappingStopTimesNotice

Trips with the same block id have overlapping stop times.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="DecreasingOrEqualShapeDistanceNotice"/>

### DecreasingOrEqualShapeDistanceNotice

When sorted by `shape.shape_pt_sequence`, two consecutive shape points should have increasing values for `shape_dist_traveled`. If the values are equal, this is considered as an error.  

#### References:
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

<a name="DecreasingOrEqualStopTimeDistanceNotice"/>

### DecreasingOrEqualStopTimeDistanceNotice

When sorted by `stop_times.stop_pt_sequence`, two consecutive stop times in a trip should have increasing distance. If the values are equal, this is considered as an error.  

#### References:
* [stops.txt specification](https://gtfs.org/reference/static#stopstxt)

<a name="DuplicatedColumnNotice"/>

### DuplicatedColumnNotice

The input file CSV header has the same column name repeated.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="DuplicateFareRuleZoneIdFieldsNotice"/>

### DuplicateFareRuleZoneIdFieldsNotice

The combination of `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id` fields should be unique in GTFS file `fare_rules.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="DuplicateKeyError"/>

### DuplicateKeyError

The values of the given key and rows are duplicates.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="EmptyFileNotice"/>

### EmptyFileNotice

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.

#### References:
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)

<a name="ForeignKeyError"/>

### ForeignKeyError

The values of the given key and rows of one table cannot be found a values of the given key in another table.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="InconsistentAgencyTimezoneNotice"/>

### InconsistentAgencyTimezoneNotice

Agencies from GTFS `agency.txt` have been found to have different timezones.

#### References:
* [GTFS agency.txt specification](https://gtfs.org/reference/static/#agencytxt)

<a name="InvalidColorNotice"/>

### InvalidColorNotice

Value of field with type `color` is not valid. A color must be encoded as a six-digit hexadecimal number. The leading "#" is not included.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidCurrencyNotice"/>

### InvalidCurrencyNotice

Value of field with type `currency` is not valid. Currency code must follow <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidDateNotice"/>

### InvalidDateNotice

Value of field with type `date` is not valid. Dates must have the YYYYMMDD format.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidEmailNotice"/>

### InvalidEmailNotice

Value of field with type `email` is not valid. 

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons EmailValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/EmailValidator.html)
 
<a name="InvalidFloatNotice"/>

### InvalidFloatNotice

Value of field with type `float` is not valid. 

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
 
<a name="InvalidIntegerNotice"/>

### InvalidIntegerNotice

Value of field with type `integer` is not valid. 

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidLanguageCodeNotice"/>

### InvalidLanguageCodeNotice

Value of field with type `language` is not valid. Language codes must follow <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">IETF BCP 47</a>.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidPhoneNumberNotice"/>

### InvalidPhoneNumberNotice

Value of field with type `phone number` is not valid.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidRowLengthError"/>

### InvalidRowLengthError

A row in the input file has a different number of values than specified by the CSV header.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="InvalidTimeNotice"/>

### InvalidTimeNotice

Value of field with type `time` is not valid. Time must be in the `H:MM:SS`, `HH:MM:SS` or `HHH:MM:SS` format.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidTimezoneNotice"/>

### InvalidTimezoneNotice

Value of field with type `timezone` is not valid.Timezones are defined at <a href="https://www.iana.org/time-zones">www.iana.org</a>. Timezone names never contain the space character but may contain an underscore. Refer to <a href="http://en.wikipedia.org/wiki/List_of_tz_zones">Wikipedia</a> for a list of valid values.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidUrlNotice"/>

### InvalidUrlNotice

Value of field with type `url` is not valid.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons UrlValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)

<a name="LeadingOrTrailingWhitespacesNotice"/>

### LeadingOrTrailingWhitespacesNotice

The value in CSV file has leading or trailing whitespaces.

#### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

<a name="LocationWithoutParentStationNotice"/>

### LocationWithoutParentStationNotice

A location that must have `parent_station` field does not have it. The following location types must have `parent_station`: entrance, generic node, boarding_area.

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="MissingCalendarAndCalendarDateFilesNotice"/>

### MissingCalendarAndCalendarDateFilesNotice

Both files calendar_dates.txt and calendar.txt are missing from the GTFS archive. At least one of the files must be provided.

#### References:
* [calendar.txt specification](http://gtfs.org/reference/static/#calendartxt)
* [calendar_dates.txt specification](http://gtfs.org/reference/static/#calendar_datestxt)

<a name="MissingRequiredColumnError"/>

### MissingRequiredColumnError

A required column is missing in the input file.

#### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingRequiredFieldError"/>

### MissingRequiredFieldError

The given field has no value in some input row, even though values are required.

#### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingRequiredFileError"/>

### MissingRequiredFileError

A required file is missing.

#### References:
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)

<a name="MissingTripEdgeNotice"/>

### MissingTripEdgeNotice

First and last stop of a trip must define both `arrival_time` and `departure_time` fields.

#### References:
* [stop_times.txt specification](https://gtfs.org/reference/static/#stop_timestxt)

<a name="NewLineInValueNotice"/>

### NewLineInValueNotice

A value in CSV file has a new line or carriage return.

#### References:
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)

<a name="NumberOutOfRangeError"/>

### NumberOutOfRangeError

The values in the given column of the input rows are out of range.

#### References:
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS field types](http://gtfs.org/reference/static/#field-types)

<a name="OverlappingFrequencyNotice"/>

### OverlappingFrequencyNotice

Trip frequencies must not overlap in time

#### References:
* [frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)

<a name="RouteBothShortAndLongNameMissingNotice"/>

### RouteBothShortAndLongNameMissingNotice

Both short_name and long_name are missing for a route.

#### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="SameNameAndDescriptionForRouteNotice"/>

### SameNameAndDescriptionForRouteNotice

The GTFS spec defines `routes.txt` [route_description](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_description` fields.

#### References:
[routes.txt specification](http://gtfs.org/reference/static/#routestxt)
[routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="StartAndEndDateOutOfOrderNotice"/>

### StartAndEndDateOutOfOrderNotice

Start and end dates are out-of-order in GTFS files `feed_info.txt` or `calendar.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StartAndEndTimeOutOfOrderNotice"/>

### StartAndEndTimeOutOfOrderNotice

Start and end times are out-of-order in GTFS file `frequencies.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StationWithParentStationNotice"/>

### StationWithParentStationNotice

Field `parent_station` must be empty when `location_type` is 2.

#### References:
[stop_times.txt](http://gtfs.org/reference/static/#stop_timestxt)

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StopTimeWithDepartureBeforeArrivalTimeNotice"/>

### StopTimeWithDepartureBeforeArrivalTimeNotice

The `departure_time` must not precede the `arrival_time` in `stop_times.txt` if both are given. 

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

### StopTimeWithOnlyArrivalOrDepartureTimeNotice

Missing `stop_time.arrival_time` or `stop_time.departure_time`

#### References:
* [stop_times.txt specification](http://gtfs.org/reference/static/#stop_timestxt)

<a name="URISyntaxError"/>

### URISyntaxError

A string could not be parsed as a URI reference.

#### References:
* [GTFS field types](http://gtfs.org/reference/static/#field_types)

<a name="WrongParentLocationTypeNotice"/>

### WrongParentLocationTypeNotice

Value of field `location_type` of parent found in field `parent_station` is invalid.

According to spec
- _Stop/platform_ can only have _Station_ as parent
- _Station_ can NOT have a parent
- _Entrance/exit_ or _generic node_ can only have _Station_ as parent
- _Boarding Area_ can only have _Platform_ as parent 

Any other combination raise this error.

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

### Warnings

<a name="AttributionWithoutRoleNotice"/>

#### AttributionWithoutRoleNotice

At least one of the fields `is_producer`, `is_operator`, or `is_authority` should be set to 1.

##### References:
* [attributions.txt specification](https://gtfs.org/reference/static#attributionstxt)

<a name="DuplicateRouteNameNotice"/>

#### DuplicateRouteNameNotice

All routes should have different `routes.route_long_name` - if two `routes.route_long_name` are the same, and the two routes belong to the same agency, a notice is generated.

Note that there may be valid cases where routes may have the same `routes.route_long_name` and this notice can be ignored. For example, routes may have the same `routes.route_long_name` if they serve difference areas. However, they must not be different trips of the same route or different directions of the same route - these cases should always have unique `routes.route_long_name`.

All routes should have different `routes.route_short_name` - if two `routes.route_short_name` are the same, and the two routes belong to the same agency, a notice is generated.

Note that there may be valid cases where routes may have the same `routes.route_short_name` and this notice can be ignored. For example, routes may have the same routes.route_short_name if they serve difference areas. However, they must not be different trips of the same route or different directions of the same route - these cases should always have unique `routes.route_short_name`.

The same combination of `route_short_name` and `route_long_name` should not be used for more than one route.

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="EmptyColumnNameNotice"/>

#### EmptyColumnNameNotice

A column name has not been provided. Such columns are skipped by the validator.

##### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

<a name="EmptyRowNotice"/>

#### EmptyRowNotice

A row in the input file has only spaces.

##### References:
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)

<a name="FeedExpirationDateNotice"/>

#### FeedExpirationDateNotice

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.
If possible, the GTFS dataset should cover at least the next 30 days of service.

##### References:
* [General Publishing & General Practices](https://gtfs.org/best-practices/#dataset-publishing--general-practices)

<a name="FeedInfoLangAndAgencyLangMismatchNotice"/>

#### FeedInfoLangAndAgencyLangMismatchNotice
1. Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` and `feed_info.feed_lang`.
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

<a name="MissingFeedInfoDateNotice"/>

#### MissingFeedInfoDateNotice

Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided.

##### References:
* [feed_info.txt Best practices](http://gtfs.org/best-practices/#feed_infotxt)

<a name="MoreThanOneEntityNotice"/>

#### MoreThanOneEntityNotice

The file is expected to have a single entity but has more (e.g., "feed_info.txt").

##### References:
* [GTFS field definition](http://gtfs.org/reference/static#field-definitions)

<a name="NonAsciiOrNonPrintableCharNotice"/>

#### NonAsciiOrNonPrintableCharNotice

A value of filed with type `id` contains non ASCII or non printable characters. This is not recommended.

##### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="PlatformWithoutParentStationNotice"/>

#### PlatformWithoutParentStationNotice

A platform has no `parent_station` field set.

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="RouteColorContrastNotice"/>

#### RouteColorContrastNotice

A route's color and `route_text_color` should be contrasting.

#### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="RouteShortAndLongNameEqualNotice"/>

#### RouteShortAndLongNameEqualNotice

Short and long name are equal for a route.

##### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="RouteShortNameTooLongNotice"/>

#### RouteShortNameTooLongNotice

Short name of a route is too long (more than 12 characters).

##### References:
* [routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="StartAndEndTimeEqualNotice"/>

#### StartAndEndTimeEqualNotice

Start and end times are equal in GTFS file `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="StopTimeTimepointWithoutTimeNotice"/>

### StopTimeTimepointWithoutTimesNotice

Any record with `stop_times.timepoint` set to 1 should define a value for `stop_times.arrival_time` and `stop_times.departure_time` fields. 

#### References:
* [GTFS stop_times.txt specification](https://gtfs.org/reference/static#stoptimestxt)

<a name="StopTooFarFromTripShapeNotice"/>

#### StopTooFarFromTripShapeNotice

Per GTFS Best Practices, route alignments (in `shapes.txt`) should be within 100 meters of stop locations which a trip serves.

##### References:
* [GTFS Best Practices shapes.txt](https://gtfs.org/best-practices/#shapestxt)

<a name="TooFastTravelNotice"/>

#### TooFastTravelNotice

As implemented in the original [Google Python GTFS validator](https://github.com/google/transitfeed/wiki/FeedValidator), the calculated speed between stops should not be greater than 150 km/h (42 m/s SI or 93 mph). 

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnexpectedEnumValueNotice"/>

#### UnexpectedEnumValueNotice

An enum has an unexpected value.

#### References:
* [GTFs field definitions](http://gtfs.org/reference/static/#field-definitions)

<a name="UnusableTripNotice"/>

#### UnusableTripNotice

A trip must visit more than one stop in stop_times.txt to be usable by passengers for boarding and alighting.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnusedShapeNotice"/>

#### UnusedShapeNotice

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnusedTripNotice"/>

#### UnusedTripNotice

Trips must be referred to at least once in `stop_times.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

### Info

<a name="UnknownColumnNotice"/>

#### UnknownColumnNotice

A column is unknown.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

<a name="UnknownFileNotice"/>

#### UnknownFileNotice

A file is unknown.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)

