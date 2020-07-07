/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ProcessParsedStopTimeTest {

    @Test
    void validParsedStopTimeShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final StopTime mockStopTime = mock(StopTime.class);

        final StopTime.StopTimeBuilder mockBuilder = mock(StopTime.StopTimeBuilder.class, RETURNS_SELF);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        doReturn(true).when(mockEntityBuildResult).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockStopTime).when(mockEntityBuildResult).getData();

        when(mockDataRepo.addStopTime(mockStopTime)).thenReturn(mockStopTime);

        final ParsedEntity mockParsedEntity = mock(ParsedEntity.class);
        when(mockParsedEntity.get("trip_id")).thenReturn("trip_id");
        when(mockParsedEntity.get("arrival_time")).thenReturn("arrival time");
        when(mockParsedEntity.get("departure_time")).thenReturn("departure time");
        when(mockParsedEntity.get("stop_id")).thenReturn("stop_id");
        when(mockParsedEntity.get("stop_sequence")).thenReturn(3);
        when(mockParsedEntity.get("stop_headsign")).thenReturn("stop_headsign");
        when(mockParsedEntity.get("pickup_type")).thenReturn(1);
        when(mockParsedEntity.get("drop_off_type")).thenReturn(0);
        when(mockParsedEntity.get("continuous_pickup")).thenReturn(0);
        when(mockParsedEntity.get("continuous_drop_off")).thenReturn(2);
        when(mockParsedEntity.get("shape_dist_traveled")).thenReturn(30f);
        when(mockParsedEntity.get("timepoint")).thenReturn(0);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("arrival time")))
                .thenReturn(34);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("departure time")))
                .thenReturn(45);

        final ProcessParsedStopTime underTest = new ProcessParsedStopTime(mockResultRepo, mockDataRepo, mockTimeUtil,
                mockBuilder);

        underTest.execute(mockParsedEntity);

        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("arrival_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("departure_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_sequence"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_headsign"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("pickup_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("drop_off_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_pickup"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_drop_off"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("shape_dist_traveled"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("timepoint"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1)).arrivalTime(ArgumentMatchers.eq(34));
        verify(mockBuilder, times(1)).departureTime(ArgumentMatchers.eq(45));
        verify(mockBuilder, times(1)).stopId(ArgumentMatchers.eq("stop_id"));
        verify(mockBuilder, times(1)).stopSequence(ArgumentMatchers.eq(3));
        verify(mockBuilder, times(1)).stopHeadsign(ArgumentMatchers.eq("stop_headsign"));
        verify(mockBuilder, times(1)).pickupType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).dropOffType(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousPickup(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousDropOff(ArgumentMatchers.eq(2));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(30f));
        verify(mockBuilder, times(1)).timepoint(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).build();

        verify(mockEntityBuildResult, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockEntityBuildResult, times(1)).getData();

        verify(mockDataRepo, times(1)).addStopTime(ArgumentMatchers.eq(mockStopTime));

        verifyNoMoreInteractions(mockBuilder, mockEntityBuildResult, mockDataRepo, mockParsedEntity,
                mockEntityBuildResult, mockStopTime);
        verifyNoInteractions(mockResultRepo);
    }

    @Test
    void invalidParsedStopTimeShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        final List<Notice> mockNoticeCollection = new ArrayList<>(List.of(mockNotice));

        final StopTime.StopTimeBuilder mockBuilder = mock(StopTime.StopTimeBuilder.class, RETURNS_SELF);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        doReturn(false).when(mockEntityBuildResult).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockEntityBuildResult).getData();

        final ParsedEntity mockParsedEntity = mock(ParsedEntity.class);
        when(mockParsedEntity.get("trip_id")).thenReturn(null);
        when(mockParsedEntity.get("arrival_time")).thenReturn("arrival time");
        when(mockParsedEntity.get("departure_time")).thenReturn("departure time");
        when(mockParsedEntity.get("stop_id")).thenReturn("stop_id");
        when(mockParsedEntity.get("stop_sequence")).thenReturn(3);
        when(mockParsedEntity.get("stop_headsign")).thenReturn("stop_headsign");
        when(mockParsedEntity.get("pickup_type")).thenReturn(1);
        when(mockParsedEntity.get("drop_off_type")).thenReturn(0);
        when(mockParsedEntity.get("continuous_pickup")).thenReturn(0);
        when(mockParsedEntity.get("continuous_drop_off")).thenReturn(2);
        when(mockParsedEntity.get("shape_dist_traveled")).thenReturn(30f);
        when(mockParsedEntity.get("timepoint")).thenReturn(0);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("arrival time")))
                .thenReturn(34);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("departure time")))
                .thenReturn(45);

        final ProcessParsedStopTime underTest = new ProcessParsedStopTime(mockResultRepo, mockDataRepo, mockTimeUtil,
                mockBuilder);

        underTest.execute(mockParsedEntity);

        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("arrival_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("departure_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_sequence"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_headsign"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("pickup_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("drop_off_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_pickup"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_drop_off"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("shape_dist_traveled"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("timepoint"));

        verify(mockBuilder, times(1)).clear();
        // parameter of method .tripId is annotated as non null, for the purpose of this test, we suppress the
        // warning resulting form passing null value to method.
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).arrivalTime(ArgumentMatchers.eq(34));
        verify(mockBuilder, times(1)).departureTime(ArgumentMatchers.eq(45));
        verify(mockBuilder, times(1)).stopId(ArgumentMatchers.eq("stop_id"));
        verify(mockBuilder, times(1)).stopSequence(ArgumentMatchers.eq(3));
        verify(mockBuilder, times(1)).stopHeadsign(ArgumentMatchers.eq("stop_headsign"));
        verify(mockBuilder, times(1)).pickupType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).dropOffType(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousPickup(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousDropOff(ArgumentMatchers.eq(2));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(30f));
        verify(mockBuilder, times(1)).timepoint(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).build();

        verify(mockEntityBuildResult, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockEntityBuildResult, times(1)).getData();

        verify(mockResultRepo, times(1))
                .addNotice(ArgumentMatchers.isA(MissingRequiredValueNotice.class));

        verifyNoMoreInteractions(mockBuilder, mockEntityBuildResult, mockParsedEntity, mockResultRepo,
                mockEntityBuildResult, mockNotice);
        verifyNoInteractions(mockDataRepo);
    }

    @Test
    void duplicateParsedStopTimeShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getStopTimeMappingKey()).thenReturn("stop time key");

        final StopTime.StopTimeBuilder mockBuilder = mock(StopTime.StopTimeBuilder.class, RETURNS_SELF);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        doReturn(true).when(mockEntityBuildResult).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockStopTime).when(mockEntityBuildResult).getData();

        when(mockDataRepo.addStopTime(mockStopTime)).thenReturn(null);

        final ParsedEntity mockParsedEntity = mock(ParsedEntity.class);
        when(mockParsedEntity.get("trip_id")).thenReturn("trip_id");
        when(mockParsedEntity.get("arrival_time")).thenReturn("arrival time");
        when(mockParsedEntity.get("departure_time")).thenReturn("departure time");
        when(mockParsedEntity.get("stop_id")).thenReturn("stop_id");
        when(mockParsedEntity.get("stop_sequence")).thenReturn(3);
        when(mockParsedEntity.get("stop_headsign")).thenReturn("stop_headsign");
        when(mockParsedEntity.get("pickup_type")).thenReturn(1);
        when(mockParsedEntity.get("drop_off_type")).thenReturn(0);
        when(mockParsedEntity.get("continuous_pickup")).thenReturn(0);
        when(mockParsedEntity.get("continuous_drop_off")).thenReturn(2);
        when(mockParsedEntity.get("shape_dist_traveled")).thenReturn(30f);
        when(mockParsedEntity.get("timepoint")).thenReturn(0);

        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("arrival time")))
                .thenReturn(34);
        when(mockTimeUtil.convertHHMMSSToIntFromNoonOfDayOfService(ArgumentMatchers.eq("departure time")))
                .thenReturn(45);

        final ProcessParsedStopTime underTest = new ProcessParsedStopTime(mockResultRepo, mockDataRepo, mockTimeUtil,
                mockBuilder);

        underTest.execute(mockParsedEntity);

        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("arrival_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("departure_time"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_id"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_sequence"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("stop_headsign"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("pickup_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("drop_off_type"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_pickup"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("continuous_drop_off"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("shape_dist_traveled"));
        verify(mockParsedEntity, times(1)).get(ArgumentMatchers.eq("timepoint"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1)).arrivalTime(ArgumentMatchers.eq(34));
        verify(mockBuilder, times(1)).departureTime(ArgumentMatchers.eq(45));
        verify(mockBuilder, times(1)).stopId(ArgumentMatchers.eq("stop_id"));
        verify(mockBuilder, times(1)).stopSequence(ArgumentMatchers.eq(3));
        verify(mockBuilder, times(1)).stopHeadsign(ArgumentMatchers.eq("stop_headsign"));
        verify(mockBuilder, times(1)).pickupType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).dropOffType(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousPickup(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).continuousDropOff(ArgumentMatchers.eq(2));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(30f));
        verify(mockBuilder, times(1)).timepoint(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).build();

        verify(mockEntityBuildResult, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockEntityBuildResult, times(1)).getData();

        verify(mockDataRepo, times(1)).addStopTime(mockStopTime);

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertNull(noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(3, noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockBuilder, mockEntityBuildResult, mockParsedEntity, mockResultRepo,
                mockEntityBuildResult, mockDataRepo);
    }
}
