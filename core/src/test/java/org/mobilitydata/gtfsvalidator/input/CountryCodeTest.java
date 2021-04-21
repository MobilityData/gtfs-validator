/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.input;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CountryCodeTest {

  @Test
  public void forStringOrUnknown() {
    assertThat(CountryCode.forStringOrUnknown("nl").getCountryCode()).isEqualTo("NL");
    assertThat(CountryCode.forStringOrUnknown("au").getCountryCode()).isEqualTo("AU");
    assertThat(CountryCode.forStringOrUnknown("uk").getCountryCode()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("UK").getCountryCode()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("gb").getCountryCode()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("zz").getCountryCode()).isEqualTo("ZZ");
    assertThat(CountryCode.forStringOrUnknown("xx").getCountryCode()).isEqualTo("ZZ");
    assertThat(CountryCode.forStringOrUnknown("XX").getCountryCode()).isEqualTo("ZZ");
    assertThrows(NullPointerException.class, () -> CountryCode.forStringOrUnknown(null));
  }

  @Test
  public void isUnknown() {
    assertThat(CountryCode.forStringOrUnknown("gp").isUnknown()).isFalse();
    assertThat(CountryCode.forStringOrUnknown("GP").isUnknown()).isFalse();
    assertThat(CountryCode.forStringOrUnknown("Gp").isUnknown()).isFalse();
    assertThat(CountryCode.forStringOrUnknown("zz").isUnknown()).isTrue();
    assertThat(CountryCode.forStringOrUnknown("abc").isUnknown()).isTrue();
    assertThat(CountryCode.forStringOrUnknown("ABC").isUnknown()).isTrue();
    assertThat(CountryCode.forStringOrUnknown("AbC").isUnknown()).isTrue();
  }

  @Test
  public void toStringTest() {
    assertThat(CountryCode.forStringOrUnknown("nl").toString()).isEqualTo("NL");
    assertThat(CountryCode.forStringOrUnknown("au").toString()).isEqualTo("AU");
    assertThat(CountryCode.forStringOrUnknown("uk").toString()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("UK").toString()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("gb").toString()).isEqualTo("GB");
    assertThat(CountryCode.forStringOrUnknown("zz").toString()).isEqualTo("ZZ");
    assertThat(CountryCode.forStringOrUnknown("xx").toString()).isEqualTo("ZZ");
    assertThat(CountryCode.forStringOrUnknown("XX").toString()).isEqualTo("ZZ");
  }
}
