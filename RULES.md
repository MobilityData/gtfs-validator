# Implemented rules
This document lists all the notices that are emitted by this validator.\
Note that the notice naming convention changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. Please refer to [MIGRATION_V1_V2.md](/docs/MIGRATION_V1_V2.md) for a mapping between v1 and v2 notices.\
Note that some severities were modified in `v3` to solve discrepancies with the specification. Please refer to [MIGRATION_V2_V3.md](/docs/MIGRATION_V2_V3.md) for a mapping between v2 and v3 notices.
<a name="definitions"/>

## Definitions
### A Rule
A part of the specification that is translated into code in the validator. A Rule will describe if a set of conditions is met or not. For example:
  - In the specification: in the `stops.txt` file, the field `zone_id` is required if providing fare information using `fare_rules.txt` ([source in the specification](https://gtfs.org/schedule/reference/#stopstxt)).
  - In the validator: this is translated into code in the file [`StopZoneIdValidator.java`](https://github.com/MobilityData/gtfs-validator/blob/master/main/src/main/java/org/mobilitydata/gtfsvalidator/validator/StopZoneIdValidator.java).

### A Notice
The output that the user will see if the conditions aren’t met.
- For example, the output of `StopZoneIdValidator.java` is the Notice `stop_without_zone_id`. 

### The Severity of a Notice

Each Notice is associated with a severity: `INFO`, `WARNING`, `ERROR`.

* `ERROR` notices are for GTFS Schedule Reference violations. These are items that the [GTFS Schedule Reference](https://gtfs.org/schedule/reference/) explicitly requires or prohibits (using the language "must"). 
* `WARNING` notices are for GTFS Schedule Best Practices. These are items that the [GTFS Schedule Reference](https://gtfs.org/schedule/reference/) explicitly recommends (using the language "should"), or items mentioned in the official [GTFS Schedule Best Practices](https://gtfs.org/schedule/best-practices/).
* `INFO` notices are for items that may affect the feed's quality. They are unexpected finds that should be brought to the user's attention. 

⚠️ Please note that this validator also generates `System Errors` that give information about things that may have gone wrong during the validation process such as the inability to unzip a GTFS file. These are generated in a second report `system_errors.json`.

<a name="ERRORS"/>

## Table of ERRORS
| Notice code                                                                                                                       | Description                                                                                                                                            |
|-----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`block_trips_with_overlapping_stop_times`](#block_trips_with_overlapping_stop_times)                                             | Block trips with overlapping stop times.                                                                                                               |
| [`csv_parsing_failed`](#csv_parsing_failed)                                                                                       | Parsing of a CSV file failed.                                                                                                                          |
| [`decreasing_shape_distance`](#decreasing_shape_distance)                                                                         | Decreasing `shape_dist_traveled` in `shapes.txt`.                                                                                                      |
| [`decreasing_or_equal_stop_time_distance`](#decreasing_or_equal_stop_time_distance)                                               | Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.                                                                                         |
| [`duplicated_column`](#duplicated_column)                                                                                         | Duplicated column in CSV.                                                                                                                              |
| [`duplicate_key`](#duplicate_key)                                                                                                 | Duplicated entity.                                                                                                                                     |
| [`empty_column_name`](#empty_column_name)                                                                                         | A column name is empty.                                                                                                                                |
| [`empty_file`](#empty_file)                                                                                                       | A CSV file is empty.                                                                                                                                   |
| [`equal_shape_distance_diff_coordinates`](#equal_shape_distance_diff_coordinates)                                                 | Two consecutive points have equal `shape_dist_traveled` and different lat/lon coordinates in `shapes.txt`.                                             |
| [`fare_transfer_rule_duration_limit_type_without_duration_limit`](#fare_transfer_rule_duration_limit_type_without_duration_limit) | A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit_type` field but no `duration_limit` specified.                            |
| [`fare_transfer_rule_duration_limit_without_type`](#fare_transfer_rule_duration_limit_without_type)                               | A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit` field but no `duration_limit_type` specified.                            |
| [`fare_transfer_rule_invalid_transfer_count`](#fare_transfer_rule_invalid_transfer_count)                                         | A row from GTFS file `fare_transfer_rules.txt` has a defined `transfer_count` with an invalid value.                                                   |
| [`fare_transfer_rule_missing_transfer_count`](#fare_transfer_rule_missing_transfer_count)                                         | A row from `fare_transfer_rules.txt` has `from_leg_group_id` equal to `to_leg_group_id`, but has no `transfer_count` specified.                        |
| [`fare_transfer_rule_with_forbidden_transfer_count`](#fare_transfer_rule_with_forbidden_transfer_count)                           | A row from `fare_transfer_rules.txt` has `from_leg_group_id` not equal to `to_leg_group_id`, but has `transfer_count` specified.                       |
| [`foreign_key_violation`](#foreign_key_violation)                                                                                 | Wrong foreign key.                                                                                                                                     |
| [`inconsistent_agency_timezone`](#inconsistent_agency_timezone)                                                                   | Inconsistent Timezone among agencies.                                                                                                                  |
| [`invalid_color`](#invalid_color)                                                                                                 | A field contains an invalid color value.                                                                                                               |
| [`invalid_currency`](#invalid_currency)                                                                                           | A field contains a wrong currency code.                                                                                                                |
| [`invalid_currency_amount`](#invalid_currency_amount)                                                                             | A currency amount field has a value that does not match the format of its corresponding currency code field.                                           |
| [`invalid_date`](#invalid_date)                                                                                                   | A field cannot be parsed as date.                                                                                                                      |
| [`invalid_email`](#invalid_email)                                                                                                 | A field contains a malformed email address.                                                                                                            |
| [`invalid_float`](#invalid_float)                                                                                                 | A field cannot be parsed as a floating point number.                                                                                                   |
| [`invalid_integer`](#invalid_integer)                                                                                             | A field cannot be parsed as an integer.                                                                                                                |
| [`invalid_language_code`](#invalid_language_code)                                                                                 | A field contains a wrong language code.                                                                                                                |
| [`invalid_phone_number`](#invalid_phone_number)                                                                                   | A field contains a malformed phone number.                                                                                                             |
| [`invalid_row_length`](#invalid_row_length)                                                                                       | Invalid csv row length.                                                                                                                                |
| [`invalid_time`](#invalid_time)                                                                                                   | A field cannot be parsed as time.                                                                                                                      |
| [`invalid_timezone`](#invalid_timezone)                                                                                           | A field cannot be parsed as a timezone.                                                                                                                |
| [`invalid_url`](#invalid_url)                                                                                                     | A field contains a malformed URL.                                                                                                                      |
| [`location_without_parent_station`](#location_without_parent_station)                                                             | A location that must have `parent_station` field does not have it.                                                                                     |
| [`location_with_unexpected_stop_time`](#location_with_unexpected_stop_time)                                                       | A location in `stops.txt` that is not a stop is referenced by some `stop_times.stop_id`.                                                               |
| [`missing_calendar_and_calendar_date_files`](#missing_calendar_and_calendar_date_files)                                           | Missing GTFS files `calendar.txt` and `calendar_dates.txt`.                                                                                            |
| [`missing_level_id`](#missing_level_id)                                                                                           | `stops.level_id` is conditionally required.                                                                                                            |
| [`missing_required_column`](#missing_required_column)                                                                             | A required column is missing in the input file.                                                                                                        |
| [`missing_required_field`](#missing_required_field)                                                                               | A required field is missing.                                                                                                                           |
| [`missing_required_file`](#missing_required_file)                                                                                 | A required file is missing.                                                                                                                            |
| [`missing_trip_edge`](#missing_trip_edge)                                                                                         | Missing trip edge `arrival_time` or `departure_time`.                                                                                                  |
| [`new_line_in_value`](#new_line_in_value)                                                                                         | New line or carriage return in a value in CSV file.                                                                                                    |
| [`number_out_of_range`](#number_out_of_range)                                                                                     | Out of range value.                                                                                                                                    |
| [`overlapping_frequency`](#overlapping_frequency)                                                                                 | Trip frequencies overlap.                                                                                                                              |
| [`pathway_to_platform_with_boarding_areas`](#pathway_to_platform_with_boarding_areas)                                             | A pathway has an endpoint that is a platform which has boarding areas.                                                                                 |
| [`pathway_to_wrong_location_type`](#pathway_to_wrong_location_type)                                                               | A pathway has an endpoint that is a station.                                                                                                           |
| [`pathway_unreachable_location`](#pathway_unreachable_location)                                                                   | A location is not reachable at least in one direction: from the entrances or to the exits.                                                             |
| [`point_near_origin`](#point_near_origin)                                                                                         | A point is too close to origin `(0, 0)`.                                                                                                               |
| [`point_near_pole`](#point_near_pole)                                                                                             | A point is too close to the North or South Pole.                                                                                                       |
| [`route_both_short_and_long_name_missing`](#route_both_short_and_long_name_missing)                                               | Missing route short name and long name.                                                                                                                |
| [`start_and_end_range_equal`](#start_and_end_range_equal)                                                                         | Two date or time fields are equal.                                                                                                                     |
| [`start_and_end_range_out_of_order`](#start_and_end_range_out_of_order)                                                           | Two date or time fields are out of order.                                                                                                              |
| [`station_with_parent_station`](#station_with_parent_station)                                                                     | A station has `parent_station` field set.                                                                                                              |
| [`stop_time_timepoint_without_times`](#stop_time_timepoint_without_times)                                                         | `arrival_time` or `departure_time` not specified for timepoint.                                                                                        |
| [`stop_time_with_arrival_before_previous_departure_time`](#stop_time_with_arrival_before_previous_departure_time)                 | Backwards time travel between stops in `stop_times.txt`                                                                                                |
| [`stop_time_with_only_arrival_or_departure_time`](#stop_time_with_only_arrival_or_departure_time)                                 | Missing `stop_times.arrival_time` or `stop_times.departure_time`.                                                                                      |
| [`stop_without_location`](#stop_without_location) | `stop_lat` and/or `stop_lon` is missing for stop with `location_type` equal to`0`, `1`, or `2`
| [`stop_without_zone_id`](#stop_without_zone_id)                                                                                   | Stop without value for `stops.zone_id`.                                                                                                                |
| [`too_many_rows`](#too_many_rows)                                                                                                 | A CSV file has too many rows.                                                                                                                          |
| [`transfer_with_invalid_stop_location_type`](#transfer_with_invalid_stop_location_type)                                           | A stop id field from GTFS file `transfers.txt` references a stop that has a `location_type` other than 0 or 1 (aka Stop/Platform or Station).          |
| [`transfer_with_invalid_trip_and_route`](#transfer_with_invalid_trip_and_route)                                                   | A trip id field from GTFS file `transfers.txt` references a route that does not match its `trips.txt` `route_id`.                                      |
| [`transfer_with_invalid_trip_and_stop`](#transfer_with_invalid_trip_and_stop)                                                     | A trip id field from GTFS file `transfers.txt` references a stop that is not included in the referenced trip's stop-times.                             |
| [`translation_foreign_key_violation`](#translation_foreign_key_violation)                                                         | An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.                                                      |
| [`translation_unexpected_value`](#translation_unexpected_value)                                                                   | A field in a translations row has value but must be empty.                                                                                             |
| [`wrong_parent_location_type`](#wrong_parent_location_type)                                                                       | Incorrect type of the parent location.                                                                                                                 |

<a name="WARNINGS"/>

## Table of WARNINGS
| Notice code                                                                                   | Description                                                                                                                                                   |
|-----------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`attribution_without_role`](#attribution_without_role)                                       | Attribution with no role.                                                                                                                                     |
| [`duplicate_route_name`](#duplicate_route_name)                                               | Two distinct routes have either the same `route_short_name`, the same `route_long_name`, or the same combination of `route_short_name` and `route_long_name`. |
| [`empty_row`](#empty_row)                                                                     | A row in the input file has only spaces.                                                                                                                      |
| [`equal_shape_distance_same_coordinates`](#equal_shape_distance_same_coordinates)             | Two consecutive points have equal `shape_dist_traveled` and the same lat/lon coordinates in `shapes.txt`.                                                     |
| [`fast_travel_between_consecutive_stops`](#fast_travel_between_consecutive_stops)             | A transit vehicle moves too fast between two consecutive stops.                                                                                               |
| [`fast_travel_between_far_stops`](#fast_travel_between_far_stops)                             | A transit vehicle moves too fast between two far stops.                                                                                                       |
| [`feed_expiration_date7_days`](#feed_expiration_date7_days)                                   | Dataset should be valid for at least the next 7 days.                                                                                                         |
| [`feed_expiration_date30_days`](#feed_expiration_date30_days)                                 | Dataset should cover at least the next 30 days of service.                                                                                                    |
| [`feed_info_lang_and_agency_lang_mismatch`](#feed_info_lang_and_agency_lang_mismatch)         | Mismatching feed and agency language fields.                                                                                                                  |
| [`inconsistent_agency_lang`](#inconsistent_agency_lang)                                       | Inconsistent language among agencies.                                                                                                                         |
| [`leading_or_trailing_whitespaces`](#leading_or_trailing_whitespaces)                         | The value in CSV file has leading or trailing whitespaces.                                                                                                    |
| [`missing_feed_info_date`](#missing_feed_info_date)                                           | `feed_end_date` should be provided if `feed_start_date` is provided. `feed_start_date` should be provided if `feed_end_date` is provided.                     |
| [`missing_recommended_file`](#missing_recommended_file)                                       | A recommended file is missing.                                                                                                                                |
| [`missing_recommended_field`](#missing_recommended_field)                                     | A recommended field is missing.                                                                                                                               |
| [`missing_timepoint_column`](#missing_timepoint_column)                                       | `timepoint` column is missing for a dataset.                                                                                                                  |
| [`missing_timepoint_value`](#missing_timepoint_value)                                         | `stop_times.timepoint` value is missing for a record.                                                                                                         |
| [`more_than_one_entity`](#more_than_one_entity)                                               | More than one row in CSV.                                                                                                                                     |
| [`non_ascii_or_non_printable_char`](#non_ascii_or_non_printable_char)                         | Non ascii or non printable char in  `id`.                                                                                                                     |
| [`pathway_dangling_generic_node`](#pathway_dangling_generic_node)                             | A generic node has only one incident location in a pathway graph.                                                                                             |
| [`pathway_loop`](#pathway_loop)                                                               | A pathway starts and ends at the same location.                                                                                                               |
| [`platform_without_parent_station`](#platform_without_parent_station)                         | A platform has no `parent_station` field set.                                                                                                                 |
| [`route_color_contrast`](#route_color_contrast)                                               | Insufficient route color contrast.                                                                                                                            |
| [`route_short_and_long_name_equal`](#route_short_and_long_name_equal)                         | `route_short_name` and `route_long_name` are equal for a single route.                                                                                        |
| [`route_short_name_too_long`](#route_short_name_too_long)                                     | Short name of a route is too long (more than 12 characters).                                                                                                  |
| [`same_name_and_description_for_route`](#same_name_and_description_for_route)                 | Same name and description for route.                                                                                                                          |
| [`same_name_and_description_for_stop`](#same_name_and_description_for_stop)                   | Same name and description for stop.                                                                                                                           |
| [`same_route_and_agency_url`](#same_route_and_agency_url)                                     | Same `routes.route_url` and `agency.agency_url`.                                                                                                              |
| [`same_stop_and_agency_url`](#same_stop_and_agency_url)                                       | Same `stops.stop_url` and `agency.agency_url`.                                                                                                                |
| [`same_stop_and_route_url`](#same_stop_and_route_url)                                         | Same `stops.stop_url` and `routes.route_url`.                                                                                                                 |
| [`stop_has_too_many_matches_for_shape`](#stop_has_too_many_matches_for_shape)                 | Stop entry that has many potential matches to the trip's path of travel.                                                                                      |
| [`stops_match_shape_out_of_order`](#stops_match_shape_out_of_order)                           | Two stop entries are different than their arrival-departure order defined by the shapes.txt                                                                   |
| [`stop_too_far_from_shape`](#stop_too_far_from_shape)                                         | Stop too far from trip shape.                                                                                                                                 |
| [`stop_too_far_from_shape_using_user_distance`](#stop_too_far_from_shape_using_user_distance) | Stop time too far from shape.                                                                                                                                 |
| [`stop_without_stop_time`](#stop_without_stop_time)                                           | A stop in `stops.txt` is not referenced by any `stop_times.stop_id`.                                                                                          |
| [`transfer_with_suspicious_mid_trip_in_seat`](#transfer_with_suspicious_mid_trip_in_seat)     | A trip id field from GTFS file `transfers.txt` with an in-seat transfer type references a stop that is not in the expected position in the trip's stop-times. |
| [`translation_unknown_table_name`](#translation_unknown_table_name)                           | A translation references an unknown or missing GTFS table.                                                                                                    |
| [`unexpected_enum_value`](#unexpected_enum_value)                                             | An enum has an unexpected value.                                                                                                                              |
| [`unusable_trip`](#unusable_trip)                                                             | Trips must have more than one stop to be usable.                                                                                                              |
| [`unused_shape`](#unused_shape)                                                               | Shape is not used in GTFS file `trips.txt`.                                                                                                                   |
| [`unused_trip`](#unused_trip)                                                                 | Trip is not be used in `stop_times.txt`                                                                                                                       |

<a name="INFOS"/>

## Table of INFOS

| Notice code                                       | Description               |
|---------------------------------------------------|---------------------------|
| [`unknown_column`](#unknown_column) | A column name is unknown. |
| [`unknown_file`](#unknown_file)     | A file is unknown.        |

<a name="SYSTEM_ERRORS"/>

## Table of SYSTEM ERRORS

| System error code                                                               | Description                                            |
|---------------------------------------------------------------------------------|--------------------------------------------------------|
| [`i_o_error`](#i_o_error)                                                       | Error in IO operation.                                 |
| [`runtime_exception_in_loader_error`](#runtime_exception_in_loader_error)       | RuntimeException while loading GTFS dataset in memory. |
| [`runtime_exception_in_validator_error`](#runtime_exception_in_validator_error) | RuntimeException while validating GTFS archive.        |
| [`thread_execution_error`](#thread_execution_error)                             | ExecutionException during multithreaded validation     |
| [`u_r_i_syntax_error`](#u_r_i_syntax_error)                                     | A string could not be parsed as a URI reference.       |

# More details - ERRORS

<a name="BlockTripsWithOverlappingStopTimesNotice"/>

### block_trips_with_overlapping_stop_times

Trips with the same block id have overlapping stop times.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [stops.txt specification](http://gtfs.org/reference/static#stopstxt)
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)

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

</details>

<a name="CsvParsingFailedNotice"/>

### csv_parsing_failed

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

### decreasing_shape_distance

When sorted by `shape.shape_pt_sequence`, two consecutive shape points must not have decreasing values for `shape_dist_traveled`.  

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

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

</details>

<a name="DecreasingOrEqualStopTimeDistanceNotice"/>

### decreasing_or_equal_stop_time_distance

When sorted by `stop_times.stop_sequence`, two consecutive entries in `stop_times.txt` should have increasing distance, based on the field `shape_dist_traveled`. If the values are equal, this is considered as an error.  

#### References
* [stops_times.txt specification](https://gtfs.org/reference/static#stop_timestxt)

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

</details>

<a name="DuplicatedColumnNotice"/>

### duplicated_column

The input file CSV header has the same column name repeated.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [Dataset files requirements](http://gtfs.org/reference/static#file-requirements)
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

</details>

<a name="DuplicateKeyNotice"/>

### duplicate_key

The values of the given key and rows are duplicates.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [Dataset files requirements](http://gtfs.org/reference/static#file-requirements)
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

</details>

<a name="EmptyColumnNameNotice"/>

### empty_column_name

A column name has not been provided. Such columns are skipped by the validator.

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
<details>

#### Notice fields description
| Field name 	| Description                    	  | Type    	|
|------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.   	| String   	|
| `index`      	| The index of the empty column. 	| Integer 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="EmptyFileNotice"/>

### empty_file

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.
#### References
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)

<details>

#### Notice fields description
| Field name 	| Description                 	| Type   	|
|------------	|-----------------------------	|--------	|
| `filename`  | The name of the faulty file   | String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

### equal_shape_distance_diff_coordinates

<a name="EqualShapeDistanceDiffCoordinatesNotice"/>

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and different coordinates indicate an error.

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)
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

</details>

<a name="FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice"/>

### fare_transfer_rule_duration_limit_type_without_duration_limit

A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit_type` field but no `duration_limit` specified.

#### References
* [GTFS fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

#### Affected files
* [`fare_transfer_rules.txt`](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

</details>

<a name="FareTransferRuleDurationLimitWithoutTypeNotice"/>

### fare_transfer_rule_duration_limit_without_type

A row from GTFS file `fare_transfer_rules.txt` has a defined `duration_limit` field but no `duration_limit_type` specified.

#### References
* [GTFS fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

#### Affected files
* [`fare_transfer_rules.txt`](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

</details>

<a name="FareTransferRuleInvalidTransferCountNotice"/>

### fare_transfer_rule_invalid_transfer_count

A row from GTFS file `fare_transfer_rules.txt` has a defined `transfer_count` with an invalid value.

#### References
* [GTFS fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|
| `transferCount`   	| The transfer count value of the faulty record.      	| Integer	|

#### Affected files
* [`fare_transfer_rules.txt`](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

</details>

<a name="FareTransferRuleMissingTransferCountNotice"/>

### fare_transfer_rule_missing_transfer_count

A row from GTFS file `fare_transfer_rules.txt` has `from_leg_group_id` equal to `to_leg_group_id`, but has no `transfer_count` specified.  Per the spec, `transfer_count` is required if the two leg group ids are equal.

#### References
* [GTFS fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

#### Affected files
* [`fare_transfer_rules.txt`](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

</details>

<a name="FareTransferRuleWithForbiddenTransferCountNotice"/>

### fare_transfer_rule_with_forbidden_transfer_count

A row from GTFS file `fare_transfer_rules.txt` has `from_leg_group_id` not equal to `to_leg_group_id`, but has `transfer_count` specified.  Per the spec, `transfer_count` is forbidden if the two leg group ids are not equal.

#### References
* [GTFS fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

<details>

#### Notice fields description
| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

#### Affected files
* [`fare_transfer_rules.txt`](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)

</details>

<a name="ForeignKeyViolationNotice"/>

### foreign_key_violation

A foreign key references the primary key of another file. A foreign key violation means that the foreign key referenced from a given row (the child file) cannot be found in the corresponding file (the parent file). The Foreign keys are defined in the specification under "Type" for each file.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)

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

</details>

<a name="InconsistentAgencyTimezoneNotice"/>

### inconsistent_agency_timezone

Agencies from GTFS `agency.txt` have been found to have different timezones.

#### References
* [GTFS agency.txt specification](https://gtfs.org/reference/static/#agencytxt)
<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `expected`   	| Expected timezone.            	| String 	|
| `actual`     	| Faulty record's timezone.     	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

</details>

<a name="InvalidColorNotice"/>

### invalid_color

Value of field with type `color` is not valid. A color must be encoded as a six-digit hexadecimal number. The leading "#" is not included.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidCurrencyNotice"/>

### invalid_currency

Value of field with type `currency` is not valid. Currency code must follow <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidCurrencyAmountNotice"/>

### invalid_currency_amount

A currency amount field has a value that does not match the format (e.g. expected number of decimal places) of its corresponding currency code field.  The number of decimal places is specified by <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
<details>

#### Notice fields description
| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `amount` 	| Faulty currency amount value.		| String 	|

#### Affected files
* [`fare_products.txt`](http://gtfs.org/reference/static#fare_productstxt)

</details>

<a name="InvalidDateNotice"/>

### invalid_date

Value of field with type `date` is not valid. Dates must have the YYYYMMDD format.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidEmailNotice"/>

### invalid_email

Value of field with type `email` is not valid. Definitions for valid emails are quite vague. We perform strict validation using the Apache Commons EmailValidator.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons EmailValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/EmailValidator.html)
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

 </details>

<a name="InvalidFloatNotice"/>

### invalid_float

Value of field with type `float` is not valid. 

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>
 
<a name="InvalidIntegerNotice"/>

### invalid_integer

Value of field with type `integer` is not valid. 

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidLanguageCodeNotice"/>

### invalid_language_code

Value of field with type `language` is not valid. Language codes must follow <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">IETF BCP 47</a>.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidPhoneNumberNotice"/>

### invalid_phone_number

Value of field with type `phone number` is not valid. This rule uses the [PhoneNumberUtil](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.4.1/com/google/i18n/phonenumbers/PhoneNumberUtil.html) class to validate a phone number based on a country code. If no country code is provided in the parameters used to run the validator, this notice won't be emitted. 

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidRowLengthNotice"/>

### invalid_row_length

A row in the input file has a different number of values than specified by the CSV header.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS files requirements](https://gtfs.org/reference/static#file-requirements)

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

</details>

<a name="InvalidTimeNotice"/>

### invalid_time

Value of field with type `time` is not valid. Time must be in the `H:MM:SS`, `HH:MM:SS` or `HHH:MM:SS` format.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidTimezoneNotice"/>

### invalid_timezone

Value of field with type `timezone` is not valid.Timezones are defined at <a href="https://www.iana.org/time-zones">www.iana.org</a>. Timezone names never contain the space character but may contain an underscore. Refer to <a href="http://en.wikipedia.org/wiki/List_of_tz_zones">Wikipedia</a> for a list of valid values.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="InvalidUrlNotice"/>

### invalid_url

Value of field with type `url` is not valid. Definitions for valid URLs are quite vague. We perform strict validation using the Apache Commons UrlValidator.

#### References
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [Apache Commons UrlValidator](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)
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

</details>

<a name="LocationWithoutParentStationNotice"/>

### location_without_parent_station

A location that must have `parent_station` field does not have it. The following location types must have `parent_station`: entrance, generic node, boarding_area.

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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

</details>

<a name="LocationWithUnexpectedStopTimeNotice"/>

### location_with_unexpected_stop_time

Referenced locations (using `stop_times.stop_id`) must be stops/platforms, i.e. their `stops.location_type` value must be 0 or empty.

#### References
* [stop_times.txt GTFS specification](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stoptimestxt)
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

</details>

<a name="MissingCalendarAndCalendarDateFilesNotice"/>

### missing_calendar_and_calendar_date_files

Both files calendar_dates.txt and calendar.txt are missing from the GTFS archive. At least one of the files must be provided.

#### References
* [calendar.txt specification](http://gtfs.org/reference/static/#calendartxt)
* [calendar_dates.txt specification](http://gtfs.org/reference/static/#calendar_datestxt)
<details>

#### Notice fields description
| Field name 	| Description 	| Type 	|
|------------	|-------------	|------	|
| N/A          	| N/A           | N/A  	|

#### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)

</details>

<a name="MissingLevelIdNotice"/>

### missing_level_id

GTFS file `levels.txt` is required for elevator (`pathway_mode=5`). A row from `stops.txt` linked to an elevator pathway has no value for `stops.level_id`.

#### References
* [levels.txt specification](http://gtfs.org/reference/static/#levelstxt)
<details>

#### Notice fields description
| Field name    	| Description                                                      	 | Type   	|
|---------------	|------------------------------------------------------------------- |--------	|
| `csvRowNumber`  | The row number of the faulty record. 	                             | Long   	|
| `stopId`   	  | The id of the faulty from `stops.txt`.                               | String   |

#### Affected files
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)

</details>

<a name="MissingRequiredColumnNotice"/>

### missing_required_column

A required column is missing in the input file.

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
<details>

#### Notice fields description
| Field name 	  | Description                     | Type   	|
|-------------	|-------------------------------- |--------	|
| `filename`   	| The name of the faulty file.    | String 	|
| `fieldName`  	| The name of the missing column. | String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="MissingRequiredFieldNotice"/>

### missing_required_field

The given field has no value in some input row, even though values are required.

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|
| `csvRowNumber`| The row of the faulty record.  	| Long   	|
| `fieldName`  	| The name of the missing field. 	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="MissingRequiredFileNotice"/>

### missing_required_file

A required file is missing. If this notice is triggered for every core file, it might be a problem with the input. To create a zip file from the GTFS `.txt` files: select all the `.txt` files, right-click, and compress. Do not compress the folder containing the files. 

#### References
* [GTFS terms definition](https://gtfs.org/reference/static/#term-definitions)
<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="MissingTripEdgeNotice"/>

### missing_trip_edge

First and last stop of a trip must define both `arrival_time` and `departure_time` fields.

#### References
* [stop_times.txt specification](https://gtfs.org/reference/static/#stop_timestxt)
<details>

#### Notice fields description
| Field name     	  | Description                                 | Type    	|
|-----------------  |-------------------------------------------- |---------	|
| `csvRowNumber`  	| The row of the faulty record.               | Long    	|
| `stopSequence`  	| `stops.stop_sequence` of the faulty record. | Integer 	|
| `tripId`        	| The `trips.trip_id` of the faulty record.   | String  	|
| `specifiedField`  | Name of the missing field.                 	| String  	|

* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

</details>

<a name="NewLineInValueNotice"/>

### new_line_in_value

A value in CSV file has a new line or carriage return.

#### References
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)
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

</details>

<a name="NumberOutOfRangeNotice"/>

### number_out_of_range

The values in the given column of the input rows are out of range.

#### References
* [GTFS file requirements](https://gtfs.org/reference/static/#file-requirements)
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [GTFS field types](http://gtfs.org/reference/static/#field-types)
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

</details>

<a name="OverlappingFrequencyNotice"/>

### overlapping_frequency

Trip frequencies must not overlap in time

#### References
* [frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)
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

</details>

<a name="PathwayToPlatformWithBoardingAreasNotice"/>

### pathway_to_platform_with_boarding_areas

A pathway has an endpoint that is a platform which has boarding areas. A platform that has boarding
areas is treated as a parent object, not a point. In such cases, the platform must not have pathways
assigned - instead, pathways must be assigned to its boarding areas.

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)

<a name="PathwayToWrongLocationTypeNotice"/>

### pathway_to_wrong_location_type

A pathway has an endpoint that is a station. Pathways endpoints must be platforms (stops),
entrances/exits, generic nodes or boarding areas.

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)

<a name="PathwayUnreachableLocationNotice"/>

### pathway_unreachable_location

A location belongs to a station that has pathways and is not reachable at least in one direction:
from the entrances or to the exits.

Notices are reported for platforms, boarding areas and generic nodes but not for entrances or
stations.

Notices are not reported for platforms that have boarding areas since such platforms may not
have incident pathways. Instead, notices are reported for the boarding areas.

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
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

 </details>

<a name="PointNearOriginNotice"/>

### point_near_origin

A point is too close to origin `(0, 0)`.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="PointNearPoleNotice"/>

### point_near_pole

A point is too close to the North or South Pole.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="RouteBothShortAndLongNameMissingNotice"/>

### route_both_short_and_long_name_missing

Both short_name and long_name are missing for a route.

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
<details>

#### Notice fields description
| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
| `routeId`      	| The id of the faulty record.         	| String 	|
| `csvRowNumber` 	| The row number of the faulty record. 	| Long   	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

</details>

<a name="StartAndEndRangeEqualNotice"/>

### start_and_end_range_equal

The fields `frequencies.start_date` and `frequencies.end_date` have been found equal in `frequencies.txt`. The GTFS spec is currently unclear how this case should be handled (e.g., is it a trip that circulates once?). It is recommended to use a trip not defined via frequencies.txt for this case.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
[frequencies.txt specification](http://gtfs.org/reference/static#frequenciestxt)
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

</details>

<a name="StartAndEndRangeOutOfOrderNotice"/>

### start_and_end_range_out_of_order

Date or time fields have been found out of order in `calendar.txt`, `feed_info.txt` and `stop_times.txt`.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [calendar.txt specification](http://gtfs.org/reference/static#calendartxt)
* [calendar_dates.txt specification](http://gtfs.org/reference/static#calendar_datestxt)
* [feed_info.txt specification](http://gtfs.org/reference/static#feed_infotxt)
* [stop_times.txt specification](http://gtfs.org/reference/static#stop_timestxt)
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

</details>

<a name="StationWithParentStationNotice"/>

### station_with_parent_station

Field `parent_station` must be empty when `location_type` is 1.

#### References
[stop.txt](http://gtfs.org/reference/static/#stopstxt)
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

</details>

<a name="StopTimeTimepointWithoutTimesNotice"/>

### stop_time_timepoint_without_times

Any records with `stop_times.timepoint` set to 1 must define a value for `stop_times.arrival_time` and `stop_times.departure_time` fields.

#### References
* [GTFS stop_times.txt specification](https://gtfs.org/reference/static#stoptimestxt)
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

</details>

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### stop_time_with_arrival_before_previous_departure_time

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence in `stop_times.txt`.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [stop_times.txt specification](http://gtfs.org/reference/static#stop_timestxt)
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

</details>

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

### stop_time_with_only_arrival_or_departure_time

Missing `stop_time.arrival_time` or `stop_time.departure_time`

#### References
* [stop_times.txt specification](http://gtfs.org/reference/static/#stop_timestxt)
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

</details>

<a name="StopWithoutLocationNotice"/>

### stop_without_location

`stop_lat` and/or `stop_lon` are required for locations that are stops (`location_type=0`), stations (`location_type=1`) or entrances/exits (`location_type=2`).

#### References
* [GTFS stops.txt specification](https://gtfs.org/reference/static#stopstxt)
<details>

#### Notice fields description
| Field name               	| Description                                	| Type   	|
|--------------------------	|--------------------------------------------	|--------	|
| `stopId`                 	| The faulty record's id.                    	| String 	|
| `locationType`            | The faulty record's `stops.location_type`. 	| Integer	|
| `csvRowNumber`           	| The row number of the faulty record.       	| Long   	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

</details>

<a name="StopWithoutZoneIdNotice"/>

### stop_without_zone_id

If `fare_rules.txt` is provided, and `fare_rules.txt` uses at least one column among `origin_id`, `destination_id`, and `contains_id`, then all stops and platforms (location_type = 0) must have `stops.zone_id` assigned. 

#### References
* [GTFS stops.txt specification](https://gtfs.org/reference/static#stopstxt)
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

</details>

<a name="TooManyRowsNotice"/>

### too_many_rows

A CSV file has too many rows. Feeds with too large files cannot be processed in a reasonable time by GTFS consumers.

#### References
None.

<details>

#### Notice fields description
| Field name          | Description                                   | Type   |
|---------------------|-----------------------------------------------|--------|
| `filename`          | Name of the CSV file that has too many rows.  | String |
| `rowNumber`         | Number of the row when reading was stopped.   | long   |

</details>

<a name="TransferWithInvalidStopLocationTypeNotice"/>

### transfer_with_invalid_stop_location_type

A `from_stop_id` or `to_stop_id` field from GTFS file `transfers.txt` references a stop that has a `location_type` other than 0 or 1 (aka Stop/Platform or Station).

#### References
* [transfers.txt specification](http://gtfs.org/reference/static/#transferstxt)

<details>

#### Notice fields description
| Field name          | Description                                                               | Type   |
|---------------------|---------------------------------------------------------------------------|--------|
| `csvRowNumber`      | The row number from `transfers.txt` for the faulty entry.                 | long   |
| `stopIdFieldName`   | The name of the stop id field (e.g. `from_stop_id`) referencing the stop. | String |
| `stopId`            | The referenced stop id.                                                   | String |
| `locationTypeValue` | The numeric value of the invalid location type.                           | int    |
| `locationTypeName`  | The name of the invalid location type.                                    | String |

</details>

<a name="TransferWithInvalidTripAndRouteNotice"/>

### transfer_with_invalid_trip_and_route

A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` references a route that does not match its `trips.txt` `route_id`.

#### References
* [transfers.txt specification](http://gtfs.org/reference/static/#transferstxt)

<details>

#### Notice fields description
| Field name        | Description                                                                  | Type   |
|-------------------|------------------------------------------------------------------------------|--------|
| `csvRowNumber`    | The row number from `transfers.txt` for the faulty entry.                    | long   |
| `tripFieldName`   | The name of the trip id field (e.g. `from_trip_id`) referencing a trip.      | String |
| `tripId`          | The referenced trip id.                                                      | String |
| `routeFieldName`  | The name of the route id field (e.g. `from_route_id`) referencing the route. | String |
| `routeId`         | The referenced route id.                                                     | String |
| `expectedRouteId` | The expected route id from `trips.txt`.                                      | String |

</details>

<a name="TransferWithInvalidTripAndStopNotice"/>

### transfer_with_invalid_trip_and_stop

A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` references a stop that is not included in the referenced trip's stop-times.

#### References
* [transfers.txt specification](http://gtfs.org/reference/static/#transferstxt)

<details>

#### Notice fields description
| Field name      | Description                                                                | Type   |
|-----------------|----------------------------------------------------------------------------|--------|
| `csvRowNumber`  | The row number from `transfers.txt` for the faulty entry.                  | long   |
| `tripFieldName` | The name of the trip id field (e.g. `from_trip_id`) referencing a trip.    | String |
| `tripId`        | The referenced trip id.                                                    | String |
| `stopFieldName` | The name of the stop id field (e.g. `stop_route_id`) referencing the stop. | String |
| `stopId`        | The referenced stop id.                                                    | String |

</details>

<a name="TransferWithSuspiciousMidTripInSeatNotice"/>

### transfer_with_suspicious_mid_trip_in_seat

A `from_trip_id` or `to_trip_id` field from GTFS file `transfers.txt` with an in-seat transfer type references a stop that is not in the expected position in the trip's stop-times. For in-seat transfers, we expect the stop to be the last stop-time in the trip sequence for `from_stop_id` and the first stop-time for `to_stop_id`. If you are intentionally using this feature to model mid-trip transfers, you can ignore this warning, but be aware that this functionality is still considered to be partially experimental in some interpretations of the spec.

<details>

#### Notice fields description
| Field name        | Description                                                               | Type   |
|-------------------|---------------------------------------------------------------------------|--------|
| `csvRowNumber`    | The row number from `transfers.txt` for the faulty entry.                 | long   |
| `tripIdFieldName` | The name of the trip id field (e.g. `from_trip_id`) referencing a trip.   | String |
| `tripId`          | The referenced trip id.                                                   | String |
| `stopIdFieldName` | The name of the stop id field (e.g. `from_stop_id`) referencing the stop. | String |
| `stopId`          | The referenced stop id.                                                   | String |

</details>

<a name="TranslationForeignKeyViolationNotice"/>

### translation_foreign_key_violation

An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced table.

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
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

</details>

<a name="TranslationUnexpectedValueNotice"/>

### translation_unexpected_value

A field in a translations row has value but must be empty.

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
<details>

#### Notice fields description
| Field name        | Description                                  	            | Type    	|
|-------------------|-----------------------------------------------------------|---------	|
| `csvRowNumber`    | The row number of the faulty record.         	            | Long    	|
| `fieldName`       | The name of the field that was expected to be empty.      | String  	|
| `fieldValue`      | Actual value of the field that was expected to be empty.  | String 	|

#### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

</details>

<a name="WrongParentLocationTypeNotice"/>

### wrong_parent_location_type

Value of field `location_type` of parent found in field `parent_station` is invalid.

According to spec
- _Stop/platform_ can only have _Station_ as parent
- _Station_ can NOT have a parent
- _Entrance/exit_ or _generic node_ can only have _Station_ as parent
- _Boarding Area_ can only have _Platform_ as parent 

Any other combination raise this error.

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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

</details>

# More details - WARNINGS

<a name="AttributionWithoutRoleNotice"/>

### attribution_without_role

At least one of the fields `is_producer`, `is_operator`, or `is_authority` should be set to 1.

#### References
* [attributions.txt specification](https://gtfs.org/reference/static#attributionstxt)
<details>

#### Notice fields description
| Field name    	| Description                          	  | Type   	|
|---------------	|---------------------------------------	|--------	|
| `csvRowNumber`  	| The row number of the faulty record. 	| Long   	|
| `attributionId` 	| The id of the faulty record.         	| String 	|

#### Affected files
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)

</details>

<a name="DuplicateRouteNameNotice"/>

### duplicate_route_name

All routes of the same `route_type` with the same `agency_id` should have unique combinations of `route_short_name` and `route_long_name`.

Note that there may be valid cases where routes have the same short and long name, e.g., if they serve different areas. However, different directions must be modeled as the same route.

Example of bad data:
| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| U1                 	| Southern          	|
| route2     	| U1                 	| Southern          	|

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [routes.txt best practices](http://gtfs.org/best-practices/#routestxt)
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


</details>

<a name="EmptyRowNotice"/>

### empty_row

A row in the input file has only spaces.

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
<details>

#### Notice fields description
| Field name   	| Description                          	| Type    	|
|--------------	|--------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.         	| String  	|
| `csvRowNumber`| The row number of the faulty record. 	| Long 	    |

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="EqualShapeDistanceSameCoordinatesNotice"/>

### equal_shape_distance_same_coordinates

When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase along a shape. Two consecutive points with equal values for `shape_dist_traveled` and the same coordinates indicate a duplicative shape point.

#### References
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)
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

</details>

<a name="FastTravelBetweenConsecutiveStopsNotice"/>

### fast_travel_between_consecutive_stops

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

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="FastTravelBetweenFarStopsNotice"/>

### fast_travel_between_far_stops

A transit vehicle moves too fast between far consecutive stops (more than in 10 km apart). 
This normally indicates a more serious problem than too fast travel between consecutive stops.
The speed threshold depends on route type.

##### Speed thresholds

Same as for [`fast_travel_between_consecutive_stops`](#fast_travel_between_consecutive_stops).

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="FeedExpirationDate7DaysNotice"/>

### feed_expiration_date7_days

The dataset expiration date defined in `feed_info.txt` is in seven days or less. At any time, the published GTFS dataset should be valid for at least the next 7 days.

### References
* [General Publishing & General Practices](https://gtfs.org/best-practices/#dataset-publishing--general-practices)

<details>

#### Notice fields description
| Field name              	        | Description                                                                     	| Type   	|
|-------------------------	|----------------------------------------------	        |--------	|
| `csvRowNumber`           	| The row number of the faulty record.         	                 | Long   	|
| `currentDate`            	        | Current date (YYYYMMDD format).              	        | String 	|
| `feedEndDate`            	        | Feed end date (YYYYMMDD format).             	        | String 	|
| `suggestedExpirationDate`	| Suggested expiration date (YYYYMMDD format). 	| String 	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
</details>


<a name="FeedExpirationDate30DaysNotice"/>

### feed_expiration_date30_days

At any time, the GTFS dataset should cover at least the next 30 days of service, and ideally for as long as the operator is confident that the schedule will continue to be operated.

#### References
* [General Publishing & General Practices](https://gtfs.org/best-practices/#dataset-publishing--general-practices)
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

</details>

<a name="FeedInfoLangAndAgencyLangMismatchNotice"/>

### feed_info_lang_and_agency_lang_mismatch
1. Files `agency.txt` and `feed_info.txt` should define matching `agency.agency_lang` and `feed_info.feed_lang`.
  The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
  * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
  * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
  * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

#### References
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)
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

</details>

<a name="InconsistentAgencyLangNotice"/>

### inconsistent_agency_lang

Agencies from GTFS `agency.txt` have been found to have different languages.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

#### Notice fields description
| Field name     	| Description                   	| Type   	|
|----------------	|-------------------------------	|--------	|
| `csvRowNumber` 	| The row of the faulty record. 	| Long   	|
| `expected`     	| Expected language.            	| String 	|
| `actual`       	| Faulty record's language.     	| String 	|

#### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

</details>

<a name="LeadingOrTrailingWhitespacesNotice"/>

### leading_or_trailing_whitespaces

The value in CSV file has leading or trailing whitespaces.

#### References
* [GTFS file requirements](http://gtfs.org/reference/static/#file-requirements)
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

</details>

<a name="MissingFeedInfoDateNotice"/>

### missing_feed_info_date

Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided.

#### References
* [feed_info.txt Best practices](http://gtfs.org/best-practices/#feed_infotxt)
<details>

#### Notice fields description
| Field name     	| Description                                 	| Type   	|
|----------------	|---------------------------------------------	|--------	|
| `fieldName`    	| Either `feed_end_date` or `feed_start_date` 	| String 	|
| `csvRowNumber` 	| The row number of the faulty record.        	| Long   	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
#### Notice fields description

</details>

<a name="MissingRecommendedFileNotice"/>

### missing_recommended_file

A recommended file is missing.

#### References
* [feed_info.txt best practices](https://gtfs.org/schedule/best-practices/#feed_infotxt)
<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

</details>

<a name="MissingRecommendedFieldNotice"/>

### missing_recommended_field

The given field has no value in some input row, even though values are recommended.

#### References
* [feed_info.txt best practices](https://gtfs.org/schedule/best-practices/#feed_infotxt)
<details>

#### Notice fields description
| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|
| `csvRowNumber`| The row of the faulty record.  	| Long   	|
| `fieldName`  	| The name of the missing field. 	| String 	|

#### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

</details>

<a name="MissingTimepointColumnNotice"/>

### missing_timepoint_column

The `timepoint` column should be provided.

#### References
* [stop_times.txt best practices](https://gtfs.org/schedule/best-practices/#stop_timestxt)
<details>

#### Notice fields description
| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `filename`    	| The name of the affected file.                  	| String   	|

#### Affected files
* [`stop_times.txt`](https://gtfs.org/schedule/reference/#stop_timestxt)
</details>

<a name="MissingTimepointValueNotice"/>

### missing_timepoint_value

Even though the column `timepoint` is optional in `stop_times.txt` according to the specification, `stop_times.timepoint` should not be empty when provided. 

#### References
* [stop_times.txt specification](https://gtfs.org/schedule/reference/#stop_timestxt)
<details>


#### Notice fields description
| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `csvRowNumber` 	| The row number of the faulty record.            	| Long   	|
| `tripId`       	| The faulty record's `stop_times.trip_id`.         | String 	|
| `stopSequence` 	| The faulty record's `stop_times.stop_sequence`. 	| String 	|

#### Affected files
* [`stop_times.txt`](https://gtfs.org/schedule/reference/#stop_timestxt)

</details>

<a name="MoreThanOneEntityNotice"/>

### more_than_one_entity

The file is expected to have a single entity but has more (e.g., "feed_info.txt").

#### References
* [GTFS field definition](http://gtfs.org/reference/static#field-definitions)
<details>

#### Notice fields description
| Field name    	| Description              	| Type   	|
|---------------	|--------------------------	|--------	|
| `filename`    	| Name of the faulty file. 	| String 	|
| `entityCount` 	| Number of occurrences.   	| Long   	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="NonAsciiOrNonPrintableCharNotice"/>

### non_ascii_or_non_printable_char

A value of a field with type `id` contains non ASCII or non printable characters. This is not recommended.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="PathwayDanglingGenericNodeNotice"/>

### pathway_dangling_generic_node

A generic node has only one incident location in a pathway graph. Such generic node is useless
because there is no benefit in visiting it.

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
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

</details>

<a name="PathwayLoopNotice"/>

### pathway_loop

A pathway should not have same values for `from_stop_id` and `to_stop_id`.

#### References
* [pathways.txt specification](http://gtfs.org/reference/static/#pathwaystxt)
<details>

#### Notice fields description
| Field name     	| Description                                                                                 	| Type   	|
|----------------	|---------------------------------------------------------------------------------------------	|--------	|
| `csvRowNumber` 	| Row number of the faulty row from `pathways.txt`.                                           	| Long   	|
| `pathwayId`    	| The id of the faulty record.                                                                	| String 	|
| `stopId`       	| The `pathway.stop_id` that is repeated in `pathways.from_stop_id` and `pathways.to_stop_id`. 	| String 	|

#### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
</details>

<a name="PlatformWithoutParentStationNotice"/>

### platform_without_parent_station

A platform has no `parent_station` field set.

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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

</details>

<a name="RouteColorContrastNotice"/>

### route_color_contrast

A route's color and `route_text_color` should be contrasting.

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
* [Original Python validator implementation](https://github.com/google/transitfeed)
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

</details>

<a name="RouteShortAndLongNameEqualNotice"/>

### route_short_and_long_name_equal

A single route has the same values for `route_short_name` and `route_long_name`.

Example of bad data:

| `route_id` 	| `route_short_name` 	| `route_long_name` 	|
|------------	|--------------------	|-------------------	|
| route1     	| L1                 	| L1                	|

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
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

</details>

<a name="RouteShortNameTooLongNotice"/>

### route_short_name_too_long

Short name of a route is too long (more than 12 characters).

#### References
* [routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)
<details>

#### Notice fields description
| Field name     	| Description                             	  | Type   	|
|----------------	|-------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| String  |
| `csvRowNumber`   	| The row number of the faulty record.    	| Long 	  |
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|

#### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

</details>

<a name="SameNameAndDescriptionForRouteNotice"/>

### same_name_and_description_for_route

The GTFS spec defines `routes.txt` [route_desc](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_desc` fields.

#### References
[routes.txt specification](http://gtfs.org/reference/static/#routestxt)
[routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)
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

</details>

<a name="SameNameAndDescriptionForStopNotice"/>

### same_name_and_description_for_stop

The GTFS spec defines `stops.txt` [stop_description](https://gtfs.org/reference/static/#stopstxt) as:

> Description of the location that provides useful, quality information. Do not simply duplicate the name of the location.

#### References
[stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
<details>

#### Notice fields description
| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `csvRowNumber`  | The row number of the faulty record.      | Long 	  |
| `stopId`        | The id of the faulty record.              | String  |
| `stopDesc`    	| The faulty record's `stop_desc`.         	| String 	|

#### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

</details>

<a name="SameRouteAndAgencyUrlNotice"/>

### same_route_and_agency_url

A route should not have the same `routes.route_url` as a record from `agency.txt`.

#### References
* [routes.txt specification](http://gtfs.org/reference/static/#routestxt)
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

</details>

<a name="SameStopAndAgencyUrlNotice"/>

### same_stop_and_agency_url

A stop should not have the same `stops.stop_url` as a record from `agency.txt`.

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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

</details>

<a name="SameStopAndRouteUrlNotice"/>

### same_stop_and_route_url

A stop should not have the same `stop.stop_url` as a record from `routes.txt`.

#### References
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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

 </details>

<a name="StopHasTooManyMatchesForShapeNotice"/>

### stop_has_too_many_matches_for_shape

A stop entry that has many potential matches to the trip's path of travel, as defined  by the shape entry in `shapes.txt`.

#### References
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)
* [stops_times.txt specification](https://gtfs.org/schedule/reference/#stop_timestxt)
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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
* [`stops_times.txt`](https://gtfs.org/schedule/reference/#stop_timestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
 </details>

<a name="StopsMatchShapeOutOfOrderNotice"/>

### stops_match_shape_out_of_order

Two stop entries in `stop_times.txt` are different than their arrival-departure order as defined by the shape in the `shapes.txt` file.

#### References
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)
* [stops_times.txt specification](https://gtfs.org/schedule/reference/#stop_timestxt)
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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
</details>

<a name="StopTooFarFromShapeNotice"/>

### stop_too_far_from_shape

Per GTFS Best Practices, route alignments (in `shapes.txt`) should be within 100 meters of stop locations which a trip serves.

#### References
* [GTFS Best Practices shapes.txt](https://gtfs.org/best-practices/#shapestxt)
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

 </details>

<a name="StopTooFarFromShapeUsingUserDistanceNotice"/>

### stop_too_far_from_shape_using_user_distance

A stop time entry that is a large distance away from the location of the shape in `shapes.txt` as defined by `shape_dist_traveled` values.

#### References
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)
* [stops_times.txt specification](https://gtfs.org/schedule/reference/#stop_timestxt)
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
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
</details>

<a name="StopWithoutStopTimeNotice"/>

### stop_without_stop_time

A stop in `stops.txt` is not referenced by any `stop_times.stop_id`, so it is not used by any trip.
Such stops normally do not provide user value. This notice may indicate a typo in `stop_times.txt`.

#### References
* [stops_times.txt specification](https://gtfs.org/schedule/reference/#stop_timestxt)
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="TranslationUnknownTableNameNotice"/>

### translation_unknown_table_name

A translation references an unknown or missing GTFS table.

#### References
* [translations.txt specification](http://gtfs.org/reference/static/#translationstxt)
<details>

#### Notice fields description
| Field name       | Description                            | Type    	|
|------------------|----------------------------------------|-------	|
| `csvRowNumber`   | The row number of the faulty record.   | Long    	|
| `tableName`      | `table_name` of the faulty record.     | String  	|

#### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

</details>
<a name="UnexpectedEnumValueNotice"/>

### unexpected_enum_value

An enum has an unexpected value.

#### References
* [GTFs field definitions](http://gtfs.org/reference/static/#field-definitions)
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

</details>

<a name="UnusableTripNotice"/>

### unusable_trip

A trip must visit more than one stop in stop_times.txt to be usable by passengers for boarding and alighting.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

#### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

</details>

<a name="UnusedShapeNotice"/>

### unused_shape

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

#### References:
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [shapes.txt specification](http://gtfs.org/reference/static#shapestxt)
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)
<details>

#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `shapeId     	| The faulty record's id.              	| String 	|

#### Affected files
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

</details>

<a name="UnusedTripNotice"/>

### unused_trip

Trips should be referred to at least once in `stop_times.txt`.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
* [trips.txt specification](http://gtfs.org/reference/static#tripstxt)
* [stop_times.txt specification](http://gtfs.org/reference/static#stop_timestxt)

<details>

#### Notice fields description
| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

#### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

</details>

# More details - INFOS

<a name="UnknownColumnNotice"/>

### unknown_column

A column is unknown.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

#### Notice fields description
| Field name 	  | Description                     	| Type    	|
|-------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.    	| String 	|
| `fieldName`  	| The name of the unknown column. 	| String  	|
| `index`      	| The index of the faulty column. 	| Integer 	|

#### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

</details>

<a name="UnknownFileNotice"/>

### unknown_file

A file is unknown.

#### References
* [Original Python validator implementation](https://github.com/google/transitfeed)
<details>

#### Notice fields description
| Field name 	  | Description                     	| Type    |
|-------------	|---------------------------------	|---------|
| `filename`   	| The name of the unknown file.    	| String 	|

</details>

# More details - SYSTEM ERRORS

<a name="IOError"/>

### i_o_error

Error in IO operation.
<details>

#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
</details>

<a name="RuntimeExceptionInLoaderError"/>

### runtime_exception_in_loader_error

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
</details>

<a name="RuntimeExceptionInValidatorError"/>

### runtime_exception_in_validator_error

A [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) occurred during validation. This normally indicates a bug in validator code, e.g., in a custom validator class.
<details>

#### Notice fields description
| Field name 	| Description                                                   	| Type    |
|------------	|---------------------------------------------------------------	|---------|
| `validator` | The name of the validator that caused the exception.            | String 	|
| `exception` | The name of the exception.                                    	| String 	|
| `message`   | The error message that explains the reason for the exception. 	| String  |
</details>

<a name="ThreadExecutionError"/>

### thread_execution_error

An [ExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutionException.html) occurred during multithreaded validation.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
</details>

<a name="URISyntaxError"/>

### u_r_i_syntax_error

A string could not be parsed as a URI reference.
<details>


#### Notice fields description
| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
</details>
