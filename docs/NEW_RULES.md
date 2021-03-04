# How to add a new validation rule _i.e._ a validator?

Adding a new validator is relatively simple:
1. Create a class for the validator that implements one of the interfaces `FileValidator`, or `SingleEntityValidator`.

   💡 Use [`SingleEntityValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/SingleEntityValidator.java) to implement a validation rule that can be applied to a single row from a GTFS file.
   
   💡 Use [`FileValidator`](../core/src/main/java/org/mobilitydata/gtfsvalidator/validator/FileValidator.java) to implement a validation rule that handles one as a whole or several files.
    
1. If needed, inject GTFS tables that will be used during the validation process in the newly created validator as class fields.  
1. Write the validation rule logic in overridden `validate` method with the correct set of parameters.

## How to test the newly added validator?
`gtfs-validator` tests rely on [`JUnit 4`](https://junit.org/junit4/), and [`Google Truth`](https://github.com/google/truth).

Validators are tested against data samples.

### Test a `SingleEntityValidator` 
1️⃣ Create a [`GtfsEntity`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsEntity.java).

2️⃣ Create a [`NoticeContainer`](../core/src/main/java/org/mobilitydata/gtfsvalidator/notice/NoticeContainer.java).

3️⃣ Execute the validator one the previously defined parameters (`GtfsEntity` and `NoticeContainer`).

4️⃣ Verify the content of `NoticeContainer`.

One can also refer to [`this example`](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/FeedExpirationDateValidatorTest.java).
 
### Test a `FileValidator`

1️⃣ Create the relevant [`GtfsTableContainer`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsTableContainer.java).

2️⃣ Create the relevant [`GtfsEntity`](../core/src/main/java/org/mobilitydata/gtfsvalidator/table/GtfsEntity.java) needed to populate the previous tables.

3️⃣ Create a [`NoticeContainer`](../core/src/main/java/org/mobilitydata/gtfsvalidator/notice/NoticeContainer.java).

4️⃣ Execute the validator one the previously defined parameters (`GtfsEntity` and `NoticeContainer`).

5️⃣ Verify the content of `NoticeContainer`.

One can also refer to [`this example`](../main/src/test/java/org/mobilitydata/gtfsvalidator/validator/TripUsageValidatorTest.java).
