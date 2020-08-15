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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.BackwardsTimeTravelInStopNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateBackwardsTimeTravelForStopsTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void validTimeCombinationShouldNotGenerateNotice() {
        // | tripId | stopSequence | arrivalTime | departureTime |
        // |--------|--------------|-------------|---------------|
        // | 0      | 2            | 07:10       | 09:20         |
        // | 0      | 5            | 9:45        | 10:11         |
        // | 0      | 8            | 11:44       | 12:01         |

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(945);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(1011);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(1144);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(945)).thenReturn("945");
        when(mockTimeUtils.convertIntegerToHMMSS(1011)).thenReturn("1011");
        when(mockTimeUtils.convertIntegerToHMMSS(1144)).thenReturn("1144");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getDepartureTime();
        verify(secondStopTimeInSequence, times(3)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(4)).getArrivalTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void arrivalTimeBeforeDepartureTimeShouldGenerateNotice() {
        // | tripId | stopSequence | arrivalTime | departureTime |
        // |--------|--------------|-------------|---------------|
        // | 0      | 2            | 07:10       | 09:20         |
        // | 0      | 5            | 09:10       | 10:11         |
        // | 0      | 8            | 10:20       | 12:01         |

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);
        when(firstStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(910);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(1011);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);
        when(secondStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(1020);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(910)).thenReturn("910");
        when(mockTimeUtils.convertIntegerToHMMSS(1011)).thenReturn("1011");
        when(mockTimeUtils.convertIntegerToHMMSS(1020)).thenReturn("1020");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");


        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getDepartureTime();
        verify(secondStopTimeInSequence, times(4)).getArrivalTime();
        verify(secondStopTimeInSequence, times(1)).getTripId();
        verify(secondStopTimeInSequence, times(1)).getStopSequence();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(4)).getArrivalTime();

        final ArgumentCaptor<BackwardsTimeTravelInStopNotice> captor =
                ArgumentCaptor.forClass(BackwardsTimeTravelInStopNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        BackwardsTimeTravelInStopNotice notice = captor.getValue();
        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("stopTimeTripId", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stopTimeStopSequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("0", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(5, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("910", notice.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME));
        assertEquals("920", notice.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME));
        assertEquals(2, notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE));
        assertEquals("no id", notice.getEntityId());

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                mockResultRepo);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullArrivalTimeWithinValidTimeCombinationShouldNotGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | null          | 10:00         |
        // | 0      | 8            | 10:20         | 12:01         |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(1000);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(1020);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(1000)).thenReturn("1000");
        when(mockTimeUtils.convertIntegerToHMMSS(1020)).thenReturn("1020");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(4)).getArrivalTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullDepartureTimeForValidTimeCombinationShouldNotGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | 09:50         | null          |
        // | 0      | 8            | 10:20         | 12:01         |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(950);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(null);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(1020);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(950)).thenReturn("950");
        when(mockTimeUtils.convertIntegerToHMMSS(1020)).thenReturn("1020");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(1)).getDepartureTime();
        verify(secondStopTimeInSequence, times(3)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(4)).getArrivalTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullArrivalTimeAndDepartureTimeForValidTimeCombinationShouldNotGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | null          | null          |
        // | 0      | 8            | 09:50         | 12:01         |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(null);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(950);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(950)).thenReturn("950");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(4)).getArrivalTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullArrivalTimeAndDepartureTimeForInvalidTimeCombinationShouldGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | null          | null          |
        // | 0      | 8            | 09:10         | 12:01         |

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(null);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(910);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(910)).thenReturn("910");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(1)).getTripId();
        verify(thirdStopTimeInSequence, times(1)).getStopSequence();
        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(5)).getArrivalTime();

        final ArgumentCaptor<BackwardsTimeTravelInStopNotice> captor =
                ArgumentCaptor.forClass(BackwardsTimeTravelInStopNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        BackwardsTimeTravelInStopNotice notice = captor.getValue();
        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("stopTimeTripId", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stopTimeStopSequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("0", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(8, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("910", notice.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME));
        assertEquals("920", notice.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME));
        assertEquals(2, notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE));
        assertEquals("no id", notice.getEntityId());

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                mockResultRepo);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullArrivalTimeForInvalidTimeCombinationShouldGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | null          | 08:45         | <- this row will be ignored since it is not valid
        // | 0      | 8            | 09:10         | 12:01         |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);
        when(firstStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(845);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);
        when(secondStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(910);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(845)).thenReturn("845");
        when(mockTimeUtils.convertIntegerToHMMSS(910)).thenReturn("910");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(2)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(5)).getArrivalTime();
        verify(thirdStopTimeInSequence, times(1)).getTripId();
        verify(thirdStopTimeInSequence, times(1)).getStopSequence();

        final ArgumentCaptor<BackwardsTimeTravelInStopNotice> captor =
                ArgumentCaptor.forClass(BackwardsTimeTravelInStopNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        BackwardsTimeTravelInStopNotice notice = captor.getValue();
        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("stopTimeTripId", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stopTimeStopSequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("0", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(8, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("910", notice.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME));
        assertEquals("920", notice.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME));
        assertEquals(2, notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE));
        assertEquals("no id", notice.getEntityId());

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                mockResultRepo);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nullDepartureTimeForInvalidTimeCombinationShouldGenerateNotice() {
        // | tripId | stopSequence | arrivalTime   | departureTime |
        // |--------|--------------|---------------|---------------|
        // | 0      | 2            | 07:10         | 09:20         |
        // | 0      | 5            | 09:30         | null          | <- this row will be ignored since it is not valid
        // | 0      | 8            | 08:20         | 12:01         |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);
        when(firstStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(930);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(null);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);
        when(secondStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(820);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);
        when(mockTimeUtils.convertIntegerToHMMSS(710)).thenReturn("710");
        when(mockTimeUtils.convertIntegerToHMMSS(920)).thenReturn("920");
        when(mockTimeUtils.convertIntegerToHMMSS(930)).thenReturn("930");
        when(mockTimeUtils.convertIntegerToHMMSS(820)).thenReturn("820");
        when(mockTimeUtils.convertIntegerToHMMSS(1201)).thenReturn("1201");

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(firstStopTimeInSequence, times(3)).getDepartureTime();
        verify(firstStopTimeInSequence, times(3)).getArrivalTime();

        verify(secondStopTimeInSequence, times(1)).getDepartureTime();
        verify(secondStopTimeInSequence, times(3)).getArrivalTime();

        verify(thirdStopTimeInSequence, times(3)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(5)).getArrivalTime();
        verify(thirdStopTimeInSequence, times(1)).getTripId();
        verify(thirdStopTimeInSequence, times(1)).getStopSequence();

        final ArgumentCaptor<BackwardsTimeTravelInStopNotice> captor =
                ArgumentCaptor.forClass(BackwardsTimeTravelInStopNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        BackwardsTimeTravelInStopNotice notice = captor.getValue();
        assertEquals("stop_times.txt", notice.getFilename());
        assertEquals("stopTimeTripId", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stopTimeStopSequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("0", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(8, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("820", notice.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME));
        assertEquals("920", notice.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME));
        assertEquals(2, notice.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE));
        assertEquals("no id", notice.getEntityId());

        verifyNoMoreInteractions(firstStopTimeInSequence, secondStopTimeInSequence, thirdStopTimeInSequence,
                mockResultRepo);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void firstNullDepartureAndArrivalTimeShouldNotGenerateNotice() {
        // | tripId | stopSequence | departureTime | arrivalTime |
        // |--------|--------------|---------------|-------------|
        // | 0      | 2            | null          | null        |
        // | 0      | 5            | 08:10         | 09:20       |
        // | 0      | 8            | 09:50         | 12:01       |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(null);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);
        when(firstStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(810);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);
        when(secondStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(950);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(1201);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(thirdStopTimeInSequence, times(1)).getDepartureTime();
        verify(thirdStopTimeInSequence, times(1)).getArrivalTime();
        verify(firstStopTimeInSequence, times(1)).getDepartureTime();

        verifyNoInteractions(secondStopTimeInSequence, mockResultRepo, mockTimeUtils);
        verifyNoMoreInteractions(firstStopTimeInSequence, thirdStopTimeInSequence);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void lastNullDepartureAndArrivalTimeShouldNotGenerateNotice() {
        // | tripId | stopSequence | departureTime | arrivalTime |
        // |--------|--------------|---------------|-------------|
        // | 0      | 2            | 07:10         | 07:20       |
        // | 0      | 5            | 08:10         | 09:20       |
        // | 0      | 8            | null          | null        |

        // Note that this is not a valid GTFS. Null check is implemented because of nullability of time fields in
        // stop_times.txt

        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getArrivalTime()).thenReturn(710);
        when(firstStopTimeInSequence.getDepartureTime()).thenReturn(720);
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(2);
        when(firstStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getArrivalTime()).thenReturn(810);
        when(secondStopTimeInSequence.getDepartureTime()).thenReturn(920);
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(5);
        when(secondStopTimeInSequence.getTripId()).thenReturn("0");

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getArrivalTime()).thenReturn(null);
        when(thirdStopTimeInSequence.getDepartureTime()).thenReturn(null);
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(8);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("0");

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final TreeMap<Integer, StopTime> mockStopTimeSequence = new TreeMap<>();
        mockStopTimeSequence.put(2, firstStopTimeInSequence);
        mockStopTimeSequence.put(5, secondStopTimeInSequence);
        mockStopTimeSequence.put(8, thirdStopTimeInSequence);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final ValidateBackwardsTimeTravelForStops underTest = new ValidateBackwardsTimeTravelForStops();
        underTest.execute(mockResultRepo, mockStopTimeSequence, mockTimeUtils);

        verify(thirdStopTimeInSequence, times(1)).getDepartureTime();

        verifyNoInteractions(secondStopTimeInSequence, mockResultRepo, mockTimeUtils);
        verifyNoMoreInteractions(firstStopTimeInSequence, thirdStopTimeInSequence);
    }
}
