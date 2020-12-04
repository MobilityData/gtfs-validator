# Architecture description
`gtfs-validator` counts three principal modules: [`main`](/), [`processor`](/) and [`core`](/).

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
- Local GTFS archive
- Command line arguments 

2️⃣  **Validator loading**
- locate all validators and load them

3️⃣  **Feed loading**
- create `GtfsInput`
  - read GTFS files
  - create `GtfsTableContainer` from data

4️⃣ **Validators execution**
- `SingleEntityValidator` for a given file are invoked and executed as soon as the file is loaded into memory (step 3)
- `FileValidator` (for multiple files) are invoked and executed right after the whole GTFS archives is loaded into memory.
 
5️⃣ **Notice export**
1. Creates path to export notices as specified by command line input `--output` (or `-o`).
1. Export notices in `NoticeContainer` as `.json` file. 

🔚 **Output: validation result report** 
