# Contribution guidelines [WIP]

## How to contribute to project? [Coming soon]

## Having problems?
Have you encountered an error? A critical step in troubleshooting is being able to reproduct the problem. Instructions to publicly reproduce errors using GitHub Actions can be found in our [guide to reproduce errors.](/docs/REPRODUCE_ERRORS.md)

## Code template [Coming soon]

## How to add a new validation rule?
[NEW_RULES.md](/docs/NEW_RULES.md) gathers instructions to add new validation rules to the validator. Please refer to this documentation for more information.
 
## How to run tests locally
This project includes unit and end-to-end tests in order to:
1. Verify the implementation behaves as expected in tests as well as on real data
1. Make sure any new code does not break existing code

Run the following command at the root of the project to run Java tests:

```
$ ./gradlew test
```

### Locally run GitHub Actions
We use GitHub Actions to build the project and test on datasets. You can run these tools locally by following our [end-to-end testing guide](/docs/END_TO_END.md)
