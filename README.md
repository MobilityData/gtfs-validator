# gtfs-validator [![Test Package Document](https://github.com/MobilityData/gtfs-validator/workflows/Test%20Package%20Document/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions?query=workflow%3A%22Test+Package+Document%22) ![End to end](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end/badge.svg) ![End to end big](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end%20big/badge.svg) ![End to end 100](https://github.com/MobilityData/gtfs-validator/workflows/End%20to%20end%20100/badge.svg) [![Rule acceptance tests](https://github.com/MobilityData/gtfs-validator/actions/workflows/acceptance_test.yml/badge.svg)](https://github.com/MobilityData/gtfs-validator/actions/workflows/acceptance_test.yml) ![Docker image](https://github.com/MobilityData/gtfs-validator/actions/workflows/docker.yml/badge.svg) [![Join the gtfs-validator chat](https://badgen.net/badge/slack/%20/green?icon=slack)](https://bit.ly/mobilitydata-slack)

A GTFS Schedule (static) [General Transit Feed Specification (GTFS)](https://gtfs.mobilitydata.org/spec/gtfs-schedule) feed validator

This README contains information for the latest version of the project, which is under active development.  You can find the current version of the validator application on the [Releases page](https://github.com/MobilityData/gtfs-validator/releases).

# Introduction
This is a cross-platform application written in Java that performs the following steps:
1. Loads input GTFS zip file from a URL or disk.
2. Checks file integrity, numeric type parsing and ranges as well as string format according to the [GTFS Schedule specification](https://gtfs.mobilitydata.org/spec/gtfs-schedule#h.hc443y62gb8c).
3. Performs GTFS [business rule validation](/RULES.md).
4. Provides an easy-to-use validation report in HTML format that can be opened in the browser and shared with other parties. The report is also available in JSON format that can be used for parsing and running additional analyses.

# Running the app
### Setup
1. Navigate to the [Releases page](https://github.com/MobilityData/gtfs-validator/releases) and download the latest `Gtfs Validator` installer for your operating system:
    * Windows => `.msi`
    * Mac OS => `.dmg`
    * Linux => `.deb`
2. Install application to your workstation.

### Run it
Once installed, run the application and you will see the following screen:

![Application-Windows](/docs/Application-Windows.png)

There are two primary options to set:

* `GTFS Input`: Use this to specify the GTFS feed to validate.  You can specify a URL, ZIP file, or a directory containing the individual `.txt` files of a feed.  You can paste the input location directly into the input field or use the `Choose Local File...` button to open a file-chooser dialog to select a file on your local system.
* `Output Directory`: This is the directory where the validation reports will be written.

With these two options set, click the "Validate" button to begin validation.

### Visualize the results

When validation is complete, the application will automatically open the HTML validation report in your local browser.  In addition, the application creates the following files in the output directory:
* `report.html`: the validation report in HTML format. It can be opened in a browser.
* `report.json`: the validation report in JSON format.
* `system_errors.json`: this file will be created every-time the validator is run. If no system errors were encountered, this file will be empty.

### Advanced Options
Before running validation, tap the `Advanced` button to configure other aspects of the application, including:
* Number of threads used to run the validator.
* The country code used for phone number validation.

# Run the app via command line
### Setup
1. Install [Java 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html). To check which version of Java is installed on your computer, type the following command in the terminal: `java --version`.
2. Navigate to the [Releases page](https://github.com/MobilityData/gtfs-validator/releases) and download the latest `Gtfs Validator` CLI jar (not OS-specific). It is located in the **Assets** section of the release, and it looks like `gtfs-validator-vX.X.X_cli.jar`
3. Open the terminal on your computer
4. Navigate to the directory containing the jar file. You can do this by typing the following command in the terminal:`cd {directory path}`, where {directory path} is the absolute or relative path to the directory. You can then make sure you're in the right directory by typing `pwd` in the terminal (this stands for *present working directory*). You can also make sure the jar file is there by typing `ls` in the terminal (this stands for *list* and will display the list of files in this directory). More about commands to navigate file and directories [here](https://help.ubuntu.com/community/UsingTheTerminal#File_.26_Directory_Commands).

### Run it
You can run this validator using a GTFS dataset on your computer, or from a URL.
- To validate a GTFS dataset on your computer, run the following command in the terminal, replacing the text in brackets:
  - `java -jar {name of the jar file} -i {path to the GTFS file} -o {name of the output directory that will be created}`
  - here is an example of what the command could look like:  `java -jar gtfs-validator-cli.jar -i /myDirectory/gtfs.zip -o output`

- To validate a GTFS dataset from a URL, run the following command in the terminal, replacing the text in brackets:
  - `java -jar {name of the jar file} -u {URL to the GTFS file} -o {name of the output directory that will be created}`
  - here is an example of what the command could look like: `java -jar gtfs-validator-cli.jar -u https://www.abc.com/gtfs.zip -o output`

More detailed instructions with all the parameters that exists are available on our ["Usage"](/docs/USAGE.md) page.

### Visualize the results
In the output directory, the reports will be created as described [here](#visualize-the-results).

# Run the app using Docker
### Setup
1. Download and install [Docker](https://docs.docker.com/get-started/)
1. Pull the [latest Docker image for this project](https://github.com/orgs/MobilityData/packages/container/package/gtfs-validator). For example, `docker pull ghcr.io/mobilitydata/gtfs-validator` for the latest snapshot version of the validator.

### Run it

#### For Mac and Linux

To run the Docker image in a new container:

`docker run -v /myDirectory:/theContainerDirectory -it ghcr.io/mobilitydata/gtfs-validator:latest`

where:
* `-v /myDirectory:/theContainerDirectory`: syntax to share directories and data between the container and the host (your computer). With the above command, any files that you place in `/myDirectory` on the host will show up in `/theContainerDirectory` inside the container and vice versa.

***NOTE:*** On Windows, you must provide the local volume (e.g., `c:`) as well:

`... c:/myDirectory:/theContainerDirectory ...`

The validator can then be executed via bash commands. See the [preceeding instructions for command line usage](#run-the-app-via-command-line).

### Build Docker container locally

To build a Docker image locally from source (e.g. to test an unmerged branch):

```bash
docker build . --build-arg=VERSION_TAG=v3 -t gtfs-validator:latest
```

The locally-built image could then be run via the local `gtfs-validator:latest` assigned above:

```bash
docker run --rm gtfs-validator:latest --help
```

### Visualize the results
In the output directory, the reports will be created as described [here](#visualize-the-results).

# Snapshot Builds, Previous Releases, and Documentation
* If you'd like to run the bleeding-edge pre-release Snapshot of the application, see the [access instructions](/docs/DOWNLOAD_SNAPSHOT_JAR.md).
* If you are looking for older releases, see the [Releases page](https://github.com/MobilityData/gtfs-validator/releases).
* If you'd like to view documentation for past releases of the project, see:
  * [v1.4.0](https://github.com/MobilityData/gtfs-validator/blob/v1.4.0-docs/README.md)
  * [v2.0.0](https://github.com/MobilityData/gtfs-validator/blob/v2.0.0-docs/README.md)
  * [v3.0.0](https://github.com/MobilityData/gtfs-validator/blob/docs/v3.0.0/README.md)

# Validation rules
* [Implemented rules](/RULES.md)

Possible future rules for:
* [GTFS Reference](https://github.com/MobilityData/gtfs-validator/labels/Rules%20-%20GTFS%20Reference)
* [GTFS Best Practices](https://github.com/MobilityData/gtfs-validator/labels/Rules%20-%20GTFS%20Best%20Practices)
* [Community rules](https://github.com/MobilityData/gtfs-validator/labels/Rules%20-%20Community%20rules)

Have a suggestion for a new rule? Open [an issue](https://github.com/MobilityData/gtfs-validator/issues/new/choose). You can see the complete process for adding new rules on the ["Adding new rules"](/docs/NEW_RULES.md) page.

# Build the code
We suggest using [IntelliJ](https://www.jetbrains.com/idea/download/) to [import](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html), build, and run this project.

Instructions to build the project from the command-line using [Gradle](https://gradle.org/) are available in our [Build documentation](/docs/BUILD.md).

# Architecture
The architecture of the `gtfs-validator` is described on our [Architecture page](/docs/ARCHITECTURE.md).

# Acceptance tests
In order to avoid sudden changes in the validation output that might declare previously valid datasets invalid, all code changes in pull requests are tested against GTFS datasets in the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page). The acceptance test process is described in [ACCEPTANCE_TESTS.md](docs/ACCEPTANCE_TESTS.md).

# License
Code licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

# Contributing
We welcome contributions to the project! Please check out our [Contribution guidelines](/docs/CONTRIBUTING.md) for details.
