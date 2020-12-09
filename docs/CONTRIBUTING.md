# Contribution guidelines [WIP]

## How to contribute to project? [coming soon]

## How to reproduce errors?
Instructions to reproduce errors can be found in our [guide to reproduce errors.](/docs/REPRODUCE_ERRORS.md)

## Code template [Coming soon]

## How to add a new validation rule?
[NEW_RULES.md](/docs/NEW_RULES.md) gathers instructions to add new validation rules to the validator. Please refer to this documentation for more information.
 
## How to run tests locally
This project includes unit and end-to-end tests in order to:
1. Verify the implementation behaves as expected in tests as well as on real data
1. Make sure any new implementation does not involuntarily break existing code

### Unitary tests
Run the following command at the root of the project to run Java tests:
```
$ ./gradlew test
```

### End to end tests
The behavior of the validator can be tested on existing datasets: instructions to proceed are available in our [end-to-end testing guide](/docs/END_TO_END.md)
