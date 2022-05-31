package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VersionInfoTest {

  @Test
  public void testUpdateAvailable_bothMissing() {
    VersionInfo info = VersionInfo.create(Optional.empty(), Optional.empty());
    assertThat(info.updateAvailable()).isFalse();
  }

  @Test
  public void testUpdateAvailable_latestReleaseMissing() {
    VersionInfo info = VersionInfo.create(Optional.of("1.2.3"), Optional.empty());
    assertThat(info.updateAvailable()).isFalse();
  }

  @Test
  public void testUpdateAvailable_versionMatch() {
    VersionInfo info = VersionInfo.create(Optional.of("1.2.3"), Optional.of("1.2.3"));
    assertThat(info.updateAvailable()).isFalse();
  }

  @Test
  public void testUpdateAvailable_versionMismatch() {
    VersionInfo info = VersionInfo.create(Optional.of("1.2.3"), Optional.of("1.2.4"));
    assertThat(info.updateAvailable()).isTrue();
  }

  @Test
  public void testUpdateAvailable_snapshotVersion() {
    VersionInfo info = VersionInfo.create(Optional.of("1.2.5-SNAPSHOT"), Optional.of("1.2.4"));
    assertThat(info.updateAvailable()).isFalse();
  }
}
