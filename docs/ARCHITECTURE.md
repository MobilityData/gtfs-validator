# Architecture description
`gtfs-validator` counts three principal modules: [`main`](/main), [`processor`](/processor) and [`core`](/core). These modules dependencies are illustrated in the following diagram:
![architecture schema](https://user-images.githubusercontent.com/35747326/101182386-610e9400-3624-11eb-84b9-ec935e44aa2b.png)

This new architecture leverages `AutoValue` and annotations to auto-generate the following classes used for loading and validation:
* all classes used to internally represent GTFS data (such as `GtfsStopTime.java`) 
* `*Schema.java` (such as `GtfsAgencySchema.java`)
* `*Enum.java` (such as `GtfsFrequencyExactTimeEnum.java`)
* `*Container.java` (such as `GtfsAgencyTableContainer.java`)
* `*Loader.java` (such as `GtfsAgencyTableLoader.java`)
* `*ForeignKeyValidator.java` (such as `GtfsAttributionAgencyIdForeignKeyValidator.java`)

## Main
_Depends on: `processor` and `core`_

If you're looking to add new GTFS fields or rules, you'll want to look at this module.

Contains:
- The [command-line (CLI) app](/main/src/main/java/org/mobilitydata/gtfsvalidator/cli) - The main application that uses the `processor` and `core` modules to read and validate a GTFS feed.
- GTFS [table schemas](/main/src/main/java/org/mobilitydata/gtfsvalidator/table) - Defines how GTFS files (e.g., `trips.txt`) and the fields contained within that file (e.g., `trip_id`) are represented in the validator. You can add new GTFS files and fields here. 
- Business logic [validation rules](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator) - Code that validates GTFS field values. You can add new validation rules here.
- Error [notices](/main/src/main/java/org/mobilitydata/gtfsvalidator/notice) - Containers for information about errors discovered during validation. You can add new notices here when implementing new validation rules.
  
## Processor
_Depends on: `core`_

Contains:
- A file analyser to analyse annotations on Java interfaces that define GTFS schema and translate them to descriptors
- Descriptors of annotations fields (`ForeignKey`, `GtfsEnum`, `GtfsField`, `GtfsFile`)
- A processor to auto-generate data classes, loaders and validators based on annotations on GTFS schema interfaces
- GTFS entity classes to generate class names for a given GTFS table
- Code generators to generate code from annotations found by file analyser (_e.g._ `EnumGenerator`)

## Core
_Depends on: nothing_

- Annotation definitions such as `ForeignKey`, `GtfsTable`
- Code to read zipped and unzipped file input
- CSV file and row parsers 
- Notice to be generated when checking data type validation rules such as `EmptyFileNotice` 
- A notice container (`NoticeContainer`)
- GTFS data type definitions such as `GtfsTime`, `GtfsDate`, or `GtfsColor`
- `GtfsFeedLoader` to load for a whole GTFS feed with all its CSV files
- GTFS feed's name

## Data pipeline 📥➡️♨➡️📤

1️⃣ **Inputs**
- A local GTFS archive (zip file) or fully qualified URL from which to download a GTFS archive
- Command line arguments 

2️⃣ **Validator loading**
- Locate all validators annotated with `@GtfsValidator` and load them

3️⃣ **Feed loading**
- Read GTFS files
- Create `GtfsTableContainer` from data
- Invoke and execute all `SingleEntityValidators` to validate data types, etc.

4️⃣ **Validators execution**
- Invoke and execute all `FileValidators` in parallel to validate GTFS semantic rules
 
5️⃣ **Notice export**
1. Creates path to export notices as specified by command line input `--output` (or `-o`).
1. Export notices from `NoticeContainer` to two JSON files in the specified directory - `report.json` for validator results and `system_errors.json` for any software errors that occurred during validation. Notices are alphabetically sorted in the `.json` files. 

## Adding new tables and fields

Let's say that you are an agency which for some reason uses `other_file.txt` as an additional table to represent GTFS information, and your goal is to implement validation rule related to this new table.
To do so, you would have to:
1. add the new table to the validator;
1. implement the new validation rules. 

This section details how existing table are defined and gives information on annotation usage. One can then transpose these explanations to add a new table or field. Let's take a look at [`GtfsCalendarSchema`](../main/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsCalendarSchema.java):

```java
package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.EndRange;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable("calendar.txt")
@ConditionallyRequired
public interface GtfsCalendarSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String serviceId();

  @Required
  GtfsCalendarService monday();

  @Required
  GtfsCalendarService tuesday();

  @Required
  GtfsCalendarService wednesday();

  @Required
  GtfsCalendarService thursday();

  @Required
  GtfsCalendarService friday();

  @Required
  GtfsCalendarService saturday();

  @Required
  GtfsCalendarService sunday();

  @Required
  @EndRange(field = "end_date", allowEqual = true)
  GtfsDate startDate();

  @Required
  GtfsDate endDate();
}
```

By order of appearance in the interface definition:
* `@GtfsTable`: annotates the interface that defines schema for `calendar.txt` - The [`processor`](../processor) will generates data classes, loaders and validators based on annotations on this GTFS schema interface. 
* `@ConditionallyRequired`: hints that this file is conditionally required.
* `@FieldType`: specifies `calendar_service_id` is defined as an ID by the GTFS specification. 
* `@PrimaryKey`: specifies `calendar_service_id` is the primary key of this table.
* `@Required`: specifies a value for `calendar_service_id` is required - A notice will be issued at the parsing stage. 
* `@EndRange`: specifies `endDate` is the end point for the date range defined by `calendar.start_date` and `calendar.end_time` - A validator will be generated and check if `calendar.start_date` is before or equal to `calendar.end_date`. 
 
## Annotations definitions

| Annotation                                                                                                          	| Definition                                                                                                                                                              	|
|---------------------------------------------------------------------------------------------------------------------	|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------	|
| [CachedField](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/CachedField.java)                     	| Enables caching of values for a given field to optimize memory usage.                                                                                                   	|
| [ConditionallyRequired](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/ConditionallyRequired.java) 	| A hint that a field or a file is required.                                                                                                                              	|
| [DefaultValue](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/DefaultValue.java)                   	| Specifies a default value for a particular GTFS field.                                                                                                                  	|
| [EndRange](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/EndRange.java)                           	| Specifies a field for the end point of a date or time range.                                                                                                            	|
| [FieldType](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/FieldType.java)                         	| Specifies type of a GTFS field, e.g., [`COLOR`](http://gtfs.org/reference/static#field-types) or [`LATITUDE`](http://gtfs.org/reference/static#field-types).            	|
| [FirstKey](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/FirstKey.java)                           	| Specifies the first part of a composite key in tables like `stop_times.txt` (`trip_id`).                                                                                	|
| [ForeignKey](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/ForeignKey.java)                       	| Specifies a reference to a foreign key.                                                                                                                                 	|
| [Generated](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/Generated.java)                         	| Marker for all classes generated by annotation processor.                                                                                                               	|
| [GtfsEnumValue](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/GtfsEnumValue.java)                 	| Specifies a value for a GTFS enum.                                                                                                                                      	|
| [GtfsEnumValues](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/GtfsEnumValues.java)               	| It is necessary for making GtfsEnumValue annotation repeatable.                                                                                                         	|
| [GtfsLoader](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/GtfsLoader.java)                       	| This annotation is placed by annotation processor on generated classes that load individual GTFS files, e.g., `stops.txt`. This annotation should not be used directly. 	|
| [GtfsTable](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/GtfsTable.java)                         	| Annotates an interface that defines schema for a single GTFS table, such as `stops.txt`.                                                                                	|
| [GtfsValidator](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/GtfsValidator.java)                 	| Annotates both custom and automatically generated validators to make them discoverable on the fly.                                                                      	|
| [Index](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/Index.java)                                 	| Asks annotation processor to create an index for quick search on a given field. The field does not need to have unique values.                                          	|
| [NonNegative](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/NonNegative.java)                     	| Generates a validation that an integer or a double (float) field is not negative.                                                                                       	|
| [NonZero](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/NonZero.java)                             	| Generates a validation that an integer or a double (float) field is not zero.                                                                                           	|
| [Positive](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/Positive.java)                           	| Generates a validation that an integer or a double (float) field is positive.                                                                                           	|
| [PrimaryKey](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/PrimaryKey.java)                       	| Specifies the primary key in a GTFS table. This also adds a validation that all values are unique.                                                                      	|
| [Required](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/Required.java)                           	| Adds a validation that the field or a file is required.                                                                                                                 	|
| [SequenceKey](../core/src/main/java/org/mobilitydata/gtfsvalidator/annotation/SequenceKey.java)                     	| Specifies the second part of a composite key in tables like `stop_times.txt` (stop_sequence). This annotation needs to be used in a combination with `@FirstKey.`       	|
