# Implemented rules

Rules are declared in the `Notice` modules: 
- [Notices related to file parsing and data types](https://github.com/MobilityData/gtfs-validator/tree/master/core/src/java/org/mobilitydata/gtfsvalidator/notice) 
- ```
- [Notices related to GTFS semantics/business logic](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/notice). 
 
Note that the notice ID naming conventions changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](/docs/MIGRATION_V1_V2.md) for a mapping between v1 and v2 rules.

## Definitions
Notices are split into three categories: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for items that the [GTFS reference specification](https://github.com/google/transit/tree/master/gtfs/spec/en) explicitly requires or prohibits (e.g., using the language "must"). The validator uses [RFC2119](https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec.
* `WARNING` notices are for items that will affect the quality of GTFS datasets but the GTFS spec does expressly require or prohibit. For example, these might be items recommended using the language "should" or "should not" in the GTFS spec, or items recommended in the MobilityData [GTFS Best Practices](https://gtfs.org/best-practices/).
* `INFO` notices are for items that do not affect the feed's quality, such as unknown files or unknown fields.

<!--suppress ALL -->

<a name="ERRORS"/>

## Errors

| Name and code                                                                                                                                                                	| Description                                                                                                                                                 	|
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)<br>(`block_trips_with_overlapping_stop_times`)                                       	| Block trips with overlapping stop times.                                                                                                                    	|
| [`DecreasingOrEqualShapeDistanceNotice`](#DecreasingOrEqualShapeDistanceNotice)<br>(`decreasing_or_equal_shape_distance`)                                                    	| Decreasing or equal `shape_dist_traveled` in `shapes.txt`.                                                                                                  	|
| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)<br>(`decreasing_or_equal_stop_time_distance`)                                          	| Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                              	|
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)<br>(`duplicated_column`)                                                                                                 	| Duplicated column in CSV.                                                                                                                                   	|
| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)<br>(`duplicate_fare_rule_zone_id_fields`)                                                      	| Duplicate rows rows from `fare_rules.txt` based on `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id`. 	|
| [`DuplicateKeyError`](#DuplicateKeyError)<br>(`duplicate_key`)                                                                                                               	| Duplicated entity.                                                                                                                                          	|
| [`EmptyFileNotice`](#EmptyFileNotice)<br>(`empty_file`)                                                                                                                      	| A CSV file is empty.                                                                                                                                        	|
| [`ForeignKeyError`](#ForeignKeyError)<br>(`foreign_key_error`)                                                                                                               	| Wrong foreign key.                                                                                                                                          	|
| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)<br>(`inconsistent_agency_timezone`)                                                                  	| Inconsistent Timezone among agencies.                                                                                                                       	|
| [`InvalidColorNotice`](#InvalidColorNotice)<br>(`invalid_color`)                                                                                                             	| A field contains an invalid color value.                                                                                                                    	|
| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)<br>(`invalid_currency`)                                                                                                    	| A field contains a wrong currency code.                                                                                                                     	|
| [`InvalidDateNotice`](#InvalidDateNotice)<br>(`invalid_date`)                                                                                                                	| A field cannot be parsed as date.                                                                                                                           	|
| [`InvalidEmailNotice`](#InvalidEmailNotice)<br>(`invalid_email`)                                                                                                             	| A field contains a malformed email address.                                                                                                                 	|
| [`InvalidFloatNotice`](#InvalidFloatNotice)<br>(`invalid_float`)                                                                                                             	| A field cannot be parsed as a floating point number.                                                                                                        	|
| [`InvalidIntegerNotice`](#InvalidIntegerNotice)<br>(`invalid_integer`)                                                                                                       	| A field cannot be parsed as an integer.                                                                                                                     	|
| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)<br>(`invalid_language_code`)                                                                                       	| A field contains a wrong language code.                                                                                                                     	|
| [`InvalidPhoneNumberNotice`](#InvalidPhoneNumberNotice)<br>(`invalid_phone_number`)                                                                                          	| A field contains a malformed phone number.                                                                                                                  	|
| [`InvalidRowLengthError`](#InvalidRowLengthError)<br>(`invalid_row_length`)                                                                                                  	| Invalid csv row length.                                                                                                                                     	|
| [`InvalidTimeNotice`](#InvalidTimeNotice)<br>(`invalid_time`)                                                                                                                	| A field cannot be parsed as time.                                                                                                                           	|
| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)<br>(`invalid_timezone`)                                                                                                    	| A field cannot be parsed as a timezone.                                                                                                                     	|
| [`InvalidUrlNotice`](#InvalidUrlNotice)<br>(`invalid_url`)                                                                                                                   	| A field contains a malformed URL.                                                                                                                           	|
| [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)<br>(`leading_or_trailing_whitespace`)                                                            	| The value in CSV file has leading or trailing whitespaces.                                                                                                  	|
| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)<br>(`location_without_parent_station`)                                                           	| A location that must have `parent_station` field does not have it.                                                                                          	|
| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)<br>(`missing_calendar_and_calendar_date_files`)                                    	| Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                                 	|
| [`MissingRequiredColumnError`](#MissingRequiredColumnError)<br>(`missing_required_column`)                                                                                   	| A required column is missing in the input file.                                                                                                             	|
| [`MissingRequiredFieldError`](#MissingRequiredFieldError)<br>(`missing_required_field`)                                                                                      	| A required field is missing.                                                                                                                                	|
| [`MissingRequiredFileError`](#MissingRequiredFileError)<br>(`missing_required_file`)                                                                                         	| A required file is missing.                                                                                                                                 	|
| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)<br>(`missing_trip_edge_arrival_time_departure_time`)                                                                       	| Missing trip edge `arrival_time` or `departure_time`.                                                                                                       	|
| [`NewLineInValueNotice`](#NewLineInValueNotice)<br>(`new_line_in_value`)                                                                                                     	| New line or carriage return in a value in CSV file.                                                                                                         	|
| [`NumberOutOfRangeError`](#NumberOutOfRangeError)<br>(`number_out_of_range`)                                                                                                 	| Out of range value.                                                                                                                                         	|
| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)<br>(`overlapping_frequency`)                                                                                     	| Trip frequencies overlap.                                                                                                                                   	|
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)<br>(`route_both_short_and_long_name_missing`)                                            	| Missing route short name and long name.                                                                                                                     	|
| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)<br>(`same_route_name_and_description`)                                                       	| Same name and description for route.                                                                                                                        	|
| [`StartAndEndDateOutOfOrderNotice`](#StartAndEndDateOutOfOrderNotice)<br>(`start_and_end_date_out_of_order`)                                                                 	| Two date fields are out of order.                                                                                                                           	|
| [`StartAndEndTimeOutOfOrderNotice`](#StartAndEndTimeOutOfOrderNotice)<br>(`start_and_end_time_out_of_order`)                                                                 	| Two time fields are out of order.                                                                                                                           	|
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)<br>(`station_with_parent_station`)                                                                       	| A station has `parent_station` field set.                                                                                                                   	|
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice)<br>(`stop_time_with_arrival_before_previous_departure_time`) 	| Backwards time travel between stops in `stop_times.txt`                                                                                                     	|
| [`StopTimeWithDepartureBeforeArrivalTimeNotice`](#StopTimeWithDepartureBeforeArrivalTimeNotice)<br>(`stop_time_with_departure_before_arrival_time`)                          	| Two time fields are out of order.                                                                                                                           	|
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)<br>(`stop_time_with_only_arrival_or_departure_time`)                         	| Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                           	|
| [`URISyntaxError`](#URISyntaxError)<br>(`uri_syntax_error`)                                                                                                                  	| A string could not be parsed as a URI reference.                                                                                                            	|
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)<br>(`wrong_parent_location_type`)                                                                          	| Incorrect type of the parent location.                                                                                                                      	|

<a name="WARNINGS"/>

## Warnings

| Name and code                                                                                                                    	| Description                                                                                                                                                 	|
|----------------------------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)<br>(`duplicate_route_name`)                                              	| Duplicate  `routes.route_long_name`. Duplicate `routes.route_short_name`. Duplicate combination of fields `route_long_name`  and `routes.route_short_name`. 	|
| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)<br>(`empty_column_name`)                                                       	| A column name is empty.                                                                                                                                     	|
| [`EmptyRowNotice`](#EmptyRowNotice)<br>(`unexpected_file`)                                                                       	| A file is unknown.                                                                                                                                          	|
| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)<br>(`feed_expires_soon`)                                                 	| Dataset should be valid for at least the next 7 days. Dataset should cover at least the next 30 days of service.                                            	|
| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice)<br>(`feed_info_lang_and_agency_lang_mismatch`) 	| Mismatching feed and agency language fields.                                                                                                                	|
| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)<br>(`inconsistent_agency_lang`)                                  	| Inconsistent language among agencies.                                                                                                                       	|
| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)<br>(`missing_feed_info_start_date_or_end_date`)                        	| `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                   	|
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)<br>(`more_than_one_entity`)                                                	| More than one row in CSV.                                                                                                                                   	|
| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)<br>(`id_contains_non_ascii_characters`)                  	| Non ascii or non printable char in  `id`.                                                                                                                   	|
| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)<br>(`platform_without_parent_station`)               	| A platform has no `parent_station` field set.                                                                                                               	|
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)<br>(`route_color_contrast`)                                              	| Insufficient route color contrast.                                                                                                                          	|
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)<br>(`route_short_and_long_name_equal`)                   	| Short and long name are equal for a route.                                                                                                                  	|
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)<br>(`route_short_name_too_long`)                                   	| Short name of a route is too long (more than 12 characters).                                                                                                	|
| [`StartAndEndTimeEqualNotice`](#StartAndEndTimeEqualNotice)<br>(`start_and_end_time_out_of_order`)                               	| Equal `frequencies.start_time` and `frequencies.end_time`.                                                                                                  	|
| [`StopTooFarFromTripShapeNotice`](#StopTooFarFromTripShapeNotice)<br>(`stop_too_far_from_trip_shape`)                            	| Stop too far from trip shape.                                                                                                                               	|
| [`TooFastTravelNotice`](#TooFastTravelNotice)<br>(`too_fast_travel`)                                                             	| Fast travel between stops in `stop_times.txt`.                                                                                                              	|
| [`UnexpectedEnumValueError`](#UnexpectedEnumValueError)<br>(`unexpected_enum_value`)                                             	| An enum has an unexpected value.                                                                                                                            	|
| [`UnusableTripNotice`](#UnusableTripNotice)<br>(`unusable_trip`)                                                                 	| Trips must have more than one stop to be usable.                                                                                                            	|
| [`UnusedShapeNotice`](#UnusedShapeNotice)<br>(`unused_shape`)                                                                    	| Shape is not used in GTFS file `trips.txt`.                                                                                                                 	|
| [`UnusedTripNotice`](#UnusedTripNotice)<br>(`unused_trip`)                                                                       	| Trip is not be used in `stop_times.txt`                                                                                                                     	|

<a name="INFOS"/>

## Info

| Name and code                                                       	| Description               	|
|---------------------------------------------------------------------	|---------------------------	|
| [`UnknownColumnNotice`](#UnknownColumnNotice)<br>(`unknown_column`) 	| A column name is unknown. 	|
| [`UnknownFileNotice`](#UnknownFileNotice)<br>(`unexpected_file`)    	| A file is unknown.        	|

## Notices

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

<a name="InvalidPhoneNumberNotice"/>

### InvalidPhoneNumberNotice

Value of field with type `phone number` is not valid.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="InvalidRowLengthError"/>

### InvalidRowLengthError

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

<a name="URISyntaxError"/>

### URISyntaxError

A string could not be parsed as a URI reference.

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
