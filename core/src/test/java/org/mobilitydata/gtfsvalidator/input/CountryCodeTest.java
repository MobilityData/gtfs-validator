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
  public void getISOAlpha2CountryCode() {
    assertThat(CountryCode.parseString("nl").getISOAlpha2CountryCode()).isEqualTo("NL");
    assertThat(CountryCode.parseString("au").getISOAlpha2CountryCode()).isEqualTo("AU");
    assertThat(CountryCode.parseString("uk").getISOAlpha2CountryCode()).isEqualTo("GB");
    assertThat(CountryCode.parseString("gb").getISOAlpha2CountryCode()).isEqualTo("GB");
  }

  @Test
  public void isValidCountryCode() {
    assertThat(CountryCode.isValidISOAlpha2("nl")).isTrue();
    assertThat(CountryCode.isValidISOAlpha2("au")).isTrue();
    assertThat(CountryCode.isValidISOAlpha2("fr")).isTrue();
    assertThat(CountryCode.isValidISOAlpha2("uk")).isTrue();
    assertThat(CountryCode.isValidISOAlpha2("gb")).isTrue();

    assertThat(CountryCode.isValidISOAlpha2("openov-nl")).isFalse();
    assertThat(CountryCode.isValidISOAlpha2("zz-zz")).isFalse();
  }

  @Test
  public void parseStringInvalid() {
    assertThrows(IllegalArgumentException.class, () -> CountryCode.parseString("openov"));
    assertThrows(IllegalArgumentException.class, () -> CountryCode.parseString("zz-zz-zz"));
    assertThrows(IllegalArgumentException.class, () -> CountryCode.parseString("abc-def"));
    assertThrows(IllegalArgumentException.class, () -> CountryCode.parseString("xx"));
  }

  @Test
  public void isProvided() {
    assertThat(CountryCode.parseString("nl").isProvided()).isTrue();
    assertThat(CountryCode.parseString("au").isProvided()).isTrue();
    assertThat(CountryCode.parseString(null).isProvided()).isFalse();
    // if country code is provided it should be correctly formatted hence throwing an exception in
    // this case
    assertThrows(IllegalArgumentException.class, () -> CountryCode.parseString("xx"));
  }
}
