# Implemented rules

Rules are declared in the [`Notice` module](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice).  Below are details of currently implemented rules.

### Table of Errors

| Error ID      | Error Title         |
|---------------|---------------------------|
| [E001](#E001) | | 
| [E002](#E002) | | 
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
| [E037](#E039) | `feed_start_date` after `feed_end_date` | 
| [E038](#E040) | Dataset should be valid for at least the next 7 days | 

### Table of Warnings

| Warning ID    | Warning Title             |
|---------------|---------------------------|
| [W001](#W001) | | 
| [W002](#W002) | | 
| [W003](#W003) | | 
| [W004](#W004) | | 
| [W005](#W005) | Route short name too long |
| [W006](#W006) | Missing route short name |
| [W007](#W007) | Missing route long name |
| [W008](#W008) | Route long name contains short name | 
| [W009](#W009) | Dataset should cover at least the next 30 days of service | 
| [W009](#W010) | `feed_end_date` should be provided if `feed_start_date` is provided | 
| [W009](#W011) | `feed_start_date` should be provided if `feed_end_date` is provided | 

# Errors

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

### E037 - `feed_start_date` after `feed_end_date`

The `feed_end_date` date must not precede the `feed_start_date` date if both are given. 

#### References:
* [feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)

<a name="E040"/>

### E038 - Dataset should be valid for at least the next 7 days

At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally for as long as the operator is confident that the schedule will continue to be operated.

#### References:
* [Dataset Publishing & General Practices](http://gtfs.org/best-practices/#dataset-publishing--general-practices)

# Warnings

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

### W010 - `feed_start_date` should be provided if `feed_end_date` is provided

`feed_end_date` should be provided in conjunction with field `feed_start_date`.
 
* [feed_info.txt Best Practices](http://gtfs.org/best-practices/#feed_infotxt)