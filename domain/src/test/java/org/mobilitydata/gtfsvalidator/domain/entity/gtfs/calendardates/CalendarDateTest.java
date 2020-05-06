/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarDateTest {

    @Test
    void createCalendarDateWithValidValueShouldNotGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(1)
                .build();
        assertTrue(entityBuildResult.getData() instanceof CalendarDate);
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullServiceIdShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId(null)
                .date(LocalDateTime.now())
                .exceptionType(1)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("service_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field date is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullDateShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(null)
                .exceptionType(1)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("date", notice.getFieldName());
        assertEquals("service_id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field exceptionType is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullExceptionTypeShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(null)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("exception_type", notice.getFieldName());
        assertEquals("service_id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createCalendarDateWithInvalidExceptionTypeShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(5)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("exception_type", notice.getFieldName());
        assertEquals("service_id", notice.getEntityId());
        assertEquals("5", notice.getEnumValue());
        assertEquals(1, noticeCollection.size());
    }
}