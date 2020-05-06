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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarTest {
    private static final String FILENAME = "calendar.txt";
    private static final String SERVICE_ID = "service_id";

    @Test
    public void createCalendarWithNullServiceIdShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions to avoid lint
        final EntityBuildResult<?> buildResult = underTest.serviceId(null)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals(SERVICE_ID, notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullStartDateShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions to avoid lint
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(null)
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("start_date", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullEndDateShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(null)
                .build();

        assertTrue(buildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);
        assertEquals(FILENAME, notice.getFilename());
        assertEquals("end_date", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidMondayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(3)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("monday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
        assertTrue(buildResult.getData() instanceof List);
    }

    @Test
    void createCalendarWithNullMondayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(null)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("monday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidTuesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(3)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("tuesday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createCalendarWithNullTuesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(null)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("tuesday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidWednesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(3)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("wednesday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullWednesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(null)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("wednesday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidThursdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(3)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);


        assertEquals(FILENAME, notice.getFilename());
        assertEquals("thursday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullThursdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(null)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("thursday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidFridayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(3)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("friday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullFridayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(null)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("friday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidSaturdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(3)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("saturday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullSaturdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(null)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("saturday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithInvalidSundayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(3)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);


        assertEquals(FILENAME, notice.getFilename());
        assertEquals("sunday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getActualValue());
        assertEquals(1, notice.getRangeMax());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullSundayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("sunday", notice.getFieldName());
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithValidValuesShouldNotGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertTrue(buildResult.getData() instanceof Calendar);
    }
}