# Implemented rules

Rules are declared in the [`Notice` module](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice).  Below are details of currently implemented rules.

### Table of Errors

| Error ID      | Error Title         |
|---------------|---------------------------|
| [E001](#E001) | Missing required field | 
| [E003](#E003) | | 
| [E004](#E004) | | 
| [E006](#E006) | | 
| [E009](#E009) | | 
| [E010](#E010) | | 
| [E011](#E011) | | 
| [E012](#E012) | |
| [E013](#E013) | |
| [E015](#E015) | |
| [E016](#E016) | | 
| [E017](#E017) | |
| [E018](#E018) | | 
| [E019](#E019) | | 
| [E020](#E020) | | 
| [E021](#E021) | | 
| [E022](#E022) | Invalid language code | 
| [E023](#E023) | Invalid email | 
| [E024](#E024) | Same name and description for route | 
| [E025](#E025) | Insufficient route color contrast |
| [E026](#E026) | Invalid route type | 
| [E027](#E027) | Missing route short name and long name | 
| [E028](#E028) | Route long name equals short name | 
| [E029](#E029) | Missing field `agency_id` for file `agency.txt` with more than 1 record | 
| [E030](#E030) | Inconsistent field `agency_timezone` | 
| [E031](#E031) | Invalid `agency_id` | 
| [E032](#E032) | `calendar.txt` `end_date` is before `start_date` |
| [E033](#E033) | `route_id` not found in GTFS `routes.txt` |
| [E034](#E034) | `shape_id` not found in GTFS `shapes.txt` |
| [E035](#E035) | `agency_id` not found in GTFS `agency.txt` |
| [E036](#E036) | `service_id` not found in GTFS `calendar.txt` or `calendar_dates.txt`|
| [E037](#E037) | `trip_id` not found in GTFS `trips.txt` |
| [E038](#E038) | All shapes should be used in `trips.txt` |
| [E039](#E039) | `feed_start_date` after `feed_end_date` | 
| [E040](#E040) | Dataset should be valid for at least the next 7 days | 
| [E041](#E041) | Invalid parent `location_type` for stop |
| [E042](#E042) | Station stop (`location_type`=2) has a parent stop |
| [E043](#E043) | Duplicated field |
| [E044](#E044) | Missing trip edge `arrival_time` or `departure_time` |
| [E045](#E045) | `arrival_time` after `departure_time` in `stop_times.txt` |
| [E046](#E046) | Fast travel between stops in `stop_times.txt` |
| [E047](#E047) | Csv file is empty |
| [E048](#E048) | `end_time` after `start_time` in `frequencies.txt` |
| [E049](#E049) | Backwards time travel between stops in `stop_times.txt` |
| [E050](#E050) | Trips must be used in `stop_times.txt` |
| [E051](#E051) | Trips must have more than one stop to be usable |
| [E053](#E053) | Trip frequencies overlap |
| [E054](#E054) | Block trips must not have overlapping stop times |
| [E055](#E055) | Mismatching feed and agency language fields |
| [E056](#E056) | Missing `calendar_dates.txt` and `calendar.txt` files |
| [E057](#E057) | Decreasing `shape_dist_traveled` in `stop_times.txt` |
| [E058](#E058) | Decreasing `shape_dist_traveled` in `shapes.txt` |

### Table of Warnings

| Warning ID    | Warning Title             |
|---------------|---------------------------|
| [W001](#W001) | | 
| [W002](#W002) | Non standard field name | 
| [W003](#W003) | | 
| [W004](#W004) | | 
| [W005](#W005) | Route short name too long |
| [W006](#W006) | Missing route short name |
| [W007](#W007) | Missing route long name |
| [W008](#W008) | Route long name contains short name | 
| [W009](#W009) | Dataset should cover at least the next 30 days of service | 
| [W010](#W010) | `feed_end_date` should be provided if `feed_start_date` is provided | 
| [W011](#W011) | `feed_start_date` should be provided if `feed_end_date` is provided | 
| [W012](#W012) | Optional csv file is empty | 
| [W014](#W014) | Duplicate `routes.route_long_name` | 
| [W015](#W015) | Duplicate `routes.route_short_name` | 
| [W016](#W016) | Duplicate combination of fields `route_long_name` and `routes.route_short_name` | 

# Errors

<a name="E001"/>

### E001 - Missing required field

A field marked as `required` is missing 

<a name="E022"/>

### E022 - Invalid language code

Language codes used in a GTFS feed should be under the IETF BCP 47 format. Please visit links below for an introduction to IETF BCP 47.

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)
* [IETF BCP 47 Language Tags Introduction](https://www.w3.org/International/articles/language-tags/)

<a name="E023"/>

### E023 - Invalid email

An email should be a valid email address (e.g., contact@agency.org)

#### References:
* [Field Types Description](http://gtfs.org/reference/static/#field-types)

<a name="E024"/>

### E024 - Same name and description for route

The GTFS spec defines `routes.txt` [route_description](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_description` fields.

References:

[GTFS routes.txt](http://gtfs.org/reference/static/#routestxt)
[GTFS routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

#### References:
* [Route.txt Specification](http://gtfs.org/reference/static/#routestxt)

<a name="E025"/>

### E025 - Insufficient route color contrast

A Route color and a Route text color should be contrasting. Minimum Contrast Ratio allowed is 4.5. Contrast Ratio is computed according to the W3 Color Contrast Procedure. Please visit links below for more information about color contrast.

#### References:
* [Route.txt Specification](http://gtfs.org/reference/static/#routestxt)
* [W3 Color Contrast Verification Procedure](https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure)

<a name="E026"/>

### E026 - Invalid route type

<a name="E027"/>

### E027 - Missing route short name and long name

At least one of `routes.route_short_name` or `routes.route_long_name` should be provided - both can't be blank or missing.

#### References:
* [routes.txt specification](https://gtfs.org/reference/static/#routestxt)

<a name="E028"/>

### E028 - Route long name equals short name

<a name="E029"/>

### E029 - Missing `agency_id` for file `agency.txt` with more than 1 record

All records of file `agency.txt` should have a non-null value for field [agency_id](https://gtfs.org/reference/static/#agencytxt) when this file counts more than one record.

<a name="E030"/>

### E030 - Inconsistent field `agency_timezone` 

All records of file `agency.txt` should have the same value for field `agency_timezone` [agency_id](https://gtfs.org/reference/static/#agencytxt) when this file counts more than one record.

<a name="E031"/>

### E031 - Invalid `agency_id` 

When provided field `agency_id` should not be blank.

<a name="E032"/>

### E032 - `calendar.txt` `end_date` is before `start_date`

In `calendar.txt`, the `end_date` of a service record must not be earlier than the `start_date`.

#### References:
* [calendar.txt specification](https://gtfs.org/reference/static/#calendartxt)

<a name="E033"/>

### E033 - `route_id` not found in GTFS `routes.txt`

Value of field `route_id` should exist in GTFS `routes.txt`.

<a name="E034"/>

### E034 - `shape_id` not found in GTFS `shapes.txt`

Value of field `shape_id` should exist in GTFS `shapes.txt`.

<a name="E035"/>

### E035 - `agency_id` not found in GTFS `agency.txt`

Value of field `agency_id` should exist in GTFS `agency.txt`.

<a name="E036"/>

### E036 - `service_id` not found

Value of field `service_id` should exist in GTFS `calendar.txt` or `calendar_dates.txt`.

<a name="E037"/>

### E037 - `trip_id` not found in GTFS `trips.txt`

Value of field `trip_id` should exist in GTFS `trips.txt`.

<a name="E038"/>

### E038 - All shapes should be used in `trips.txt` 

All records defined by GTFS `shapes.txt` should be used in `trips.txt`.

<a name="E039"/>

### E039 - `feed_start_date` after `feed_end_date`

The `feed_end_date` date must not precede the `feed_start_date` date if both are given. 

#### References:
* [feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)

<a name="E040"/>

### E040 - Dataset should be valid for at least the next 7 days

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.

#### References:
* [Dataset Publishing & General Practices](http://gtfs.org/best-practices/#dataset-publishing--general-practices)

<a name="E041"/>

### E041 - Invalid parent `location_type` for stop

Value of field `location_type` of parent found in field `parent_station` is invalid.

According to spec
- _Stop/platform_ can only have _Station_ as parent
- _Station_ can NOT have a parent
- _Entrance/exit_ or _generic node_ can only have _Station_ as parent
- _Boarding Area_ can only have _Platform_ as parent 

Any other combination raise this error

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="E042"/>

### E042 - Station stop (`location_type` = 2) has a parent stop

Field `parent_station` must be empty when `location_type` is 2

#### References:
* [stops.txt specification](http://gtfs.org/reference/static/#stopstxt)

<a name="E043"/>

### E043 - Duplicated field

A file cannot contain the same header value twice (i.e., duplicated column of data).

<a name="E044"/>

### E044 - Missing trip edge `arrival_time` and `departure_time`

First and last stop of a trip must define both fields

<a name="E045"/>

### E045 - `arrival_time` after `departure_time` in `stop_times.txt`

The `departure_time` must not precede the `arrival_time` in `stop_times.txt` if both are given. 

#### References:
* [stop_times.txt specification](http://gtfs.org/reference/static/#stop_timestxt)

<a name="E046"/>

### E046 - Fast travel between stops in `stop_times.txt`

Calculated speed between stops is too fast (>150 kmh) 

<a name="E047"/>

### E047 - Csv file is empty

Empty csv file found in the archive: file does not have any headers, or is a required file and does not have any data. The GTFS specification requires the first line of each file to contain field names and required files must have data.
This is related to [W012](#https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#W012).

#### References:
* [File requirements](http://gtfs.org/reference/static#file-requirements)


### E048 - `end_time` after `start_time` in `frequencies.txt`

The `end_time` must not precede the `start_time` in `frequencies.txt`. 

#### References:
* [GTFS frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)

<a name="E049"/>

### E049 - Backwards time travel between stops in `stop_times.txt`

For a given `trip_id`, the `arrival_time` of (n+1)-th stoptime in sequence must not precede the `departure_time` of n-th stoptime in sequence
 
 <a name="E050"/>

### E050 - Trips must be used in `stop_times.txt`

Trips must be referred to at least once in `stop_times.txt`.

<a name="E051"/>

### E051 - Trips must have more than one stop to be usable

A trip must visit more than one stop in `stop_times.txt` to be usable by passengers for boarding and alighting.

<a name="E053"/>

### E053 - Trip frequencies overlap

Trip frequencies must not overlap in time

#### References:

* [GTFS frequencies.txt specification](http://gtfs.org/reference/static/#frequenciestxt)

<a name="E054"/>

### E054 - Block trips must not have overlapping stop times

Trip stop times should not overlap when they are part of the same block operating on the same day.

#### References:

* [GTFS trips.txt specification](http://gtfs.org/reference/static/#tripstxt)

<a name="E055"/>

### E055 - Mismatching feed and agency language fields

Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` and `feed_info.feed_lang`.
The default language may be multilingual for datasets with the original text in multiple languages. In such cases, the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
* If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
* If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
* If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

#### References:
* [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
* [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)

<a name="E056"/>

### E056 - Missing both `calendar_dates.txt` and `calendar.txt` files

Both files `calendar_dates.txt` and `calendar.txt` are missing from the GTFS archive. At least one of the files must be provided.
                        
<a name="E057"/>

### E057 - Decreasing `shape_dist_traveled` in `stop_times.txt`

Stop times in a trip should have increasing distance.

<a name="E058"/>

### E058 - Decreasing `shape_dist_traveled` in `shapes.txt`

`shape_dist_traveled` should increase along a shape.

#### References:
* [shapes.txt specification](https://gtfs.org/reference/static#shapestxt)

# Warnings

<a name="W002"/>

### W002 - Non standard field name

A field not defined in the specification was found. It will be ignored.

<a name="W005"/>

### W005 - Route short name too long

<a name="W006"/>

### W006 - Missing route short name

<a name="W007"/>

### W007 - Missing route long name

<a name="W008"/>

### W008 - Route long name contains short name

<a name="W009"/>

### W009 - Dataset should cover at least the next 30 days of service

If possible, the GTFS dataset should cover at least the next 30 days of service

#### References:
* [Dataset Publishing & General Practices](http://gtfs.org/best-practices/#dataset-publishing--general-practices)

<a name="W010"/>

### W010 - `feed_end_date` should be provided if `feed_start_date` is provided

`feed_end_date` should be provided in conjunction with field `feed_start_date`.
 
* [feed_info.txt Best Practices](http://gtfs.org/best-practices/#feed_infotxt)

<a name="W011"/>

### W011 - `feed_start_date` should be provided if `feed_end_date` is provided

`feed_end_date` should be provided in conjunction with field `feed_start_date`.
 
* [feed_info.txt Best Practices](http://gtfs.org/best-practices/#feed_infotxt)

<a name="W012"/>

### W012 - Optional csv file is empty

Empty csv optional file found in the archive: file contains header but does not have data.  
This is related to [E047](https://github.com/MobilityData/gtfs-validator/blob/master/RULES.md#E047).

<a name="W014"/>

### W014 - Duplicate `routes.route_long_name`

All routes should have different `routes.route_long_name`. If routes have the same `routes.route_long_name`, they must be different routes serving different areas; and must not be different trips of the same route or different directions of the same route.
Note that two routes can have the same `routes.route_long_name` if they do not belong to the same agency.

<a name="W015"/>

### W015 - Duplicate `routes.route_short_name`

All routes should have different `rouytes.route_short_name`. If routes have the same `routes.route_short_name`, they must be different routes serving different areas; and must not be different trips of the same route or different directions of the same route. 
Note that two routes can have the same `routes.route_short_name` if they do not belong to the same agency.

<a name="W016"/>

### W016 - Duplicate combination of fields `routes.route_long_name` and `routes.route_short_name`

The same combination of `route_short_name` and `route_long_name` should not be used for more than one route.
