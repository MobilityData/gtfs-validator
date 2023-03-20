# Release instructions
### 1. Update documentation
Update the [README.md](/README.md) to match the latest developments. The documentation should accurately reflect the use of the `jar` that is to be released. 
If the release contains changes in the notices (new notices, dropped notices, a change in severities, update the [NOTICE_MIGRATION.md](/docs/NOTICE_MIGRATION.md) and add a column.

### 2. Do a pre relase
1. Go to the release section of GitHub ![step 1](https://user-images.githubusercontent.com/35747326/99820876-567dd600-2b1f-11eb-87d2-eef132b3016a.png)

1. Start a draft release ![step 2](https://user-images.githubusercontent.com/35747326/99822107-ce003500-2b20-11eb-9364-6dc8356e1276.png)
1. Create a tag like `v1.3.2` and **use the current master branch**
1. Leave the release description empty
1. Check the `this is a pre release checkbox`
1. Publish the prerelease
![publish](https://user-images.githubusercontent.com/35747326/99821598-3ef31d00-2b20-11eb-9f5e-26f6583ad6c9.png)

### 3. Do the release
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

### 3. Remove all `sha` Docker images added since last release
1. Find the [list of Docker images for this project.](https://github.com/orgs/MobilityData/packages/container/gtfs-validator/versions)
1. Delete all `sha`tagged Docker images added since last release.
![grhc preview](https://user-images.githubusercontent.com/35747326/100006687-e1b5d080-2d98-11eb-846d-af12fbd7ca9f.png)
**⚠️ Note: this manipulation can only be done by someone whose GitHub account has `Admin` access rights over the `gtfs-validator` package.** 

### 3. Update the release number in the wiki
By updating the version number in the project's wiki, users of the app will be advised to upgrade if their local version does not match.
Update [this page](https://github.com/MobilityData/gtfs-validator/wiki/Current-Version) with the new version.
