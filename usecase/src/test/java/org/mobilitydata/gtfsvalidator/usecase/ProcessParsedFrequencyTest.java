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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedFrequencyTest {
    private static final String TRIP_ID = "trip_id";
    private static final String START_TIME = "start_time";
    private static final String START_TIME_VALUE = "12:00:00";
    private static final String END_TIME = "end_time";
    private static final String END_TIME_VALUE = "13:00:00";
    private static final String HEADWAY_SECS = "headway_secs";
    private static final Integer HEADWAY_SECS_VALUE = 600;
    private static final String EXACT_TIMES = "exact_times";
    private static final Integer EXACT_TIMES_VALUE = 0;

    @Test
    void validFrequencyEntityShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Frequency.FrequencyBuilder mockBuilder = mock(Frequency.FrequencyBuilder.class, RETURNS_SELF);
        final Frequency mockFrequency = mock(Frequency.class);
        final ParsedEntity mockParsedFrequency = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFrequency).when(mockEntityBuildResult).getData();

        when(mockParsedFrequency.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedFrequency.get(START_TIME)).thenReturn(START_TIME_VALUE);
        when(mockParsedFrequency.get(END_TIME)).thenReturn(END_TIME_VALUE);
        when(mockParsedFrequency.get(HEADWAY_SECS)).thenReturn(HEADWAY_SECS_VALUE);
        when(mockParsedFrequency.get(EXACT_TIMES)).thenReturn(EXACT_TIMES_VALUE);

        when(mockGtfsDataRepo.addFrequency(mockFrequency)).thenReturn(mockFrequency);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(START_TIME_VALUE)))
                .thenReturn(0);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(END_TIME_VALUE)))
                .thenReturn(3600);

        final ProcessParsedFrequency underTest =
                new ProcessParsedFrequency(mockResultRepo, mockGtfsDataRepo, mockTimeUtil, mockBuilder);

        underTest.execute(mockParsedFrequency);

        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(START_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(END_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(HEADWAY_SECS));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(EXACT_TIMES));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).tripId(TRIP_ID);
        verify(mockBuilder, times(1)).startTime(0);
        verify(mockBuilder, times(1)).endTime(3600);
        verify(mockBuilder, times(1)).headwaySecs(HEADWAY_SECS_VALUE);
        verify(mockBuilder, times(1)).exactTimes(EXACT_TIMES_VALUE);
        verify(mockBuilder, times(1)).build();
        verify(mockGtfsDataRepo, times(1)).addFrequency(ArgumentMatchers.eq(mockFrequency));

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockBuilder, mockFrequency, mockParsedFrequency, mockGtfsDataRepo);
    }

    @Test
    void invalidFrequencyEntityShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Frequency.FrequencyBuilder mockBuilder = mock(Frequency.FrequencyBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFrequency = mock(ParsedEntity.class);
        final Notice mockNotice = mock(Notice.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        noticeCollection.add(mockNotice);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGenericObject.isSuccess()).thenReturn(false);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(noticeCollection).when(mockGenericObject).getData();

        final ProcessParsedFrequency underTest =
                new ProcessParsedFrequency(mockResultRepo, mockGtfsDataRepo, mockTimeUtil, mockBuilder);

        when(mockParsedFrequency.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedFrequency.get(START_TIME)).thenReturn(START_TIME_VALUE);
        when(mockParsedFrequency.get(END_TIME)).thenReturn(END_TIME_VALUE);
        when(mockParsedFrequency.get(HEADWAY_SECS)).thenReturn(HEADWAY_SECS_VALUE);
        when(mockParsedFrequency.get(EXACT_TIMES)).thenReturn(EXACT_TIMES_VALUE);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(START_TIME_VALUE)))
                .thenReturn(0);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(END_TIME_VALUE)))
                .thenReturn(3600);

        underTest.execute(mockParsedFrequency);

        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(START_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(END_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(HEADWAY_SECS));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(EXACT_TIMES));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).tripId(TRIP_ID);
        verify(mockBuilder, times(1)).startTime(0);
        verify(mockBuilder, times(1)).endTime(3600);
        verify(mockBuilder, times(1)).headwaySecs(HEADWAY_SECS_VALUE);
        verify(mockBuilder, times(1)).exactTimes(EXACT_TIMES_VALUE);
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();
        verify(mockResultRepo, times(1)).addNotice(ArgumentMatchers.eq(mockNotice));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        verifyNoInteractions(mockGtfsDataRepo);
        verifyNoMoreInteractions(mockParsedFrequency, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    void duplicateFrequencyEntityShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Frequency.FrequencyBuilder mockBuilder = mock(Frequency.FrequencyBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFrequency = mock(ParsedEntity.class);
        when(mockParsedFrequency.getEntityId()).thenReturn("entity id");
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final Frequency mockFrequency = mock(Frequency.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFrequency).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addFrequency(mockFrequency)).thenReturn(null);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(START_TIME_VALUE)))
                .thenReturn(0);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq(END_TIME_VALUE)))
                .thenReturn(3600);

        final ProcessParsedFrequency underTest =
                new ProcessParsedFrequency(mockResultRepo, mockGtfsDataRepo, mockTimeUtil, mockBuilder);

        when(mockParsedFrequency.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedFrequency.get(START_TIME)).thenReturn(START_TIME_VALUE);
        when(mockParsedFrequency.get(END_TIME)).thenReturn(END_TIME_VALUE);
        when(mockParsedFrequency.get(HEADWAY_SECS)).thenReturn(HEADWAY_SECS_VALUE);
        when(mockParsedFrequency.get(EXACT_TIMES)).thenReturn(EXACT_TIMES_VALUE);

        underTest.execute(mockParsedFrequency);

        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(START_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(END_TIME));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(HEADWAY_SECS));
        verify(mockParsedFrequency, times(1)).get(ArgumentMatchers.eq(EXACT_TIMES));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).tripId(TRIP_ID);
        verify(mockBuilder, times(1)).startTime(0);
        verify(mockBuilder, times(1)).endTime(3600);
        verify(mockBuilder, times(1)).headwaySecs(HEADWAY_SECS_VALUE);
        verify(mockBuilder, times(1)).exactTimes(EXACT_TIMES_VALUE);
        verify(mockBuilder, times(1)).build();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFrequency, times(1)).getEntityId();
        verify(mockGtfsDataRepo, times(1)).addFrequency(ArgumentMatchers.eq(mockFrequency));

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("frequencies.txt", noticeList.get(0).getFilename());
        assertEquals("trip_id; start_time",
                noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFrequency, mockResultRepo, mockGtfsDataRepo, mockFrequency, mockBuilder,
                mockGenericObject);
    }
}