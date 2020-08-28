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

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OverlappingTripFrequenciesNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateTripFrequenciesOverlapTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void overlappingFrequenciesShouldGenerateNotice() {
        // frequencies.txt
        // | trip_id | start_time | end_time | headway_secs | exact_time |
        // |---------|------------|----------|--------------|------------|
        // | 12      | 12:00      | 18:00    | 600          | 0          |
        // | 12      | 15:44      | 21:43    | 400          | 1          | this row overlaps with the previous one
        // | 12      | 03:44      | 08:45    | 200          | 0          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);
        when(mockTimeUtil.convertIntegerToHMMSS(1200)).thenReturn("1200");
        when(mockTimeUtil.convertIntegerToHMMSS(1800)).thenReturn("1800");
        when(mockTimeUtil.convertIntegerToHMMSS(1544)).thenReturn("1544");
        when(mockTimeUtil.convertIntegerToHMMSS(2143)).thenReturn("2143");
        when(mockTimeUtil.arePeriodsOverlapping(1200, 1800,
                1544, 2143)).thenReturn(true);
        when(mockTimeUtil.arePeriodsOverlapping(1200, 1800,
                344, 845)).thenReturn(false);
        when(mockTimeUtil.arePeriodsOverlapping(1544, 2143,
                344, 845)).thenReturn(false);

        final Frequency firstFrequency = mock(Frequency.class);
        when(firstFrequency.getStartTime()).thenReturn(1200);
        when(firstFrequency.getEndTime()).thenReturn(1800);
        when(firstFrequency.getFrequencyMappingKey()).thenReturn("121200");
        final Frequency secondFrequency = mock(Frequency.class);
        when(secondFrequency.getStartTime()).thenReturn(1544);
        when(secondFrequency.getEndTime()).thenReturn(2143);
        when(secondFrequency.getFrequencyMappingKey()).thenReturn("121544");
        final Frequency thirdFrequency = mock(Frequency.class);
        when(thirdFrequency.getStartTime()).thenReturn(344);
        when(thirdFrequency.getEndTime()).thenReturn(845);
        when(thirdFrequency.getFrequencyMappingKey()).thenReturn("120344");

        final Map<String, List<Frequency>> frequenciesPerTripIdCollection = new HashMap<>();
        final List<Frequency> tripFrequencyCollection = new ArrayList<>();
        tripFrequencyCollection.add(firstFrequency);
        tripFrequencyCollection.add(secondFrequency);
        tripFrequencyCollection.add(thirdFrequency);
        frequenciesPerTripIdCollection.put("12", tripFrequencyCollection);
        when(mockDataRepo.getFrequencyAllByTripId()).thenReturn(frequenciesPerTripIdCollection);

        final ValidateTripFrequenciesOverlap underTest =
                new ValidateTripFrequenciesOverlap(mockDataRepo, mockResultRepo, mockTimeUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E053 - Trip frequencies overlap'");

        verify(mockDataRepo, times(1)).getFrequencyAllByTripId();
        verify(firstFrequency, times(4)).getFrequencyMappingKey();
        verify(firstFrequency, times(1)).getStartTime();
        verify(firstFrequency, times(1)).getEndTime();

        verify(secondFrequency, times(4)).getFrequencyMappingKey();
        verify(secondFrequency, times(2)).getStartTime();
        verify(secondFrequency, times(2)).getEndTime();

        verify(thirdFrequency, times(4)).getFrequencyMappingKey();
        verify(thirdFrequency, times(3)).getEndTime();
        verify(thirdFrequency, times(3)).getStartTime();

        verify(mockTimeUtil, times(1)).convertIntegerToHMMSS(1200);
        verify(mockTimeUtil, times(1)).convertIntegerToHMMSS(1800);
        verify(mockTimeUtil, times(1)).convertIntegerToHMMSS(1544);
        verify(mockTimeUtil, times(1)).convertIntegerToHMMSS(2143);
        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1200, 1800,
                        1544, 2143);
        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1200, 1800,
                        344, 845);
        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1544, 2143,
                        344, 845);

        final ArgumentCaptor<OverlappingTripFrequenciesNotice> captor =
                ArgumentCaptor.forClass(OverlappingTripFrequenciesNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<OverlappingTripFrequenciesNotice> noticeList = captor.getAllValues();

        assertEquals("frequencies.txt", noticeList.get(0).getFilename());
        assertEquals("tripId", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("startTime", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("12", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("1200", noticeList.get(0).getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("1800", noticeList.get(0).getNoticeSpecific(Notice.KEY_FREQUENCY_END_TIME));
        assertEquals("1544", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FREQUENCY_START_TIME));
        assertEquals("2143", noticeList.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FREQUENCY_END_TIME));

        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockTimeUtil, firstFrequency,
                secondFrequency, thirdFrequency);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void nonOverlappingFrequenciesShouldNotGenerateNotice() {
        // frequencies.txt -- no overlap
        // | trip_id | start_time | end_time | headway_secs | exact_time |
        // |---------|------------|----------|--------------|------------|
        // | 12      | 12:00      | 18:00    | 600          | 0          |
        // | 12      | 21:43      | 22:00    | 400          | 1          |
        // | 12      | 03:44      | 08:45    | 200          | 0          |

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        when(mockTimeUtil.arePeriodsOverlapping(1200, 1800,
                2143, 2200)).thenReturn(false);
        when(mockTimeUtil.arePeriodsOverlapping(1200, 1800,
                344, 845)).thenReturn(false);
        when(mockTimeUtil.arePeriodsOverlapping(2143, 2200,
                344, 845)).thenReturn(false);

        final Frequency firstFrequency = mock(Frequency.class);
        when(firstFrequency.getStartTime()).thenReturn(1200);
        when(firstFrequency.getEndTime()).thenReturn(1800);
        when(firstFrequency.getFrequencyMappingKey()).thenReturn("121200");
        final Frequency secondFrequency = mock(Frequency.class);
        when(secondFrequency.getStartTime()).thenReturn(2143);
        when(secondFrequency.getEndTime()).thenReturn(2200);
        when(secondFrequency.getFrequencyMappingKey()).thenReturn("122143");
        final Frequency thirdFrequency = mock(Frequency.class);
        when(thirdFrequency.getStartTime()).thenReturn(344);
        when(thirdFrequency.getEndTime()).thenReturn(845);
        when(thirdFrequency.getFrequencyMappingKey()).thenReturn("12344");

        final Map<String, List<Frequency>> frequenciesPerTripIdCollection = new HashMap<>();
        final List<Frequency> tripFrequencyCollection = new ArrayList<>();
        tripFrequencyCollection.add(firstFrequency);
        tripFrequencyCollection.add(secondFrequency);
        tripFrequencyCollection.add(thirdFrequency);
        frequenciesPerTripIdCollection.put("12", tripFrequencyCollection);
        when(mockDataRepo.getFrequencyAllByTripId()).thenReturn(frequenciesPerTripIdCollection);

        final ValidateTripFrequenciesOverlap underTest =
                new ValidateTripFrequenciesOverlap(mockDataRepo, mockResultRepo, mockTimeUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E053 - Trip frequencies overlap'");

        verify(mockDataRepo, times(1)).getFrequencyAllByTripId();
        verify(firstFrequency, times(4)).getFrequencyMappingKey();
        verify(firstFrequency, times(1)).getStartTime();
        verify(firstFrequency, times(1)).getEndTime();

        verify(secondFrequency, times(4)).getFrequencyMappingKey();
        verify(secondFrequency, times(2)).getStartTime();
        verify(secondFrequency, times(2)).getEndTime();

        verify(thirdFrequency, times(4)).getFrequencyMappingKey();
        verify(thirdFrequency, times(3)).getEndTime();
        verify(thirdFrequency, times(3)).getStartTime();

        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1200, 1800,
                        2143, 2200);
        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1200, 1800,
                        344, 845);
        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(2143, 2200,
                        344, 845);

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockTimeUtil, firstFrequency,
                secondFrequency, thirdFrequency);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void misorderedStartTimeAndEndTimeShouldNotGenerateNotice() {
        // frequencies.txt: no frequencies overlap
        // | trip_id | start_time | end_time | headway_secs | exact_time |
        // |---------|------------|----------|--------------|------------|
        // | 12      | 12:00      | 18:00    | 600          | 0          |
        // | 12      | 21:43      | 18:05    | 400          | 1          |
        // | 12      | 03:44      | 08:45    | 200          | 0          |
        // here start time and end_time are misordered. This should not generate any notice, since this is assessed
        // by E048.

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final Frequency firstFrequency = mock(Frequency.class);
        when(firstFrequency.getStartTime()).thenReturn(1200);
        when(firstFrequency.getEndTime()).thenReturn(1800);
        when(firstFrequency.getFrequencyMappingKey()).thenReturn("121200");
        final Frequency secondFrequency = mock(Frequency.class);
        when(secondFrequency.getStartTime()).thenReturn(2143);
        when(secondFrequency.getEndTime()).thenReturn(1805);
        when(secondFrequency.getFrequencyMappingKey()).thenReturn("122143");
        final Frequency thirdFrequency = mock(Frequency.class);
        when(thirdFrequency.getStartTime()).thenReturn(344);
        when(thirdFrequency.getEndTime()).thenReturn(845);
        when(thirdFrequency.getFrequencyMappingKey()).thenReturn("12344");

        final Map<String, List<Frequency>> frequenciesPerTripIdCollection = new HashMap<>();
        final List<Frequency> tripFrequencyCollection = new ArrayList<>();
        tripFrequencyCollection.add(firstFrequency);
        tripFrequencyCollection.add(secondFrequency);
        tripFrequencyCollection.add(thirdFrequency);
        frequenciesPerTripIdCollection.put("12", tripFrequencyCollection);
        when(mockDataRepo.getFrequencyAllByTripId()).thenReturn(frequenciesPerTripIdCollection);

        final ValidateTripFrequenciesOverlap underTest =
                new ValidateTripFrequenciesOverlap(mockDataRepo, mockResultRepo, mockTimeUtil, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E053 - Trip frequencies overlap'");

        verify(mockDataRepo, times(1)).getFrequencyAllByTripId();
        verify(firstFrequency, times(3)).getFrequencyMappingKey();
        verify(firstFrequency, times(1)).getStartTime();
        verify(firstFrequency, times(1)).getEndTime();

        verify(secondFrequency, times(3)).getFrequencyMappingKey();
        verify(secondFrequency, times(2)).getStartTime();
        verify(secondFrequency, times(2)).getEndTime();

        verify(thirdFrequency, times(3)).getFrequencyMappingKey();
        verify(thirdFrequency, times(2)).getEndTime();
        verify(thirdFrequency, times(2)).getStartTime();

        verify(mockTimeUtil, times(1))
                .arePeriodsOverlapping(1200, 1800,
                        344, 845);
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, firstFrequency, secondFrequency, thirdFrequency, mockTimeUtil);
    }
}
