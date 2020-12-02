/*
 * Copyright 2020 Google LLC
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsFeedNameTest {
    @Test
    public void getISOAlpha2CountryCode() {
        assertThat(GtfsFeedName.parseString("nl-openov").getISOAlpha2CountryCode()).isEqualTo("NL");
        assertThat(GtfsFeedName.parseString("au-sydney-buses").getISOAlpha2CountryCode()).isEqualTo("AU");
        assertThat(GtfsFeedName.parseString("openov-nl").getISOAlpha2CountryCode()).isEqualTo("NL");
    }

    @Test
    public void isValidCountryFirstFeedName() {
        assertThat(GtfsFeedName.isValidCountryFirstFeedName("nl-openov")).isTrue();
        assertThat(GtfsFeedName.isValidCountryFirstFeedName("au-sydney-buses")).isTrue();
        assertThat(GtfsFeedName.isValidCountryFirstFeedName("fr-nl")).isTrue();

        assertThat(GtfsFeedName.isValidCountryFirstFeedName("openov-nl")).isFalse();
        assertThat(GtfsFeedName.isValidCountryFirstFeedName("zz-zz")).isFalse();
    }

    @Test
    public void getCountryLastName() {
        assertThat(GtfsFeedName.parseString("nl-openov").getCountryLastName()).isEqualTo("openov-nl");
        assertThat(GtfsFeedName.parseString("au-sydney-buses").getCountryLastName()).isEqualTo("buses-sydney-au");
        assertThat(GtfsFeedName.parseString("openov-nl").getCountryLastName()).isEqualTo("openov-nl");
        assertThat(GtfsFeedName.parseString("nl-au").getCountryLastName()).isEqualTo("au-nl");
    }

    @Test
    public void parseString() {
        assertThat(GtfsFeedName.parseString("nl-openov").getCountryFirstName()).isEqualTo("nl-openov");
        assertThat(GtfsFeedName.parseString("au-sydney-buses").getCountryFirstName()).isEqualTo("au-sydney-buses");
        assertThat(GtfsFeedName.parseString("openov-nl").getCountryFirstName()).isEqualTo("nl-openov");
        assertThat(GtfsFeedName.parseString("nl-au").getCountryFirstName()).isEqualTo("nl-au");
        assertThat(GtfsFeedName.parseString("nl-zz").getCountryFirstName()).isEqualTo("nl-zz");
        assertThat(GtfsFeedName.parseString("zz-nl").getCountryFirstName()).isEqualTo("nl-zz");
    }

    @Test
    public void parseStringInvalid() {
        assertThrows(IllegalArgumentException.class, () -> GtfsFeedName.parseString("openov"));
        assertThrows(IllegalArgumentException.class, () -> GtfsFeedName.parseString("zz-zz-zz"));
        assertThrows(IllegalArgumentException.class, () -> GtfsFeedName.parseString("abc-def"));
        assertThrows(IllegalArgumentException.class, () -> GtfsFeedName.parseString("au"));
    }

    @Test
    public void equalsMethod() {
        assertThat(GtfsFeedName.parseString("nl-openov")).isEqualTo(GtfsFeedName.parseString("nl-openov"));
        assertThat(GtfsFeedName.parseString("nl-openov")).isEqualTo(GtfsFeedName.parseString("openov-nl"));
    }
}
