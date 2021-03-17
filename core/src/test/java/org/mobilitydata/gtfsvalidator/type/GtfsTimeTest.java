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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsTimeTest {
  @Test
  public void fromString() {
    assertThat(GtfsTime.fromString("12:20:30").getSecondsSinceMidnight())
        .isEqualTo(12 * 3600 + 20 * 60 + 30);
    assertThat(GtfsTime.fromString("2:34:12").getSecondsSinceMidnight())
        .isEqualTo(2 * 3600 + 34 * 60 + 12);
    assertThat(GtfsTime.fromString("101:34:12").getSecondsSinceMidnight())
        .isEqualTo(101 * 3600 + 34 * 60 + 12);

    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("0"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("qwerty"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("midnight"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("1234:00:12"));
    assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("prefix4:00:12suffix"));
  }

  @Test
  public void fromHourMinuteSecondShouldReturnEntityWithCorrectData() {
    assertThat(GtfsTime.fromHourMinuteSecond(12, 20, 20).getSecond()).isEqualTo(20);
    assertThat(GtfsTime.fromHourMinuteSecond(12, 20, 20).getMinute()).isEqualTo(20);
    assertThat(GtfsTime.fromHourMinuteSecond(12, 20, 20).getHour()).isEqualTo(12);
  }

  @Test
  public void fromSecondsSinceMidnightShouldReturnEntityWithCorrectData() {
    assertThat(GtfsTime.fromSecondsSinceMidnight(20 * 60 + 20).getSecond()).isEqualTo(20);
    assertThat(GtfsTime.fromSecondsSinceMidnight(20 * 60 + 20).getMinute()).isEqualTo(20);
    assertThat(GtfsTime.fromSecondsSinceMidnight(20 * 60 + 20).getHour()).isEqualTo(0);
  }

  @Test
  public void toHHMMSS() {
    assertThat(GtfsTime.fromHourMinuteSecond(2, 20, 20).toHHMMSS()).matches("02:20:20");
    assertThat(GtfsTime.fromHourMinuteSecond(12, 20, 20).toHHMMSS()).matches("12:20:20");
    assertThat(GtfsTime.fromHourMinuteSecond(25, 20, 20).toHHMMSS()).matches("25:20:20");

    assertThat(GtfsTime.fromHourMinuteSecond(12, 20, 20).toString()).matches("12:20:20");
  }

  @Test
  public void gtfsTimeShouldBeComparable() {
    GtfsTime firstGtfsTime = GtfsTime.fromSecondsSinceMidnight(18614);
    GtfsTime secondGtfsTime = GtfsTime.fromSecondsSinceMidnight(20614);

    assertThat(firstGtfsTime.compareTo(secondGtfsTime)).isLessThan(0);
    assertThat(secondGtfsTime.compareTo(firstGtfsTime)).isAtLeast(1);
    assertThat(firstGtfsTime.compareTo(firstGtfsTime)).isEqualTo(0);
  }

  @Test
  public void firstTimeShouldBeIdentifiedAsBeforeSecondTime() {
    GtfsTime firstGtfsTime = GtfsTime.fromSecondsSinceMidnight(18614);
    GtfsTime secondGtfsTime = GtfsTime.fromSecondsSinceMidnight(20614);

    assertThat(firstGtfsTime.isBefore(secondGtfsTime)).isTrue();
    assertThat(firstGtfsTime.isAfter(secondGtfsTime)).isFalse();
  }

  @Test
  public void secondTimeShouldBeIdentifiedAsAfterFirstTime() {
    GtfsTime firstGtfsTime = GtfsTime.fromSecondsSinceMidnight(18614);
    GtfsTime secondGtfsTime = GtfsTime.fromSecondsSinceMidnight(20614);

    assertThat(secondGtfsTime.isAfter(firstGtfsTime)).isTrue();
    assertThat(secondGtfsTime.isBefore(firstGtfsTime)).isFalse();
  }

  @Test
  public void gtfsTimeShouldNotBeIdentifiedAsBeforeItself() {
    GtfsTime underTest = GtfsTime.fromSecondsSinceMidnight(18614);
    assertThat(underTest.isBefore(underTest)).isFalse();
  }

  @Test
  public void gtfsTimeShouldNotBeIdentifiedAsAfterItself() {
    GtfsTime underTest = GtfsTime.fromSecondsSinceMidnight(18614);
    assertThat(underTest.isAfter(underTest)).isFalse();
  }
}
