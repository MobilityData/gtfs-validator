# How to add a new validation rule _i.e._ a validator?

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

3️⃣ Implement `initMocks`  method. Annotate it `@Before` so that it is ran before all tests of the class.

4️⃣ Stub all required methods for all mocks used.

5️⃣ Verify interactions with the `NoticeContainer`

6️⃣ Verify interactions with all mocks

## Resources

[Use Mockito to Mock Autowired Fields](https://dzone.com/articles/use-mockito-mock-autowired)
