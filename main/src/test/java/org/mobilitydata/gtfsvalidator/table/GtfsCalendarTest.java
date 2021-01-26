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
import static org.mobilitydata.gtfsvalidator.table.GtfsCalendar.DEFAULT_END_DATE;
import static org.mobilitydata.gtfsvalidator.table.GtfsCalendar.DEFAULT_SERVICE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsCalendar.DEFAULT_START_DATE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@RunWith(JUnit4.class)
public class GtfsCalendarTest {
  @Test
  public void shouldReturnFieldValues() {
    GtfsCalendar.Builder builder = new GtfsCalendar.Builder();
    GtfsDate startDate = GtfsDate.fromEpochDay(356);
    GtfsDate endDate = GtfsDate.fromEpochDay(450);

    GtfsCalendar underTest =
        builder
            .setServiceId("service id")
            .setMonday(1)
            .setTuesday(1)
            .setWednesday(1)
            .setThursday(1)
            .setFriday(1)
            .setSaturday(1)
            .setSunday(1)
            .setStartDate(startDate)
            .setEndDate(endDate)
            .build();

    assertThat(underTest.serviceId()).matches("service id");
    assertThat(underTest.monday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.tuesday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.wednesday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.thursday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.friday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.saturday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.sunday()).isEqualTo(GtfsCalendarService.AVAILABLE);
    assertThat(underTest.startDate()).isEqualTo(startDate);
    assertThat(underTest.endDate()).isEqualTo(endDate);

    assertThat(underTest.hasServiceId()).isTrue();
    assertThat(underTest.hasMonday()).isTrue();
    assertThat(underTest.hasTuesday()).isTrue();
    assertThat(underTest.hasWednesday()).isTrue();
    assertThat(underTest.hasThursday()).isTrue();
    assertThat(underTest.hasFriday()).isTrue();
    assertThat(underTest.hasSaturday()).isTrue();
    assertThat(underTest.hasSunday()).isTrue();
    assertThat(underTest.hasStartDate()).isTrue();
    assertThat(underTest.hasEndDate()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    GtfsCalendar.Builder builder = new GtfsCalendar.Builder();

    GtfsCalendar underTest =
        builder
            .setServiceId(null)
            .setMonday(null)
            .setTuesday(null)
            .setWednesday(null)
            .setThursday(null)
            .setFriday(null)
            .setSaturday(null)
            .setSunday(null)
            .setStartDate(null)
            .setEndDate(null)
            .build();

    assertThat(underTest.serviceId()).matches(DEFAULT_SERVICE_ID);
    assertThat(underTest.monday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.tuesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.wednesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.thursday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.friday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.saturday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.sunday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.startDate()).isEqualTo(DEFAULT_START_DATE);
    assertThat(underTest.endDate()).isEqualTo(DEFAULT_END_DATE);

    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasMonday()).isFalse();
    assertThat(underTest.hasTuesday()).isFalse();
    assertThat(underTest.hasWednesday()).isFalse();
    assertThat(underTest.hasThursday()).isFalse();
    assertThat(underTest.hasFriday()).isFalse();
    assertThat(underTest.hasSaturday()).isFalse();
    assertThat(underTest.hasSunday()).isFalse();
    assertThat(underTest.hasStartDate()).isFalse();
    assertThat(underTest.hasEndDate()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    GtfsCalendar.Builder builder = new GtfsCalendar.Builder();
    GtfsDate startDate = GtfsDate.fromEpochDay(356);
    GtfsDate endDate = GtfsDate.fromEpochDay(450);

    builder
        .setServiceId("service id")
        .setMonday(1)
        .setTuesday(1)
        .setWednesday(1)
        .setThursday(1)
        .setFriday(1)
        .setSaturday(1)
        .setSunday(1)
        .setStartDate(startDate)
        .setEndDate(endDate);

    builder.clear();
    GtfsCalendar underTest = builder.build();

    assertThat(underTest.serviceId()).matches(DEFAULT_SERVICE_ID);
    assertThat(underTest.monday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.tuesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.wednesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.thursday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.friday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.saturday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.sunday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.startDate()).isEqualTo(DEFAULT_START_DATE);
    assertThat(underTest.endDate()).isEqualTo(DEFAULT_END_DATE);

    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasMonday()).isFalse();
    assertThat(underTest.hasTuesday()).isFalse();
    assertThat(underTest.hasWednesday()).isFalse();
    assertThat(underTest.hasThursday()).isFalse();
    assertThat(underTest.hasFriday()).isFalse();
    assertThat(underTest.hasSaturday()).isFalse();
    assertThat(underTest.hasSunday()).isFalse();
    assertThat(underTest.hasStartDate()).isFalse();
    assertThat(underTest.hasEndDate()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    GtfsCalendar.Builder builder = new GtfsCalendar.Builder();

    GtfsCalendar underTest = builder.build();

    assertThat(underTest.serviceId()).isNull();
    assertThat(underTest.monday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.tuesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.wednesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.thursday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.friday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.saturday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.sunday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
    assertThat(underTest.startDate()).isNull();
    assertThat(underTest.endDate()).isNull();

    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasMonday()).isFalse();
    assertThat(underTest.hasTuesday()).isFalse();
    assertThat(underTest.hasWednesday()).isFalse();
    assertThat(underTest.hasThursday()).isFalse();
    assertThat(underTest.hasFriday()).isFalse();
    assertThat(underTest.hasSaturday()).isFalse();
    assertThat(underTest.hasSunday()).isFalse();
    assertThat(underTest.hasStartDate()).isFalse();
    assertThat(underTest.hasEndDate()).isFalse();
  }
}
