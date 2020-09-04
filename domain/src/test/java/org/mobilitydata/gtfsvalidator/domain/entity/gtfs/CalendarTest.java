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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;

class CalendarTest {
    private static final String FILENAME = "calendar.txt";
    private static final String SERVICE_ID = "service_id";

    @Test
    public void createCalendarWithNullServiceIdShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        // suppressed warning regarding nullability of parameter used in method .serviceId for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(null)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals(SERVICE_ID, notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullStartDateShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .startDate for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(null)
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("start_date", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullEndDateShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        // suppressed warning regarding nullability of parameter used in method .endDate for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(null)
                .build();

        assertTrue(buildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);
        assertEquals(FILENAME, notice.getFilename());
        assertEquals("end_date", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("monday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
        assertTrue(buildResult.getData() instanceof List);
    }

    @Test
    void createCalendarWithNullMondayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .monday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(null)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("monday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("tuesday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createCalendarWithNullTuesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        // suppressed warning regarding nullability of parameter used in method .tuesday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(null)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();

        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("tuesday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();

        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("wednesday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullWednesdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .wednesday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(null)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("wednesday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);


        assertEquals(FILENAME, notice.getFilename());
        assertEquals("thursday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullThursdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .thursday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(null)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("thursday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("friday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullFridayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .friday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(null)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("friday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("saturday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullSaturdayValueShouldGenerateNotice() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .saturday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(null)
                .sunday(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("saturday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection
                = (List<IntegerFieldValueOutOfRangeNotice>) buildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);


        assertEquals(FILENAME, notice.getFilename());
        assertEquals("sunday", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(SERVICE_ID, notice.getEntityId());
        assertEquals(3, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createCalendarWithNullSundayValueShouldThrowException() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();
        // suppressed warning regarding nullability of parameter used in method .sunday for the purpose of this test,
        // since this parameter is annotated as non null
        //noinspection ConstantConditions
        final EntityBuildResult<?> buildResult = underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(null)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection
                = (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("sunday", notice.getNoticeSpecific(KEY_FIELD_NAME));
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        assertTrue(buildResult.getData() instanceof Calendar);
    }

    @Test
    void disjointDatesShouldReturnFalseWhenCallingIsOverlapping() {
        // periods `a` and `b` are totally disjoint
        //  a |--------|            2020/01/01 -> 2020/01/31
        //              b  |----|   2020/03/01 -> 2020/04/30
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar calendarA = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 31))
                .build()
                .getData();

        final Calendar calendarB = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 3, 1))
                .endDate(LocalDate.of(2020, 4, 30))
                .build()
                .getData();

        assertFalse(calendarA.isOverlapping(calendarB));
        // permute periods `a` and `b`
        //            a  |----|    2020/03/01 -> 2020/04/30
        // b |--------|            2020/01/01 -> 2020/01/31
        assertFalse(calendarB.isOverlapping(calendarA));
    }

    @Test
    void periodContainedOtherPeriodShouldReturnTrueWhenCallingIsOverlapping() {
        // periods `b` is contained within period `a`
        //  a |--------| 2020/01/01 -> 2020/12/31
        //   b  |----|   2020/03/01 -> 2020/04/30
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar calendarA = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .build()
                .getData();

        final Calendar calendarB = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 3, 1))
                .endDate(LocalDate.of(2020, 4, 30))
                .build()
                .getData();

        assertTrue(calendarA.isOverlapping(calendarB));

        // permute periods `a` and `b` in method call
        // periods `b` is contained within period `a`
        //   b  |----|   2020/03/01 -> 2020/04/30
        //  a |--------| 2020/01/01 -> 2020/12/31
        assertTrue(calendarB.isOverlapping(calendarA));
    }

    @Test
    void partiallyOverlappingPeriodsWithCommonStartDateShouldReturnTrueWhenCallingIsOverlapping() {
        // periods `a` and `b` share same start date
        //  a |--------| 2020/01/01 -> 2020/12/31
        //  b |----|     2020/01/01 -> 2020/04/30
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar calendarA = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .build()
                .getData();

        final Calendar calendarB = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 4, 30))
                .build()
                .getData();

        assertTrue(calendarA.isOverlapping(calendarB));

        // permute periods `a` and `b` in method call
        //  b |----|     2020/01/01 -> 2020/04/30
        //  a |--------| 2020/01/01 -> 2020/12/31
        assertTrue(calendarB.isOverlapping(calendarA));
    }

    @Test
    void partiallyOverlappingPeriodsWithCommonEndDateShouldReturnTrueWhenCallingIsOverlapping() {
        // periods `a` and `b` share same end date
        //  a |--------|     2020/01/01 -> 2020/12/31
        //      b |----|     2020/05/06 -> 2020/12/31
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar calendarA = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 12, 31))
                .build()
                .getData();

        final Calendar calendarB = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 5, 6))
                .endDate(LocalDate.of(2020, 12, 31))
                .build()
                .getData();

        assertTrue(calendarA.isOverlapping(calendarB));

        // permute periods `a` and `b` in method call
        //      b |----|     2020/05/06 -> 2020/12/31
        //  a |--------|     2020/01/01 -> 2020/12/31
        assertTrue(calendarB.isOverlapping(calendarA));
    }

    @Test
    void partiallyOverlappingPeriodsShouldReturnTrueWhenCallingIsOverlapping() {
        // periods `a` and `b` partially overlap
        //  a |--------|     2020/01/01 -> 2020/06/30
        //      b |-------|  2020/05/06 -> 2020/12/31
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar calendarA = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 6, 30))
                .build()
                .getData();

        final Calendar calendarB = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 5, 6))
                .endDate(LocalDate.of(2020, 12, 31))
                .build()
                .getData();

        assertTrue(calendarA.isOverlapping(calendarB));

        // permute periods `a` and `b` in method call
        //      b |-------|  2020/05/06 -> 2020/12/31
        //  a |--------|     2020/01/01 -> 2020/06/30
        assertTrue(calendarB.isOverlapping(calendarA));
    }

    @Test
    void getOverlappingDaysShouldReturnCollectionOfCommonOperationDays() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar firstCalendar = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(1)
                .wednesday(0)
                .thursday(0)
                .friday(1)
                .saturday(0)
                .sunday(1)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 31))
                .build()
                .getData();

        final Calendar nonOverlappingCalendar = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(1)
                .thursday(0)
                .friday(1)
                .saturday(0)
                .sunday(1)
                .startDate(LocalDate.of(2020, 3, 1))
                .endDate(LocalDate.of(2020, 4, 30))
                .build()
                .getData();

        assertEquals(firstCalendar.getOverlappingDays(nonOverlappingCalendar).size(), 2);
        assertTrue(firstCalendar.getOverlappingDays(nonOverlappingCalendar).contains("friday"));
        assertTrue(firstCalendar.getOverlappingDays(nonOverlappingCalendar).contains("sunday"));
    }

    @Test
    void getOverlappingDaysShouldReturnEmptyListIfNoCommonOperationDays() {
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder();

        final Calendar firstCalendar = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(1)
                .saturday(1)
                .sunday(1)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 31))
                .build()
                .getData();

        final Calendar nonOverlappingCalendar = (Calendar) underTest.serviceId(SERVICE_ID)
                .monday(1)
                .tuesday(1)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDate.of(2020, 3, 1))
                .endDate(LocalDate.of(2020, 4, 30))
                .build()
                .getData();

        assertEquals(0, firstCalendar.getOverlappingDays(nonOverlappingCalendar).size());
    }
}