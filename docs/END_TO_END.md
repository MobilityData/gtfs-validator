### End-to-end testing guide
 
- End-to-end testing can be performed via `run-on-data` GitHub workflow job, see [error reproduction section](/docs/REPRODUCE_ERRORS.md)
- There is a way to locally execute the [`run-on-data` job of the end_to_end GitHub workflow](https://github.com/MobilityData/gtfs-validator/blob/24d58c8ee76af00aa3ab413b218b4a8e2cfafc4b/.github/workflows/end_to_end.yml#L9): you need Docker to be installed.

1. Install [act](https://github.com/nektos/act):
```
brew install act
```

2. Run the following command in the repo root folder:
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
