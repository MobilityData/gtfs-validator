/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsFrequency.DEFAULT_END_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsFrequency.DEFAULT_EXACT_TIMES;
import static org.mobilitydata.gtfsvalidator.table.GtfsFrequency.DEFAULT_HEADWAY_SECS;
import static org.mobilitydata.gtfsvalidator.table.GtfsFrequency.DEFAULT_START_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsFrequency.DEFAULT_TRIP_ID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class GtfsFrequencyTest {
  @Test
  public void shouldReturnFieldValues() {
    GtfsFrequency.Builder builder = new GtfsFrequency.Builder();
    GtfsTime startTime = GtfsTime.fromHourMinuteSecond(12, 34, 40);
    GtfsTime endTime = GtfsTime.fromHourMinuteSecond(12, 55, 40);
    GtfsFrequency underTest =
        builder
            .setTripId("trip id")
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setHeadwaySecs(12)
            .setExactTimes(0)
            .build();

    assertThat(underTest.tripId()).isEqualTo("trip id");
    assertThat(underTest.startTime()).isEqualTo(startTime);
    assertThat(underTest.endTime()).isEqualTo(endTime);
    assertThat(underTest.headwaySecs()).isEqualTo(12);
    assertThat(underTest.exactTimes()).isEqualTo(GtfsFrequencyExactTimes.FREQUENCY_BASED);

    assertThat(underTest.hasTripId()).isTrue();
    assertThat(underTest.hasStartTime()).isTrue();
    assertThat(underTest.hasEndTime()).isTrue();
    assertThat(underTest.hasHeadwaySecs()).isTrue();
    assertThat(underTest.hasExactTimes()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    GtfsFrequency.Builder builder = new GtfsFrequency.Builder();
    GtfsFrequency underTest =
        builder
            .setTripId(null)
            .setStartTime(null)
            .setEndTime(null)
            .setHeadwaySecs(null)
            .setExactTimes(null)
            .build();

    assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
    assertThat(underTest.startTime()).isEqualTo(DEFAULT_START_TIME);
    assertThat(underTest.endTime()).isEqualTo(DEFAULT_END_TIME);
    assertThat(underTest.headwaySecs()).isEqualTo(DEFAULT_HEADWAY_SECS);
    assertThat(underTest.exactTimes())
        .isEqualTo(GtfsFrequencyExactTimes.forNumber(DEFAULT_EXACT_TIMES));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasStartTime()).isFalse();
    assertThat(underTest.hasEndTime()).isFalse();
    assertThat(underTest.hasHeadwaySecs()).isFalse();
    assertThat(underTest.hasExactTimes()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    GtfsFrequency.Builder builder = new GtfsFrequency.Builder();
    GtfsTime startTime = GtfsTime.fromHourMinuteSecond(12, 34, 40);
    GtfsTime endTime = GtfsTime.fromHourMinuteSecond(12, 55, 40);
    builder
        .setTripId("trip id")
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setHeadwaySecs(12)
        .setExactTimes(0);

    builder.clear();
    GtfsFrequency underTest = builder.build();

    assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
    assertThat(underTest.startTime()).isEqualTo(DEFAULT_START_TIME);
    assertThat(underTest.endTime()).isEqualTo(DEFAULT_END_TIME);
    assertThat(underTest.headwaySecs()).isEqualTo(DEFAULT_HEADWAY_SECS);
    assertThat(underTest.exactTimes())
        .isEqualTo(GtfsFrequencyExactTimes.forNumber(DEFAULT_EXACT_TIMES));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasStartTime()).isFalse();
    assertThat(underTest.hasEndTime()).isFalse();
    assertThat(underTest.hasHeadwaySecs()).isFalse();
    assertThat(underTest.hasExactTimes()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    GtfsFrequency.Builder builder = new GtfsFrequency.Builder();
    GtfsFrequency underTest = builder.build();

    assertThat(underTest.tripId()).isNull();
    assertThat(underTest.startTime()).isNull();
    assertThat(underTest.endTime()).isNull();
    assertThat(underTest.headwaySecs()).isEqualTo(DEFAULT_HEADWAY_SECS);
    assertThat(underTest.exactTimes())
        .isEqualTo(GtfsFrequencyExactTimes.forNumber(DEFAULT_EXACT_TIMES));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasStartTime()).isFalse();
    assertThat(underTest.hasEndTime()).isFalse();
    assertThat(underTest.hasHeadwaySecs()).isFalse();
    assertThat(underTest.hasExactTimes()).isFalse();
  }
}
