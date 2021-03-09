# Architecture description
`gtfs-validator` counts three principal modules: [`main`](/main), [`processor`](/processor) and [`core`](/core). These modules dependencies are illustrated in the following diagram:

![architecture schema](https://user-images.githubusercontent.com/35747326/101182386-610e9400-3624-11eb-84b9-ec935e44aa2b.png)

### Main
_Depends on: `processor` and `core`_

If you're looking to add new GTFS fields or rules, you'll want to look at this module.

Contains:
- The [command-line (CLI) app](/main/src/main/java/org/mobilitydata/gtfsvalidator/cli) - The main application that uses the `processor` and `core` modules to read and validate a GTFS feed.
- GTFS [table schemas](/main/src/main/java/org/mobilitydata/gtfsvalidator/table) - Defines how GTFS files (e.g., `trips.txt`) and the fields contained within that file (e.g., `trip_id`) are represented in the validator. You can add new GTFS files and fields here. 
- Business logic [validation rules](/main/src/main/java/org/mobilitydata/gtfsvalidator/validator) - Code that validates GTFS field values. You can add new validation rules here.
- Error [notices](/main/src/main/java/org/mobilitydata/gtfsvalidator/notice) - Containers for information about errors discovered during validation. You can add new notices here when implementing new validation rules.
  
### Processor
_Depends on: `core`_

Contains:
- A file analyser to analyse annotations on Java interfaces that define GTFS schema and translate them to descriptors
- Descriptors of annotations fields (`ForeignKey`, `GtfsEnum`, `GtfsField`, `GtfsFile`)
- A processor to auto-generate data classes, loaders and validators based on annotations on GTFS schema interfaces
- GTFS entity classes to generate class names for a given GTFS table
- Code generators to generate code from annotations found by file analyser (_e.g._ `EnumGenerator`)

### Core
_Depends on: nothing_

- Annotation definitions such as `ForeignKey`, `GtfsTable`
- Code to read zipped and unzipped file input
- CSV file and row parsers 
- Notice to be generated when checking data type validation rules such as `EmptyFileNotice` 
- A notice container (`NoticeContainer`)
- GTFS data type definitions such as `GtfsTime`, `GtfsDate`, or `GtfsColor`
- `GtfsFeedLoader` to load for a whole GTFS feed with all its CSV files
- GTFS feed's name

### Data pipeline 📥➡️♨➡️📤

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
