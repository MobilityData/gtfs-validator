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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.TripIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateStopTimeTripIdTest {
    // suppressed warning regarding unused result of method, since this behavior is wanted
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void tripIdInDatasetShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        // suppressed warning regarding unchecked type since it is not required here
        @SuppressWarnings("unchecked") final Map<String, Trip> mockTripCollection = mock(HashMap.class);
        when(mockTripCollection.containsKey("trip id")).thenReturn(true);

        final ValidateStopTimeTripId underTest = new ValidateStopTimeTripId();

        underTest.execute(mockResultRepo, mockStopTime, mockTripCollection);

        verify(mockTripCollection, times(1)).containsKey(ArgumentMatchers.eq("trip id"));
        verify(mockStopTime, times(1)).getTripId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockStopTime, mockTripCollection);
    }

    @Test
    void tripIdNotFoundShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(1);
        when(mockStopTime.getTripId()).thenReturn("trip id");

        // suppressed warning regarding unchecked type since it is not required here
        @SuppressWarnings("unchecked") final Map<String, Trip> mockTripCollection = mock(HashMap.class);
        when(mockTripCollection.containsKey("trip id")).thenReturn(false);

        final ValidateStopTimeTripId underTest = new ValidateStopTimeTripId();

        underTest.execute(mockResultRepo, mockStopTime, mockTripCollection);

        final ArgumentCaptor<TripIdNotFoundNotice> captor = ArgumentCaptor.forClass(TripIdNotFoundNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<TripIdNotFoundNotice> noticeList = captor.getAllValues();

        assertEquals("stop_times.txt", noticeList.get(0).getFilename());
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("trip_id", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip id", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(1, noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("trip id", noticeList.get(0).getNoticeSpecific(Notice.KEY_UNKNOWN_TRIP_ID));
    }
}
