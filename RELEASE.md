# Release instructions

### 1. Do a pre relase
1. Go to the release section of GitHub
1. Start a draft release
1. Put a tag like v1.3.2 and **specify the master branch as to where to push it**
1. Leave the release description empty
1. Check the `this is a pre release checkbox`
1. Publish the prerelease

### 2. Do the release
1. While CI is running to create the artifacts, you can work on the release description. 

💡 Command to find PRs merged after a certain date:
```
is:pr is:closed merged:>2020-07-28 base:master sort:updated-desc 
```
2. When the CI is done, drag and drop the artifacts in the pre release assets section. Unfortunately, some manual work is required on some assets as they are badly named.

‼️ **NOTE**: The CI `.yml` files should be updated so that the output artifact names don't contain spaces and don't have to be manualy reworked when attached to a release (all space characters are replaced by . characters by GitHub).

3. Once everything is ready, simply uncheck the this is a `pre release` and publish again ✅


Prepare a draft release on GitHub and **tag the master branch**

Let the CI run and collect the artifacts
Add them to the release
Release

💡 For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).
