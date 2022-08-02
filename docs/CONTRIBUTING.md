# Contribution guidelines 

If you’re reading this section, you are probably interested in contributing to the Canonical GTFS Schedule Validator. First, thank you for your interest, and welcome to the community! This document describes the different ways you can contribute to this project, and what the processes look like. 
This project is a community effort, and anyone interested in this project can join the community, participate in decision-making and help advance this project in different ways. There are many different ways to contribute: sharing your idea for a new feature, improving the documentation, teaching others how to use it, interacting on Pull Requests, helping answer questions, etc. We welcome and value any type of contribution, and yes: you do have something to contribute to this validator! If you are not on the [MobilityData Slack](https://docs.google.com/forms/d/e/1FAIpQLSczZbZB9ql_Xl-1uBtmvYmA0fwfm1UX92SyWAdkuMEDfxac5w/viewform) already, please join the channel #gtfs-validators to introduce yourself and share what area of the project you’re interested in working on.

We value discussions, respect, and openness in our community. All experiences are welcome, regardless of the technical knowledge. We particularly encourage people from underrepresented backgrounds in Open Source to participate. 

Please read our [Contributor Code of Conduct](https://github.com/MobilityData/gtfs-validator/blob/master/CODE_OF_CONDUCT.md) before contributing to this project.

## Ways to contribute

There are many ways to contribute to this project: improving code and documentation, teaching others, answering questions on the Slack channel, etc. Good documentation is as important as quality code. The list below is an attempt to list different ways of contributing and it is not exhaustive. If you think of any other way, let us know!

| Contribution type                                 | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|---------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Participate in the community                      | What makes this tool successful is its international & diverse community. One of the best ways to contribute is simply to be part of it!  Participate in the discussions, answer on the newly opened issues, help others, attend the community meetings. This is all happening on the Slack channel #gtfs-validators.                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Comment or vote on issues issues already opened  | We use the [GitHub issue tracker](https://github.com/MobilityData/gtfs-validator/issues) to capture bugs, new feature ideas and improvements.  If you see an issue that is relevant to you, give a thumbs up or comment. This will increase the priority of the issue, and it will be solved earlier as a result!                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Share your ideas and report issues you’re facing  | Not sure if this is an issue with the tool or something you are doing wrong? We encourage you to still open an issue, even if it is just to ask a question. If something is not working or is unclear to you, chances are you’re not the only one. See the [Submitting a feature request or a bug report](#submitting-a-feature-request-or-a-bug-report) for more information.                                                                                                                                                                                                                                                                                                                                                        |   |
| Spread the word                                   | Reference this project in your articles, on your website, or simply mention that you use it. This will help its implementation & improve collaboration in the GTFS ecosystem.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Improving documentation                           | Documentation is one of the most important parts of this tool. Good documentation makes it easier for people to use it, troubleshoot the validation report and fix their dataset. See the [Submitting a Pull Request Contribution](#Submitting-a-Pull-Request-contribution) and [Improving Documentation](#Improving-documentation) sections for more information.                                                                                                                                                                                                                                                                                                                                                                    |   |
| Commenting and reviewing Pull Requests            | Good Pull Request reviews improve their quality and reduce the burden on project maintainers: this is a great way to contribute to this project. Pull Requests reviews are not restricted to code reviews. If a new feature is added to the project, there are many aspects to consider: is the new functionality well documented? Are there any user-facing modifications? Is it going to be easy to maintain? See the [Pull Request comments and reviews](#Pull-Request-comments-and-reviews) section for more information.                                                                                                                                                                                                         |   |
| Contributing code                                 | If you are interested in working on a new feature, or solving a bug, we encourage you to share your intention with others in the [Slack channel](https://docs.google.com/forms/d/e/1FAIpQLSczZbZB9ql_Xl-1uBtmvYmA0fwfm1UX92SyWAdkuMEDfxac5w/viewform). See the [Submitting a Pull Request Contribution](#Submitting-a-Pull-Request-contribution) and [Code Contributions](#Code-contributions) sections for more information. We encourage those that are new to our code base to implement the issues labeled [Good first issue](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22) or [Help wanted](https://github.com/MobilityData/gtfs-validator/labels/help%20wanted). |   |


## Roles and Responsibilities
**Contributors**

They are community members that interacted in this project (opened an issue, a PR, or left a comment). Anyone can become a contributor, regardless of the technical skills, and no previous experience with the tool is needed. 

**Triagers**

They are responsible for triaging new issues and they have Triage permissions to the repository. The typical workflow for triaging an issue is:
- Answer the author.
- If it is a question, answer, close, and update the project documentation if needed.
- Is this a duplicate issue?
- Is the necessary information provided?
- Is the issue reproducible? 
- Add the relevant labels (such as documentation or good first issue)
Remove the Needs triage label and replace it with the appropriate status label (such as Needs discussion, Ready, Won’t fix, etc)
@TODO how to become a triager

**Core developers**

Contributors who actively push to the project. They can perform code reviews, request changes, approve and merge Pull Requests. Core developers have Write permissions to the repository.
@TODO how to become a core developer

**Technical committee**
The technical committee is composed of core developers and the product manager, who have additional responsibilities to ensure the project runs smoothly and the progress is in line with the big picture vision: they discuss considerable changes to the project, perform strategic planning, and approve changes to the governance model. 
The Steering committee currently consists of [Isabelle de Robert](https://github.com/isabelle-dr), [Maxime Armstrong](https://github.com/maximearmstrong), and [Brian Ferriss](https://github.com/bdferris-v2).
@TODO how to join the steering committee.

## Overview of the contribution process
![contribution process](https://user-images.githubusercontent.com/63653518/182148793-55bbe40d-19ec-474b-9174-f55f848ef4d9.jpg)

## Submitting a feature request or a bug report
We use the [GitHub issue tracker](https://github.com/MobilityData/gtfs-validator/issues) to capture bugs, new feature ideas and improvements. First, look at the list of open issues. If you see an issue that is relevant to you, give a thumbs up or comment. If no open issue is relevant to your case, [open a new issue](https://github.com/MobilityData/gtfs-validator/issues/new/choose). 
A label is assigned automatically to a new issue depending on the template that was chosen: a bug report issue will get a bug label, and a feature request issue will get an enhancement label. All new issues are assigned the needs triage label.
Additional labels can be assigned to the issue to provide additional information (such as documentation or good first issue), or to inform of the stage (such as blocked or needs triage). The list of labels is available [here](https://github.com/MobilityData/gtfs-validator/labels). 
After an issue is open, someone with triage permissions will read it, ask for additional information if needed, assign additional labels and assign the issue to a milestone when it is ready to be worked on.

## Submitting a Pull Request contribution
An issue should be opened describing the piece of work proposed and the problems it solves before a Pull Request is open. This is because it lets the community members participate in the design discussion. We recommend that each Pull Request encapsulates one specific new functionality or fix. For example, if you find the solution to a bug as you’re working on a new feature, open a second Pull Request to fix it. This helps troubleshooting in the future. 
We recommend you open the Pull Request even if your work is not completed and have it labeled as Draft until it is ready to be reviewed, in order to let the community members participate and help.

To open a Pull Request, please do the following:
- [Create an account on GitHub](https://github.com/join) if you do not already have one
- [Fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo) this repository
- Create a new branch, and
- Propose your changes by opening a [new pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests).

We use [semantic commit messages](https://github.com/electron/electron/blob/main/docs/development/pull-requests.md#commit-message-guidelines) to streamline the release process. Before your pull request can be merged, you should update your pull request title to start with a semantic prefix.
  Examples of Pull Request titles with semantic prefixes:
- `fix: fix: Bug with ssl network connections + Java module permissions.`
- `feat: feat: Initial support for multiple @PrimaryKey annotations.`
- `docs: update RELEASE.md with new process`

This process is described in more detail in the GitHub documentation [**Contributing to projects**](https://docs.github.com/en/get-started/quickstart/contributing-to-projects). We encourage contributors to format pull requests commits following the [Conventional Commit Specification](https://www.conventionalcommits.org/en/v1.0.0/).

## Pull Request comments and reviews
Reviewing Pull Requests is a great way to get familiar with the code & architecture of this tool, and to make sure a functionality meets your needs. Each Pull Request has to be approved by at least one one core developer, but having community members helping with this process is significant for the MobilityData team. Additionally, having the eyes of people from different expertise and backgrounds on a contribution makes it higher quality (nobody can think of everything!).

**Code review guidelines**

A Pull Request is a gift to the community: please start a review with a positive comment, and ask the high level questions before diving into a line-by-line review. Take your time during the process: reviews are a critical step of every Pull Request and they can take a while. Often during reviews, we have discussions that lead to re-framing the problem and solution, we find a new approach to a problem, we notice other issues, etc. 

If you have a suggestion that the author could ignore, use the prefix nit” in your comment (for example nit: removed whitespace).

Below are a few questions that need to be considered during a Pull Request review.
- Is the code easy to read and understand?
- Is the code well documented?
- Is the code following the conventions of the programming language used?
- Would this functionality be easy to extend? Does it design make sense?
- Is this new feature easily maintainable by other developers?
- Is the code consistent with the validator architecture?
- Does this new functionality have dependencies with other parts of the validator? Or have external dependencies that could compromise security (outdated libraries, etc)?
- Are the tests appropriate to validate the additional code? Is there a case that is not being tested?
- Is all the necessary documentation for this new functionality available?
- Is the user facing documentation easy to understand? Is it including terms that casual users would be confused by?
- Does the pull request pass all the required GitHub Action continuous integration tests? If not, why is something failing?

## Code contributions
Before starting a code contribution, take the time to familiarize yourself with the current architecture of the validator (described in [ARCHITECTURE.md](https://github.com/MobilityData/gtfs-validator/blob/master/docs/ARCHITECTURE.md)) and the current code base. We encourage those that are new to our code base to implement the issues labeled [Good first issue](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22) or [Help wanted](https://github.com/MobilityData/gtfs-validator/labels/help%20wanted).

**Coding style**

Sticking to a single consistent and documented coding style for this project is important to ensure that code reviewers dedicate their attention to the functionality of the validation, as opposed to disagreements about the coding style (and avoid [bike-shedding](https://en.wikipedia.org/wiki/Law_of_triviality)). This project uses the [Google Java Style](https://google.github.io/styleguide/javaguide.html). IDE plugins to automatically format your code in this style are [here](https://github.com/google/google-java-format).

**How do I add a new validation rule?**

The [NEW_RULES.md](https://github.com/MobilityData/gtfs-validator/blob/master/docs/NEW_RULES.md) document includes step-by-step instructions for adding new validation rules to the validator.

**Have you encountered an error?**

A critical step in troubleshooting is being able to reproduce the problem. Instructions to publicly reproduce errors using GitHub Actions can be found in our [guide to reproduce errors](https://github.com/MobilityData/gtfs-validator/blob/master/docs/REPRODUCE_ERRORS.md).

**How to run tests locally?**

This project includes unit and end-to-end tests in order to:
1. Verify the implementation behaves as expected in tests as well as on real data
2. Make sure any new code does not break existing code
Run the following command at the root of the project to run Java tests (you need JDK11+ installed locally):
$ ./gradlew test

## Improving documentation
The documentation is a core aspect of this project! We welcome all types of documentation, including how to’s and translated material.
Documentation is written in Markdown, you can see the complete syntax in the [Markdown Guide](https://www.markdownguide.org/).

**Writing guidelines**
- When describing steps to reproduce or explaining a feature, we recommend using the second person in the active tense. For example: we prefer “Make sure any new code does not break existing code” than “Any new code written should not break existing code”.
- Start each document with a short description of what the documentation is about.
- Use heading levels, line breaks and bulleted lists to avoid long paragraphs of text.
- Leverage the rich [Markdown syntax](https://www.markdownguide.org/cheat-sheet/): code blocks, tables, bold, etc. 
- Use [collapsable sections](https://gist.github.com/pierrejoubert73/902cc94d79424356a8d20be2b382e1ab) for long documents

**Credits**
This contribution guideline was inspired by the [JupyterLab](https://jupyterlab.readthedocs.io/en/stable/index.html) and [SKLearn](https://scikit-learn.org/stable/developers/contributing.html#) open source projects.
