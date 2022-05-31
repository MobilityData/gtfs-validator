package org.mobilitydata.gtfsvalidator.util;

import com.google.auto.value.AutoValue;
import java.util.Optional;

/** Version information about the validator. */
@AutoValue
public abstract class VersionInfo {

  /** The version of the currently running validator instance. */
  public abstract Optional<String> currentVersion();

  /** The latest released version of the validator. */
  public abstract Optional<String> latestReleaseVersion();

  public boolean updateAvailable() {
    if (currentVersion().isEmpty() || latestReleaseVersion().isEmpty()) {
      return false;
    }
    // We don't trigger an update if the user is running a development snapshot.
    if (currentVersion().get().endsWith("SNAPSHOT")) {
      return false;
    }
    return !latestReleaseVersion().get().equals(currentVersion().get());
  }

  public static VersionInfo empty() {
    return create(Optional.empty(), Optional.empty());
  }

  public static VersionInfo create(
      Optional<String> currentVersion, Optional<String> latestReleaseVersion) {
    return new AutoValue_VersionInfo(currentVersion, latestReleaseVersion);
  }
}
