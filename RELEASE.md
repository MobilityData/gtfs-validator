# Release instructions

### 1. Do a pre relase
1. Go to the release section of GitHub ![step 1](https://user-images.githubusercontent.com/35747326/99820876-567dd600-2b1f-11eb-87d2-eef132b3016a.png)

1. Start a draft release ![step 2](https://user-images.githubusercontent.com/35747326/99822107-ce003500-2b20-11eb-9364-6dc8356e1276.png)
1. Create a tag like `v1.3.2` and **use the current master branch**
1. Leave the release description empty
1. Check the `this is a pre release checkbox`
1. Publish the prerelease
![publish](https://user-images.githubusercontent.com/35747326/99821598-3ef31d00-2b20-11eb-9f5e-26f6583ad6c9.png)

### 2. Do the release
1. While CI is running to create the artifacts, you can work on the release description. ![edit](https://user-images.githubusercontent.com/35747326/99821184-ba080380-2b1f-11eb-8efe-57be80a0bd29.png)


💡 Command to find PRs merged after a certain date:
```
is:pr is:closed merged:>2020-07-28 base:master sort:updated-desc 
```
2. When the CI is done, drag and drop the artifacts in the pre release assets section. You'll need to manually rename some of the assets for now (see below).

‼️ **TODO**: The CI `.yml` files should be updated so that the output artifact names don't contain spaces and don't have to be manualy reworked when attached to a release (all space characters are replaced by . characters by GitHub).

3. Once everything is ready, simply uncheck the `pre release` box and publish again ✅
![publish](https://user-images.githubusercontent.com/35747326/99821105-99d84480-2b1f-11eb-9661-493966904a11.png)

💡 For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).

### 3. Remove all intermediate `sha` Docker images added since last release
1. Find the list of Docker images for this project [here](https://github.com/orgs/MobilityData/packages/container/gtfs-validator/versions)
