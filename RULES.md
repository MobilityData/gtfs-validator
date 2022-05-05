# Implemented rules
This document lists all the notices that are emitted by this validator.\
Note that the notice naming convention changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](/docs/MIGRATION_V1_V2.md) for a mapping between v1 and v2 notices.\
Note that some severities were modified in `v3` to solve discrepancies with the specification. Please refer to [MIGRATION_V2_V3.md](/docs/MIGRATION_V2_V3.md) for a mapping between v2 and v3 notices.
<a name="definitions"/>

## Definitions
### A Rule
A part of the specification that is translated into code in the validator. A Rule will describe if a set of conditions is met or not. For example:
  - In the specification: in the `stops.txt` file, the field `zone_id` is required if providing fare information using `fare_rules.txt` ([source in the spec](https://gtfs.org/schedule/reference/#stopstxt)).
  - In the validator: this is translated into code in the file `StopZoneIdValidator.java` ([link to the validator file](https://github.com/MobilityData/gtfs-validator/blob/master/main/src/main/java/org/mobilitydata/gtfsvalidator/validator/StopZoneIdValidator.java)).

### A Notice
What the Rule outputs if the conditions aren’t met. It is what the user will see in the validation report.
- For example, the output of `StopZoneIdValidator.java` Rule is the Notice `StopWithoutZoneIdNotice`, and it has the notice code `stop_without_zone_id`. 

### The Severity of a Notice

Each Notice is associated with a severity:: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for items that the [GTFS reference specification](https://github.com/google/transit/tree/master/gtfs/spec/en) explicitly requires or prohibits (e.g., using the language "must"). The validator uses [RFC2119](https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec.
  * ⚠️ Please note that this validator also generates `System Errors` that give information about things that may have gone wrong during the validation process such as the inability to unzip a GTFS file. These are generated in a second report `system_errors.json`.
* `WARNING` notices are for items that will affect the quality of GTFS datasets but the GTFS spec does expressly require or prohibit. For example, these might be items recommended using the language "should" or "should not" in the GTFS spec, or items recommended in the MobilityData [GTFS Best Practices](https://gtfs.org/best-practices/).
* `INFO` notices are for items that do not affect the feed's quality, such as unknown files or unknown fields.

<!--suppress ALL -->

<a name="ERRORS"/>

## Table of ERRORS

| Notice name                                                                                                     | Description                                                                                                                                            | Notice Code (as it appears in the `report.json` file) |
|-----------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         | Block trips with overlapping stop times.                                                                                                               | block_trips_with_overlapping_stop_times               |
| [`CsvParsingFailedNotice`](#CsvParsingFailedNotice)                                                             | Parsing of a CSV file failed.                                                                                                                          | csv_parsing_failed                                    |
| [`DecreasingShapeDistanceNotice`](#DecreasingShapeDistanceNotice)                                               | Decreasing `shape_dist_traveled` in `shapes.txt`.                                                                                                      | decreasing_shape_distance                             |
| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           | Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                         | decreasing_or_equal_stop_time_distance                |
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             | Duplicated column in CSV.                                                                                                                              | duplicated_column                                     |
| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   | Duplicate rows from `fare_rules.txt` based on `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id`. | duplicate_fare_rule_zone_id_fields                    |
| [`DuplicateKeyNotice`](#DuplicateKeyNotice)                                                                     | Duplicated entity.                                                                                                                                     | duplicate_key                                         |
| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                                            	  | A column name is empty.                                                                                                                                | empty_column_name                                     |
| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           | A CSV file is empty.                                                                                                                                   | empty_file                                            |
| [`EqualShapeDistanceDiffCoordinatesNotice`](#EqualShapeDistanceDiffCoordinatesNotice)                           | Two consecutive points have equal `shape_dist_traveled` and different lat/lon coordinates in `shapes.txt`.                                             | equal_shape_distance_diff_coordinates                 |
| [`ForeignKeyViolationNotice`](#ForeignKeyViolationNotice)                                                       | Wrong foreign key.                                                                                                                                     | foreign_key_violation                                 |
| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)                                         | Inconsistent Timezone among agencies.                                                                                                                  | inconsistent_agency_timezone                          |
| [`InvalidColorNotice`](#InvalidColorNotice)                                                                     | A field contains an invalid color value.                                                                                                               | invalid_color                                         |
| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)                                                               | A field contains a wrong currency code.                                                                                                                | invalid_currency                                      |
| [`InvalidDateNotice`](#InvalidDateNotice)                                                                       | A field cannot be parsed as date.                                                                                                                      | invalid_date                                          |
| [`InvalidEmailNotice`](#InvalidEmailNotice)                                                                     | A field contains a malformed email address.                                                                                                            | invalid_email                                         |
| [`InvalidFloatNotice`](#InvalidFloatNotice)                                                                     | A field cannot be parsed as a floating point number.                                                                                                   | invalid_float                                         |
| [`InvalidIntegerNotice`](#InvalidIntegerNotice)                                                                 | A field cannot be parsed as an integer.                                                                                                                | invalid_integer                                       |
| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)                                                       | A field contains a wrong language code.                                                                                                                | invalid_language_code                                 |
| [`InvalidPhoneNumberNotice`](#InvalidPhoneNumberNotice)                                                         | A field contains a malformed phone number.                                                                                                             | invalid_phone_number                                  |
| [`InvalidRowLengthNotice`](#InvalidRowLengthNotice)                                                             | Invalid csv row length.                                                                                                                                | invalid_row_length                                    |
| [`InvalidTimeNotice`](#InvalidTimeNotice)                                                                       | A field cannot be parsed as time.                                                                                                                      | invalid_time                                          |
| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)                                                               | A field cannot be parsed as a timezone.                                                                                                                | invalid_timezone                                      |
| [`InvalidUrlNotice`](#InvalidUrlNotice)                                                                         | A field contains a malformed URL.                                                                                                                      | invalid_url                                           |
| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     | A location that must have `parent_station` field does not have it.                                                                                     | location_without_parent_station                       |
| [`LocationWithUnexpectedStopTimeNotice`](#LocationWithUnexpectedStopTimeNotice)                                 | A location in `stops.txt` that is not a stop is referenced by some `stop_times.stop_id`.                                                               | location_with_unexpected_stop_time                    |
| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       | Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                            | missing_calendar_and_calendar_date_files              |
| [`MissingLevelIdNotice`](#MissingLevelIdNotice)       	                                                      | `stops.level_id` is conditionally required.                                                                                                            | missing_level_id                                      |
| [`MissingRequiredColumnNotice`](#MissingRequiredColumnNotice)                                                   | A required column is missing in the input file.                                                                                                        | missing_required_column                               |
| [`MissingRequiredFieldNotice`](#MissingRequiredFieldNotice)                                                     | A required field is missing.                                                                                                                           | missing_required_field                                |
| [`MissingRequiredFileNotice`](#MissingRequiredFileNotice)                                                       | A required file is missing.                                                                                                                            | missing_required_file                                 |
| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               | Missing trip edge `arrival_time` or `departure_time`.                                                                                                  | missing_trip_edge                                     |
| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                 | New line or carriage return in a value in CSV file.                                                                                                    | new_line_in_value                                     |
| [`NumberOutOfRangeNotice`](#NumberOutOfRangeNotice)                                                             | Out of range value.                                                                                                                                    | number_out_of_range                                   |
| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     | Trip frequencies overlap.                                                                                                                              | overlapping_frequency                                 |
| [`PathwayToPlatformWithBoardingAreasNotice`](#PathwayToPlatformWithBoardingAreasNotice)                         | A pathway has an endpoint that is a platform which has boarding areas.                                                                                 | pathway_to_platform_with_boarding_areas               |
| [`PathwayToWrongLocationTypeNotice`](#PathwayToWrongLocationTypeNotice)                                         | A pathway has an endpoint that is a station.                                                                                                           | pathway_to_wrong_location_type                        |
| [`PathwayUnreachableLocationNotice`](#PathwayUnreachableLocationNotice)                                         | A location is not reachable at least in one direction: from the entrances or to the exits.                                                             | pathway_unreachable_location                          |
| [`PointNearOriginNotice`](#PointNearOriginNotice)                                                               | A point is too close to origin `(0, 0)`.                                                                                                               | point_near_origin                                     |
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             | Missing route short name and long name.                                                                                                                | route_both_short_and_long_name_missing                |
| [`StartAndEndRangeEqualNotice`](#StartAndEndRangeEqualNotice)                                                   | Two date or time fields are equal.                                                                                                                     | start_and_end_range_equal                             |
| [`StartAndEndRangeOutOfOrderNotice`](#StartAndEndRangeOutOfOrderNotice)                                         | Two date or time fields are out of order.                                                                                                              | start_and_end_range_out_of_order                      |
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             | A station has `parent_station` field set.                                                                                                              | station_with_parent_station                           |
| [`StopTimeTimepointWithoutTimesNotice`](#StopTimeTimepointWithoutTimesNotice)     	                          | `arrival_time` or `departure_time` not specified for timepoint.                                                                                        | stop_time_timepoint_without_times                     |
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) | Backwards time travel between stops in `stop_times.txt`                                                                                                | stop_time_with_arrival_before_previous_departure_time  |
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 | Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                      | stop_time_with_only_arrival_or_departure_time         |
| [`StopWithoutZoneIdNotice`](#StopWithoutZoneIdNotice)                                                           | Stop without value for `stops.zone_id`.                                                                                                                | stop_without_zone_id                                  |
| [`TranslationForeignKeyViolationNotice`](#TranslationForeignKeyViolationNotice)                                 | An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.                                                      | translation_foreign_key_violation                     |
| [`TranslationUnexpectedValueNotice`](#TranslationUnexpectedValueNotice)                                         | A field in a translations row has value but must be empty.                                                                                             | translation_unexpected_value                          |
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               | Incorrect type of the parent location.                                                                                                                 | wrong_parent_location_type                            |

<a name="WARNINGS"/>

## Table of warnings

| Notice name                                                                          	| Description                                                                                                                                                 	| Notice Code (as it appears in the `report.json` file) |
|-----------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------|
| [`AttributionWithoutRoleNotice`](#AttributionWithoutRoleNotice)                   	| Attribution with no role.                                                                                                                                   	| attribution_without_role                              |
| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	| Two distinct routes have either the same `route_short_name`, the same `route_long_name`, or the same combination of `route_short_name` and `route_long_name`. | duplicate_route_name                                  |
| [`EmptyRowNotice`](#EmptyRowNotice)                                               	| A row in the input file has only spaces.                                                                                                                      | empty_row                                             |
| [`EqualShapeDistanceSameCoordinatesNotice`](#EqualShapeDistanceSameCoordinatesNotice) | Two consecutive points have equal `shape_dist_traveled` and the same lat/lon coordinates in `shapes.txt`.                                                     | equal_shape_distance_same_coordinates                 |
| [`FastTravelBetweenConsecutiveStopsNotice`](#FastTravelBetweenConsecutiveStopsNotice) | A transit vehicle moves too fast between two consecutive stops.                                                                            	                | fast_travel_between_consecutive_stops                 |
| [`FastTravelBetweenFarStopsNotice`](#FastTravelBetweenFarStopsNotice)                 | A transit vehicle moves too fast between two far stops.                                                                            	                        | fast_travel_between_far_stops                         |
| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	| Dataset should be valid for at least the next 7 days. Dataset should cover at least the next 30 days of service.                                            	| feed_expiration_date                                  |
| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	| Mismatching feed and agency language fields.                                                                                                                	| feed_info_lang_and_agency_mismatch                    |
| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	| Inconsistent language among agencies.                                                                                                                       	| inconsistent_agency_lang                              |
| [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)           | The value in CSV file has leading or trailing whitespaces.                                                                                                  	| leading_or_trailing_whitespaces                       |
| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	| `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                   	| missing_feed_info_date                                |
| [`MissingTimepointColumnNotice`](#MissingTimepointColumnNotice)                       | `timepoint` column is missing for a dataset.                                                                                                        	        | missing_timepoint_column                              |
| [`MissingTimepointValueNotice`](#MissingTimepointValueNotice)                         | `stop_times.timepoint` value is missing for a record.                                                                                                        	| missing_timepoint_value                               |
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	| More than one row in CSV.                                                                                                                                   	| more_than_one_entity                                  |
| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	| Non ascii or non printable char in  `id`.                                                                                                                   	| non_ascii_or_non_printable_char                       |
| [`PathwayDanglingGenericNodeNotice`](#PathwayDanglingGenericNodeNotice)           	| A generic node has only one incident location in a pathway graph.                                                                                             | pathway_dangling_generic_node                         |
| [`PathwayLoopNotice`](#PathwayLoopNotice)                                         	| A pathway starts and ends at the same location.                                                                                                               | pathway_loop                                          |
| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	| A platform has no `parent_station` field set.                                                                                                               	| platform_without_parent_station                       |
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	| Insufficient route color contrast.                                                                                                                          	| route_color_contrast                                  |
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	| `route_short_name` and `route_long_name` are equal for a single route.                                                                                        | route_short_and_long_name_equal                       |
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	| Short name of a route is too long (more than 12 characters).                                                                                                	| route_short_name_too_long                             |
| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)       | Same name and description for route.                                                                                                                        	| same_name_and_description_for_route                   |
| [`SameNameAndDescriptionForStopNotice`](#SameNameAndDescriptionForStopNotice)         | Same name and description for stop.                                                                                                                      	    | same_name_and_description_for_stop                    |
| [`SameRouteAndAgencyUrlNotice`](#SameRouteAndAgencyUrlNotice)                         | Same `routes.route_url` and `agency.agency_url`.                                                                                                  	        | same_route_and_agency_url                             |
| [`SameStopAndAgencyUrlNotice`](#SameStopAndAgencyUrlNotice)                           | Same `stops.stop_url` and `agency.agency_url`.                                                                                                  	            | same_stop_and_agency_url                              |
| [`SameStopAndRouteUrlNotice`](#SameStopAndRouteUrlNotice)                          	| Same `stops.stop_url` and `routes.route_url`.                                                                                                  	            | same_stop_and_route_url                               |
| [`StopHasTooManyMatchesForShapeNotice`](#StopHasTooManyMatchesForShapeNotice)     	| Stop entry that has many potential matches to the trip's path of travel.                                                                                      | stop_has_too_many_matches_for_shape                   |
| [`StopsMatchShapeOutOfOrderNotice`](#StopsMatchShapeOutOfOrderNotice)     	        | Two stop entries are different than their arrival-departure order defined by the shapes.txt                                                                   | stops_match_shape_out_of_order                        |
| [`StopTooFarFromShapeNotice`](#StopTooFarFromShapeNotice)                 	        | Stop too far from trip shape.                                                                                                                               	| stop_too_far_from_shape                               |
| [`StopTooFarFromShapeUsingUserDistanceNotice`](#StopTooFarFromShapeUsingUserDistanceNotice)| Stop time too far from shape.                                                                                                                            | stop_too_far_from_shape_using_user_distance           |
| [`StopWithoutStopTimeNotice`](#StopWithoutStopTimeNotice)                             | A stop in `stops.txt` is not referenced by any `stop_times.stop_id`.                                                                                          | stop_without_stop_time                                |
| [`TranslationUnknownTableNameNotice`](#TranslationUnknownTableNameNotice)             | A translation references an unknown or missing GTFS table.                                                                                                    | translation_unknown_table_name                        |
| [`UnexpectedEnumValueNotice`](#UnexpectedEnumValueNotice)                         	| An enum has an unexpected value.                                                                                                                            	| unexpected_enum_value                                 |
| [`UnusableTripNotice`](#UnusableTripNotice)                                       	| Trips must have more than one stop to be usable.                                                                                                            	| unusable_trip                                         |
| [`UnusedShapeNotice`](#UnusedShapeNotice)                                         	| Shape is not used in GTFS file `trips.txt`.                                                                                                                 	| unused_shape                                          |
| [`UnusedTripNotice`](#UnusedTripNotice)                                           	| Trip is not be used in `stop_times.txt`                                                                                                                     	| unused_trip                                           |

<a name="INFOS"/>

## Table of info

| Notice name                                     	| Description               	| Notice Code (as it appears in the `report.json` file) |
|-----------------------------------------------	|---------------------------	|-------------------------------------------------------|
| [`UnknownColumnNotice`](#UnknownColumnNotice) 	| A column name is unknown. 	| unknown_column_notice                                 |
| [`UnknownFileNotice`](#UnknownFileNotice)     	| A file is unknown.        	|unknown_file_notice                                    |

<a name="SYSTEM_ERRORS"/>

## Table of system errors

| System error name                                                         | Description                                            	| Notice Code (as it appears in the `system_errors.json` file) |
|-------------------------------------------------------------------------	|--------------------------------------------------------	|--------------------------------------------------------------|
| [`IOError`](#IOError)                                                   	| Error in IO operation.                                 	| i_o_error                                                    |
| [`RuntimeExceptionInLoaderError`](#RuntimeExceptionInLoaderError)       	| RuntimeException while loading GTFS dataset in memory. 	| runtime_exception_in_loader_error                            |
| [`RuntimeExceptionInValidatorError`](#RuntimeExceptionInValidatorError) 	| RuntimeException while validating GTFS archive.        	| runtime_exception_in_validator_error                         |
| [`ThreadExecutionError`](#ThreadExecutionError)                         	| ExecutionException during multithreaded validation     	| thread_execution_error                                       |
| [`URISyntaxError`](#URISyntaxError)                                     	| A string could not be parsed as a URI reference.       	| u_r_i_syntax_error                                           |

## Errors

<a name="BlockTripsWithOverlappingStopTimesNotice"/>

### BlockTripsWithOverlappingStopTimesNotice

Trips with the same block id have overlapping stop times.
<details>

#### Notice fields description
| Field name      	| Description                               	            | Type   	|
|-----------------	|---------------------------------------------------------	|--------	|
| `csvRowNumberA` 	| The row number from `trips.txt` of the first faulty trip. | Long   	|
| `tripIdA`       	| The id of first faulty trip.                           	| String 	|
| `serviceIdA`    	| The service id of the first faulty trip.                 	| String 	|
| `csvRowNumberB` 	| The row number from `trips.txt` of the second faulty trip.| Long   	|
| `tripIdB`       	| The id of the other faulty trip.             	            | String 	|
| `serviceIdB`    	| The service id of the other faulty trip.     	            | String 	|
| `blockId`       	| The `trips.block_id` of the overlapping trip.	            | String 	|
| `intersection`  	| The overlapping period.                      	            | Date   	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="CsvParsingFailedNotice"/>

### CsvParsingFailedNotice

Parsing of a CSV file failed. One common case of the problem is when a cell value contains more than 4096 characters.

<details>

#### Notice fields description
| Field name    	| Description                                                                             	| Type    	|
|---------------	|-----------------------------------------------------------------------------------------	|---------	|
| `filename`    	| The name of the faulty file.                                                            	| Long    	|
| `charIndex`   	| The location of the last character read from before the error occurred.                 	| Long    	|
| `columnIndex` 	| The column index where the exception occurred.                                          	| Integer 	|
| `lineIndex`   	| The line number where the exception occurred.                                           	| Long    	|
| `message`     	| The detailed message describing the error, and the internal state of the parser/writer. 	| String  	|
| `content`     	| The record number when the exception occurred.                                          	| String  	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)
</details>

<a name="DecreasingShapeDistanceNotice"/>

### DecreasingShapeDistanceNotice

When sorted by `shape.shape_pt_sequence`, two consecutive shape points must not have decreasing values for `shape_dist_traveled`.  

<details>

#### Notice fields description
| Field name            	  | Description                                                                                    	| Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

#### Affected files
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

</details>

<a name="DecreasingOrEqualStopTimeDistanceNotice"/>

### DecreasingOrEqualStopTimeDistanceNotice

When sorted by `stop_times.stop_pt_sequence`, two consecutive stop times in a trip should have increasing distance. If the values are equal, this is considered as an error.  

<details>

#### Notice fields description
| Field name               	| Description                                                                                    	| Type    	|
|--------------------------	|------------------------------------------------------------------------------------------------	|---------	|
| `tripId`                  | The id of the faulty trip.                                                                     	| String  	|
| `csvRowNumber`            | The row number from `stop_times.txt`.                                                          	| Long    	|
| `shapeDistTraveled`       | Actual distance traveled along the shape from the first shape point to the faulty record.      	| Double  	|
| `stopSequence`            | The faulty record's `stop_times.stop_sequence`.                                                	| Integer 	|
| `prevCsvRowNumber`        | The row number from `stop_times.txt` of the previous stop time.                                	| Long    	|
| `prevStopTimeDistTraveled`| Actual distance traveled along the shape from the first shape point to the previous stop time. 	| Double  	|
| `prevStopSequence`        | The previous record's `stop_times.stop_sequence`.                                              	| Integer 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [stops.txt specification](https://gtfs.org/reference/static#stopstxt)
</details>

<a name="DuplicatedColumnNotice"/>

### DuplicatedColumnNotice

The input file CSV header has the same column name repeated.

<details>

#### Notice fields description
| Field name  	| Description                   	| Type    	|
|-------------	|-------------------------------	|---------	|
| `filename`    | The name of the faulty file.  	| String  	|
| `fieldName`   | The name of the faulty field. 	| String  	|
| `firstIndex`  | Index of the first occurrence. 	| Integer 	|
| `secondIndex` | Index of the other occurrence. 	| Integer 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="DuplicateFareRuleZoneIdFieldsNotice"/>

### DuplicateFareRuleZoneIdFieldsNotice

The combination of `fare_rules.route_id`, `fare_rules.origin_id`, `fare_rules.contains_id` and `fare_rules.destination_id` fields should be unique in GTFS file `fare_rules.txt`.

<details>

#### Notice fields description
| Field name           	| Description                     	| Type    	|
|----------------------	|---------------------------------	|---------	|
| `csvRowNumber`       	| The row of the first occurrence. 	| Long  	  |
| `fareId`             	| The id of the first occurrence.  	| String  	|
| `previousCsvRowNumber`| The row of the other occurrence. 	| Long   	  |
| `previousFareId`     	| The id of the other occurrence.  	| Integer 	|

#### Affected files
* [fare_rules.txt](http://gtfs.org/reference/static/#fare_rulestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="DuplicateKeyNotice"/>

### DuplicateKeyNotice

The values of the given key and rows are duplicates.

<details>

#### Notice fields description
| Field name      	| Description                        	| Type   	|
|-----------------	|------------------------------------	|--------	|
| `filename`       	| The name of the faulty file        	| String 	|
| `oldCsvRowNumber`	| The row of the first occurrence.    | Long   	|
| `newCsvRowNumber`	| The row of the other occurrence.   	| Long   	|
| `fieldName1`     	| Composite key's first field name.  	| String 	|
| `fieldValue1`    	| Composite key's first value.       	| Object 	|
| `fieldName2`     	| Composite key's second field name. 	| String 	|
| `fieldValue2`    	| Composite key's second value.      	| Object 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="EmptyColumnNameNotice"/>

### EmptyColumnNameNotice

A column name has not been provided. Such columns are skipped by the validator.

<details>

#### Notice fields description
| Field name 	| Description                    	  | Type    	|
|------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.   	| String   	|
| `index`      	| The index of the empty column. 	| Integer 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
</details>

<a name="EmptyFileNotice"/>

### EmptyFileNotice

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.

<details>

#### Notice fields description
| Field name 	| Description                 	| Type   	|
|------------	|-----------------------------	|--------	|
| `filename`  | The name of the faulty file   | String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)
</details>

### EqualShapeDistanceDiffCoordinatesNotice

<a name="EqualShapeDistanceDiffCoordinatesNotice"/>

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and different coordinates indicate an error.

<details>

#### Notice fields description
| Field name            	  | Description                                                                                    	  | Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------	|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)
</details>

<a name="ForeignKeyViolationNotice"/>

### ForeignKeyViolationNotice

The values of the given key and rows of one table cannot be found a values of the given key in another table. The Foreign keys are defined in the specification under "Type" for each file.

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `childFilename`  	| The name of the file from which reference is made. 	| String 	|
| `childFieldName` 	| The name of the field that makes reference.        	| String 	|
| `parentFilename` 	| The name of the file that is referred to.          	| String 	|
| `parentFieldName`	| The name of the field that is referred to.         	| String 	|
| `fieldValue`     	| The faulty record's value.                         	| String 	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

#### Affected files
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`fare_rules.txt`](http://gtfs.org/reference/static#fare_rulestxt)
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`tranfers.txt`](http://gtfs.org/reference/static#tranferstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="InconsistentAgencyTimezoneNotice"/>

### InconsistentAgencyTimezoneNotice

Agencies from GTFS `agency.txt` have been found to have different timezones.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `expected`   	| Expected timezone.            	| String 	|
| `actual`     	| Faulty record's timezone.     	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

#### References
* [GTFS agency.txt specification](https://gtfs.org/reference/static/#agencytxt)
</details>

<a name="InvalidColorNotice"/>

### InvalidColorNotice

Value of field with type `color` is not valid. A color must be encoded as a six-digit hexadecimal number. The leading "#" is not included.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidCurrencyNotice"/>

### InvalidCurrencyNotice

Value of field with type `currency` is not valid. Currency code must follow <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidDateNotice"/>

### InvalidDateNotice

Value of field with type `date` is not valid. Dates must have the YYYYMMDD format.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidEmailNotice"/>

### InvalidEmailNotice

Value of field with type `email` is not valid. Definitions for valid emails are quite vague. We perform strict validation in the upstream using the Apache Commons EmailValidator.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons EmailValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/EmailValidator.html)
 </details>

<a name="InvalidFloatNotice"/>

### InvalidFloatNotice

Value of field with type `float` is not valid. 

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>
 
<a name="InvalidIntegerNotice"/>

### InvalidIntegerNotice

Value of field with type `integer` is not valid. 

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`transfers.txt`](http://gtfs.org/reference/static#transferstxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidLanguageCodeNotice"/>

### InvalidLanguageCodeNotice

Value of field with type `language` is not valid. Language codes must follow <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">IETF BCP 47</a>.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidPhoneNumberNotice"/>

### InvalidPhoneNumberNotice

Value of field with type `phone number` is not valid. This rule uses the [PhoneNumberUtil](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.4.1/com/google/i18n/phonenumbers/PhoneNumberUtil.html) class to validate a phone number based on a country code. If no country code is provided in the parameters used to run the validator, this notice won't be emitted. 

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidRowLengthNotice"/>

### InvalidRowLengthNotice

A row in the input file has a different number of values than specified by the CSV header.

<details>

#### Notice fields description
| Field name   	| Description                              	| Type   	|
|--------------	|------------------------------------------	|--------	|
| `filename`   	| The row of the faulty record.            	| String 	|
| `csvRowNumber`| The row of the faulty record.            	| Long   	|
| `rowLength`  	| The length of the faulty record.         	| Integer |
| `headerCount`	| The number of column in the faulty file. 	| Intege 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="InvalidTimeNotice"/>

### InvalidTimeNotice

Value of field with type `time` is not valid. Time must be in the `H:MM:SS`, `HH:MM:SS` or `HHH:MM:SS` format.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidTimezoneNotice"/>

### InvalidTimezoneNotice

Value of field with type `timezone` is not valid.Timezones are defined at <a href="https://www.iana.org/time-zones">www.iana.org</a>. Timezone names never contain the space character but may contain an underscore. Refer to <a href="http://en.wikipedia.org/wiki/List_of_tz_zones">Wikipedia</a> for a list of valid values.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
</details>

<a name="InvalidUrlNotice"/>

### InvalidUrlNotice

Value of field with type `url` is not valid. Definitions for valid URLs are quite vague. We perform strict validation in the upstream using the Apache Commons UrlValidator.

<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons UrlValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)
</details>

<a name="LocationWithoutParentStationNotice"/>

### LocationWithoutParentStationNotice

A location that must have `parent_station` field does not have it. The following location types must have `parent_station`: entrance, generic node, boarding_area.

<details>

#### Notice fields description
| Field name   	| Description                                     	| Type    	|
|--------------	|-------------------------------------------------	|---------	|
| `csvRowNumber`| The row of the faulty record.                   	| Long    	|
| `stopId`     	| The id of the faulty record.                    	| String  	|
| `stopName`   	| The `stops.stop_name` of the faulty record.     	| String  	|
| `locationType`| The `stops.location_type` of the faulty record. 	| Integer 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
</details>

<a name="LocationWithUnexpectedStopTimeNotice"/>

### LocationWithUnexpectedStopTimeNotice

Referenced locations (using `stop_times.stop_id`) must be stops/platforms, i.e. their `stops.location_type` value must be 0 or empty.

<details>

#### Notice fields description
| Field name             	| Description                                                	| Type   	|
|------------------------	|------------------------------------------------------------	|--------	|
| `csvRowNumber`         	| The row number of the faulty record from `stops.txt`.      	| Long   	|
| `stopId`               	| The id of the faulty record from `stops.txt`.              	| String 	|
| `stopName`             	| The `stops.stop_name` of the faulty record.                	| String 	|
| `stopTimeCsvRowNumber` 	| The row number of the faulty record from `stop_times.txt`. 	| Long   	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [stop_times.txt GTFS specification](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stoptimestxt)
</details>

<a name="MissingCalendarAndCalendarDateFilesNotice"/>

### MissingCalendarAndCalendarDateFilesNotice

Both files calendar_dates.txt and calendar.txt are missing from the GTFS archive. At least one of the files must be provided.

<details>

#### Notice fields description
| Field name 	| Description 	| Type 	|
|------------	|-------------	|------	|
| N/A          	| N/A           | N/A  	|

#### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)

#### References
* [calendar.txt specification](http://gtfs.org/reference/static/#calendartxt)
* [calendar_dates.txt specification](http://gtfs.org/reference/static/#calendar_datestxt)
</details>

<a name="MissingLevelIdNotice"/>

### MissingLevelIdNotice

GTFS file `levels.txt` is required for elevator (`pathway_mode=5`). A row from `stops.txt` linked to an elevator pathway has no value for `stops.level_id`.

<details>

#### Notice fields description
| Field name    	| Description                                                      	 | Type   	|
|---------------	|------------------------------------------------------------------- |--------	|
| `csvRowNumber`  | The row number of the faulty record. 	                             | Long   	|
| `stopId`   	  | The id of the faulty from `stops.txt`.                               | String   |

#### Affected files
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)

#### References
* [levels.txt specification](http://gtfs.org/reference/static/#levelstxt)
</details>

<a name="MissingRequiredColumnNotice"/>

### MissingRequiredColumnNotice

A required column is missing in the input file.

<details>

#### Notice fields description
| Field name 	  | Description                     | Type   	|
|-------------	|-------------------------------- |--------	|
| `filename`   	| The name of the faulty file.    | String 	|
| `fieldName`  	| The name of the missing column. | String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
</details>

<a name="MissingRequiredFieldNotice"/>

### MissingRequiredFieldNotice

The given field has no value in some input row, even though values are required.

<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|
| `csvRowNumber`| The row of the faulty record.  	| Long   	|
| `fieldName`  	| The name of the missing field. 	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
</details>

<a name="MissingRequiredFileNotice"/>

### MissingRequiredFileNotice

A required file is missing.

<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
</details>

<a name="MissingTripEdgeNotice"/>

### MissingTripEdgeNotice

First and last stop of a trip must define both `arrival_time` and `departure_time` fields.

<details>

#### Notice fields description
| Field name     	  | Description                                 | Type    	|
|-----------------  |-------------------------------------------- |---------	|
| `csvRowNumber`  	| The row of the faulty record.               | Long    	|
| `stopSequence`  	| `stops.stop_sequence` of the faulty record. | Integer 	|
| `tripId`        	| The `trips.trip_id` of the faulty record.   | String  	|
| `specifiedField`  | Name of the missing field.                 	| String  	|

* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [stop_times.txt specification](https://gtfs.org/reference/static/#stop_timestxt)
</details>

<a name="NewLineInValueNotice"/>

### NewLineInValueNotice

A value in CSV file has a new line or carriage return.

<details>


#### Notice fields description
| Field name   	| Description                   	| Type    	|
|--------------	|-------------------------------	|---------	|
| `filename`   	| The name of the faulty file.  	| String  	|
| `csvRowNumber`| The row of the faulty record. 	| Integer 	|
| `fieldName`  	| The name of the faulty field. 	| String  	|
| `fieldValue` 	| Faulty value.                 	| String  	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)
</details>

<a name="NumberOutOfRangeNotice"/>

### NumberOutOfRangeNotice

The values in the given column of the input rows are out of range.

<details>


#### Notice fields description
| Field name   	| Description                   	| Type    	|
|--------------	|-------------------------------	|---------	|
| `filename`   	| The name of the faulty file.  	| String  	|
| `csvRowNumber`| The row of the faulty record. 	| Integer 	|
| `fieldName`  	| The name of the faulty field. 	| String  	|
| `fieldType`  	| The type of the faulty field. 	| String  	|
| `fieldValue` 	| Faulty value.                 	| Object  	|

#### Affected files
* [All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS field types](http://gtfs.org/reference/static/#field-types)
</details>

<a name="OverlappingFrequencyNotice"/>

### OverlappingFrequencyNotice

Trip frequencies must not overlap in time

<details>


#### Notice fields description
| Field name       	| Description                                    	| Type   	|
|------------------	|------------------------------------------------	|--------	|
| `prevCsvRowNumber`| The row number of the first frequency.         	| Long   	|
| `prevEndTime`     | The first frequency end time.                  	| String 	|
| `currCsvRowNumber`| The overlapping frequency's row number.        	| Long   	|
| `currStartTime`   | The overlapping frequency's start time.        	| String 	|
| `tripId`          | The trip id associated to the first frequency. 	| String 	|

#### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)

#### References
* [frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)
</details>

<a name="PathwayToPlatformWithBoardingAreasNotice"/>

### PathwayToPlatformWithBoardingAreasNotice

A pathway has an endpoint that is a platform which has boarding areas. A platform that has boarding
areas is treated as a parent object, not a point. In such cases, the platform must not have pathways
assigned - instead, pathways must be assigned to its boarding areas.

<details>


#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
</details>

<a name="PathwayToWrongLocationTypeNotice"/>

### PathwayToWrongLocationTypeNotice

A pathway has an endpoint that is a station. Pathways endpoints must be platforms (stops),
entrances/exits, generic nodes or boarding areas.

<details>


#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
</details>

<a name="PathwayUnreachableLocationNotice"/>

### PathwayUnreachableLocationNotice

A location belongs to a station that has pathways and is not reachable at least in one direction:
from the entrances or to the exits.

Notices are reported for platforms, boarding areas and generic nodes but not for entrances or
stations.

Notices are not reported for platforms that have boarding areas since such platforms may not
have incident pathways. Instead, notices are reported for the boarding areas.

<details>


#### Notice fields description
| Field name   	 | Description                                         | Type    	|
|----------------|--------------------------------------------------|---------	|
| `csvRowNumber` | Row number of the unreachable location.             | Long    	|
| `stopId`     	 | The id of the unreachable location.                 | String  	|
| `stopName`   	 | The stop name of the unreachable location.     	   | String  	|
| `locationType` | The type of the unreachable location. 	           | Integer 	|
| `parentStation`| The parent of the unreachable location. 	           | String 	|
| `hasEntrance`  | Whether the location is reachable from entrances.   | String 	|
| `hasExit`      | Whether some exit can be reached from the location. | String 	|

#### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
 </details>

<a name="PointNearOriginNotice"/>

### PointNearOriginNotice

A point is too close to origin `(0, 0)`.

<details>


#### Notice fields description
| Field name      	| Description                                      	| Type    	|
|-----------------	|--------------------------------------------------	|---------	|
| `filename`      	| The name of the affected GTFS file.              	| String  	|
| `csvRowNumber`  	| The row of the faulty row.                       	| Integer 	|
| `latFieldName`  	| The name of the field that uses latitude value.  	| String  	|
| `latFieldValue` 	| The latitude of the faulty row.                  	| Double  	|
| `lonFieldName`  	| The name of the field that uses longitude value. 	| String  	|
| `lonFieldValue` 	| The longitude of the faulty row                  	| Double  	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

### RouteBothShortAndLongNameMissingNotice

Both short_name and long_name are missing for a route.

<details>


#### Notice fields description
| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
| `routeId`      	| The id of the faulty record.         	| String 	|
| `csvRowNumber` 	| The row number of the faulty record. 	| Long   	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
</details>

<a name="StartAndEndRangeEqualNotice"/>

### StartAndEndRangeEqualNotice

The fields `frequencies.start_date` and `frequencies.end_date` have been found equal in `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.

<details>


#### Notice fields description
| Field name     	  | Description                          	| Type   	|
|-----------------	|-------------------------------------- |--------	|
| `filename`       	| The name of the faulty file.         	| String 	|
| `csvRowNumber`   	| The row number of the faulty record. 	| Long   	|
| `startFieldName` 	| The start value's field name.        	| String 	|
| `endFieldName`   	| The end value's field name.          	| String 	|
| `value`          	| The faulty value.                    	| String 	|

#### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="StartAndEndRangeOutOfOrderNotice"/>

### StartAndEndRangeOutOfOrderNotice

Date or time fields have been found out of order in `calendar.txt`, `feed_info.txt` and `stop_times.txt`.

<details>


#### Notice fields description
| Field name     	  | Description                          	| Type   	|
|-----------------	|-------------------------------------  |--------	|
| `filename`       	| The name of the faulty file.         	| String 	|
| `csvRowNumber`   	| The row number of the faulty record. 	| Long   	|
| `entityId`       	| The faulty service id.               	| String 	|
| `startFieldName` 	| The start value's field name.        	| String 	|
| `startValue`     	| The start value.                     	| String 	|
| `endFieldName`   	| The end value's field name.          	| String 	|
| `endValue`       	| The end value.                       	| String 	|

#### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="StationWithParentStationNotice"/>

### StationWithParentStationNotice

Field `parent_station` must be empty when `location_type` is 1.

<details>


#### Notice fields description
| Field name    	| Description                               	  | Type   	|
|---------------	|---------------------------------------------	|--------	|
| `stopId`        	| The id of the faulty record.              	| String 	|
| `stopName`      	| The stops.stop_name of the faulty record. 	| String  |
| `csvRowNumber`  	| The row number of the faulty record.      	| Long  	|
| `parentStation` 	| Parent station's id.                        | String 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
[stop.txt](http://gtfs.org/reference/static/#stopstxt)
</details>

<a name="StopTimeTimepointWithoutTimesNotice"/>

### StopTimeTimepointWithoutTimesNotice

Any records with `stop_times.timepoint` set to 1 must define a value for `stop_times.arrival_time` and `stop_times.departure_time` fields.

<details>


#### Notice fields description
| Field name     	  | Description                                	| Type   	|
|-----------------	|--------------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.       	| Long   	|
| `tripId`         	| The faulty record's id.                    	| String 	|
| `stopSequence`   	| The faulty record's `stops.stop_sequence`. 	| String 	|
| `specifiedField` 	| Either `departure_time` or `arrival_time`. 	| String 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [GTFS stop_times.txt specification](https://gtfs.org/reference/static#stoptimestxt)
</details>

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence in `stop_times.txt`.

<details>


#### Notice fields description
| Field name       	| Description                                  	| Type   	|
|------------------	|----------------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.         	| Long   	|
| `prevCsvRowNumber`| The row of the previous stop time.           	| Long   	|
| `tripId`         	| The trip_id associated to the faulty record. 	| String 	|
| `departureTime`  	| Departure time at the previous stop time.    	| String 	|
| `arrivalTime`    	| Arrival time at the faulty record.           	| String 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

### StopTimeWithOnlyArrivalOrDepartureTimeNotice

Missing `stop_time.arrival_time` or `stop_time.departure_time`

<details>


#### Notice fields description
| Field name      	| Description                                  	| Type    	|
|-----------------	|-----------------------------------------------|---------	|
| `csvRowNumber`   	| The row number of the faulty record.         	| Long    	|
| `tripId`         	| The trip_id associated to the faulty record. 	| String  	|
| `stopSequence`   	| The sequence of the faulty stop.             	| Integer 	|
| `specifiedField` 	| Either `arrival_time` or `departure_time`    	| String  	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [stop_times.txt specification](http://gtfs.org/reference/static/#stop_timestxt)
</details>

<a name="StopWithoutZoneIdNotice"/>

### StopWithoutZoneIdNotice

If `fare_rules.txt` is provided, and `fare_rules.txt` uses at least one column among `origin_id`, `destination_id`, and `contains_id`, then all stops and platforms (location_type = 0) must have `stops.zone_id` assigned. 

<details>


#### Notice fields description
| Field name               	| Description                                	| Type   	|
|--------------------------	|--------------------------------------------	|--------	|
| `stopId`                 	| The faulty record's id.                    	| String 	|
| `stopName`                | The faulty record's `stops.stop_name`.       	| String 	|
| `csvRowNumber`        	| The row number of the faulty record.       	| Long   	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`fare_rules.txt`](http://gtfs.org/reference/static#farerulestxt)

#### References
* [GTFS stops.txt specification](https://gtfs.org/reference/static#stopstxt)
</details>

<a name="TranslationForeignKeyViolationNotice"/>

### TranslationForeignKeyViolationNotice

An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.

<details>


#### Notice fields description
| Field name       | Description                            | Type    	|
|------------------|----------------------------------------|-------	|
| `csvRowNumber`   | The row number of the faulty record.   | Long    	|
| `tableName`      | `table_name` of the faulty record.     | String  	|
| `recordId`       | `record_id` of the faulty record.      | String  	|
| `recordSubId`    | `record_sub_id` of the faulty record.  | String  	|

#### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
</details>

<a name="TranslationUnexpectedValueNotice"/>

### TranslationUnexpectedValueNotice

A field in a translations row has value but must be empty.

<details>


#### Notice fields description
| Field name        | Description                                  	            | Type    	|
|-------------------|-----------------------------------------------------------|---------	|
| `csvRowNumber`    | The row number of the faulty record.         	            | Long    	|
| `fieldName`       | The name of the field that was expected to be empty.      | String  	|
| `fieldValue`      | Actual value of the field that was expected to be empty.  | String 	|

#### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
</details>

<a name="WrongParentLocationTypeNotice"/>

### WrongParentLocationTypeNotice

Value of field `location_type` of parent found in field `parent_station` is invalid.

According to spec
- _Stop/platform_ can only have _Station_ as parent
- _Station_ can NOT have a parent
- _Entrance/exit_ or _generic node_ can only have _Station_ as parent
- _Boarding Area_ can only have _Platform_ as parent 

Any other combination raise this error.

<details>


#### Notice fields description
| Field name           	| Description                                      	| Type    	|
|----------------------	|--------------------------------------------------	|---------	|
| `csvRowNumber`       	| The row number of the faulty record.             	| Long    	|
| `stopId`             	| The id of the faulty record.                     	| String  	|
| `stopName`           	| The faulty record's `stops.stop_name`.           	| String  	|
| `locationType`       	| The faulty record's `stops.location_type`.       	| Integer 	|
| `parentCsvRowNumber` 	| The row number of the faulty record's parent.    	| Long    	|
| `parentStation`      	| The id of the faulty record's parent station.    	| String  	|
| `parentStopName`     	| The stop name of the faulty record's parent.     	| String  	|
| `parentLocationType` 	| The location type of the faulty record's parent. 	| Integer 	|
| `expectedLocationType`| The expected location type of the faulty record. 	| Integer 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
</details>

## Warnings

<a name="AttributionWithoutRoleNotice"/>

### AttributionWithoutRoleNotice

At least one of the fields `is_producer`, `is_operator`, or `is_authority` should be set to 1.

<details>


#### Notice fields description
| Field name    	| Description                          	  | Type   	|
|---------------	|---------------------------------------	|--------	|
| `csvRowNumber`  	| The row number of the faulty record. 	| Long   	|
| `attributionId` 	| The id of the faulty record.         	| String 	|

#### Affected files
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)

#### References
* [attributions.txt specification](https://gtfs.org/reference/static#attributionstxt)
</details>

<a name="DuplicateRouteNameNotice"/>

### DuplicateRouteNameNotice

All routes of the same `route_type` with the same `agency_id` should have unique combinations of `route_short_name` and `route_long_name`.

Note that there may be valid cases where routes have the same short and long name, e.g., if they serve different areas. However, different directions must be modeled as the same route.

Example of bad data:
| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| U1                 	| Southern          	|
| route2     	| U1                 	| Southern          	|

<details>


#### Notice fields description
| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| csvRowNumber1  	| The row number of the first occurrence. 	| Long   	|
| routeId1       	| The id of the the first occurrence.     	| String 	|
| csvRowNumber2  	| The row number of the other occurrence. 	| Long   	|
| routeId2       	| The id of the the other occurrence.     	| String 	|
| routeShortName 	| Common `routes.route_short_name`.       	| String 	|
| routeLongName  	| Common `routes.route_long_name`.        	| String 	|
| routeType      	| Common `routes.route_type`.             	| String 	|
| agencyId       	| Common `routes.agency_id`.              	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [routes.txt best practices](http://gtfs.org/best-practices/#routestxt)

<a name="EmptyRowNotice"/>
</details>

### EmptyRowNotice

A row in the input file has only spaces.

<details>


#### Notice fields description
| Field name   	| Description                          	| Type    	|
|--------------	|--------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.         	| String  	|
| `csvRowNumber`| The row number of the faulty record. 	| Long 	    |

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
</details>

### EqualShapeDistanceSameCoordinatesNotice

<a name="EqualShapeDistanceSameCoordinatesNotice"/>

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and the same coordinates indicate a duplicative shape point.

<details>


#### Notice fields description
| Field name            	  | Description                                                                                    	  | Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------	|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)
</details>

<a name="FastTravelBetweenConsecutiveStopsNotice"/>

### FastTravelBetweenConsecutiveStopsNotice

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

<details>


#### Notice fields description
| Field name        	| Description                             | Type   	|
|-----------------------|---------------------------------------- |--------	|
| `tripCsvRowNumber`    | The row number of the problematic trip. | Long 	|
| `tripId`           	| `trip_id` of the problematic trip.      | String 	|
| `routeId`             | `route_id` of the problematic trip.     | String 	|
| `speedKph`         	| Travel speed (km/h).                    | Double 	|
| `distanceKm`       	| Distance between stops (km).            | Double 	|
| `csvRowNumber1`       | The row number of the first stop time.  | Long  	|
| `stopSequence1`       | `stop_sequence` of the first stop.      | Integer	|
| `stopId1`             | `stop_id` of the first stop.            | String 	|
| `stopName1`           | `stop_name` of the first stop.          | String 	|
| `departureTime1`      | `departure_time` of the first stop.     | Time 	|
| `csvRowNumber2`       | The row number of the second stop time. | Long 	|
| `stopSequence2`       | `stop_sequence` of the second stop.     | Integer	|
| `stopId2`             | `stop_id` of the second stop.           | String 	|
| `stopName2`           | `stop_name` of the second stop.         | String 	|
| `arrivalTime2`        | `arrival_time` of the second stop.      | Time 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

### FastTravelBetweenFarStopsNotice

A transit vehicle moves too fast between far consecutive stops (more than in 10 km apart). 
This normally indicates a more serious problem than too fast travel between consecutive stops.
The speed threshold depends on route type.

##### Speed thresholds

Same as for [`FastTravelBetweenConsecutiveStopsNotice`](#FastTravelBetweenConsecutiveStopsNotice).

<details>


#### Notice fields description
| Field name        	| Description                             | Type   	|
|-----------------------|---------------------------------------- |--------	|
| `tripCsvRowNumber`    | The row number of the problematic trip. | Long 	|
| `tripId`           	| `trip_id` of the problematic trip.      | String 	|
| `routeId`             | `route_id` of the problematic trip.     | String 	|
| `speedKph`         	| Travel speed (km/h).                    | Double 	|
| `distanceKm`       	| Distance between stops (km).            | Double 	|
| `csvRowNumber1`       | The row number of the first stop time.  | Long  	|
| `stopSequence1`       | `stop_sequence` of the first stop.      | Integer	|
| `stopId1`             | `stop_id` of the first stop.            | String 	|
| `stopName1`           | `stop_name` of the first stop.          | String 	|
| `departureTime1`      | `departure_time` of the first stop.     | Time 	|
| `csvRowNumber2`       | The row number of the second stop time. | Long 	|
| `stopSequence2`       | `stop_sequence` of the second stop.     | Integer	|
| `stopId2`             | `stop_id` of the second stop.           | String 	|
| `stopName2`           | `stop_name` of the second stop.         | String 	|
| `arrivalTime2`        | `arrival_time` of the second stop.      | Time 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
</details>

<a name="FeedExpirationDateNotice"/>

### FeedExpirationDateNotice

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.
If possible, the GTFS dataset should cover at least the next 30 days of service.

<details>


#### Notice fields description
| Field name              	| Description                                  	| Type   	|
|-------------------------	|----------------------------------------------	|--------	|
| `csvRowNumber`           	| The row number of the faulty record.         	| Long   	|
| `currentDate`            	| Current date (YYYYMMDD format).              	| String 	|
| `feedEndDate`            	| Feed end date (YYYYMMDD format).             	| String 	|
| `suggestedExpirationDate`	| Suggested expiration date (YYYYMMDD format). 	| String 	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### References
* [General Publishing & General Practices](https://gtfs.org/best-practices/#dataset-publishing--general-practices)
</details>

<a name="FeedInfoLangAndAgencyLangMismatchNotice"/>

### FeedInfoLangAndAgencyLangMismatchNotice
1. Files `agency.txt` and `feed_info.txt` should define matching `agency.agency_lang` and `feed_info.feed_lang`.
  The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
  * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
  * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
  * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

<details>


#### Notice fields description
| Field name   	| Description                               	| Type   	|
|--------------	|-------------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record.      	| Long   	|
| `agencyId`   	| The agency id of the faulty record.       	| String 	|
| `agencyName` 	| The agency name of the faulty record.     	| String 	|
| `agencyLang` 	| The agency language of the faulty record. 	| String 	|
| `feedLang`   	| The feed language of the faulty record.   	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### References
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)
<details>

<a name="InconsistentAgencyLangNotice"/>

### InconsistentAgencyLangNotice

Agencies from GTFS `agency.txt` have been found to have different languages.

<details>


#### Notice fields description
| Field name     	| Description                   	| Type   	|
|----------------	|-------------------------------	|--------	|
| `csvRowNumber` 	| The row of the faulty record. 	| Long   	|
| `expected`     	| Expected language.            	| String 	|
| `actual`       	| Faulty record's language.     	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="LeadingOrTrailingWhitespacesNotice"/>

### LeadingOrTrailingWhitespacesNotice

The value in CSV file has leading or trailing whitespaces.

<details>


#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
<details>

<a name="MissingFeedInfoDateNotice"/>

### MissingFeedInfoDateNotice

Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided.
<details>


#### Notice fields description
| Field name     	| Description                                 	| Type   	|
|----------------	|---------------------------------------------	|--------	|
| `fieldName`    	| Either `feed_end_date` or `feed_start_date` 	| String 	|
| `csvRowNumber` 	| The row number of the faulty record.        	| Long   	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
#### Notice fields description

#### References
* [feed_info.txt Best practices](http://gtfs.org/best-practices/#feed_infotxt)
 <details>

<a name="MissingTimepointValueNotice"/>

#### MissingTimepointValueNotice

Even though the column `timepoint` is optional in `stop_times.txt` according to the specification, `stop_times.timepoint` should not be empty when provided. 
<details>


#### Notice fields description
| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `csvRowNumber` 	| The row number of the faulty record.            	| Long   	|
| `tripId`       	| The faulty record's `stop_times.trip_id`.         | String 	|
| `stopSequence` 	| The faulty record's `stop_times.stop_sequence`. 	| String 	|

#### Affected files
* [`stop_times.txt`](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)

#### References
* [stop_times.txt specification](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)
<details>

<a name="MissingTimepointColumnNotice"/>

### MissingTimepointColumnNotice

The `timepoint` column should be provided.
<details>


#### Notice fields description
| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `filename`    	| The name of the affected file.                  	| String   	|

#### Affected files
* [`stop_times.txt`](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)

#### References
* [stop_times.txt bets practices](https://github.com/MobilityData/GTFS_Schedule_Best-Practices/blob/master/en/stop_times.md)
<details>

<a name="MoreThanOneEntityNotice"/>

#### MoreThanOneEntityNotice

The file is expected to have a single entity but has more (e.g., "feed_info.txt").
<details>


#### Notice fields description
| Field name    	| Description              	| Type   	|
|---------------	|--------------------------	|--------	|
| `filename`    	| Name of the faulty file. 	| String 	|
| `entityCount` 	| Number of occurrences.   	| Long   	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [GTFS field definition](http://gtfs.org/reference/static#field-definitions)
<details>

<a name="NonAsciiOrNonPrintableCharNotice"/>

### NonAsciiOrNonPrintableCharNotice

A value of a field with type `id` contains non ASCII or non printable characters. This is not recommended.
<details>


#### Notice fields description
| Field name   	| Description                                  	| Type   	|
|--------------	|----------------------------------------------	|--------	|
| `filename`   	| Name of the faulty file.                     	| String 	|
| `csvRowNumber`| Row number of the faulty record.             	| Long   	|
| `columnName` 	| Name of the column where the error occurred. 	| String 	|
| `fieldValue` 	| Faulty value.                                	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="PathwayDanglingGenericNodeNotice"/>

### PathwayDanglingGenericNodeNotice

A generic node has only one incident location in a pathway graph. Such generic node is useless
because there is no benefit in visiting it.
<details>


#### Notice fields description
| Field name     | Description                                         | Type    	|
|----------------|-----------------------------------------------------|---------	|
| `csvRowNumber` | Row number of the dangling generic node.            | Long    	|
| `stopId`       | The id of the dangling generic node.                | String  	|
| `stopName`     | The stop name of the dangling generic node.         | String  	|
| `parentStation`| The parent station of the dangling generic node.    | String 	|

#### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
<details>

<a name="PathwayLoopNotice"/>

### PathwayLoopNotice

A pathway should not have same values for `from_stop_id` and `to_stop_id`.
<details>


#### Notice fields description
| Field name     	| Description                                                                                 	| Type   	|
|----------------	|---------------------------------------------------------------------------------------------	|--------	|
| `csvRowNumber` 	| Row number of the faulty row from `pathways.txt`.                                           	| Long   	|
| `pathwayId`    	| The id of the faulty record.                                                                	| String 	|
| `stopId`       	| The `pathway.stop_id` that is repeated in `pathways.from_stop_id` and `pathways.to_stop_id`. 	| String 	|

#### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
<details>

<a name="PlatformWithoutParentStationNotice"/>

### PlatformWithoutParentStationNotice

A platform has no `parent_station` field set.
<details>


#### Notice fields description
| Field name   	| Description                             	| Type    	|
|--------------	|-----------------------------------------	|---------	|
| `csvRowNumber`| Row number of the faulty record.        	| Long    	|
| `stopId`     	| The id of the faulty record.             	| String  	|
| `stopName`   	| The stop name of the faulty record.     	| String  	|
| `locationType`| The location type of the faulty record. 	| Integer 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
<details>

<a name="RouteColorContrastNotice"/>

#### RouteColorContrastNotice

A route's color and `route_text_color` should be contrasting.
<details>


#### Notice fields description
| Field name     	| Description                                	  | Type   	|
|----------------	|---------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.               	| String 	|
| `csvRowNumber`   	| The row number of the faulty record.       	| Long 	  |
| `routeColor`     	| The faulty record's HTML route color.      	| String 	|
| `routeTextColor` 	| The faulty record's HTML route text color. 	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="RouteShortAndLongNameEqualNotice"/>

### RouteShortAndLongNameEqualNotice

A single route has the same values for `route_short_name` and `route_long_name`.

Example of bad data:

| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| L1                 	| L1                	|
<details>


#### Notice fields description
| Field name     	| Description                             	  | Type   	|
|----------------	|-------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| String  |
| `csvRowNumber`   	| The row number of the faulty record.    	| Long 	  |
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|
| `routeLongName`  	| The faulty record's `route_long_name`.  	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
<details>

<a name="RouteShortNameTooLongNotice"/>

### RouteShortNameTooLongNotice

Short name of a route is too long (more than 12 characters).
<details>


#### Notice fields description
| Field name     	| Description                             	  | Type   	|
|----------------	|-------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| String  |
| `csvRowNumber`   	| The row number of the faulty record.    	| Long 	  |
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)
<details>

<a name="SameNameAndDescriptionForRouteNotice"/>

### SameNameAndDescriptionForRouteNotice

The GTFS spec defines `routes.txt` [route_desc](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_desc` fields.
<details>


#### Notice fields description
| Field name     	| Description                                    	| Type   	|
|----------------	|------------------------------------------------	|--------	|
| `filename`      | The name of the faulty file.                   	| String 	|
| `routeId`       | The id of the faulty record.                   	| String 	|
| `csvRowNumber`  | The row number of the faulty record.           	| Long   	|
| `routeDesc`     | The `routes.routes_desc` of the faulty record. 	| String 	|
| `specifiedField`| Either `route_short_name` or `route_long_name`. | String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
[routes.txt specification](http://gtfs.org/reference/static/#routestxt)
[routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

<a name="SameNameAndDescriptionForStopNotice"/>
<details>
### SameNameAndDescriptionForStopNotice

The GTFS spec defines `stops.txt` [stop_description](https://gtfs.org/reference/static/#stopstxt) as:

> Description of the location that provides useful, quality information. Do not simply duplicate the name of the location.
<details>


#### Notice fields description
| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `csvRowNumber`  | The row number of the faulty record.      | Long 	  |
| `stopId`        | The id of the faulty record.              | String  |
| `stopDesc`    	| The faulty record's `stop_desc`.         	| String 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
[stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
<details>

<a name="SameRouteAndAgencyUrlNotice"/>

### SameRouteAndAgencyUrlNotice

A route should not have the same `routes.route_url` as a record from `agency.txt`.
<details>


#### Notice fields description
| Field name     	| Description                                	| Type   	|
|----------------	|--------------------------------------------	|--------	|
| `routeCsvRowNumber`    | The row number of the faulty record from `routes.txt`.       	| Long   	|
| `routeId`         | The faulty record's id.                    	| String 	|
| `agencyId`    	| The faulty record's `routes.agency_id`.    	| String 	|
| `routeUrl`     	| The duplicate URL value                    	| String 	|
| `agencyCsvRowNumber`    | The row number of the faulty record from `agency.txt`.       	| Long   	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
<details>

<a name="SameStopAndAgencyUrlNotice"/>

### SameStopAndAgencyUrlNotice

A stop should not have the same `stops.stop_url` as a record from `agency.txt`.
<details>


#### Notice fields description
| Field name     	| Description                                            	| Type   	|
|----------------	|--------------------------------------------------------	|--------	|
| `stopCsvRowNumber`| The row number of the faulty record from `stops.txt`.     | Long   	|
| `stopId`       	| The faulty record's id.                                	| String 	|
| `agencyName`   	| The faulty record's `agency.agency_name`.              	| String 	|
| `stopUrl`      	| The duplicate URL value.                                  | String 	|
| `agencyCsvRowNumber` 	| The row number of the faulty record from `agency.txt`.|  Long   	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
<details>

<a name="SameStopAndRouteUrlNotice"/>

### SameStopAndRouteUrlNotice

A stop should not have the same `stop.stop_url` as a record from `routes.txt`.
<details>


#### Notice fields description
| Field name          	| Description                                            	| Type   	|
|---------------------	|--------------------------------------------------------	|--------	|
| `stopsvRowNumber`     | The row number of the faulty record from `stops.txt`.    	| Long   	|
| `stopId`            	| The faulty record's id.                                	| String 	|
| `stopUrl`           	| The duplicate URL value.                                | String 	|
| `routeId`           	| The faulty record's id from `routes.txt.               	| String 	|
| `routeCsvRowNumber` 	| The row number of the faulty record from `routes.txt`. 	| Long   	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
 <details>

<a name="StopHasTooManyMatchesForShapeNotice"/>

### StopHasTooManyMatchesForShapeNotice

A stop entry that has many potential matches to the trip's path of travel, as defined  by the shape entry in `shapes.txt`.
<details>


#### Notice fields description
| Field name             	| Description                                                	| Type    	|
|------------------------	|------------------------------------------------------------	|---------	|
| `tripCsvRowNumber`     	| The row number of the faulty record from `trips.txt`.      	| Long    	|
| `shapeId`              	| The id of the shape that is referred to.                   	| String  	|
| `tripId`               	| The id of the trip that is referred to.                    	| String  	|
| `stopTimeCsvRowNumber` 	| The row number of the faulty record from `stop_times.txt`. 	| Long  	|
| `stopId`               	| The id of the stop that is referred to.                    	| String  	|
| `stopName`             	| The name of the stop that is referred to.                  	| String  	|
| `match`                	| Latitude and longitude pair of the location.               	| Object  	|
| `matchCount`           	| The number of matches for the stop that is referred to.    	| Integer 	|

#### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stops_times.txt`](http://gtfs.org/reference/static#stopstimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
 <details>

<a name="StopsMatchShapeOutOfOrderNotice"/>

### StopsMatchShapeOutOfOrderNotice

Two stop entries in `stop_times.txt` are different than their arrival-departure order as defined by the shape in the `shapes.txt` file.
<details>


#### Notice fields description
| Field name              	| Description                                                       	| Type   	|
|-------------------------	|-------------------------------------------------------------------	|--------	|
| `tripCsvRowNumber`      	| The row number of the faulty record from `trips.txt`.             	| Long   	|
| `shapeId`               	| The id of the shape that is referred to.                          	| String 	|
| `tripId`                	| The id of the trip that is referred to.                           	| String 	|
| `stopTimeCsvRowNumber1` 	| The row number of the first faulty record from `stop_times.txt`.  	| Long   	|
| `stopId1`               	| The id of the first stop that is referred to.                     	| String 	|
| `stopName1`             	| The name of the first stop that is referred to.                   	| String 	|
| `match1`                	| Latitude and longitude pair of the first matching location.       	| Object 	|
| `stopTimeCsvRowNumber2` 	| The row number of the second faulty record from `stop_times.txt`. 	| Long   	|
| `stopId2`               	| The id of the second stop that is referred to.                    	| String 	|
| `stopName2`             	| The name of the second stop that is referred to.                  	| String 	|
| `match2`                	| Latitude and longitude pair of the second matching location.      	| Object 	|

#### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stops_times.txt`](http://gtfs.org/reference/static#stopstimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
<details>

<a name="StopTooFarFromShapeNotice"/>

### StopTooFarFromShapeNotice

Per GTFS Best Practices, route alignments (in `shapes.txt`) should be within 100 meters of stop locations which a trip serves.
<details>


#### Notice fields description
| Field name             	| Description                                                	| Type   	|
|------------------------	|------------------------------------------------------------	|--------	|
| `tripCsvRowNumber`     	| The row number of the faulty record from `trips.txt`.      	| Long   	|
| `shapeId`              	| The id of the shape that is referred to.                   	| String 	|
| `tripId`               	| The id of the trip that is referred to.                    	| String 	|
| `stopTimeCsvRowNumber` 	| The row number of the faulty record from `stop_times.txt`. 	| Long   	|
| `stopId`               	| The id of the stop that is referred to.                    	| String 	|
| `stopName`             	| The name of the stop that is referred to.                  	| String 	|
| `match`                	| Latitude and longitude pair of the location.               	| Object 	|
| `geoDistanceToShape`   	| Distance from stop to shape.                               	| Double 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stoptimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References
* [GTFS Best Practices shapes.txt](https://gtfs.org/best-practices/#shapestxt)
 <details>

<a name="StopTooFarFromShapeUsingUserDistanceNotice"/>

### StopTooFarFromShapeUsingUserDistanceNotice

A stop time entry that is a large distance away from the location of the shape in `shapes.txt` as defined by `shape_dist_traveled` values.
<details>


#### Notice fields description
| Field name             	| Description                                                	| Type   	|
|------------------------	|------------------------------------------------------------	|--------	|
| `tripCsvRowNumber`     	| The row number of the faulty record from `trips.txt`.      	| Long   	|
| `shapeId`              	| The id of the shape that is referred to.                   	| String 	|
| `tripId`               	| The id of the trip that is referred to.                    	| String 	|
| `stopTimeCsvRowNumber` 	| The row number of the faulty record from `stop_times.txt`. 	| Long   	|
| `stopId`               	| The id of the stop that is referred to.                    	| String 	|
| `stopName`             	| The name of the stop that is referred to.                  	| String 	|
| `match`                	| Latitude and longitude pair of the location.               	| Object 	|
| `geoDistanceToShape`   	| Distance from stop to shape.                               	| Double 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stoptimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
  A stop time entry that is a large distance away from the location of the shape in `shapes.txt` as defined by `shape_dist_traveled` values.
<details>

<a name="StopWithoutStopTimeNotice"/>

### StopWithoutStopTimeNotice

A stop in `stops.txt` is not referenced by any `stop_times.stop_id`, so it is not used by any trip.
Such stops normally do not provide user value. This notice may indicate a typo in `stop_times.txt`.

<a name="TranslationUnknownTableNameNotice"/>

### TranslationUnknownTableNameNotice

A translation references an unknown or missing GTFS table.
<details>


#### Notice fields description
| Field name       | Description                            | Type    	|
|------------------|----------------------------------------|-------	|
| `csvRowNumber`   | The row number of the faulty record.   | Long    	|
| `tableName`      | `table_name` of the faulty record.     | String  	|

#### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
<details>
<a name="UnexpectedEnumValueNotice"/>

### UnexpectedEnumValueNotice

An enum has an unexpected value.
<details>


#### Notice fields description
| Field name   	| Description                                     	| Type    	|
|--------------	|-------------------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.                    	| String  	|
| `csvRowNumber`| The row number of the faulty record.            	| Long    	|
| `fieldName`  	| The name of the field where the error occurred. 	| String  	|
| `fieldValue` 	| Faulty value.                                   	| Integer 	|

#### Affected files
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`transfers.txt`](http://gtfs.org/reference/static#transferstxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)

#### References
* [GTFs field definitions](http://gtfs.org/reference/static/#field-definitions)
<details>

<a name="UnusableTripNotice"/>

### UnusableTripNotice

A trip must visit more than one stop in stop_times.txt to be usable by passengers for boarding and alighting.
<details>


#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="UnusedShapeNotice"/>

### UnusedShapeNotice

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.
<details>


#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `shapeId     	| The faulty record's id.              	| String 	|

#### Affected files
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="UnusedTripNotice"/>

### UnusedTripNotice

Trips should be referred to at least once in `stop_times.txt`.
<details>


#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

#### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

## Infos

<a name="UnknownColumnNotice"/>

### UnknownColumnNotice

A column is unknown.
<details>


#### Notice fields description
| Field name 	  | Description                     	| Type    	|
|-------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.    	| String 	|
| `fieldName`  	| The name of the unknown column. 	| String  	|
| `index`      	| The index of the faulty column. 	| Integer 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

<a name="UnknownFileNotice"/>

### UnknownFileNotice

A file is unknown.
<details>


#### Notice fields description
| Field name 	  | Description                     	| Type    |
|-------------	|---------------------------------	|---------|
| `filename`   	| The name of the unknown file.    	| String 	|

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>
## System errors

<a name="IOError"/>

### IOError

Error in IO operation.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
<details>
<a name="RuntimeExceptionInLoaderError"/>

### RuntimeExceptionInLoaderError

A [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) occurred while loading a table. This normally indicates a bug in validator.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `filename`  	| The name of the file that caused the exception.            	    | String 	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)
<details>

<a name="RuntimeExceptionInValidatorError"/>

### RuntimeExceptionInValidatorError

A [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) occurred during validation. This normally indicates a bug in validator code, e.g., in a custom validator class.
<details>


#### Notice fields description
| Field name 	| Description                                                   	| Type    |
|------------	|---------------------------------------------------------------	|---------|
| `validator` | The name of the validator that caused the exception.            | String 	|
| `exception` | The name of the exception.                                    	| String 	|
| `message`   | The error message that explains the reason for the exception. 	| String  |
<details>

<a name="ThreadExecutionError"/>

### ThreadExecutionError

An [ExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutionException.html) occurred during multithreaded validation.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
<details>

<a name="URISyntaxError"/>

### URISyntaxError

A string could not be parsed as a URI reference.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
<details>