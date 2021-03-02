# Implemented rules

Rules are declared in the `Notice` modules: 
- [Notice related to CSV structure issues](https://github.com/MobilityData/gtfs-validator/tree/master/core/src/java/org/mobilitydata/gtfsvalidator/notice) 
- [Notices related to GTFS semantic rules issues](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/notice). 
 
Note that the notice ID naming conventions changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](https://github.com/MobilityData/gtfs-validator/MIGRATION_V1_V2.md) to have the correspondance between v1 and v2 nomenclature of notices.

## Definitions
Notices are split into three categories: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for items that the [GTFS reference specification](https://github.com/google/transit/tree/master/gtfs/spec/en) explicitly requires or prohibits (e.g., using the language "must"). The validator uses [RFC2119](https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec.
* `WARNING` notices are for items that will affect the quality of GTFS datasets but the GTFS spec does expressly require or prohibit. For example, these might be items recommended using the language "should" or "should not" in the GTFS spec, or items recommended in the MobilityData [GTFS Best Practices](https://gtfs.org/best-practices/).
* `INFO` notices are for items that do not affect the feed's quality, such as unknown files or unknown fields.

## Error notices

| Notice name                                                                                                     	| Notice description                                                                                                                                           	| Notice code                                             	|
|-----------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|---------------------------------------------------------	|
| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         	| Block trips with overlapping stop times.                                                                                                                    	| `block_trips_with_overlapping_stop_times`               	|
| [`DecreasingOrEqualShapeDistanceNotice`](#DecreasingOrEqualShapeDistanceNotice)                                 	| Decreasing or equal `shape_dist_traveled` in `shapes.txt`.                                                                                                  	| `decreasing_or_equal_shape_distance`                    	|
| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           	| Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                              	| `decreasing_or_equal_stop_time_distance`                	|
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             	| Duplicated column in CSV.                                                                                                                                   	| `duplicated_column`                                     	|
| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   	| Duplicate rows rows from `fare_rules.txt` based on `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id`. 	| `duplicate_fare_rule_zone_id_fields`                    	|
| [`DuplicateKeyError`](#DuplicateKeyError)                                                                       	| Duplicated entity.                                                                                                                                          	| `duplicate_key`                                         	|
| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           	| A CSV file is empty.                                                                                                                                        	| `empty_file`                                            	|
| [`ForeignKeyError`](#ForeignKeyError)                                                                           	| Wrong foreign key.                                                                                                                                          	| `foreign_key_error`                                     	|
| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)                                         	| Inconsistent Timezone among agencies.                                                                                                                       	| `inconsistent_agency_timezone`                          	|
| [`InvalidColorNotice`](#InvalidColorNotice)                                                                     	| A field contains an invalid color value.                                                                                                                    	| `invalid_color`                                         	|
| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)                                                               	| A field contains a wrong currency code.                                                                                                                     	| `invalid_currency`                                      	|
| [`InvalidDateNotice`](#InvalidDateNotice)                                                                       	| A field cannot be parsed as date.                                                                                                                           	| `invalid_date`                                          	|
| [`InvalidEmailNotice`](#InvalidEmailNotice)                                                                     	| A field contains a malformed email address.                                                                                                                 	| `invalid_email`                                         	|
| [`InvalidFloatNotice`](#InvalidFloatNotice)                                                                     	| A field cannot be parsed as a floating point number.                                                                                                        	| `invalid_float`                                         	|
| [`InvalidIntegerNotice`](#InvalidIntegerNotice)                                                                 	| A field cannot be parsed as an integer.                                                                                                                     	| `invalid_integer`                                       	|
| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)                                                        	| A field contains a wrong language code.                                                                                                                     	| `invalid_language_code`                                 	|
| [`InvalidPhoneNotice`](#InvalidPhoneNotice)                                                                     	| A field contains a malformed phone number.                                                                                                                  	| `invalid_phone_number`                                  	|
| [`InvalidRowLengthNotice`](#InvalidRowLengthNotice)                                                             	| Invalid csv row length.                                                                                                                                     	| `invalid_row_length`                                    	|
| [`InvalidTimeNotice`](#InvalidTimeNotice)                                                                       	| A field cannot be parsed as time.                                                                                                                           	| `invalid_time`                                          	|
| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)                                                               	| A field cannot be parsed as a timezone.                                                                                                                     	| `invalid_timezone`                                      	|
| [`InvalidUrlNotice`](#InvalidUrlNotice)                                                                         	| A field contains a malformed URL.                                                                                                                           	| `invalid_url`                                           	|
| [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)                                     	| The value in CSV file has leading or trailing whitespaces.                                                                                                  	| `leading_or_trailing_whitespace`                        	|
| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     	| A location that must have `parent_station` field does not have it.                                                                                          	| `location_without_parent_station`                       	|
| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       	| Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                                  	| `missing_calendar_and_calendar_date_files`              	|
| [`MissingRequiredColumnError`](#MissingRequiredColumnError)                                                       | A required column is missing in the input file.                                                                                                              	| `missing_required_column`                                	|
| [`MissingRequiredFieldError`](#MissingRequiredFieldError)                                                       	| A required field is missing.                                                                                                                                	| `missing_required_field`                                	|
| [`MissingRequiredFileError`](#MissingRequiredFileError)                                                         	| A required file is missing.                                                                                                                                 	| `missing_required_file`                                 	|
| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               	| Missing trip edge `arrival_time` or `departure_time`.                                                                                                       	| `missing_trip_edge_arrival_time_departure_time`         	|
| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                  	| New line or carriage return in a value in CSV file.                                                                                                         	| `new_line_in_value`                                     	|
| [`NumberOutOfRangeError`](#NumberOutOfRangeError)                                                               	| Out of range value.                                                                                                                                         	| `number_out_of_range`                                   	|
| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     	| Trip frequencies overlap.                                                                                                                                   	| `overlapping_frequency`                                 	|
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             	| Missing route short name and long name.                                                                                                                     	| `route_both_short_and_long_name_missing`                	|
| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)                                 	| Same name and description for route.                                                                                                                        	| `same_route_name_and_description`                       	|
| [`StartAndEndDateOutOfOrderNotice`](#StartAndEndDateOutOfOrderNotice)                                           	| Two date fields are out of order.                                                                                                                           	| `start_and_end_date_out_of_order`                       	|
| [`StartAndEndTimeOutOfOrderNotice`](#StartAndEndTimeOutOfOrderNotice)                                           	| Two time fields are out of order.                                                                                                                           	| `start_and_end_time_out_of_order`                       	|
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             	| A station has `parent_station` field set.                                                                                                                   	| `station_with_parent_station`                           	|
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) 	| Backwards time travel between stops in `stop_times.txt`                                                                                                     	| `stop_time_with_arrival_before_previous_departure_time` 	|
| [`StopTimeWithDepartureBeforeArrivalTimeNotice`](#StopTimeWithDepartureBeforeArrivalTimeNotice)                 	| Two time fields are out of order.                                                                                                                           	| `stop_time_with_departure_before_arrival_time`          	|
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 	| Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                           	| `stop_time_with_only_arrival_or_departure_time`         	|
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               	| Incorrect type of the parent location.                                                                                                                      	| `wrong_parent_location_type`                            	|

## Warning notices

| Notice name                                                                       	| Notice description                                                                                                                                          	| Notice code                                	|
|-----------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|--------------------------------------------	|
| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	| Duplicate  `routes.route_long_name`. Duplicate `routes.route_short_name`. Duplicate combination of fields `route_long_name`  and `routes.route_short_name`. 	| `duplicate_route_name`                     	|
| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                 	| A column name is empty.                                                                                                                                     	| `empty_column_name`                        	|
| [`EmptyRowNotice`](#EmptyRowNotice)                                               	| A file is unknown.                                                                                                                                          	| `unexpected_file`                          	|
| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	| Dataset should be valid for at least the next 7 days. Dataset should cover at least the next 30 days of service.                                            	| `feed_expires_soon`                        	|
| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	| Mismatching feed and agency language fields.                                                                                                                	| `feed_info_lang_and_agency_lang_mismatch`  	|
| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	| Inconsistent language among agencies.                                                                                                                       	| `inconsistent_agency_lang`                 	|
| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	| `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                   	| `missing_feed_info_start_date_or_end_date` 	|
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	| More than one row in CSV.                                                                                                                                   	| `more_than_one_entity`                     	|
| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	| Non ascii or non printable char in  `id`.                                                                                                                   	| `id_contains_non_ascii_characters`         	|
| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	| A platform has no `parent_station` field set.                                                                                                               	| `platform_without_parent_station`          	|
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	| Insufficient route color contrast.                                                                                                                          	| `route_color_contrast`                     	|
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	| Short and long name are equal for a route.                                                                                                                  	| `route_short_and_long_name_equal`          	|
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	| Short name of a route is too long (more than 12 characters).                                                                                                	| `route_short_name_too_long`                	|
| [`StartAndEndTimeEqualNotice`](#StartAndEndTimeEqualNotice)                       	| Equal `frequencies.start_time` and `frequencies.end_time`.                                                                                                  	| `start_and_end_time_out_of_order`          	|
| [`StopTooFarFromTripShapeNotice`](#StopTooFarFromTripShapeNotice)                 	| Stop too far from trip shape.                                                                                                                               	| `stop_too_far_from_trip_shape`             	|
| [`TooFastTravelNotice`](#TooFastTravelNotice)                                     	| Fast travel between stops in `stop_times.txt`.                                                                                                              	| `too_fast_travel`                          	|
| [`UnexpectedEnumValueError`](#UnexpectedEnumValueError)                           	| An enum has an unexpected value.                                                                                                                            	| `unexpected_enum_value`                    	|
| [`UnusableTripNotice`](#UnusableTripNotice)                                       	| Trips must have more than one stop to be usable.                                                                                                            	| `unusable_trip`                            	|
| [`UnusedShapeNotice`](#UnusedShapeNotice)                                         	| Shape is not used in GTFS file `trips.txt`.                                                                                                                 	| `unused_shape`                             	|
| [`UnusedTripNotice`](#UnusedTripNotice)                                           	| Trip is not be used in `stop_times.txt`                                                                                                                     	| `unused_trip`                              	|

## Info notices

| Notice name                                   	| Notice description           	| Notice code     	|
|-----------------------------------------------	|---------------------------	|-----------------	|
| [`UnknownColumnNotice`](#UnknownColumnNotice) 	| A column name is unknown. 	| unknown_column  	|
| [`UnknownFileNotice`](#UnknownFileNotice)     	| A file is unknown.        	| unexpected_file 	|

## Notices

<!--suppress ALL -->
<a name="BlockTripsWithOverlappingStopTimesNotice"/>

### BlockTripsWithOverlappingStopTimesNotice

Trips with the same block id have overlapping stop times.

<a name="DecreasingOrEqualShapeDistanceNotice"/>

### DecreasingOrEqualShapeDistanceNotice

When sorted by `shape.shape_pt_sequence`, two consecutive shape points should have increasing values for `shape_dist_traveled`. If the values are equal, this is considered as an error.  

<a name="DecreasingOrEqualStopTimeDistanceNotice"/>

### DecreasingOrEqualStopTimeDistanceNotice

When sorted by `stop_times.stop_pt_sequence`, two consecutive stop times in a trip should have increasing distance. If the values are equal, this is considered as an error.  

<a name="DuplicatedColumnNotice"/>

### DuplicatedColumnNotice

The input file CSV header has the same column name repeated.

<a name="DuplicateFareRuleZoneIdFieldsNotice"/>

### DuplicateFareRuleZoneIdFieldsNotice

The combination of `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id` fields should be unique in GTFS file `fare_rules.txt`.

<a name="DuplicateRouteNameNotice"/>

### DuplicateRouteNameNotice

All routes should have different `routes.route_long_name` - if two `routes.route_long_name` are the same, and the two routes belong to the same agency, a notice is generated.

Note that there may be valid cases where routes may have the same `routes.route_long_name` and this notice can be ignored. For example, routes may have the same `routes.route_long_name` if they serve difference areas. However, they must not be different trips of the same route or different directions of the same route - these cases should always have unique `routes.route_long_name`.

All routes should have different `routes.route_short_name` - if two `routes.route_short_name` are the same, and the two routes belong to the same agency, a notice is generated.

Note that there may be valid cases where routes may have the same `routes.route_short_name` and this notice can be ignored. For example, routes may have the same routes.route_short_name if they serve difference areas. However, they must not be different trips of the same route or different directions of the same route - these cases should always have unique `routes.route_short_name`.

The same combination of `route_short_name` and `route_long_name` should not be used for more than one route.

#### References:
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="DuplicateKeyError"/>

### DuplicateKeyError

The values of the given key and rows are duplicates.

<a name="EmptyFileNotice"/>

### EmptyFileNotice

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.

<a name="EmptyColumnNameNotice"/>

### EmptyColumnNameNotice

A column name has not been provided. Such columns are skipped by the validator.

<a name="EmptyRowNotice"/>

### EmptyRowNotice

A row in the input file has only spaces.

<a name="FeedExpirationDateNotice"/>

### FeedExpirationDateNotice

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.
If possible, the GTFS dataset should cover at least the next 30 days of service.

<a name="FeedInfoLangAndAgencyLangMismatchNotice"/>

### FeedInfoLangAndAgencyLangMismatchNotice
1. Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` and `feed_info.feed_lang`.
  The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
  * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
  * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
  * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

#### References:
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)

<a name="ForeignKeyError"/>

### ForeignKeyError

The values of the given key and rows of one table cannot be found a values of the given key in another table.

<a name="InconsistentAgencyLangNotice"/>

### InconsistentAgencyLangNotice

Agencies from GTFS `agency.txt` have been found to have different languages.

<a name="InconsistentAgencyTimezoneNotice"/>

### InconsistentAgencyTimezoneNotice

Agencies from GTFS `agency.txt` have been found to have different timezones.

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

<a name="InvalidPhoneNotice"/>

### InvalidPhoneNotice

Value of field with type `phone number` is not valid.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidRowLengthNotice"/>

### InvalidRowLengthNotice

A row in the input file has a different number of values than specified by the CSV header.

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

<a name="LocationWithoutParentStationNotice"/>

### LocationWithoutParentStationNotice

A location that must have `parent_station` field does not have it. The following location types must have `parent_station`: entrance, generic node, boarding_area.

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="MissingCalendarAndCalendarDateFilesNotice"/>

### MissingCalendarAndCalendarDateFilesNotice

Both files calendar_dates.txt and calendar.txt are missing from the GTFS archive. At least one of the files must be provided.

<a name="MissingFeedInfoDateNotice"/>

### MissingFeedInfoDateNotice

Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided.

<a name="MissingRequiredColumnError"/>

### MissingRequiredColumnError

A required column is missing in the input file.

<a name="MissingRequiredFieldError"/>

### MissingRequiredFieldError

The given field has no value in some input row, even though values are required.

<a name="MissingRequiredFileError"/>

### MissingRequiredFileError

A required file is missing.

<a name="MissingTripEdgeNotice"/>

### MissingTripEdgeNotice

First and last stop of a trip must define both `arrival_time` and `departure_time` fields.

<a name="MissingRequiredColumn"/>

### MoreThanOneEntityNotice

The file is expected to have a single entity but has more (e.g., "feed_info.txt").

<a name="NewLineInValueNotice"/>

### NewLineInValueNotice

A value in CSV file has a new line or carriage return.

<a name="NonAsciiOrNonPrintableCharNotice"/>

### NonAsciiOrNonPrintableCharNotice

A value of filed with type `id` contains non ASCII or non printable characters. This is not recommended.

<a name="NumberOutOfRangeError"/>

### NumberOutOfRangeError

The values in the given column of the input rows are out of range.

<a name="OverlappingFrequencyNotice"/>

### OverlappingFrequencyNotice

Trip frequencies must not overlap in time

#### References:

* [GTFS frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)

<a name="PlatformWithoutParentStationNotice"/>

### PlatformWithoutParentStationNotice

A platform has no `parent_station` field set.

#### References:

* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="RouteBothShortAndLongNameMissingNotice"/>

### RouteBothShortAndLongNameMissingNotice

Both short_name and long_name are missing for a route.

<a name="RouteColorContrastNotice"/>

### RouteColorContrastNotice

A route's color and `route_text_color` should be contrasting.

#### References:

* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="RouteShortAndLongNameEqualNotice"/>

### RouteShortAndLongNameEqualNotice

Short and long name are equal for a route.

#### References:

* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)

<a name="RouteShortNameTooLongNotice"/>

### RouteShortNameTooLongNotice

Short name of a route is too long (more than 12 characters).

#### References:

* [routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="SameNameAndDescriptionForRouteNotice"/>

### SameNameAndDescriptionForRouteNotice

The GTFS spec defines `routes.txt` [route_description](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_description` fields.

References:

[GTFS routes.txt](http://gtfs.org/reference/static/#routestxt)
[GTFS routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="StartAndEndDateOutOfOrderNotice"/>

### StartAndEndDateOutOfOrderNotice

Start and end dates are out-of-order in GTFS files `feed_info.txt` or `calendar.txt`.

<a name="StartAndEndTimeEqualNotice"/>

### StartAndEndTimeEqualNotice

Start and end times are equal in GTFS file `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.

<a name="StartAndEndTimeOutOfOrderNotice"/>

### StartAndEndTimeOutOfOrderNotice

Start and end times are out-of-order in GTFS file `frequencies.txt`.

<a name="StationWithParentStationNotice"/>

### StationWithParentStationNotice

Field `parent_station` must be empty when `location_type` is 2.

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence.

<a name="StopTimeWithDepartureBeforeArrivalTimeNotice"/>

### StopTimeWithDepartureBeforeArrivalTimeNotice

The `departure_time` must not precede the `arrival_time` in `stop_times.txt` if both are given. 

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

### StopTimeWithOnlyArrivalOrDepartureTimeNotice

Missing `stop_time.arrival_time` or `stop_time.departure_time`

<a name="StopTooFarFromTripShapeNotice"/>

### StopTooFarFromTripShapeNotice

Per GTFS Best Practices, route alignments (in `shapes.txt`) should be within 100 meters of stop locations which a trip serves.

#### References:
* [GTFS Best Practices shapes.txt](https://gtfs.org/best-practices/#shapestxt)

<a name="TooFastTravelNotice"/>

### TooFastTravelNotice

As implemented in the original [Google Python GTFS validator](https://github.com/google/transitfeed/wiki/FeedValidator), the calculated speed between stops should not be greater than 150 km/h (42 m/s SI or 93 mph). 

<a name="UnexpectedEnumValueError"/>

### UnexpectedEnumValueError

An enum has an unexpected value.

<a name="UnusableTripNotice"/>

### UnusableTripNotice

A trip must visit more than one stop in stop_times.txt to be usable by passengers for boarding and alighting.

<a name="UnusedShapeNotice"/>

### UnusedShapeNotice

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

<a name="UnusedTripNotice"/>

### UnusedTripNotice

Trips must be referred to at least once in `stop_times.txt`.

<a name="UnknownColumnNotice"/>

### UnknownColumnNotice

A column is unknown.

<a name="UnknownFileNotice"/>

### UnknownFileNotice

A file is unknown.

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
