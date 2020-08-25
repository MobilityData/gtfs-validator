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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DecreasingStopTimeDistanceErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.DecreasingStopTimeDistanceWarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateStopTimeIncreasingDistanceTest {

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void strictlyIncreasingDistanceInStopTimeWithoutNullValuesShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(10f);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(15f);
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(20f);

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void strictlyIncreasingDistanceInStopTimeWithNullValuesShouldNotGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(20f);

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void decreasingDistanceInStopTimeWithoutNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(10f);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(15f);
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(13f);
        when(fourthStopTimeInSequence.getTripId()).thenReturn("trip id value");

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getTripId();

        final ArgumentCaptor<DecreasingStopTimeDistanceErrorNotice> captor =
                ArgumentCaptor.forClass(DecreasingStopTimeDistanceErrorNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final DecreasingStopTimeDistanceErrorNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id value", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(3, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(15f, notice.getNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED));
        assertEquals(13f, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_SHAPE_DIST_TRAVELED));
        assertEquals(4, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_STOP_SEQUENCE));

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void decreasingDistanceInStopTimeWithNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(2f);
        when(fourthStopTimeInSequence.getTripId()).thenReturn("trip id value");

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getTripId();

        final ArgumentCaptor<DecreasingStopTimeDistanceErrorNotice> captor =
                ArgumentCaptor.forClass(DecreasingStopTimeDistanceErrorNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final DecreasingStopTimeDistanceErrorNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id value", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED));
        assertEquals(2f, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_SHAPE_DIST_TRAVELED));
        assertEquals(4, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_STOP_SEQUENCE));

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void equalDistanceInStopTimeWithoutNullValuesShouldGenerateWarningErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(10f);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(10f);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("trip id value");
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(22f);

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getTripId();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingStopTimeDistanceWarningNotice> captor =
                ArgumentCaptor.forClass(DecreasingStopTimeDistanceWarningNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final DecreasingStopTimeDistanceWarningNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id value", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(2, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(10f, notice.getNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED));
        assertEquals(10f, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_SHAPE_DIST_TRAVELED));
        assertEquals(3, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_STOP_SEQUENCE));

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void equalDistanceInStopTimeWithNullValuesShouldGenerateWarningNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getShapeDistTraveled()).thenReturn(null);
        final StopTime fourthStopTimeInSequence = mock(StopTime.class);
        when(fourthStopTimeInSequence.getShapeDistTraveled()).thenReturn(5f);
        when(fourthStopTimeInSequence.getTripId()).thenReturn("trip id value");

        final TreeMap<Integer, StopTime> stopTimeSequence = new TreeMap<>();
        stopTimeSequence.put(1, firstStopTimeInSequence);
        stopTimeSequence.put(2, secondStopTimeInSequence);
        stopTimeSequence.put(3, thirdStopTimeInSequence);
        stopTimeSequence.put(4, fourthStopTimeInSequence);

        final ValidateStopTimeIncreasingDistance underTest =
                new ValidateStopTimeIncreasingDistance(mockResultRepo);

        underTest.execute(stopTimeSequence);

        verify(firstStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(secondStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(thirdStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getShapeDistTraveled();
        verify(fourthStopTimeInSequence, times(1)).getTripId();

        final ArgumentCaptor<DecreasingStopTimeDistanceWarningNotice> captor =
                ArgumentCaptor.forClass(DecreasingStopTimeDistanceWarningNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final DecreasingStopTimeDistanceWarningNotice notice = captor.getAllValues().get(0);

        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id value", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(1, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED));
        assertEquals(5f, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_SHAPE_DIST_TRAVELED));
        assertEquals(4, notice.getNoticeSpecific(KEY_STOP_TIME_CONFLICTING_STOP_SEQUENCE));

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                fourthStopTimeInSequence, mockResultRepo);
    }
}
