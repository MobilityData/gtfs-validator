/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndTimeOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mockito.ArgumentCaptor;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FrequencyTimeInOrderValidatorTest {

    @Test
    public void startTimeBeforeEndTimeShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsFrequency mockFrequency = mock(GtfsFrequency.class);
        GtfsTime mockStartTime = mock(GtfsTime.class);
        GtfsTime mockEndTime = mock(GtfsTime.class);
        when(mockFrequency.startTime()).thenReturn(mockStartTime);
        when(mockFrequency.endTime()).thenReturn(mockEndTime);
        when(mockStartTime.isAfter(mockEndTime)).thenReturn(false);

        FrequencyTimeInOrderValidator underTest = new FrequencyTimeInOrderValidator();

        underTest.validate(mockFrequency, mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockStartTime, times(1)).isAfter(mockEndTime);
        verify(mockFrequency, times(1)).startTime();
        verify(mockFrequency, times(1)).endTime();
        verifyNoMoreInteractions(mockEndTime, mockStartTime, mockFrequency);
    }

    @Test
    public void startTimeAfterEndTimeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsFrequency mockFrequency = mock(GtfsFrequency.class);
        GtfsTime mockStartTime = mock(GtfsTime.class);
        GtfsTime mockEndTime = mock(GtfsTime.class);
        when(mockFrequency.startTime()).thenReturn(mockStartTime);
        when(mockFrequency.endTime()).thenReturn(mockEndTime);
        when(mockFrequency.tripId()).thenReturn("trip id value");
        when(mockFrequency.csvRowNumber()).thenReturn(4L);
        when(mockStartTime.isAfter(mockEndTime)).thenReturn(true);
        when(mockStartTime.toHHMMSS()).thenReturn("start time value");
        when(mockEndTime.toHHMMSS()).thenReturn("end time value");

        FrequencyTimeInOrderValidator underTest = new FrequencyTimeInOrderValidator();

        underTest.validate(mockFrequency, mockNoticeContainer);

        final ArgumentCaptor<StartAndEndTimeOutOfOrderNotice> captor =
                ArgumentCaptor.forClass(StartAndEndTimeOutOfOrderNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());

        StartAndEndTimeOutOfOrderNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("start_and_end_time_out_of_order");
        assertThat(notice.getContext()).containsEntry("filename", "frequencies.txt");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 4L);
        assertThat(notice.getContext()).containsEntry("entityId", "trip id value");
        assertThat(notice.getContext()).containsEntry("startTime", "start time value");
        assertThat(notice.getContext()).containsEntry("endTime", "end time value");

        verify(mockStartTime, times(1)).isAfter(mockEndTime);
        verify(mockFrequency, times(2)).startTime();
        verify(mockFrequency, times(2)).endTime();
        verify(mockFrequency, times(1)).csvRowNumber();
        verify(mockFrequency, times(1)).tripId();
        verify(mockStartTime, times(1)).toHHMMSS();
        verify(mockEndTime, times(1)).toHHMMSS();
        verifyNoMoreInteractions(mockEndTime, mockStartTime, mockFrequency);
    }
}
