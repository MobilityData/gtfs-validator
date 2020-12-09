# Implemented rules

Rules are declared in the `Notice` modules: 
- [Notice related to CSV structure issues](https://github.com/MobilityData/gtfs-validator/tree/master/core/src/java/org/mobilitydata/gtfsvalidator/notice) 
- [Notices related to GTFS semantic rules issues](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/notice). 
 
Below are details of currently implemented rules.
** Please note that the error ID naming conventions changed in `v2` to make contributions of new rules easier by reducing the likelihood of conflicting IDs during parallel development. The table below indicates the ID used in gtfs-validator `v2` and higher in the first column and the legacy ID in the second column.** 

### Table of Notices [WIP]

| Error ID (v2.0+) | Error ID (v1.x) 	| Error Title                                                             	|
|--------------------------	|--------------------	|-------------------------------------------------------------------------	|
| [`MissingRequiredFieldError`](#MissingRequiredFieldError) | [E015](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E015)      	| Missing required `field`                                                	|
| [`DuplicatedColumnNotice`](#DuplicatedColumnNotice)  |     ❌   	            | Duplicated column                                                      	|
| [`MissingRequiredColumn`](#MissingRequiredColumn)   | [E001](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E001)          | Missing required column                                                  	|
| [`MoreThanOneEntityNotice`](#MoreThanOneEntityNotice) |     ❌   	            | More than one row in CSV                                                 	|
| [`MissingRequiredFileError`](#MissingRequiredFileError)| [E003](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E003)      	| Missing required `file`                                                 	|
| [`InvalidRowLengthError`](#InvalidRowLengthError)   | [E004](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E004)      	| Invalid csv row length                                                  	|
| [`FieldParsingError`](#FieldParsingError)       | [E005](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E005), [E006](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E006), [E017](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E017)    	| Cannot parse value                            |
| [`DuplicatedKeyError`](#DuplicatedKeyError)      | [E020](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E020)      	| Duplicated entity                                                       	|
| [`NumberOfOutRangeError`](#NumberOfOutRangeError)   | [E010](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E010), [E011](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E011)      	| Field value out of range                                      	|
| [`UnexpectedEnumValueError`](#UnexpectedEnumValueError) | [E021](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E021)      	| Unexpected `enum` value                                                 	|
| [`UnknownFileNotice`](#UnknownFileNotice)           | [W004](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W004)        	| Extra `file` found                                                              	|
| [`ForeignKeyError`](#ForeignKeyError)         | [E033](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E033), [E034](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E034), [E035](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E035), [E036](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E036)      	| Wrong foreign key             |
| [`EmptyFileNotice`](#EmptyFileNotice)         | [E047](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E047), [W012](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W012)      	| Csv file is empty                                                       	|
| [`RouteColorContrastNotice`](#RouteColorContrastNotice)                        | [E025](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E025)      	| Insufficient route color contrast                                       	|
| [`RouteShortNameTooLongNotice`](#RouteShortNameTooLongNotice) | [W005](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W005)        	| Route short name too long                                                       	|
| [`RouteBothShortAndLongNameMissingNotice`](#RouteBothShortAndLongNameMissingNotice) | [E027](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E027)      	| Missing route short name and long name                                  	|
| [`RouteShortAndLongNameEqualNotice`](#RouteShortAndLongNameEqualNotice) | [E028](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E028)| Route long name equals short name                                       	|
| [`InconsistentAgencyFieldNotice`](#InconsistentAgencyFieldNotice) | [E030](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E030), [E055](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E055)  	| Inconsistent field `agency_timezone`                                    	|
| [`StartAndEndDateOutOfOrderNotice`](#StartAndEndDateOutOfOrderNotice)| [E032](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E032)  | `calendar.txt` `end_date` is before `start_date`                        	|
| [`UnusedShapeNotice`](#UnusedShapeNotice)                         	| [E038](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E038)      	| All shapes should be used in `trips.txt`                                	|
| [`StopTimeWithOnlyArrivalOrDepartureTimeNotice`](#StopTimeWithOnlyArrivalOrDepartureTimeNotice)                         	| [E044](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E044)     	| Missing `stop_time.arrival_time` or `stop_time.departure_time`                    	|
| [`StopTimeWithArrivalBeforePreviousDepartureTimeNotice`](#StopTimeWithArrivalBeforePreviousDepartureTimeNotice)                         	| [E049](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E049)      	| Backwards time travel between stops in `stop_times.txt`               	|
| [`StopTimeWithDepartureBeforeArrivalTimeNotice`](#StopTimeWithDepartureBeforeArrivalTimeNotice)                        	| [E045](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E045)      	| `arrival_time` after `departure_time` in `stop_times.txt`                 	|
| [`DecreasingShapeDistanceNotice`](#DecreasingShapeDistanceNotice)| [E058](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E058)   	| Decreasing `shape_dist_traveled` in `stop_times.txt`                    	|
| [`WrongParentLocationTypeNotice`](#WrongParentLocationTypeNotice)                        	| [E041](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E041)      	| Invalid parent `location_type` for stop                                 	|
| [`StationWithParentStationNotice`](#StationWithParentStationNotice)                         	| [E042](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E042)      	| Station stop (`location_type`=2) has a parent stop                      	|
|                           | [E007](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E007)          | Cannot `download` archive | 
|                           | [E008](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E008)          | Cannot `unzip` archive | 
|                          	| [E016](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E016)      	| Invalid `time` value                                                    	|
|                          	| [E018](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E018)      	| Invalid `currency code`                                                 	|
|                          	| [E019](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E019)      	| Illegal field value combination                                         	|
|                          	| [E022](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E022)      	| Invalid language code                                                   	|
|                          	| [E023](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E023)      	| Invalid email                                                           	|
|                          	| [E024](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E024)      	| Same name and description for route                                     	|
|                          	| [E026](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E026)      	| Invalid route type                                                      	|
|                          	| [E029](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E029)      	| Missing field `agency_id` for file `agency.txt` with more than 1 record 	|
|                          	| [E031](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E031)      	| Invalid `agency_id`                                                     	|
|                          	| [E039](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E039)      	| `feed_start_date` after `feed_end_date`                                 	|
|                          	| [E040](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E040)      	| Dataset should be valid for at least the next 7 days                    	|
|                          	| [E043](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E043)      	| Duplicated field                                                        	|
|                          	| [E046](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E046)      	| Fast travel between stops in `stop_times.txt`                           	|
|                          	| [E048](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E048)      	| `end_time` after `start_time` in `frequencies.txt`                      	|
|                          	| [E050](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E050)      	| Trips must be used in `stop_times.txt`                                  	|
|                          	| [E051](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E051)      	| Trips must have more than one stop to be usable                         	|
|                          	| [E052](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E052)      	| Stop too far from trip shape                                            	|
|                          	| [E053](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E053)      	| Trip frequencies overlap                                                	|
|                          	| [E054](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E054)      	| Block trips must not have overlapping stop times                        	|
|                          	| [E056](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E056)      	| Missing `calendar_dates.txt` and `calendar.txt` files                   	|
|                          	| [E057](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E057)      	| Decreasing `shape_dist_traveled` in `stop_times.txt`| 
|                          	| [E059](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E059)      	| GTFS dataset too big                                                    	|
|                          	| [E060](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E060)      	| Fatal internal error -- please report                                   	|
|                          	| [E061](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#E061)      	| Out of memory                                                           	|
|                           | [W001](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W001)          | Input zip archive contains folder | 
|                           | [W002](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W002)          | Non standard field name | 
|                           | [W003](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W003)        	| Non ascii or non printable char in `id`                                         	|
|                           | [W006](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W006)        	| Missing route short name                                                        	|
|                           | [W007](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W007)        	| Missing route long name                                                         	|
|                           | [W008](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W008)        	| Route long name contains short name                                             	|
|                           | [W009](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W009)        	| Dataset should cover at least the next 30 days of service                       	|
|                           | [W010](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W010)        	| `feed_end_date` should be provided if `feed_start_date` is provided             	|
|                           | [W011](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W011)        	| `feed_start_date` should be provided if `feed_end_date` is provided             	|
|                           | [W014](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W014)        	| Duplicate `routes.route_long_name`                                              	|
|                           | [W015](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W015)        	| Duplicate `routes.route_short_name`                                             	|
|                           | [W016](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W016)        	| Duplicate combination of fields `route_long_name` and `routes.route_short_name` 	|

# Errors

<a name="MissingRequiredFieldError"/>

### MissingRequiredFieldError

A field marked as `required` is missing.

<a name="DuplicatedColumnNotice"/>

### DuplicatedColumnNotice

The input file CSV header has the same column name repeated.

<a name="MissingRequiredColumn"/>

### MissingRequiredColumn

A required column is missing in the input file.

<a name="MissingRequiredColumn"/>

### MoreThanOneEntityNotice

The file is expected to have a single entity but has more (e.g., "feed_info.txt").

<a name="MissingRequiredFileError"/>

### MissingRequiredFileError

A required file is missing.

<a name="InvalidRowLengthError"/>

### InvalidRowLengthError

A row in the input file has a different number of values than specified by the CSV header.

<a name="FieldParsingError"/>

### FieldParsingError

The values in the given column of the input rows do not represent valid values according to the column type, or have values that conflict with others according to the requirements on the input.

<a name="DuplicatedKeyError"/>

### DuplicatedKeyError

The values of the given key and rows are duplicates.

<a name="NumberOfOutRangeError"/>

### NumberOfOutRangeError

The values in the given column of the input rows are out of range.

<a name="UnexpectedEnumValueError"/>

### UnexpectedEnumValueError

An enum has an unexpected value.

<a name="UnknownFileNotice"/>

### UnknownFileNotice

A file is unknown.

<a name="ForeignKeyError"/>

### ForeignKeyError

The values of the given key and rows of one table cannot be found a values of the given key in another table.

<a name="EmptyFileNotice"/>

### EmptyFileNotice

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.
This is related to [W012](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0/RULES.md#W012).

<a name="RouteColorContrastNotice"/>

### RouteColorContrastNotice

A route color and a route text color should be contrasting.

<a name="RouteShortNameTooLongNotice"/>

### RouteShortNameTooLongNotice

Short name of a route is too long (more than 12 characters, https://gtfs.org/best-practices/#routestxt).

<a name="RouteBothShortAndLongNameMissingNotice"/>

### RouteBothShortAndLongNameMissingNotice

Both short_name and long_name are missing for a route.

<a name="RouteShortAndLongNameEqualNotice"/>

### RouteShortAndLongNameEqualNotice

Short and long name are equal for a route.

<a name="InconsistentAgencyFieldNotice"/>

### InconsistentAgencyFieldNotice

Inconsistency has been found in a field of `agency.txt` and a field of another file.
1. All records of file `agency.txt` should have the same value for field `agency_timezone` [agency_id](https://gtfs.org/reference/static/#agencytxt) when this file counts more than one record.

1. Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` and `feed_info.feed_lang`.
  The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
  * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
  * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
  * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

#### References:
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)

<a name="StartAndEndDateOutOfOrderNotice"/>

### StartAndEndDateOutOfOrderNotice

Start and end dates have been found to be out-of-order in GTFS files `feed_info.txt` or `calendar.txt`.

<a name="UnusedShapeNotice"/>

### UnusedShapeNotice

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

<a name="StopTimeWithOnlyArrivalOrDepartureTimeNotice"/>

### StopTimeWithOnlyArrivalOrDepartureTimeNotice

Missing `stop_time.arrival_time` or `stop_time.departure_time`

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence.

<a name="StopTimeWithDepartureBeforeArrivalTimeNotice"/>

### StopTimeWithDepartureBeforeArrivalTimeNotice

The `departure_time` must not precede the `arrival_time` in `stop_times.txt` if both are given. 

<a name="StopTimeWithArrivalBeforePreviousDepartureTimeNotice"/>

### StopTimeWithArrivalBeforePreviousDepartureTimeNotice

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence.

<a name="DecreasingShapeDistanceNotice"/>

### DecreasingShapeDistanceNotice

`shape_dist_traveled` should increase along a shape.

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

<a name="StationWithParentStationNotice"/>

### StationWithParentStationNotice

Field `parent_station` must be empty when `location_type` is 2.

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)
