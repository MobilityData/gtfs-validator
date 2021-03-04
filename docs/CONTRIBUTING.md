# Contribution guidelines 

## How to contribute to project? 
All external contribution to this project is welcome. To propose changes, we encourage contributors to open a new branch and propose their changes by opening a new PR. 

## Having problems?
Have you encountered an error? A critical step in troubleshooting is being able to reproduce the problem. Instructions to publicly reproduce errors using GitHub Actions can be found in our [guide to reproduce errors.](/docs/REPRODUCE_ERRORS.md)

## Code template

###Issue and PR templates
Issue templates have been designed to ease the processes to suggest a new feature or report a bug. We encourage contributors not only to format their PR's title following the [Conventional Commit Specification](https://www.conventionalcommits.org/en/v1.0.0/), but also use the PR templates made available on this repository.

###Coding style
Sticking to a single consistent and documented coding style for this project is important to ensure that code reviewers dedicate their attention to the functionality of the validation, as opposed to disagreements about the coding style (and avoid bike-shedding). 
Google Java Style has been chosen for this project, therefore we suggest future contributors to maintain the same style for the consistency of the codebase. 
Developers should refer to Google Java Style Guide (https://google.github.io/styleguide/javaguide.html) for more information. Automated code formatting plugins for popular Java IDEs are available here: https://github.com/google/google-java-format.

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
