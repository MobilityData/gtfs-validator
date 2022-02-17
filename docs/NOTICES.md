# Notices field description

## Validation notices
### Errors

**Notice code table**

| Notice code                                             | Notice                                                                                                          	|
|--------------------------------------------------------	|-----------------------------------------------------------------------------------------------------------------	|
| `block_trips_with_overlapping_stop_times`               | [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         	|
| `csv_parsing_failed`                                   	| [`CsvParsingFailedNotice`](#CsvParsingFailedNotice)                                                             	|
| `decreasing_shape_distance`                            	| [`DecreasingShapeDistanceNotice`](#DecreasingShapeDistanceNotice)                                               	|
| `decreasing_or_equal_stop_time_distance`                | [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           	|
| `duplicated_column`                                    	| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             	|
| `duplicate_fare_rule_zone_id_fields`                   	| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   	|
| `duplicate_key`                                        	| [`DuplicateKeyNotice`](#DuplicateKeyNotice)                                                                     	|
| `empty_column_name`                                   	| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                         	                    |
| `empty_file`                                           	| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           	|
| `equal_shape_distance_diff_coordinates`                   | [`EqualShapeDistanceDiffCoordinatesNotice`](#EqualShapeDistanceDiffCoordinatesNotice)                             |
| `foreign_key_violation`                                	| [`ForeignKeyViolationNotice`](#ForeignKeyViolationNotice)                                                       	|
| `inconsistent_agency_timezone`                         	| [`InconsistentAgencyTimezoneNotice`](#InconsistentAgencyTimezoneNotice)                                         	|
| `invalid_color`                                        	| [`InvalidColorNotice`](#InvalidColorNotice)                                                                     	|
| `invalid_currency`                                     	| [`InvalidCurrencyNotice`](#InvalidCurrencyNotice)                                                               	|
| `invalid_date`                                         	| [`InvalidDateNotice`](#InvalidDateNotice)                                                                       	|
| `invalid_email`                                        	| [`InvalidEmailNotice`](#InvalidEmailNotice)                                                                     	|
| `invalid_float`                                        	| [`InvalidFloatNotice`](#InvalidFloatNotice)                                                                     	|
| `invalid_integer`                                      	| [`InvalidIntegerNotice`](#InvalidIntegerNotice)                                                                 	|
| `invalid_language_code`                                	| [`InvalidLanguageCodeNotice`](#InvalidLanguageCodeNotice)                                                       	|
| `invalid_phone_number`                                 	| [`InvalidPhoneNumberNotice`](#InvalidPhoneNumberNotice)                                                         	|
| `invalid_row_length`                                   	| [`InvalidRowLengthNotice`](#InvalidRowLengthNotice)                                                             	|
| `invalid_time`                                         	| [`InvalidTimeNotice`](#InvalidTimeNotice)                                                                       	|
| `invalid_timezone`                                     	| [`InvalidTimezoneNotice`](#InvalidTimezoneNotice)                                                               	|
| `invalid_url`                                          	| [`InvalidUrlNotice`](#InvalidUrlNotice)                                                                         	|
| `location_with_unexpected_stop_time`                      | [`LocationWithUnexpectedStopTimeNotice`](#LocationWithUnexpectedStopTimeNotice)	                                |
| `location_without_parent_station`                      	| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     	|
| `missing_calendar_and_calendar_date_files`             	| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       	|
| `missing_level_id`                         	            | [`MissingLevelIdNotice`](#MissingLevelIdNotice)                                       	                        |
| `missing_required_column`                              	| [`MissingRequiredColumnNotice`](#MissingRequiredColumnNotice)                                                   	|
| `missing_required_field`                               	| [`MissingRequiredFieldNotice`](#MissingRequiredFieldNotice)                                                     	|
| `missing_required_file`                                	| [`MissingRequiredFileNotice`](#MissingRequiredFileNotice)                                                       	|
| `missing_trip_edge`                                    	| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               	|
| `new_line_in_value`                                    	| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                 	|
| `number_out_of_range`                                  	| [`NumberOutOfRangeNotice`](#NumberOutOfRangeNotice)                                                             	|
| `overlapping_frequency`                                	| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     	|
| `pathway_unreachable_location`                            | [`PathwayUnreachableLocationNotice`](#PathwayUnreachableLocationNotice)	                                        |
| `point_near_origin`                                       | [`PointNearOriginNotice`](#PointNearOriginNotice)	                                        |
| `route_both_short_and_long_name_missing`               	| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             	|
| `start_and_end_range_equal`                            	| [`StartAndEndRangeEqualNotice`](#StartAndEndRangeEqualNotice)                                                   	|
| `start_and_end_range_out_of_order`                     	| [`StartAndEndRangeOutOfOrderNotice`](#StartAndEndRangeOutOfOrderNotice)                                         	|
| `station_with_parent_station`                          	| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             	|
| `stop_time_timepoint_without_times`        	            | [`StopTimeTimepointWithoutTimesNotice`](#StopTimeTimepointWithoutTimesNotice)     	                            |
| `stop_without_zone_id`                     	            | [`StopWithoutZoneIdNotice`](#StopWithoutZoneIdNotice)                 	                                        |
| `stop_time_wit_arrival_before_previous_departure_time` 	| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) 	|
| `stop_time_with_only_arrival_or_departure_time`        	| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 	|
| `translation_foreign_key_violation`                       | [`TranslationForeignKeyViolationNotice`](#TranslationForeignKeyViolationNotice)	                                |
| `translation_unexpected_value`                           	| [`TranslationUnexpectedValueNotice`](#TranslationUnexpectedValueNotice)                                               	|
| `wrong_parent_location_type`                           	| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               	|

#### [`BlockTripsWithOverlappingStopTimesNotice`](/RULES.md#BlockTripsWithOverlappingStopTimesNotice)
##### Fields description

| Field name      	| Description                               	              | Type   	|
|-----------------	|---------------------------------------------------------	|--------	|
| `csvRowNumberA` 	| The row number from `trips.txt` of the first faulty trip. | Long   	|
| `tripIdA`       	| The id of first faulty trip.                           	  | String 	|
| `serviceIdA`    	| The service id of the first faulty trip.                 	| String 	|
| `csvRowNumberB` 	| The row number from `trips.txt` of the second faulty trip.| Long   	|
| `tripIdB`       	| The id of the other faulty trip.             	            | String 	|
| `serviceIdB`    	| The service id of the other faulty trip.     	            | String 	|
| `blockId`       	| The `trips.block_id` of the overlapping trip.	            | String 	|
| `intersection`  	| The overlapping period.                      	            | Date   	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [`CsvParsingFailedNotice`](/RULES.md#CsvParsingFailedNotice)
##### Fields description

| Field name    	| Description                                                                             	| Type    	|
|---------------	|-----------------------------------------------------------------------------------------	|---------	|
| `filename`    	| The name of the faulty file.                                                            	| Long    	|
| `charIndex`   	| The location of the last character read from before the error occurred.                 	| Long    	|
| `columnIndex` 	| The column index where the exception occurred.                                          	| Integer 	|
| `lineIndex`   	| The line number where the exception occurred.                                           	| Long    	|
| `message`     	| The detailed message describing the error, and the internal state of the parser/writer. 	| String  	|
| `content`     	| The record number when the exception occurred.                                          	| String  	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`DecreasingShapeDistanceNotice`](/RULES.md#DecreasingShapeDistanceNotice)
##### Fields description

| Field name            	  | Description                                                                                    	  | Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------	|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

##### Affected files
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### [`DecreasingOrEqualStopTimeDistanceNotice`](/RULES.md#DecreasingOrEqualStopTimeDistanceNotice)
##### Fields description

| Field name               	| Description                                                                                    	| Type    	|
|--------------------------	|------------------------------------------------------------------------------------------------	|---------	|
| `tripId`                  | The id of the faulty trip.                                                                     	| String  	|
| `csvRowNumber`            | The row number from `stop_times.txt`.                                                          	| Long    	|
| `shapeDistTraveled`       | Actual distance traveled along the shape from the first shape point to the faulty record.      	| Double  	|
| `stopSequence`            | The faulty record's `stop_times.stop_sequence`.                                                	| Integer 	|
| `prevCsvRowNumber`        | The row number from `stop_times.txt` of the previous stop time.                                	| Long    	|
| `prevStopTimeDistTraveled`| Actual distance traveled along the shape from the first shape point to the previous stop time. 	| Double  	|
| `prevStopSequence`        | The previous record's `stop_times.stop_sequence`.                                              	| Integer 	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`DuplicatedColumnNotice`](/RULES.md#DuplicatedColumnNotice)
##### Fields description

| Field name  	| Description                   	| Type    	|
|-------------	|-------------------------------	|---------	|
| `filename`    | The name of the faulty file.  	| String  	|
| `fieldName`   | The name of the faulty field. 	| String  	|
| `firstIndex`  | Index of the first occurrence. 	| Integer 	|
| `secondIndex` | Index of the other occurrence. 	| Integer 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`DuplicateFareRuleZoneIdFieldsNotice`](/RULES.md#DuplicateFareRuleZoneIdFieldsNotice)
##### Fields description

| Field name           	| Description                     	| Type    	|
|----------------------	|---------------------------------	|---------	|
| `csvRowNumber`       	| The row of the first occurrence. 	| Long  	  |
| `fareId`             	| The id of the first occurrence.  	| String  	|
| `previousCsvRowNumber`| The row of the other occurrence. 	| Long   	  |
| `previousFareId`     	| The id of the other occurrence.  	| Integer 	|

##### Affected files
* [fare_rules.txt](http://gtfs.org/reference/static/#fare_rulestxt)

#### [`DuplicateKeyNotice`](/RULES.md#DuplicateKeyNotice)
##### Fields description

| Field name      	| Description                        	| Type   	|
|-----------------	|------------------------------------	|--------	|
| `filename`       	| The name of the faulty file        	| String 	|
| `oldCsvRowNumber`	| The row of the first occurrence.    | Long   	|
| `newCsvRowNumber`	| The row of the other occurrence.   	| Long   	|
| `fieldName1`     	| Composite key's first field name.  	| String 	|
| `fieldValue1`    	| Composite key's first value.       	| Object 	|
| `fieldName2`     	| Composite key's second field name. 	| String 	|
| `fieldValue2`    	| Composite key's second value.      	| Object 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [EmptyColumnNameNotice](/RULES.md#EmptyColumnNameNotice)
##### Fields description

| Field name 	| Description                    	  | Type    	|
|------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.   	| String   	|
| `index`      	| The index of the empty column. 	| Integer 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`EmptyFileNotice`](/RULES.md#EmptyFileNotice)
##### Fields description

| Field name 	| Description                 	| Type   	|
|------------	|-----------------------------	|--------	|
| `filename`  | The name of the faulty file   | String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`EqualShapeDistanceDiffCoordinatesNotice`](/RULES.md#EqualShapeDistanceDiffCoordinatesNotice)
##### Fields description

| Field name            	  | Description                                                                                    	  | Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------	|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### [`ForeignKeyViolationNotice`](/RULES.md#ForeignKeyViolationNotice)
##### Fields description

| Field name      	| Description                                        	| Type   	|
|-----------------	|----------------------------------------------------	|--------	|
| `childFilename`  	| The name of the file from which reference is made. 	| String 	|
| `childFieldName` 	| The name of the field that makes reference.        	| String 	|
| `parentFilename` 	| The name of the file that is referred to.          	| String 	|
| `parentFieldName`	| The name of the field that is referred to.         	| String 	|
| `fieldValue`     	| The faulty record's value.                         	| String 	|
| `csvRowNumber`   	| The row of the faulty record.                      	| Long   	|

##### Affected files
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

#### [`InconsistentAgencyTimezoneNotice`](/RULES.md#InconsistentAgencyTimezoneNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `expected`   	| Expected timezone.            	| String 	|
| `actual`     	| Faulty record's timezone.     	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

#### [`InvalidColorNotice`](/RULES.md#InvalidColorNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [`InvalidCurrencyNotice`](/RULES.md#InvalidCurrencyNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)

#### [`InvalidDateNotice`](/RULES.md#InvalidDateNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### [`InvalidEmailNotice`](/RULES.md#InvalidEmailNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [`InvalidFloatNotice`](/RULES.md#InvalidFloatNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`InvalidIntegerNotice`](/RULES.md#InvalidIntegerNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`fare_attributes.txt`](http://gtfs.org/reference/static#fare_attributestxt)
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`transfers.txt`](http://gtfs.org/reference/static#transferstxt)

#### [`InvalidLanguageCodeNotice`](/RULES.md#InvalidLanguageCodeNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [`InvalidPhoneNumberNotice`](/RULES.md#InvalidPhoneNumberNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [`InvalidRowLengthNotice`](/RULES.md#InvalidRowLengthNotice)
##### Fields description

| Field name   	| Description                              	| Type   	|
|--------------	|------------------------------------------	|--------	|
| `filename`   	| The row of the faulty record.            	| String 	|
| `csvRowNumber`| The row of the faulty record.            	| Long   	|
| `rowLength`  	| The length of the faulty record.         	| Integer |
| `headerCount`	| The number of column in the faulty file. 	| Intege 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`InvalidTimeNotice`](/RULES.md#InvalidTimeNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`InvalidTimezoneNotice`](/RULES.md#InvalidTimezoneNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#frequenciestxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`InvalidUrlNotice`](/RULES.md#InvalidUrlNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [LocationWithUnexpectedStopTimeNotice](/RULES.md#LocationWithUnexpectedStopTimeNotice)
##### Fields description

| Field name             	| Description                                                	| Type   	|
|------------------------	|------------------------------------------------------------	|--------	|
| `csvRowNumber`         	| The row number of the faulty record from `stops.txt`.      	| Long   	|
| `stopId`               	| The id of the faulty record from `stops.txt`.              	| String 	|
| `stopName`             	| The `stops.stop_name` of the faulty record.                	| String 	|
| `stopTimeCsvRowNumber` 	| The row number of the faulty record from `stop_times.txt`. 	| Long   	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`LocationWithoutParentStationNotice`](/RULES.md#LocationWithoutParentStationNotice)
##### Fields description

| Field name   	| Description                                     	| Type    	|
|--------------	|-------------------------------------------------	|---------	|
| `csvRowNumber`| The row of the faulty record.                   	| Long    	|
| `stopId`     	| The id of the faulty record.                    	| String  	|
| `stopName`   	| The `stops.stop_name` of the faulty record.     	| String  	|
| `locationType`| The `stops.location_type` of the faulty record. 	| Integer 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [`MissingCalendarAndCalendarDateFilesNotice`](/RULES.md#MissingCalendarAndCalendarDateFilesNotice)
##### Fields description
| Field name 	| Description 	| Type 	|
|------------	|-------------	|------	|
| -          	| -           	| -    	|

##### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)

#### [MissingLevelIdNotice](/RULES.md#MissingLevelIdNotice)
##### Fields description

| Field name    	| Description                                                      	 | Type   	|
|---------------	|------------------------------------------------------------------- |--------	|
| `csvRowNumber`  | The row number of the faulty record. 	                             | Long   	|
| `stopId`   	  | The id of the faulty from `stops.txt`.                               | String   |

##### Affected files
* [`levels.txt`](http://gtfs.org/reference/static#levelstxt)

#### [`MissingRequiredColumnNotice`](/RULES.md#MissingRequiredColumnNotice)
##### Fields description

| Field name 	  | Description                     | Type   	|
|-------------	|-------------------------------- |--------	|
| `filename`   	| The name of the faulty file.    | String 	|
| `fieldName`  	| The name of the missing column. | String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`MissingRequiredFieldNotice`](/RULES.md#MissingRequiredFieldNotice)
##### Fields description

| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|
| `csvRowNumber`| The row of the faulty record.  	| Long   	|
| `fieldName`  	| The name of the missing field. 	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`MissingRequiredFileNotice`](/RULES.md#MissingRequiredFileNotice)
##### Fields description

| Field name   	| Description                    	| Type   	|
|--------------	|--------------------------------	|--------	|
| `filename`   	| The name of the faulty file.   	| String 	|
| `csvRowNumber`| The row of the faulty record.  	| Long   	|
| `fieldName`  	| The name of the missing field. 	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`MissingTripEdgeNotice`](/RULES.md#MissingTripEdgeNotice)
##### Fields description

| Field name     	  | Description                                 | Type    	|
|-----------------  |-------------------------------------------- |---------	|
| `csvRowNumber`  	| The row of the faulty record.               | Long    	|
| `stopSequence`  	| `stops.stop_sequence` of the faulty record. | Integer 	|
| `tripId`        	| The `trips.trip_id` of the faulty record.   | String  	|
| `specifiedField`  | Name of the missing field.                 	| String  	|

* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [`NewLineInValueNotice`](/RULES.md#NewLineInValueNotice)
##### Fields description

| Field name   	| Description                   	| Type    	|
|--------------	|-------------------------------	|---------	|
| `filename`   	| The name of the faulty file.  	| String  	|
| `csvRowNumber`| The row of the faulty record. 	| Integer 	|
| `fieldName`  	| The name of the faulty field. 	| String  	|
| `fieldValue` 	| Faulty value.                 	| String  	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`NumberOutOfRangeNotice`](/RULES.md#NumberOutOfRangeNotice)
##### Fields description

| Field name   	| Description                   	| Type    	|
|--------------	|-------------------------------	|---------	|
| `filename`   	| The name of the faulty file.  	| String  	|
| `csvRowNumber`| The row of the faulty record. 	| Integer 	|
| `fieldName`  	| The name of the faulty field. 	| String  	|
| `fieldType`  	| The type of the faulty field. 	| String  	|
| `fieldValue` 	| Faulty value.                 	| Object  	|

##### Affected files
* [All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`OverlappingFrequencyNotice`](/RULES.md#OverlappingFrequencyNotice)
##### Fields description

| Field name       	| Description                                    	| Type   	|
|------------------	|------------------------------------------------	|--------	|
| `prevCsvRowNumber`| The row number of the first frequency.         	| Long   	|
| `prevEndTime`     | The first frequency end time.                  	| String 	|
| `currCsvRowNumber`| The overlapping frequency's row number.        	| Long   	|
| `currStartTime`   | The overlapping frequency's start time.        	| String 	|
| `tripId`          | The trip id associated to the first frequency. 	| String 	|

##### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)

#### [PathwayUnreachableLocationNotice](/RULES.md#PathwayUnreachableLocationNotice)
##### Fields description

| Field name   	 | Description                                         | Type    	|
|----------------|--------------------------------------------------|---------	|
| `csvRowNumber` | Row number of the unreachable location.             | Long    	|
| `stopId`     	 | The id of the unreachable location.                 | String  	|
| `stopName`   	 | The stop name of the unreachable location.     	   | String  	|
| `locationType` | The type of the unreachable location. 	           | Integer 	|
| `parentStation`| The parent of the unreachable location. 	           | String 	|
| `hasEntrance`  | Whether the location is reachable from entrances.   | String 	|
| `hasExit`      | Whether some exit can be reached from the location. | String 	|

##### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [PointNearOriginNotice](/RULES.md#PointNearOriginNotice)
##### Fields description

| Field name      	| Description                                      	| Type    	|
|-----------------	|--------------------------------------------------	|---------	|
| `filename`      	| The name of the affected GTFS file.              	| String  	|
| `csvRowNumber`  	| The row of the faulty row.                       	| Integer 	|
| `latFieldName`  	| The name of the field that uses latitude value.  	| String  	|
| `latFieldValue` 	| The latitude of the faulty row.                  	| Double  	|
| `lonFieldName`  	| The name of the field that uses longitude value. 	| String  	|
| `lonFieldValue` 	| The longitude of the faulty row                  	| Double  	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### [`RouteBothShortAndLongNameMissingNotice`](/RULES.md#RouteBothShortAndLongNameMissingNotice)
##### Fields description

| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
| `routeId`      	| The id of the faulty record.         	| String 	|
| `csvRowNumber` 	| The row number of the faulty record. 	| Long   	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [`StartAndEndRangeEqualNotice`](/RULES.md#StartAndEndRangeEqualNotice)
##### Fields description

| Field name     	  | Description                          	| Type   	|
|-----------------	|-------------------------------------- |--------	|
| `filename`       	| The name of the faulty file.         	| String 	|
| `csvRowNumber`   	| The row number of the faulty record. 	| Long   	|
| `startFieldName` 	| The start value's field name.        	| String 	|
| `endFieldName`   	| The end value's field name.          	| String 	|
| `value`          	| The faulty value.                    	| String 	|

##### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)

#### [`StartAndEndRangeOutOfOrderNotice`](/RULES.md#StartAndEndRangeOutOfOrderNotice)
##### Fields description

| Field name     	  | Description                          	| Type   	|
|-----------------	|-------------------------------------  |--------	|
| `filename`       	| The name of the faulty file.         	| String 	|
| `csvRowNumber`   	| The row number of the faulty record. 	| Long   	|
| `entityId`       	| The faulty service id.               	| String 	|
| `startFieldName` 	| The start value's field name.        	| String 	|
| `startValue`     	| The start value.                     	| String 	|
| `endFieldName`   	| The end value's field name.          	| String 	|
| `endValue`       	| The end value.                       	| String 	|

##### Affected files
* [`calendar.txt`](http://gtfs.org/reference/static#calendartxt)
* [`calendar_dates.txt`](http://gtfs.org/reference/static#calendar_datestxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`StationWithParentStationNotice`](/RULES.md#StationWithParentStationNotice)
##### Fields description

| Field name    	| Description                               	  | Type   	|
|---------------	|---------------------------------------------	|--------	|
| `stopId`        	| The id of the faulty record.              	| String 	|
| `stopName`      	| The stops.stop_name of the faulty record. 	| String  |
| `csvRowNumber`  	| The row number of the faulty record.      	| Long  	|
| `parentStation` 	| Parent station's id.                        | String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [StopTimeTimepointWithoutTimesNotice](/RULES.md#StopTimeTimepointWithoutTimesNotice)
##### Fields description

| Field name     	  | Description                                	| Type   	|
|-----------------	|--------------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.       	| Long   	|
| `tripId`         	| The faulty record's id.                    	| String 	|
| `stopSequence`   	| The faulty record's `stops.stop_sequence`. 	| String 	|
| `specifiedField` 	| Either `departure_time` or `arrival_time`. 	| String 	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [StopWithoutZoneIdNotice](/RULES.md#StopWithoutZoneIdNotice)
##### Fields description

| Field name               	| Description                                	| Type   	|
|--------------------------	|--------------------------------------------	|--------	|
| `stopId`                 	| The faulty record's id.                    	| String 	|
| `stopName`                | The faulty record's `stops.stop_name`.       	| String 	|
| `csvRowNumber`        	| The row number of the faulty record.       	| Long   	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`fare_rules.txt`](http://gtfs.org/reference/static#farerulestxt)

#### [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](/RULES.md#StopTimeWithArrivalBeforePreviousDepartureTimeNotice)
##### Fields description

| Field name       	| Description                                  	| Type   	|
|------------------	|----------------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.         	| Long   	|
| `prevCsvRowNumber`| The row of the previous stop time.           	| Long   	|
| `tripId`         	| The trip_id associated to the faulty record. 	| String 	|
| `departureTime`  	| Departure time at the previous stop time.    	| String 	|
| `arrivalTime`    	| Arrival time at the faulty record.           	| String 	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](/RULES.md#StopTimeWithOnlyArrivalOrDepartureTimeNotice)
##### Fields description

| Field name      	| Description                                  	| Type    	|
|-----------------	|-----------------------------------------------|---------	|
| `csvRowNumber`   	| The row number of the faulty record.         	| Long    	|
| `tripId`         	| The trip_id associated to the faulty record. 	| String  	|
| `stopSequence`   	| The sequence of the faulty stop.             	| Integer 	|
| `specifiedField` 	| Either `arrival_time` or `departure_time`    	| String  	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [`TranslationForeignKeyViolationNotice`](/RULES.md#TranslationForeignKeyViolationNotice)
##### Fields description

| Field name       | Description                            | Type    	|
|------------------|----------------------------------------|-------	|
| `csvRowNumber`   | The row number of the faulty record.   | Long    	|
| `tableName`      | `table_name` of the faulty record.     | String  	|
| `recordId`       | `record_id` of the faulty record.      | String  	|
| `recordSubId`    | `record_sub_id` of the faulty record.  | String  	|

##### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [`TranslationUnexpectedValueNotice`](/RULES.md#TranslationUnexpectedValueNotice)
##### Fields description

| Field name        | Description                                  	            | Type    	|
|-------------------|-----------------------------------------------------------|---------	|
| `csvRowNumber`    | The row number of the faulty record.         	            | Long    	|
| `fieldName`       | The name of the field that was expected to be empty.      | String  	|
| `fieldValue`      | Actual value of the field that was expected to be empty.  | String 	|

##### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [`WrongParentLocationTypeNotice`](/RULES.md#WrongParentLocationTypeNotice)
##### Fields description

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

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

### Warnings

**Notice code table**

| Notice code                                	| Notice                                                                            	|
|--------------------------------------------	|-----------------------------------------------------------------------------------	|
| `attribution_without_role`           	      | [`AttributionWithoutRoleNotice`](#AttributionWithoutRoleNotice)                   	|
| `duplicate_route_name`                      | [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	|
| `empty_row`                                	| [`EmptyRowNotice`](#EmptyRowNotice)                                               	|
| `equal_shape_distance_same_coordinates`       | [`EqualShapeDistanceSameCoordinatesNotice`](#EqualShapeDistanceSameCoordinatesNotice) |
| `fast_travel_between_consecutive_stops`      	| [`FastTravelBetweenConsecutiveStopsNotice`](#FastTravelBetweenConsecutiveStopsNotice) |
| `fast_travel_between_far_stops`               | [`FastTravelBetweenFarStopsNotice`](#FastTravelBetweenFarStopsNotice)                 |
| `feed_expiration_date`                     	| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	|
| `feed_info_lang_and_agency_mismatch` 	      | [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	|
| `inconsistent_agency_lang`                 	| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	|
| `leading_or_trailing_whitespaces`           | [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)         |
| `missing_feed_info_date`                   	| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	|
| `missing_timepoint_column`                   	| [`MissingTimepointColumnNotice`](#MissingTimepointColumnNotice)                       |
| `missing_timepoint_value`                   	| [`MissingTimepointValueNotice`](#MissingTimepointValueNotice)                         |
| `more_than_one_entity`                     	| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	|
| `non_ascii_or_non_printable_char`          	| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	|
| `pathway_dangling_generic_node`               | [`PathwayDanglingGenericNodeNotice`](#PathwayDanglingGenericNodeNotice)	            |
| `pathway_loop`                                | [`PathwayLoopNotice`](#PathwayLoopNotice)	                                            |
| `platform_without_parent_station`          	| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	|
| `route_color_contrast`                     	| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	|
| `route_short_and_long_name_equal`          	| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	|
| `route_short_name_too_long`                	| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	|
| `same_name_and_description_for_route`       | [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)     |
| `same_name_and_description_for_stop`       	| [`SameNameAndDescriptionForStopNotice`](#SameNameAndDescriptionForStopNotice)     	|
| `same_route_and_agency_url`                	| [`SameRouteAndAgencyUrlNotice`](#SameRouteAndAgencyUrlNotice)                       |
| `same_stop_and_agency_url`                 	| [`SameStopAndAgencyUrlNotice`](#SameStopAndAgencyUrlNotice)                         |
| `same_stop_and_route_url`                  	| [`SameStopAndRouteUrlNotice`](#SameStopAndRouteUrlNotice)                           |
| `stop_has_too_many_matches_for_shape`        	| [`StopHasTooManyMatchesForShapeNotice`](#StopHasTooManyMatchesForShapeNotice)     	|
| `stops_match_shape_out_of_order`             	| [`StopsMatchShapeOutOfOrderNotice`](#StopsMatchShapeOutOfOrderNotice)              	|
| `stop_too_far_from_trip_shape`            	| [`StopTooFarFromTripShapeNotice`](#StopTooFarFromTripShapeNotice)                 	|
| `stop_too_far_from_shape_using_user_distance`	| [`StopTooFarFromShapeUsingUserDistanceNotice`](#StopTooFarFromShapeUsingUserDistanceNotice)                 	|
| `stop_too_far_from_shape`                 	| [`StopTooFarFromShapeNotice`](#StopTooFarFromShapeNotice)                 	|
| `too_fast_travel`                          	| [`TooFastTravelNotice`](#TooFastTravelNotice)                                     	|
| `translation_unknown_table_name`              | [`TranslationUnknownTableNameNotice`](#TranslationUnknownTableNameNotice)	            |
| `unexpected_enum_value`                    	| [`UnexpectedEnumValueNotice`](#UnexpectedEnumValueNotice)                         	|
| `unusable_trip`                            	| [`UnusableTripNotice`](#UnusableTripNotice)                                       	|
| `unused_shape`                             	| [`UnusedShapeNotice`](#UnusedShapeNotice)                                         	|
| `unused_trip`                              	| [`UnusedTripNotice`](#UnusedTripNotice)                                           	|
| `wrong_stop_time_stop_location_type`        | [`WrongStopTimeStopLocationTypeNotice`](#WrongStopTimeStopLocationTypeNotice)	      |

#### [AttributionWithoutRoleNotice](/RULES.md#AttributionWithoutRoleNotice)
##### Fields description

| Field name    	| Description                          	  | Type   	|
|---------------	|---------------------------------------	|--------	|
| `csvRowNumber`  	| The row number of the faulty record. 	| Long   	|
| `attributionId` 	| The id of the faulty record.         	| String 	|

##### Affected files
* [`attributions.txt`](http://gtfs.org/reference/static#attributionstxt)

#### [DuplicateRouteNameNotice](/RULES.md#DuplicateRouteNameNotice)
##### Fields description

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

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [EmptyRowNotice](/RULES.md#EmptyRowNotice)
##### Fields description

| Field name   	| Description                          	| Type    	|
|--------------	|--------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.         	| String  	|
| `csvRowNumber`| The row number of the faulty record. 	| Long 	    |

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)


#### [EqualShapeDistanceSameCoordinatesNotice](/RULES.md#EqualShapeDistanceSameCoordinatesNotice)
##### Fields description

| Field name            	  | Description                                                                                    	  | Type    	|
|-----------------------	  |-------------------------------------------------------------------------------------------------	|---------	|
| `shapeId`               	| The id of the faulty shape.                                                                      	| String  	|
| `csvRowNumber`          	| The row number from `shapes.txt`.                                                                	| Long    	|
| `shapeDistTraveled`     	| Actual distance traveled along the shape from the first shape point to the faulty record.        	| Double  	|
| `shapePtSequence`       	| The faulty record's `shapes.shape_pt_sequence`.                                                  	| Integer 	|
| `prevCsvRowNumber`      	| The row number from `shapes.txt` of the previous shape point.                                    	| Long    	|
| `prevShapeDistTraveled` 	| Actual distance traveled along the shape from the first shape point to the previous shape point. 	| Double  	|
| `prevShapePtSequence`   	| The previous record's `shapes.shape_pt_sequence`.                                                	| Integer 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)

#### [FeedExpirationDateNotice](/RULES.md#FeedExpirationDateNotice)
##### Fields description

| Field name              	| Description                                  	| Type   	|
|-------------------------	|----------------------------------------------	|--------	|
| `csvRowNumber`           	| The row number of the faulty record.         	| Long   	|
| `currentDate`            	| Current date (YYYYMMDD format).              	| String 	|
| `feedEndDate`            	| Feed end date (YYYYMMDD format).             	| String 	|
| `suggestedExpirationDate`	| Suggested expiration date (YYYYMMDD format). 	| String 	|

##### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### [FeedInfoLangAndAgencyLangMismatchNotice](/RULES.md#FeedInfoLangAndAgencyLangMismatchNotice)
##### Fields description

| Field name   	| Description                               	| Type   	|
|--------------	|-------------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record.      	| Long   	|
| `agencyId`   	| The agency id of the faulty record.       	| String 	|
| `agencyName` 	| The agency name of the faulty record.     	| String 	|
| `agencyLang` 	| The agency language of the faulty record. 	| String 	|
| `feedLang`   	| The feed language of the faulty record.   	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### [InconsistentAgencyLangNotice](/RULES.md#InconsistentAgencyLangNotice)
##### Fields description

| Field name     	| Description                   	| Type   	|
|----------------	|-------------------------------	|--------	|
| `csvRowNumber` 	| The row of the faulty record. 	| Long   	|
| `expected`     	| Expected language.            	| String 	|
| `actual`       	| Faulty record's language.     	| String 	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)

#### [LeadingOrTrailingWhitespacesNotice](/RULES.md#LeadingOrTrailingWhitespacesNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [MissingFeedInfoDateNotice](/RULES.md#MissingFeedInfoDateNotice)
##### Fields description

| Field name     	| Description                                 	| Type   	|
|----------------	|---------------------------------------------	|--------	|
| `fieldName`    	| Either `feed_end_date` or `feed_start_date` 	| String 	|
| `csvRowNumber` 	| The row number of the faulty record.        	| Long   	|

##### Affected files
* [`feed_info.txt`](http://gtfs.org/reference/static#feed_infotxt)

#### [MissingTimepointColumnNotice](/RULES.md#MissingTimepointColumnNotice)
##### Fields description

| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `filename`    	| The name of the affected file.                  	| String   	|

##### Affected files
* [`stop_times.txt`](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)

#### [MissingTimepointValueNotice](/RULES.md#MissingTimepointValueNotice)
##### Fields description

| Field name     	| Description                                     	| Type   	|
|----------------	|-------------------------------------------------	|--------	|
| `csvRowNumber` 	| The row number of the faulty record.            	| Long   	|
| `tripId`       	| The faulty record's `stop_times.trip_id`.         | String 	|
| `stopSequence` 	| The faulty record's `stop_times.stop_sequence`. 	| String 	|

##### Affected files
* [`stop_times.txt`](https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#stop_timestxt)

#### [MoreThanOneEntityNotice](/RULES.md#MoreThanOneEntityNotice)
##### Fields description

| Field name    	| Description              	| Type   	|
|---------------	|--------------------------	|--------	|
| `filename`    	| Name of the faulty file. 	| String 	|
| `entityCount` 	| Number of occurrences.   	| Long   	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [NonAsciiOrNonPrintableCharNotice](/RULES.md#NonAsciiOrNonPrintableCharNotice)
##### Fields description

| Field name   	| Description                                  	| Type   	|
|--------------	|----------------------------------------------	|--------	|
| `filename`   	| Name of the faulty file.                     	| String 	|
| `csvRowNumber`| Row number of the faulty record.             	| Long   	|
| `columnName` 	| Name of the column where the error occurred. 	| String 	|
| `fieldValue` 	| Faulty value.                                	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [PathwayDanglingGenericNodeNotice](/RULES.md#PathwayDanglingGenericNodeNotice)
##### Fields description

| Field name     | Description                                         | Type    	|
|----------------|-----------------------------------------------------|---------	|
| `csvRowNumber` | Row number of the dangling generic node.            | Long    	|
| `stopId`       | The id of the dangling generic node.                | String  	|
| `stopName`     | The stop name of the dangling generic node.         | String  	|
| `parentStation`| The parent station of the dangling generic node.    | String 	|

##### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [PathwayLoopNotice](/RULES.md#PathwayLoopNotice)
##### Fields description

| Field name     	| Description                                                                                 	| Type   	|
|----------------	|---------------------------------------------------------------------------------------------	|--------	|
| `csvRowNumber` 	| Row number of the faulty row from `pathways.txt`.                                           	| Long   	|
| `pathwayId`    	| The id of the faulty record.                                                                	| String 	|
| `stopId`       	| The `pathway.stop_id` that is repeated in `pathways.from_stop_id` and `pathways.to_stop_id`. 	| String 	|

##### Affected files
* [`pathways.txt`](http://gtfs.org/reference/static#pathwaystxt)

#### [PlatformWithoutParentStationNotice](/RULES.md#PlatformWithoutParentStationNotice)
##### Fields description

| Field name   	| Description                             	| Type    	|
|--------------	|-----------------------------------------	|---------	|
| `csvRowNumber`| Row number of the faulty record.        	| Long    	|
| `stopId`     	| The id of the faulty record.             	| String  	|
| `stopName`   	| The stop name of the faulty record.     	| String  	|
| `locationType`| The location type of the faulty record. 	| Integer 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [RouteColorContrastNotice](/RULES.md#RouteColorContrastNotice)
##### Fields description

| Field name     	| Description                                	  | Type   	|
|----------------	|---------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.               	| String 	|
| `csvRowNumber`   	| The row number of the faulty record.       	| Long 	  |
| `routeColor`     	| The faulty record's HTML route color.      	| String 	|
| `routeTextColor` 	| The faulty record's HTML route text color. 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [RouteShortAndLongNameEqualNotice](/RULES.md#RouteShortAndLongNameEqualNotice)
##### Fields description

| Field name     	| Description                             	  | Type   	|
|----------------	|-------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| String  |
| `csvRowNumber`   	| The row number of the faulty record.    	| Long 	  |
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|
| `routeLongName`  	| The faulty record's `route_long_name`.  	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [RouteShortNameTooLongNotice](/RULES.md#RouteShortNameTooLongNotice)
##### Fields description

| Field name     	| Description                             	  | Type   	|
|----------------	|-------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| String  |
| `csvRowNumber`   	| The row number of the faulty record.    	| Long 	  |
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [`SameNameAndDescriptionForRouteNotice`](/RULES.md#SameNameAndDescriptionForRouteNotice)
##### Fields description

| Field name     	| Description                                    	| Type   	|
|----------------	|------------------------------------------------	|--------	|
| `filename`      | The name of the faulty file.                   	| String 	|
| `routeId`       | The id of the faulty record.                   	| String 	|
| `csvRowNumber`  | The row number of the faulty record.           	| Long   	|
| `routeDesc`     | The `routes.routes_desc` of the faulty record. 	| String 	|
| `specifiedField`| Either `route_short_name` or `route_long_name`. | String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [SameNameAndDescriptionForStopNotice](/RULES.md#SameNameAndDescriptionForStopNotice)
##### Fields description

| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `csvRowNumber`  | The row number of the faulty record.      | Long 	  |
| `stopId`        | The id of the faulty record.              | String  |
| `stopDesc`    	| The faulty record's `stop_desc`.         	| String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [SameRouteAndAgencyUrlNotice](/RULES.md#SameRouteAndAgencyUrlNotice)
##### Fields description

| Field name     	| Description                                	| Type   	|
|----------------	|--------------------------------------------	|--------	|
| `routeCsvRowNumber`    | The row number of the faulty record from `routes.txt`.       	| Long   	|
| `routeId`         | The faulty record's id.                    	| String 	|
| `agencyId`    	| The faulty record's `routes.agency_id`.    	| String 	|
| `routeUrl`     	| The duplicate URL value                    	| String 	|
| `agencyCsvRowNumber`    | The row number of the faulty record from `agency.txt`.       	| Long   	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [SameStopAndAgencyUrlNotice](/RULES.md#SameStopAndAgencyUrlNotice)
##### Fields description

| Field name     	| Description                                            	| Type   	|
|----------------	|--------------------------------------------------------	|--------	|
| `stopCsvRowNumber`| The row number of the faulty record from `stops.txt`.     | Long   	|
| `stopId`       	| The faulty record's id.                                	| String 	|
| `agencyName`   	| The faulty record's `agency.agency_name`.              	| String 	|
| `stopUrl`      	| The duplicate URL value.                                  | String 	|
| `agencyCsvRowNumber` 	| The row number of the faulty record from `agency.txt`.|  Long   	|

##### Affected files
* [`agency.txt`](http://gtfs.org/reference/static#agencytxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [SameStopAndRouteUrlNotice](/RULES.md#SameStopAndRouteUrlNotice)
##### Fields description

| Field name          	| Description                                            	| Type   	|
|---------------------	|--------------------------------------------------------	|--------	|
| `stopsvRowNumber`     | The row number of the faulty record from `stops.txt`.    	| Long   	|
| `stopId`            	| The faulty record's id.                                	| String 	|
| `stopUrl`           	| The duplicate URL value.                                | String 	|
| `routeId`           	| The faulty record's id from `routes.txt.               	| String 	|
| `routeCsvRowNumber` 	| The row number of the faulty record from `routes.txt`. 	| Long   	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [StopHasTooManyMatchesForShapeNotice](/RULES.md#StopHasTooManyMatchesForShapeNotice)
##### Fields description

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

##### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stops_times.txt`](http://gtfs.org/reference/static#stopstimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [StopsMatchShapeOutOfOrderNotice](/RULES.md#StopsMatchShapeOutOfOrderNotice)
##### Fields description

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

##### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stops_times.txt`](http://gtfs.org/reference/static#stopstimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [StopTooFarFromShapeUsingUserDistanceNotice](/RULES.md#StopTooFarFromShapeUsingUserDistanceNotice)
##### Fields description

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

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stoptimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [StopTooFarFromShapeNotice](/RULES.md#StopTooFarFromShapeNotice)
##### Fields description

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

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stoptimestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [FastTravelBetweenConsecutiveStopsNotice](/RULES.md#FastTravelBetweenConsecutiveStopsNotice)
##### Fields description

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

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [FastTravelBetweenFarStopsNotice](/RULES.md#FastTravelBetweenFarStopsNotice)
##### Fields description

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

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [`TranslationUnknownTableNameNotice`](/RULES.md#TranslationUnknownTableNameNotice)
##### Fields description

| Field name       | Description                            | Type    	|
|------------------|----------------------------------------|-------	|
| `csvRowNumber`   | The row number of the faulty record.   | Long    	|
| `tableName`      | `table_name` of the faulty record.     | String  	|

##### Affected files
* [`translations.txt`](http://gtfs.org/reference/static#translationstxt)

#### [UnexpectedEnumValueNotice](/RULES.md#UnexpectedEnumValueNotice)
##### Fields description

| Field name   	| Description                                     	| Type    	|
|--------------	|-------------------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.                    	| String  	|
| `csvRowNumber`| The row number of the faulty record.            	| Long    	|
| `fieldName`  	| The name of the field where the error occurred. 	| String  	|
| `fieldValue` 	| Faulty value.                                   	| Integer 	|

##### Affected files
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

#### [UnusableTripNotice](/RULES.md#UnusableTripNotice)
##### Fields description

| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [UnusedShapeNotice](/RULES.md#UnusedShapeNotice)
##### Fields description

| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `shapeId     	| The faulty record's id.              	| String 	|

##### Affected files
* [`shapes.txt`](http://gtfs.org/reference/static#shapestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [UnusedTripNotice](/RULES.md#UnusedTripNotice)
##### Fields description

| Field name   	| Description                          	| Type   	|
|--------------	|--------------------------------------	|--------	|
| `csvRowNumber`| The row number of the faulty record. 	| Long   	|
| `tripId`     	| The faulty record's id.              	| String 	|

##### Affected files
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [WrongStopTimeStopLocationTypeNotice](/RULES.md#WrongStopTimeStopLocationTypeNotice)
##### Fields description

| Field name     	| Description                                            	| Type   	|
|----------------	|--------------------------------------------------------	|--------	|
| `csvRowNumber` 	| The row number of the faulty record.                   	| Long   	|
| `tripId`       	| `stop_times.trip_id` value for the faulty record.      	| String 	|
| `stopSequence` 	| The faulty record's `stops.stop_sequence`.             	| String 	|
| `stopId`       	| `stop_times.stop_id` value for the faulty record.      	| String 	|
| `locationType` 	| The `stops.location_type` the faulty record refers to. 	| String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

### Info

**Notice code table**

| Notice code             	| Notice                                        	|
|-------------------------	|-----------------------------------------------	|
| `unknown_column_notice` 	| [`UnknownColumnNotice`](#UnknownColumnNotice) 	|
| `unknown_file_notice`   	| [`UnknownFileNotice`](#UnknownFileNotice)     	|

#### [UnknownColumnNotice](/RULES.md#UnknownColumnNotice)
##### Fields description

| Field name 	  | Description                     	| Type    	|
|-------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.    	| String 	|
| `fieldName`  	| The name of the unknown column. 	| String  	|
| `index`      	| The index of the faulty column. 	| Integer 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [UnknownFileNotice](/RULES.md#UnknownFileNotice)
##### Fields description

| Field name 	  | Description                     	| Type    |
|-------------	|---------------------------------	|---------|
| `filename`   	| The name of the unknown file.    	| String 	|

## System Errors

**Notice code table**

| Notice code                            	| Notice                                                                  	|
|----------------------------------------	|-------------------------------------------------------------------------	|
| `i_o_error`                            	| [`IOError`](#IOError)                                                   	|
| `runtime_exception_in_loader_error`    	| [`RuntimeExceptionInLoaderError`](#RuntimeExceptionInLoaderError)       	|
| `runtime_exception_in_validator_error` 	| [`RuntimeExceptionInValidatorError`](#RuntimeExceptionInValidatorError) 	|
| `thread_excecution_error`              	| [`ThreadExecutionError`](#ThreadExecutionError)                         	|
| `u_r_i_syntax_error`                   	| [`URISyntaxError`](#URISyntaxError)                                     	|

### [IOError](/RULES.md#IOError)
#### Fields description

| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |

### [RuntimeExceptionInLoaderError](/RULES.md#RuntimeExceptionInLoaderError)
#### Fields description

| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `filename`  	| The name of the file that caused the exception.            	    | String 	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

### [RuntimeExceptionInValidatorError](/RULES.md#RuntimeExceptionInValidatorError)
#### Fields description

| Field name 	| Description                                                   	| Type    |
|------------	|---------------------------------------------------------------	|---------|
| `validator` | The name of the validator that caused the exception.            | String 	|
| `exception` | The name of the exception.                                    	| String 	|
| `message`   | The error message that explains the reason for the exception. 	| String  |

### [ThreadExecutionError](/RULES.md#ThreadExecutionError)
#### Fields description

| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |

### [URISyntaxError](/RULES.md#URISyntaxError)
#### Fields description

| Field name 	  | Description                                                   	| Type    |
|-------------	|---------------------------------------------------------------	|---------|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  |
