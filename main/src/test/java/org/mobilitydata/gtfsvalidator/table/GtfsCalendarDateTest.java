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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class GtfsCalendarDateTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsCalendarDate.Builder builder = new GtfsCalendarDate.Builder();
        GtfsDate date = GtfsDate.fromEpochDay(356);

        GtfsCalendarDate underTest = builder
                .setServiceId("service id")
                .setDate(date)
                .setExceptionType(1)
                .build();

        assertThat(underTest.serviceId()).matches("service id");
        assertThat(underTest.date()).isEqualTo(date);
        assertThat(underTest.exceptionType()).isEqualTo(GtfsCalendarDateExceptionType.SERVICE_ADDED);

        assertThat(underTest.hasServiceId()).isTrue();
        assertThat(underTest.hasDate()).isTrue();
        assertThat(underTest.hasExceptionType()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsCalendarDate.Builder builder = new GtfsCalendarDate.Builder();

        GtfsCalendarDate underTest = builder
                .setServiceId(null)
                .setDate(null)
                .setExceptionType(null)
                .build();

        assertThat(underTest.serviceId()).matches(GtfsCalendarDate.DEFAULT_SERVICE_ID);
        assertThat(underTest.date()).isEqualTo(GtfsCalendarDate.DEFAULT_DATE);
        // exception_type is a required field and has no default value. Therefore, if field is not provided it will be
        // interpreted as unrecognized. No GtfsEntity will be created from such data: a notice will be created and added
        // to a notice container.
        assertThat(underTest.exceptionType()).isEqualTo(GtfsCalendarDateExceptionType.UNRECOGNIZED);

        assertThat(underTest.hasServiceId()).isFalse();
        assertThat(underTest.hasDate()).isFalse();
        assertThat(underTest.hasExceptionType()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsCalendarDate.Builder builder = new GtfsCalendarDate.Builder();
        GtfsDate date = GtfsDate.fromEpochDay(356);

        builder.setServiceId("service id")
                .setDate(date)
                .setExceptionType(1);

        builder.clear();
        GtfsCalendarDate underTest = builder.build();

        assertThat(underTest.serviceId()).matches(GtfsCalendarDate.DEFAULT_SERVICE_ID);
        assertThat(underTest.date()).isEqualTo(GtfsCalendarDate.DEFAULT_DATE);
        // exception_type is a required field and has no default value. Therefore, if field is not provided it will be
        // interpreted as unrecognized. No GtfsEntity will be created from such data: a notice will be created and added
        // to a notice container.
        assertThat(underTest.exceptionType()).isEqualTo(GtfsCalendarDateExceptionType.UNRECOGNIZED);

        assertThat(underTest.hasServiceId()).isFalse();
        assertThat(underTest.hasDate()).isFalse();
        assertThat(underTest.hasExceptionType()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsCalendarDate.Builder builder = new GtfsCalendarDate.Builder();

        GtfsCalendarDate underTest = builder.build();

        assertThat(underTest.serviceId()).isNull();
        assertThat(underTest.date()).isNull();
        // exception_type is a required field and has no default value. Therefore, if field is not provided it will be
        // interpreted as unrecognized. No GtfsEntity will be created from such data: a notice will be created and added
        // to a notice container.
        assertThat(underTest.exceptionType()).isEqualTo(GtfsCalendarDateExceptionType.UNRECOGNIZED);

        assertThat(underTest.hasServiceId()).isFalse();
        assertThat(underTest.hasDate()).isFalse();
        assertThat(underTest.hasExceptionType()).isFalse();
    }
}
