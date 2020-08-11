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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FrequencyStartTimeAfterEndTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateFrequencyStartTimeBeforeEndTimeTest {

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void frequencyEndTimeAfterStartTimeShouldNotGenerateNotice() {
        final Frequency mockFrequency = mock(Frequency.class);
        final Integer startTime = 0;
        final Integer endTime = 60;
        when(mockFrequency.getStartTime()).thenReturn(startTime);
        when(mockFrequency.getEndTime()).thenReturn(endTime);

        final Map<String, Frequency> mockFrequencyCollection = new HashMap<>();
        mockFrequencyCollection.put(String.valueOf(startTime + endTime), mockFrequency);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFrequencyAll()).thenReturn(mockFrequencyCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final TimeUtils mockTimeUtil = mock(TimeUtils.class);

        final ValidateFrequencyStartTimeBeforeEndTime underTest =
                new ValidateFrequencyStartTimeBeforeEndTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E046 - `start_time` and `end_time`" +
                " out of order");

        verify(mockDataRepo, times(1)).getFrequencyAll();

        verify(mockFrequency, times(1)).getStartTime();
        verify(mockFrequency, times(1)).getEndTime();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockFrequency, mockTimeUtil);
    }

    // suppressed warning regarding ignored result of method since it is not necessary here
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void frequencyEndTimeBeforeStartTimeShouldGenerateNotice() {
        final Frequency mockFrequency = mock(Frequency.class);
        final Integer startTime = 60;
        final Integer endTime = 0;
        when(mockFrequency.getEndTime()).thenReturn(endTime);
        when(mockFrequency.getStartTime()).thenReturn(startTime);
        when(mockFrequency.getTripId()).thenReturn("trip_id");

        final Map<String, Frequency> mockFrequencyCollection = new HashMap<>();
        mockFrequencyCollection.put(String.valueOf(startTime + endTime), mockFrequency);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getFrequencyAll()).thenReturn(mockFrequencyCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final TimeUtils mockTimeUtil = mock(TimeUtils.class);
        when(mockTimeUtil.convertIntegerToHMMSS(endTime)).thenReturn("end_time");
        when(mockTimeUtil.convertIntegerToHMMSS(startTime)).thenReturn("start_time");

        final ValidateFrequencyStartTimeBeforeEndTime underTest =
                new ValidateFrequencyStartTimeBeforeEndTime(mockDataRepo, mockResultRepo,
                        mockTimeUtil, mockLogger);

        underTest.execute();
        verify(mockLogger, times(1)).info("Validating rule 'E046 - `start_time` and `end_time` " +
                "out of order");

        verify(mockDataRepo, times(1)).getFrequencyAll();

        verify(mockFrequency, times(1)).getEndTime();
        verify(mockFrequency, times(1)).getStartTime();

        final ArgumentCaptor<FrequencyStartTimeAfterEndTimeNotice> captor =
                ArgumentCaptor.forClass(FrequencyStartTimeAfterEndTimeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        verify(mockTimeUtil, times(2)).convertIntegerToHMMSS(anyInt());

        final List<FrequencyStartTimeAfterEndTimeNotice> noticeList = captor.getAllValues();

        assertEquals("frequencies.txt", noticeList.get(0).getFilename());
        assertEquals("tripId", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("startTime", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("trip_id", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("end_time", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals("start_time", noticeList.get(0)
                .getNoticeSpecific(Notice.KEY_COMPOSITE_KEY_THIRD_VALUE));
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockResultRepo, mockTimeUtil);
    }
}
