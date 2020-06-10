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
| [E028](#E032) | `feed_start_date` after `feed_end_date` | 
| [E028](#E033) | Dataset should be valid for at least the next 7 days | 

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

<a name="E032"/>

### E028 - `feed_start_date` after `feed_end_date`

The `feed_end_date` date must not precede the `feed_start_date` date if both are given. 

#### References:
* [feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)

<a name="E033"/>

### E033 - Dataset should be valid for at least the next 7 days

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