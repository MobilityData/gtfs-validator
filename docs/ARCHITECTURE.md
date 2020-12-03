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

### How to add a new validation rule _i.e._ a validator?

Adding a new validator is relatively simple:
1. Create a class for the validator that implements one of the interfaces `FileValidator`, or `SingleEntityValidator`.

   💡 Use [`SingleEntityValidator`](/org/mobilitydata/gtfsvalidator/validator/SingleEntityValidator.java) to implement a validation rule that can be applied to a single row from a GTFS file.
   
   💡 Use [`FileValidator`](/org/mobilitydata/gtfsvalidator/validator/FileValidator.java) to implement a validation rule that handles one as a whole or several files.
    
1. If needed, inject GTFS tables that will be used during the validation process in the newly created validator as class fields.  
1. Write the validation rule logic in overridden `validate` method with the correct set of parameters.

## How to test the newly added validator?
`gtfs-validator` tests rely on [`JUnit 4`](https://junit.org/junit4/), [`Mockito`](https://site.mockito.org/) and [`Google Truth`](https://github.com/google/truth).

### Test a `SingleEntityValidator` 
1️⃣ Mock a `NoticeContainer`

2️⃣ Mock a `GtfsEntity` and stub its methods that are used in the validator

3️⃣ Execute the validator on the mocked `GtfsEntity`

4️⃣ Verify interactions with the `NoticeContainer`

5️⃣ Verify interactions with the mocked `GtfsEntity` 

### Test a `FileValidator`
1️⃣ Create a class constant `NoticeContainer` and annotate it `@Mock`

2️⃣ Declare the validator to be tested as a class constat and annotate it `@InjectMocks`: mockito will inject the `NoticeContainer`.

3️⃣ Implement `initMocks`  method. Annotatet it `@Before` so that it is ran before all tests of the class.

4️⃣ Stub all required methods for all mocks used.

5️⃣ Verify interactions with the `NoticeContainer`

6️⃣ Verify interactions with all mocks

## Resources

[Use Mockito to Mock Autowired Fields](https://dzone.com/articles/use-mockito-mock-autowired)
