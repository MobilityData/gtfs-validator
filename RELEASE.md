# Release instructions

### 1. Do the release

Prepare a draft release on GitHub and **tag the master branch**
here is a useful command to filter PRs
```
is:pr is:closed merged:>2020-07-28 base:master sort:updated-desc 
```
Let the CI run and collect the artifacts
Add them to the release
Release

For more details on versioning, see [Understanding Maven Version Numbers](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855).
