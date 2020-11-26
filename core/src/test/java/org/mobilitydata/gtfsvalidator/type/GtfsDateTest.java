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

package org.mobilitydata.gtfsvalidator.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsDateTest {
    @Test
    public void fromString() {
        assertThat(GtfsDate.fromString("20200901").getLocalDate()).isEqualTo(LocalDate.of(2020, 9, 1));
        assertThat(GtfsDate.fromString("19970103").getLocalDate()).isEqualTo(LocalDate.of(1997, 1, 3));

        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("0"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("qwerty"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("today"));
    }
}
