package org.mobilitydata.gtfsvalidator.util;

import java.util.Optional;

/** Version information about the validator. */
public class VersionInfo {

  private Optional<String> currentVersion;
  private Optional<String> latestReleaseVersion;

  public VersionInfo(Optional<String> currentVersion, Optional<String> latestReleaseVersion) {
    this.currentVersion = currentVersion;
    this.latestReleaseVersion = latestReleaseVersion;
  }

  public Optional<String> currentVersion() {
    return currentVersion;
  }

  public void setCurrentVersion(Optional<String> currentVersion) {
    this.currentVersion = currentVersion;
  }

  public Optional<String> latestReleaseVersion() {
    return latestReleaseVersion;
  }

  public void setLatestReleaseVersion(Optional<String> latestReleaseVersion) {
    this.latestReleaseVersion = latestReleaseVersion;
  }

  public boolean updateAvailable() {
    if (currentVersion.isEmpty() || latestReleaseVersion.isEmpty()) {
      return false;
    }
    if (currentVersion.get().endsWith("SNAPSHOT")) {
      return false;
    }
    return !latestReleaseVersion.get().equals(currentVersion.get());
  }

  public static VersionInfo empty() {
    return create(Optional.empty(), Optional.empty());
  }

  public static VersionInfo create(
      Optional<String> currentVersion, Optional<String> latestReleaseVersion) {
    return new VersionInfo(currentVersion, latestReleaseVersion);
  }
}
