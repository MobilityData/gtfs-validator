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
| [E024](#E024) | Route short name too long | 

### Table of Warnings

| Warning ID    | Warning Title             |
|---------------|---------------------------|
| [W001](#W001) | | 
| [W002](#W002) | | 
| [W003](#W003) | | 
| [W004](#W004) | | 
| [W005](#W005) | Same name and description for route | 
| [W006](#W006) | Insufficient route color contrast | 

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

# Warnings

<a name="W005"/>

### W005 - Same name and description for route

The GTFS spec defines `routes.txt` [route_description](https://gtfs.org/reference/static/#routestxt) as:

> Description of a route that provides useful, quality information. Do not simply duplicate the name of the route.

See the GTFS and GTFS Best Practices links below for more examples of how to populate the `route_short_name`, `route_long_name`, and `route_description` fields.

References:

[GTFS routes.txt](http://gtfs.org/reference/static/#routestxt)
[GTFS routes.txt Best Practices](https://gtfs.org/best-practices/#routestxt)

#### References:
* [Route.txt Specification](http://gtfs.org/reference/static/#routestxt)

<a name="W006"/>

### W005 - Insufficient route color contrast

A Route color and a Route text color should be contrasting. Minimum Contrast Ratio allowed is 4.5. Contrast Ratio is computed according to the W3 Color Contrast Procedure. Please visit links below for more information about color contrast.

#### References:
* [Route.txt Specification](http://gtfs.org/reference/static/#routestxt)
* [W3 Color Contrast Verification Procedure](https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure)
