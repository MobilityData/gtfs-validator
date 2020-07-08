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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;


class FrequencyTest {
    private static final String STRING_TEST = "string test";
    private static final Integer VALID_START_TIME = 0;
    private static final Integer VALID_END_TIME = 3600;
    private static final Integer VALID_HEADWAY_SECS = 600;
    private static final Integer VALID_EXACT_TIMES_INTEGER = 0;

    @Test
    void createFrequencyWithValidValuesShouldNotGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(VALID_EXACT_TIMES_INTEGER);

        assertTrue(underTest.build().getData() instanceof Frequency);
    }

    @Test
    void createFrequencyWithNullTripIdShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(null)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertNull(notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(VALID_START_TIME, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithNullStartTimeShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(STRING_TEST)
                .startTime(null)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("start_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(STRING_TEST, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertNull(notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithNullEndTimeShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(null)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("end_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(STRING_TEST, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(VALID_START_TIME, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithNullHeadwaySecsShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(null)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("headway_secs", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(STRING_TEST, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(VALID_START_TIME, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithInvalidHeadwaySecsShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(-1800)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("headway_secs", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(STRING_TEST, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(VALID_START_TIME, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(0, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(Integer.MAX_VALUE, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(-1800, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithInvalidExactTimesShouldGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(2)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("frequencies.txt", notice.getFilename());
        assertEquals("exact_times", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("start_time", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(STRING_TEST, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(VALID_START_TIME, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(2, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFrequencyWithNullTransfersShouldNotGenerateNotice() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();

        underTest.tripId(STRING_TEST)
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(null);

        assertTrue(underTest.build().getData() instanceof Frequency);
    }

    @Test
    void getFrequencyMappingKeyShouldReturnStringOfConcatenatedFieldValues() {
        final Frequency.FrequencyBuilder underTest = new Frequency.FrequencyBuilder();
        final Frequency frequency = (Frequency) underTest.tripId("trip id")
                .startTime(VALID_START_TIME)
                .endTime(VALID_END_TIME)
                .headwaySecs(VALID_HEADWAY_SECS)
                .exactTimes(VALID_EXACT_TIMES_INTEGER)
                .build()
                .getData();

        assertEquals("trip id" + 0,
                frequency.getFrequencyMappingKey());
    }
}