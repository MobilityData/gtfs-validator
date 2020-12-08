# Contribution guidelines [WIP]

## How to contribute to project?

## Fork this repository (detailed instructions)
1. go to https://github.com/MobilityData/gtfs-validator
1. **click** on the `fork` button on the *top right corner*
1. **wait** for the fork creation, you should now see your fork (https://github.com/YOUR_USERNAME/gtfs-validator)

## Create a pull request (PR)
1. **navigate** to `.github/workflows/end_to_end.yml`
1. **click** the *crayon* icon to enter edit mode
1. on line 5, **replace** `transport-agency-name` by something significant like `societe-de-transport-de-montreal` if you were adding a dataset from *STM*
1. **keep it around** as you'll need it in *step 11.*
1. **uncomment** line 43 by removing the `#` character
1. on line 43, **replace** `ACRONYM` by some acronym for the Agency/publisher, in our example that would be `STM`
1. **uncomment** line 44 by removing the `#` character
1. on line 44, **replace** `[[[ACRONYM]]]` in `[[[ACRONYM]]].zip` by what you put down in step 4 - **NO SPACES OR SPECIAL CHARACTERS -- keep the .zip extension intact**
1. on line 44, **replace** `DATASET_PUBLIC_URL` by a *public url* pointing to a [GTFS Schedule](https://gtfs.mobilitydata.org/spec/gtfs-schedule) zip archive
1. **click** on the *green* `Start commit` button on the right of the page
1. **select the option ` Create a new branch for this commit and start a pull request.`**
1. **replace** the proposed default branch name by what you got from *step 7.* Note that the branch name must exactly match the line 5 text (e.g., `societe-de-transport-de-montreal`).
1. **click** the *green* `Propose changes` button
1. on the next screen, **click** `Create pull request`

You should now see the workflow `End to end / run-on-data` start automatically in your PR checks, running the validator on the dataset you just added. The validation report is collected as a run artifact in the Actions tab of your fork repository on GitHub.

## Propose changes (detailed instructions)
1. **go** to https://github.com/MobilityData/gtfs-validator
1. **select** the `Pull requests` tab
1. **click** the *green* `New pull request` button
1. in the `Compare changes` section, **click** the *blue* link **compare across forks.**
1. on the **left** side of the `←` *base repository:* **should be** `MobilityData/gtfs-validator` and *base:* be `master`
1. on the **right** side of the `←` use the first dropdown to **change** *head repository:* to your forked one (like `ilovetramways/gtfs-validator` for GitHub handle `ilovetramways`)
1. on the **right** side of the `←` use the second dropdown to **change** *compare:* to **the branch in your fork containing the changes you made to end_to_end.yml that led to an issue**
1. **click** the *green* `Create pull request` button
1. use the dropdown on the *green* `Create pull request` button to **select `Create draft pull request`**
1. **click** the *green* `Draft pull request` button

Then we're all set, thk you very very much! The end to end workflow will run on the newly created PR in our repository and automatically collect all relevant information. We take care of everything from then and will follow up directly in the PR.

While we welcome all contributions, our [members and sponsors](https://mobilitydata.org/members/) see their PRs and issues prioritized.

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

### End to end

### via `run-on-data` GitHub workflow job

There is a way to locally execute the [`run-on-data` job of the end_to_end GitHub workflow](https://github.com/MobilityData/gtfs-validator/blob/24d58c8ee76af00aa3ab413b218b4a8e2cfafc4b/.github/workflows/end_to_end.yml#L9)

You need Docker to be installed

Install [act](https://github.com/nektos/act)
```
brew install act
```

In the repo root folder
```
act -j run-on-data
```

Note: we run into a [know issue](nektos/act#329) of `act` when trying to collect artifacts
```
[End to end/run-on-data]   ❗  ::error::Unable to get ACTIONS_RUNTIME_TOKEN env variable
```

.zip dataset files and .json validation report files still are available **within the Docker image (`docker exec -it`) ** for manual collection
```
MacBook-Pro-de-Fabrice:~ fabricev$ docker exec -it b22cf048e47ad10c65be3071dd14dad999dbcf59531a2e31326733c05d861048 /bin/sh; exit
# ls
ADDING_NEW_RULES.md  build.gradle		      mst.zip
Dockerfile	     config			      null
LICENSE		     domain			      octa.zip
MTBA.zip	     gradle			      one_empty_gtfs_file.zip
README.md	     gradlew			      output
RELEASE.md	     gradlew.bat		      settings.gradle
RULES.md	     input			      usecase
adapter		     mbta.zip
application	     mixed_empty_full_gtfs_files.zip
# cd output
# ls
MBTA__2020-10-26_08-51-29.211229.json
MST__2020-10-26_08-51-39.835092.json
Orange_County_Transportation_Authority__2020-10-26_08-52-19.024739.json
