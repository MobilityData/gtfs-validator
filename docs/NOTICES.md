# Notices field description

## Validation notices
### Errors

**Notice code table**

| Notice code                                             	| Notice                                                                                                          	|
|--------------------------------------------------------	|-----------------------------------------------------------------------------------------------------------------	|
| `block_trips_with_overlapping_stopTimes`               	| [`BlockTripsWithOverlappingStopTimesNotice`](#BlockTripsWithOverlappingStopTimesNotice)                         	|
| `csv_parsing_failed`                                   	| [`CsvParsingFailedNotice`](#CsvParsingFailedNotice)                                                             	|
| `decreasing_or_equal_shape_distance`                   	| [`DecreasingOrEqualShapeDistanceNotice`](#DecreasingOrEqualShapeDistanceNotice)                                 	|
| `decreasing_or_equal_stopTime_distance`                	| [`DecreasingOrEqualStopTimeDistanceNotice`](#DecreasingOrEqualStopTimeDistanceNotice)                           	|
| `duplicated_column`                                    	| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)                                                             	|
| `duplicate_fare_rule_zone_id_fields`                   	| [`DuplicateFareRuleZoneIdFieldsNotice`](#DuplicateFareRuleZoneIdFieldsNotice)                                   	|
| `duplicate_key`                                        	| [`DuplicateKeyNotice`](#DuplicateKeyNotice)                                                                     	|
| `empty_file`                                           	| [`EmptyFileNotice`](#EmptyFileNotice)                                                                           	|
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
| `location_without_parent_station`                      	| [`LocationWithoutParentStationNotice`](#LocationWithoutParentStationNotice)                                     	|
| `missing_calendar_and_calendar_date_files`             	| [`MissingCalendarAndCalendarDateFilesNotice`](#MissingCalendarAndCalendarDateFilesNotice)                       	|
| `missing_required_column`                              	| [`MissingRequiredColumnNotice`](#MissingRequiredColumnNotice)                                                   	|
| `missing_required_field`                               	| [`MissingRequiredFieldNotice`](#MissingRequiredFieldNotice)                                                     	|
| `missing_required_file`                                	| [`MissingRequiredFileNotice`](#MissingRequiredFileNotice)                                                       	|
| `missing_trip_edge`                                    	| [`MissingTripEdgeNotice`](#MissingTripEdgeNotice)                                                               	|
| `new_line_in_value`                                    	| [`NewLineInValueNotice`](#NewLineInValueNotice)                                                                 	|
| `number_out_of_range`                                  	| [`NumberOutOfRangeNotice`](#NumberOutOfRangeNotice)                                                             	|
| `overlapping_frequency`                                	| [`OverlappingFrequencyNotice`](#OverlappingFrequencyNotice)                                                     	|
| `route_both_short_and_long_name_missing`               	| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice)                             	|
| `same_name_and_description_for_route`                  	| [`SameNameAndDescriptionForRouteNotice`](#SameNameAndDescriptionForRouteNotice)                                 	|
| `start_and_end_range_equal`                            	| [`StartAndEndRangeEqualNotice`](#StartAndEndRangeEqualNotice)                                                   	|
| `start_and_end_range_out_of_order`                     	| [`StartAndEndRangeOutOfOrderNotice`](#StartAndEndRangeOutOfOrderNotice)                                         	|
| `station_with_parent_station`                          	| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                                             	|
| `stop_time_wit_arrival_before_previous_departure_time` 	| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice) 	|
| `stop_time_with_only_arrival_or_departure_time`        	| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                 	|
| `wrong_parent_location_type`                           	| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                                               	|

#### [`BlockTripsWithOverlappingStopTimesNotice`](/RULES.md#BlockTripsWithOverlappingStopTimesNotice)
##### Fields description

| Field name      	| Description                               	            | Type   	|
|-----------------	|--------------------------------------------------------	|--------	|
| `csvRowNumberA` 	| The row number from `trips.txt` of the first faulty trip. | Long   	|
| `tripIdA`       	| The id of first faulty trip.                           	| String 	|
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

#### [`DecreasingOrEqualShapeDistanceNotice`](/RULES.md#DecreasingOrEqualShapeDistanceNotice)
##### Fields description

| Field name            	| Description                                                                                      	| Type    	|
|-----------------------	|--------------------------------------------------------------------------------------------------	|---------	|
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
| `csvRowNumber`       	| The row of the first occurrence. 	| Long  	|
| `fareId`             	| The id of the first occurrence.  	| String  	|
| `previousCsvRowNumber`| The row of the other occurrence. 	| Long   	|
| `previousFareId`     	| The id of the other occurrence.  	| Integer 	|

##### Affected files
* [fare_rules.txt](http://gtfs.org/reference/static/#fare_rulestxt)

#### [`DuplicateKeyNotice`](/RULES.md#DuplicateKeyNotice)
##### Fields description

| Field name      	| Description                        	| Type   	|
|-----------------	|------------------------------------	|--------	|
| `filename`       	| The name of the faulty file        	| String 	|
| `oldCsvRowNumber`	| The row of the first occurrence.    	| Long   	|
| `newCsvRowNumber`	| The row of the other occurrence.    	| Long   	|
| `fieldName1`     	| Composite key's first field name.  	| String 	|
| `fieldValue1`    	| Composite key's first value.       	| Object 	|
| `fieldName2`     	| Composite key's second field name. 	| String 	|
| `fieldValue2`    	| Composite key's second value.      	| Object 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [`EmptyFileNotice`](/RULES.md#EmptyFileNotice)
##### Fields description

| Field name 	| Description                 	| Type   	|
|------------	|-----------------------------	|--------	|
| `filename`   	| The name of the faulty file 	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

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
| `rowLength`  	| The length of the faulty record.         	| Integer 	|
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

#### [`MissingRequiredColumnNotice`](/RULES.md#MissingRequiredColumnNotice)
##### Fields description

| Field name 	| Description                     	| Type   	|
|------------	|---------------------------------	|--------	|
| `filename`   	| The name of the faulty file.    	| String 	|
| `fieldName`  	| The name of the missing column. 	| String 	|

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

| Field name     	| Description                                 	| Type    	|
|----------------	|---------------------------------------------	|---------	|
| `csvRowNumber`  	| The row of the faulty record.               	| Long    	|
| `stopSequence`  	| `stops.stop_sequence` of the faulty record. 	| Integer 	|
| `tripId`        	| The `trips.trip_id` of the faulty record.   	| String  	|
| `specifiedField` | Name of the missing field.                  	| String  	|

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

#### [`RouteBothShortAndLongNameMissingNotice`](/RULES.md#RouteBothShortAndLongNameMissingNotice)
##### Fields description

| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
| `routeId`      	| The id of the faulty record.         	| String 	|
| `csvRowNumber` 	| The row number of the faulty record. 	| Long   	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [`SameNameAndDescriptionForRouteNotice`](/RULES.md#SameNameAndDescriptionForRouteNotice)
##### Fields description

| Field name     	| Description                                    	| Type   	|
|----------------	|------------------------------------------------	|--------	|
| `filename`       	| The name of the faulty file.                   	| String 	|
| `routeId`        	| The id of the faulty record.                   	| String 	|
| `csvRowNumber`   	| The row number of the faulty record.           	| Long   	|
| `routeDesc`      	| The `routes.routes_desc` of the faulty record. 	| String 	|
| `specifiedField` 	| Either `route_short_name` or `route_long_name`. 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [`StartAndEndRangeEqualNotice`](/RULES.md#StartAndEndRangeEqualNotice)
##### Fields description

| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
| `filename`       	| The name of the faulty file.         	| String 	|
| `csvRowNumber`   	| The row number of the faulty record. 	| Long   	|
| `startFieldName` 	| The start value's field name.        	| String 	|
| `endFieldName`   	| The end value's field name.          	| String 	|
| `value`          	| The faulty value.                    	| String 	|

##### Affected files
* [`frequencies.txt`](http://gtfs.org/reference/static#frequenciestxt)

#### [`StartAndEndRangeOutOfOrderNotice`](/RULES.md#StartAndEndRangeOutOfOrderNotice)
##### Fields description

| Field name     	| Description                          	| Type   	|
|----------------	|--------------------------------------	|--------	|
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

| Field name    	| Description                               	| Type   	|
|---------------	|-------------------------------------------	|--------	|
| `stopId`        	| The id of the faulty record.              	| String 	|
| `stopName`      	| The stops.stop_name of the faulty record. 	| Long   	|
| `csvRowNumber`  	| The row number of the faulty record.      	| String 	|
| `parentStation` 	| Parent station's id.                         	| String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

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

| Field name     	| Description                                  	| Type    	|
|----------------	|----------------------------------------------	|---------	|
| `csvRowNumber`   	| The row number of the faulty record.         	| Long    	|
| `tripId`         	| The trip_id associated to the faulty record. 	| String  	|
| `stopSequence`   	| The sequence of the faulty stop.             	| Integer 	|
| `specifiedField` 	| Either `arrival_time` or `departure_time`    	| String  	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

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
| `attribution_without_role`           	| [`AttributionWithoutRoleNotice`](#AttributionWithoutRoleNotice)                   	|
| `duplicate_route_name`               	| [`DuplicateRouteNameNotice`](#DuplicateRouteNameNotice)                           	|
| `empty_column_name`                  	| [`EmptyColumnNameNotice`](#EmptyColumnNameNotice)                                 	|
| `empty_row`                          	| [`EmptyRowNotice`](#EmptyRowNotice)                                               	|
| `feed_expiration_date`               	| [`FeedExpirationDateNotice`](#FeedExpirationDateNotice)                           	|
| `feed_info_lang_and_agency_mismatche` 	| [`FeedInfoLangAndAgencyMismatchNotice`](#FeedInfoLangAndAgencyLangMismatchNotice) 	|
| `inconsistent_agency_lang`           	| [`InconsistentAgencyLangNotice`](#InconsistentAgencyLangNotice)                   	|
| `leading_or_trailing_whitespaces`             | [`LeadingOrTrailingWhitespacesNotice`](#LeadingOrTrailingWhitespacesNotice)           |
| `missing_feed_info_date`             	| [`MissingFeedInfoDateNotice`](#MissingFeedInfoDateNotice)                         	|
| `more_than_one_entity`               	| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice)                             	|
| `non_ascii_or_non_printable_char`    	| [`NonAsciiOrNonPrintableCharNotice`](#NonAsciiOrNonPrintableCharNotice)           	|
| `platform_without_parent_station`    	| [`PlatformWithoutParentStationNotice`](#PlatformWithoutParentStationNotice)       	|
| `route_color_contrast`               	| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                           	|
| `route_short_and_long_name_equal`    	| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice)           	|
| `route_short_name_too_long`          	| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice)                     	|
| `same_name_and_description_for_stop`        	| [`SameNameAndDescriptionForStopNotice`](#SameNameAndDescriptionForStopNotice)       	|
| `stop_time_timepoint_without_times`  	| [`StopTimeTimepointWithoutTimesNotice`](#StopTimeTimepointWithoutTimesNotice)     	|
| `stop_too_far_from_trip_shape`       	| [`StopTooFarFromTripShapeNotice`](#StopTooFarFromTripShapeNotice)                 	|
| `stop_without_zone_id`                       	| [`StopWithoutZoneIdNotice`](#StopWithoutZoneIdNotice)                 	            |
| `too_fast_travel`                    	| [`TooFastTravelNotice`](#TooFastTravelNotice)                                     	|
| `unexpected_enum_value`              	| [`UnexpectedEnumValueNotice`](#UnexpectedEnumValueNotice)                         	|
| `unusable_trip`                      	| [`UnusableTripNotice`](#UnusableTripNotice)                                       	|
| `unused_shape`                       	| [`UnusedShapeNotice`](#UnusedShapeNotice)                                         	|
| `unused_trip`                        	| [`UnusedTripNotice`](#UnusedTripNotice)                                           	|

#### [AttributionWithoutRoleNotice](/RULES.md#AttributionWithoutRoleNotice)
##### Fields description

| Field name    	| Description                          	| Type   	|
|---------------	|--------------------------------------	|--------	|
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

#### [EmptyColumnNameNotice](/RULES.md#EmptyColumnNameNotice)
##### Fields description

| Field name 	| Description                    	| Type    	|
|------------	|--------------------------------	|---------	|
| `filename`   	| The name of the faulty file.   	| String   	|
| `index`      	| The index of the empty column. 	| Integer 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [EmptyRowNotice](/RULES.md#EmptyRowNotice)
##### Fields description

| Field name   	| Description                          	| Type    	|
|--------------	|--------------------------------------	|---------	|
| `filename`   	| The name of the faulty file.         	| String  	|
| `csvRowNumber`| The row number of the faulty record. 	| Long 	    |

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

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

| Field name     	| Description                                	| Type   	|
|----------------	|--------------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.               	| Long   	|
| `csvRowNumber`   	| The row number of the faulty record.       	| String 	|
| `routeColor`     	| The faulty record's HTML route color.      	| String 	|
| `routeTextColor` 	| The faulty record's HTML route text color. 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [RouteShortAndLongNameEqualNotice](/RULES.md#RouteShortAndLongNameEqualNotice)
##### Fields description

| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| Long   	|
| `csvRowNumber`   	| The row number of the faulty record.    	| String 	|
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|
| `routeLongName`  	| The faulty record's `route_long_name`.  	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [RouteShortNameTooLongNotice](/RULES.md#RouteShortNameTooLongNotice)
##### Fields description

| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `routeId`        	| The id of the faulty record.            	| Long   	|
| `csvRowNumber`   	| The row number of the faulty record.    	| String 	|
| `routeShortName` 	| The faulty record's `route_short_name`. 	| String 	|

##### Affected files
* [`routes.txt`](http://gtfs.org/reference/static#routestxt)

#### [SameNameAndDescriptionForStopNotice](/RULES.md#SameNameAndDescriptionForStopNotice)
##### Fields description

| Field name     	| Description                             	| Type   	|
|----------------	|-----------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.    	| String 	|
| `stopId`        	| The id of the faulty record.            	| Long   	|
| `routeDesc`    	| The faulty record's `stop_desc`.       	| String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

#### [StopTimeTimepointWithoutTimesNotice](/RULES.md#StopTimeTimepointWithoutTimesNotice)
##### Fields description

| Field name     	| Description                                	| Type   	|
|----------------	|--------------------------------------------	|--------	|
| `csvRowNumber`   	| The row number of the faulty record.       	| Long   	|
| `tripId`         	| The faulty record's id.                    	| String 	|
| `stopSequence`   	| The faulty record's `stops.stop_sequence`. 	| String 	|
| `specifiedField` 	| Either `departure_time` or `arrival_time`. 	| String 	|

##### Affected files
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)

#### [StopTooFarFromTripShapeNotice](/RULES.md#StopTooFarFromTripShapeNotice)
##### Fields description

| Field name               	| Description                                	| Type   	|
|--------------------------	|--------------------------------------------	|--------	|
| `stopId`                 	| The faulty record's id.                    	| String 	|
| `stopSequence`           	| The faulty record's `stops.stop_sequence`. 	| String 	|
| `tripId`                 	| The faulty record's `tripId`.              	| String 	|
| `shapeId`                	| The faulty record's `shapeId`.             	| String 	|
| `stopShapeThresholdMeters`| Distance margin.                           	| Double 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

#### [StopWithoutZoneIdNotice](/RULES.md#StopWithoutZoneIdNotice)
##### Fields description

| Field name               	| Description                                	| Type   	|
|--------------------------	|--------------------------------------------	|--------	|
| `stopId`                 	| The faulty record's id.                    	| String 	|
| `csvRowNumber`        	| The row number of the faulty record.       	| Long   	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`fare_rules.txt`](http://gtfs.org/reference/static#farerulestxt)

#### [TooFastTravelNotice](/RULES.md#TooFastTravelNotice)
##### Fields description

| Field name        	| Description                   	| Type   	|
|-------------------	|-------------------------------	|--------	|
| `tripId`           	| The faulty record's `tripId`. 	| String 	|
| `speedkmh`         	| Travel speed (km/h).          	| String 	|
| `firstStopSequence`	| The first sequence in trip.   	| String 	|
| `lastStopSequence` 	| The stop sequence in trip.    	| String 	|

##### Affected files
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)
* [`stop_times.txt`](http://gtfs.org/reference/static#stop_timestxt)
* [`trips.txt`](http://gtfs.org/reference/static#tripstxt)

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

### Info

**Notice code table**

| Notice code             	| Notice                                        	|
|-------------------------	|-----------------------------------------------	|
| `unknown_column_notice` 	| [`UnknownColumnNotice`](#UnknownColumnNotice) 	|
| `unknown_file_notice`   	| [`UnknownFileNotice`](#UnknownFileNotice)     	|

#### [UnknownColumnNotice](/RULES.md#UnknownColumnNotice)
##### Fields description

| Field name 	| Description                     	| Type    	|
|------------	|---------------------------------	|---------	|
| `filename`   	| The name of the faulty file.    	| String 	|
| `fieldName`  	| The name of the unknown column. 	| String  	|
| `index`      	| The index of the faulty column. 	| Integer 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

#### [UnknownFileNotice](/RULES.md#UnknownFileNotice)
##### Fields description

| Field name 	| Description                     	| Type    	|
|------------	|---------------------------------	|---------	|
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

| Field name 	| Description                                                   	| Type    	|
|------------	|---------------------------------------------------------------	|---------	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  	|

### [RuntimeExceptionInLoaderError](/RULES.md#RuntimeExceptionInLoaderError)
#### Fields description

| Field name 	| Description                                                   	| Type    	|
|------------	|---------------------------------------------------------------	|---------	|
| `filename`  	| The name of the file that caused the exception.            	    | String 	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

### [RuntimeExceptionInValidatorError](/RULES.md#RuntimeExceptionInValidatorError)
#### Fields description

| Field name 	| Description                                                   	| Type    	|
|------------	|---------------------------------------------------------------	|---------	|
| `validator`  	| The name of the validator that caused the exception.            	| String 	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  	|

### [ThreadExecutionError](/RULES.md#ThreadExecutionError)
#### Fields description

| Field name 	| Description                                                   	| Type    	|
|------------	|---------------------------------------------------------------	|---------	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  	|

### [URISyntaxError](/RULES.md#URISyntaxError)
#### Fields description

| Field name 	| Description                                                   	| Type    	|
|------------	|---------------------------------------------------------------	|---------	|
| `exception`  	| The name of the exception.                                    	| String 	|
| `message`    	| The error message that explains the reason for the exception. 	| String  	|
