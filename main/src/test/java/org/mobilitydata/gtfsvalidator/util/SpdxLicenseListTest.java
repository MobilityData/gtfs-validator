/*
 * Copyright 2026 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class SpdxLicenseListTest {

  private final SpdxLicenseList spdxLicenses = SpdxLicenseList.getInstance();

  @Test
  public void bundledListHasVersion() {
    assertThat(spdxLicenses.getVersion()).isNotEmpty();
  }

  @Test
  public void currentLicense_isKnownAndNotDeprecated() {
    assertThat(spdxLicenses.isKnown("CC-BY-4.0")).isTrue();
    assertThat(spdxLicenses.isDeprecated("CC-BY-4.0")).isFalse();
  }

  @Test
  public void deprecatedLicense_isKnownAndDeprecated() {
    assertThat(spdxLicenses.isKnown("GPL-3.0")).isTrue();
    assertThat(spdxLicenses.isDeprecated("GPL-3.0")).isTrue();
  }

  @Test
  public void unknownLicense_isNotKnownNorDeprecated() {
    assertThat(spdxLicenses.isKnown("Not-A-License")).isFalse();
    assertThat(spdxLicenses.isDeprecated("Not-A-License")).isFalse();
  }

  @Test
  public void matchingIsCaseInsensitive() {
    assertThat(spdxLicenses.isKnown("odbl-1.0")).isTrue();
    assertThat(spdxLicenses.isDeprecated("gpl-3.0")).isTrue();
  }

  /** Guards against a truncated or wrong file when the bundled list is refreshed. */
  @Test
  public void bundledListHasPlausibleSize() {
    assertThat(spdxLicenses.size()).isGreaterThan(500);
  }
}
