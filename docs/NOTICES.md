# Notices field description
## Validation notices
### Errors
#### [`BlockTripsWithOverlappingStopTimesNotice`](/RULES.md#BlockTripsWithOverlappingStopTimesNotice)
##### Fields description

| Field name      	| Description                              	| Type   	|
|-----------------	|------------------------------------------	|--------	|
| `csvRowNumberA` 	| row number from `trips.txt`              	| Long   	|
| `tripIdA`       	| id of first faulty trip                  	| String 	|
| `serviceIdA`    	| service id of the first faulty trip      	| String 	|
| `csvRowNumberB` 	| row number from `trips.txt`              	| Long   	|
| `tripIdB`       	| id of the other faulty trip              	| String 	|
| `serviceIdB`    	| service id of the other faulty trip      	| String 	|
| `blockId`       	| `trips.block_id` of the overlapping trip 	| String 	|
| `intersection`  	| overlapping period                       	| Date   	|

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

#### [`LeadingOrTrailingWhitespacesNotice`](/RULES.md#LeadingOrTrailingWhitespacesNotice)
##### Fields description

| Field name   	| Description                   	| Type   	|
|--------------	|-------------------------------	|--------	|
| `filename`   	| The row of the faulty record. 	| String 	|
| `csvRowNumber`| The row of the faulty record. 	| Long   	|
| `fieldName`  	| Faulty record's field name.   	| String 	|
| `fieldValue` 	| Faulty value.                 	| String 	|

##### Affected files
[All GTFS files supported by the specification.](http://gtfs.org/reference/static#dataset-files)

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
* [`stops.txt`](http://gtfs.org/reference/static#stopstxt)

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
