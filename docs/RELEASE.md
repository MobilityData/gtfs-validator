# Release instructions
### 1. Update documentation
Update the [README.md](/README.md) to match the latest developments. The documentation should accurately reflect the use of the `jar` that is to be released. 

### 2. Update NOTICE_MIGRATION.md file
- [NOTICE_MIGRATION.md](https://github.com/MobilityData/gtfs-validator/blob/master/docs/NOTICE_MIGRATION.md) is a file that is committed in git. To change it, you need a pull request.
- Modifying the file and creating the PR can be done automatically by executing the `Update NOTICE_MIGRATION.md` GitHub action (found [here](https://github.com/MobilityData/gtfs-validator/actions/workflows/notice_migration_generation.yml)).
- When running the Github action, you need to specify the new version that will soon be released (e.g. v5.0.2).
- The action will modify the file and create a pull request with this title: `docs: Automated update of NOTICE_MIGRATION.md`
- Examine the pull request and if satisfactory merge it.

### 2. Do a pre relase
1. Go to the release section of GitHub ![step 1](https://user-images.githubusercontent.com/35747326/99820876-567dd600-2b1f-11eb-87d2-eef132b3016a.png)

1. Start a draft release ![step 2](https://user-images.githubusercontent.com/35747326/99822107-ce003500-2b20-11eb-9364-6dc8356e1276.png)
1. Create a tag like `v1.3.2` and **use the current master branch**
1. Leave the release description empty
1. Check the `this is a pre release checkbox`
1. Publish the prerelease
![publish](https://user-images.githubusercontent.com/35747326/99821598-3ef31d00-2b20-11eb-9f5e-26f6583ad6c9.png)

### 3. Run analytics comparing the pre-release with the last release. 
1. Generate analytics based on the acceptance tests to identify any performance concerns or problems with new notices. [Example report here](https://docs.google.com/spreadsheets/d/1AH4fELPPIB3R3w_TPGUBzSn_T6cVuYpmekW-er1Xogg/edit#gid=0). 
2. Identify if any critical issues should be resolved before release. 

### 4. Do the release
1. While CI is running to create the artifacts, you can work on the release description. ![edit](https://user-images.githubusercontent.com/35747326/99821184-ba080380-2b1f-11eb-8efe-57be80a0bd29.png)


💡 Command to find PRs merged after a certain date:
```
is:pr is:closed merged:>2020-07-28 base:master sort:updated-desc 
```
2. When the CI is done, drag and drop the artifacts in the pre release assets section. You'll need to manually rename some of the assets for now (see below).

3. Rename the artefacts so the names to stay consistent with previous releases. **It is important that the installers artefacts always have exactly the same name**, in order to have stable URL's that point to the latest installer. The installers should be named as follow:
- Installer.windows.zip
- Installer.ubuntu.zip
- Installer.macos.zip

4. Once everything is ready, simply uncheck the `pre release` box and publish again ✅
![publish](https://user-images.githubusercontent.com/35747326/99821105-99d84480-2b1f-11eb-9661-493966904a11.png)

💡 For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).

### 4. Remove all `sha` Docker images added since last release
1. Find the [list of Docker images for this project.](https://github.com/orgs/MobilityData/packages/container/gtfs-validator/versions)
1. Delete all `sha`tagged Docker images added since last release.
![grhc preview](https://user-images.githubusercontent.com/35747326/100006687-e1b5d080-2d98-11eb-846d-af12fbd7ca9f.png)
**⚠️ Note: this manipulation can only be done by someone whose GitHub account has `Admin` access rights over the `gtfs-validator` package.** 

### 5. Update the release number in the wiki and on gtfs.org
The version number in the project's wiki is used to inform users of the Desktop app that a new version is available, and will prompt them to upgrade if their local version does not match.
Update [this page](https://github.com/MobilityData/gtfs-validator/wiki/Current-Version) with the new version.

The version number on [gtfs.org/schedule/validate/](https://gtfs.org/schedule/validate/) is used to inform validator users of which version is currently running on [gtfs-validator.mobilitydata.org/](https://gtfs-validator.mobilitydata.org/). Update by opening a Pull Request on [github.com/MobilityData/gtfs.org](https://github.com/MobilityData/gtfs.org).

### 6. Publishing to Maven Central
* Maven central is a repository used by developers to download libraries that can be used in their own development.
* We upload some jars (currently gtfs-validator-main, gtfs-validator-core and gtfs-validator-model) there to make them available.
Uploaded artefacts have versions.
* Publication to Maven Central requires some manual operations.

* Typically when doing a release the publish_assets.yml Github action is automatically run. 
This will upload some assets
to be available on the release page itself (see for example [Release 4.1.0 assets](https://github.com/MobilityData/gtfs-validator/releases/tag/v4.1.0#:~:text=7%20other%20contributors-,Assets,-6))


* This Github action also publishes to Sonatype. This is used as a staging area before making the arftefacts available via Maven Central. 
* See [Sonatype Staging Repositories](https://s01.oss.sonatype.org/#stagingRepositories) (login required)
* There should be a repository in the list with name orgmobilitydata-####. This is automatically created by Sonatype when files are uploaded.

![image](https://github.com/MobilityData/gtfs-validator/assets/106176106/f08a24ec-addb-4d63-840d-24297c505822)


* You can browse the repo content to make sure everything is there. In particular there should be the jars for the code, jars for javadoc, for sources, and files for the maven pom. 
* Everything should be signed, as evidenced by the presence of files with extension .sha1, .sha256, .sha512 etc.
* Also make sure the version is correct.
* You then need to manually close the repo. Doing this will trigger acceptance tests for Maven Central.

![image](https://github.com/MobilityData/gtfs-validator/assets/106176106/1d8916c6-a640-43cf-9658-82193d127b1d)

* Once the repository is closed it becomes available for inclusion in projects for testing. The URL to use as repository in your gradle or maven configuration files can be found in the summary for the repo.
![image](https://github.com/MobilityData/gtfs-validator/assets/106176106/c809c1ca-67d7-4c45-bfa5-47441e163d2f)

* Once satisfied with the testing, the repo can be released to Maven Central.
* Note that once a release is deployed on Maven Central, it cannot be removed or modified. If problems are detected after this stage, a new release with a different version has to be created.
