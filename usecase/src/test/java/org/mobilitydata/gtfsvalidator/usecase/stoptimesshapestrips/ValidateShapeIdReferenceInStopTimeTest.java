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

package org.mobilitydata.gtfsvalidator.usecase.stoptimesshapestrips;

import org.mobilitydata.gtfsvalidator.usecase.crossvalidationusecase.stoptimesshapestrips.ValidateShapeIdReferenceInStopTime;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ShapeIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateShapeIdReferenceInStopTimeTest {

    @Test
    void nullStopTimeShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime nullStoptime = null;
        // suppressed warning regarding unchecked type since it is not required here
        // noinspection unchecked
        final Map<Integer, ShapePoint> mockShape = mock(TreeMap.class);
        final Trip mockTrip = mock(Trip.class);

        final ValidateShapeIdReferenceInStopTime underTest = new ValidateShapeIdReferenceInStopTime();

        underTest.execute(mockResultRepo, nullStoptime, mockShape, mockTrip);

        verifyNoInteractions(mockResultRepo, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void stopTimeWithNullShapeDistTraveledShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(null);
        // suppressed warning regarding unchecked type since it is not required here
        // noinspection unchecked
        final Map<Integer, ShapePoint> mockShape = mock(TreeMap.class);
        final Trip mockTrip = mock(Trip.class);

        final ValidateShapeIdReferenceInStopTime underTest = new ValidateShapeIdReferenceInStopTime();

        underTest.execute(mockResultRepo, mockStopTime, mockShape, mockTrip);

        verify(mockStopTime, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockStopTime, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullTripShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(340f);
        // suppressed warning regarding unchecked type since it is not required here
        // noinspection unchecked
        final Map<Integer, ShapePoint> mockShape = mock(TreeMap.class);

        // querying an non existing trip id returns null value, therefore mockTrip = null here to mock this behavior
        final Trip nullTrip = null;

        final ValidateShapeIdReferenceInStopTime underTest = new ValidateShapeIdReferenceInStopTime();

        underTest.execute(mockResultRepo, mockStopTime, mockShape, nullTrip);

        verify(mockStopTime, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockStopTime);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullShapeIdShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(340f);
        when(mockStopTime.getStopSequence()).thenReturn(3);

        // suppressed warning regarding unchecked type since it is not required here
        // noinspection unchecked
        final Map<Integer, ShapePoint> mockShape = mock(TreeMap.class);
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn(null);
        when(mockTrip.getTripId()).thenReturn("trip id");

        final ValidateShapeIdReferenceInStopTime underTest = new ValidateShapeIdReferenceInStopTime();

        underTest.execute(mockResultRepo, mockStopTime, mockShape, mockTrip);

        verify(mockTrip, times(1)).getTripId();
        verify(mockTrip, times(1)).getShapeId();

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockStopTime, times(1)).getStopSequence();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("trip id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockResultRepo, mockStopTime, mockShape, mockTrip);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullShapeShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(340f);
        when(mockStopTime.getStopSequence()).thenReturn(3);

        final Map<Integer, ShapePoint> nullShape = null;
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn("non existing shape id");
        when(mockTrip.getTripId()).thenReturn("trip id");

        final ValidateShapeIdReferenceInStopTime underTest = new ValidateShapeIdReferenceInStopTime();

        underTest.execute(mockResultRepo, mockStopTime, nullShape, mockTrip);

        verify(mockTrip, times(1)).getTripId();
        verify(mockTrip, times(1)).getShapeId();

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verify(mockStopTime, times(1)).getStopSequence();

        final ArgumentCaptor<ShapeIdNotFoundNotice> captor =
                ArgumentCaptor.forClass(ShapeIdNotFoundNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<ShapeIdNotFoundNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(3, noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockResultRepo, mockStopTime, mockTrip);
    }
}
