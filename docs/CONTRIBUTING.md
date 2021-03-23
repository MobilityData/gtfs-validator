# Contribution guidelines 

## How to contribute to project? 
All contributions to this project are welcome. To propose changes, we encourage contributors to:
1. [Fork](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo) this project on GitHub
1. Create a new branch, and
1. Propose their changes by opening a [new pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests). 

If you're looking for somewhere to start, check out the issues labeled ["Good first issue"](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22) or [Community](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3Acommunity).

### Issue and PR templates
We encourage contributors to format pull request titles following the Conventional Commit Specification.

## Having problems?
Have you encountered an error? A critical step in troubleshooting is being able to reproduce the problem. Instructions to publicly reproduce errors using GitHub Actions can be found in our [guide to reproduce errors.](/docs/REPRODUCE_ERRORS.md)

## Code template

### Coding style
"Sticking to a single consistent and documented coding style for this project is important to ensure that code reviewers dedicate their attention to the functionality of the validation, as opposed to disagreements about the coding style (and avoid [bike-shedding](https://en.wikipedia.org/wiki/Law_of_triviality))." 
This project uses the [Google Java Style](https://google.github.io/styleguide/javaguide.html). IDE plugins to automatically format your code in this style are [here](https://github.com/google/google-java-format).

## How do I add a new validation rule?
The ["Adding new rules" documentation](/docs/NEW_RULES.md) includes instructions for adding new validation rules to the validator.
 
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
