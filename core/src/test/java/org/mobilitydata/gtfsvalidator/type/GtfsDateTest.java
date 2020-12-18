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
        assertThat(GtfsDate.fromString("20200901").getLocalDate())
                .isEqualTo(LocalDate.of(2020, 9, 1));
        assertThat(GtfsDate.fromString("19970103").getLocalDate())
                .isEqualTo(LocalDate.of(1997, 1, 3));

        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("0"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("qwerty"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("today"));
    }

    @Test
    public void dateFromLocalDateShouldReturnGtfsDateWithSameData() {
        GtfsDate underTest = GtfsDate.fromLocalDate(LocalDate.of(2022,1,20));
        assertThat(underTest.getDay()).isEqualTo(20);
        assertThat(underTest.getMonth()).isEqualTo(1);
        assertThat(underTest.getYear()).isEqualTo(2022);
    }

    @Test
    public void dateFromEpochShouldReturnGtfsDateWithSameData() {
        GtfsDate underTest = GtfsDate.fromEpochDay(18614);
        assertThat(underTest.getDay()).isEqualTo(18);
        assertThat(underTest.getMonth()).isEqualTo(12);
        assertThat(underTest.getYear()).isEqualTo(2020);
    }

    @Test
    public void gtfsDateShouldBeComparable() {
        GtfsDate firstGtfsDate = GtfsDate.fromEpochDay(18614);
        GtfsDate secondGtfsDate = GtfsDate.fromEpochDay(20614);

        assertThat(firstGtfsDate.compareTo(secondGtfsDate)).isLessThan(0);
        assertThat(secondGtfsDate.compareTo(firstGtfsDate)).isAtLeast(1);
        assertThat(firstGtfsDate.compareTo(firstGtfsDate)).isEqualTo(0);
    }

    @Test
    public void firstDateShouldBeIdentifiedAsBeforeSecondDate() {
        GtfsDate firstGtfsDate = GtfsDate.fromEpochDay(18614);
        GtfsDate secondGtfsDate = GtfsDate.fromEpochDay(20614);

        assertThat(firstGtfsDate.isBefore(secondGtfsDate)).isTrue();
        assertThat(firstGtfsDate.isAfter(secondGtfsDate)).isFalse();
    }

    @Test
    public void secondDateShouldBeIdentifiedAsAfterFirstDate() {
        GtfsDate firstGtfsDate = GtfsDate.fromEpochDay(18614);
        GtfsDate secondGtfsDate = GtfsDate.fromEpochDay(20614);

        assertThat(secondGtfsDate.isAfter(firstGtfsDate)).isTrue();
        assertThat(secondGtfsDate.isBefore(firstGtfsDate)).isFalse();
    }

    @Test
    public void gtfsDateShouldNotBeIdentifiedAsBeforeItself() {
        GtfsDate underTest = GtfsDate.fromEpochDay(18614);
        assertThat(underTest.isBefore(underTest)).isFalse();
    }

    @Test
    public void gtfsDateShouldNotBeIdentifiedAsAfterItself() {
        GtfsDate underTest = GtfsDate.fromEpochDay(18614);
        assertThat(underTest.isAfter(underTest)).isFalse();
    }
}
