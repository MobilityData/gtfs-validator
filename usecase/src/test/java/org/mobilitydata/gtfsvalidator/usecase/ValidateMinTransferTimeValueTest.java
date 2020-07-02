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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousMinTransferTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateMinTransferTimeValueTest {

    @Test
    void tooBigMinTransferTimeShouldGenerateNotice() {
        final Transfer mockTransfer = mock(Transfer.class);
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getMinTransferTime()).thenReturn(240);
        final Map<String, Map<String, Transfer>> mockTransferCollection = new HashMap<>();
        final Map<String, Transfer> innerMap = new HashMap<>();
        innerMap.put("stop id 1", mockTransfer);
        mockTransferCollection.put("stop id 1", innerMap);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getTransferAll()).thenReturn(mockTransferCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN))
                .thenReturn("40");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX))
                .thenReturn("160");
        final Logger mockLogger = mock(Logger.class);

        final ValidateMinTransferTimeValue underTest =
                new ValidateMinTransferTimeValue(mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'W009 - `min_transfer_time` is" +
                        " outside allowed range" + System.lineSeparator());

        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN));
        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX));

        verify(mockDataRepo, times(1)).getTransferAll();

        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(4)).getMinTransferTime();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(1)).getToStopId();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(1)).getFromStopId();


        final ArgumentCaptor<SuspiciousMinTransferTimeNotice> captor =
                ArgumentCaptor.forClass(SuspiciousMinTransferTimeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<SuspiciousMinTransferTimeNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("min_transfer_time", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(40, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(160, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(240, noticeList.get(0).getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals("from_stop_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("to_stop_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("stop id 0", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop id 1", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockTransfer, mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);
    }

    @Test
    void tooSmallMinTransferTimeShouldGenerateNotice() {
        final Transfer mockTransfer = mock(Transfer.class);
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getMinTransferTime()).thenReturn(3);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Map<String, Transfer>> mockTransferCollection = new HashMap<>();
        final Map<String, Transfer> innerMap = new HashMap<>();
        innerMap.put("stop id 1", mockTransfer);
        mockTransferCollection.put("stop id 1", innerMap);

        when(mockDataRepo.getTransferAll()).thenReturn(mockTransferCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN))
                .thenReturn("40");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX))
                .thenReturn("160");
        final Logger mockLogger = mock(Logger.class);

        final ValidateMinTransferTimeValue underTest =
                new ValidateMinTransferTimeValue(mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'W009 - `min_transfer_time` is" +
                " outside allowed range" + System.lineSeparator());

        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN));
        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX));

        verify(mockDataRepo, times(1)).getTransferAll();

        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(3)).getMinTransferTime();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(1)).getToStopId();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(1)).getFromStopId();


        final ArgumentCaptor<SuspiciousMinTransferTimeNotice> captor =
                ArgumentCaptor.forClass(SuspiciousMinTransferTimeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<SuspiciousMinTransferTimeNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("min_transfer_time", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(40, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(160, noticeList.get(0).getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(3, noticeList.get(0).getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals("from_stop_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("to_stop_id", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals("stop id 0", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals("stop id 1", noticeList.get(0).getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));

        verifyNoMoreInteractions(mockTransfer, mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);
    }

    @Test
    void nonSuspiciousMinTransferTimeShouldNotGenerateNotice() {
        final Transfer mockTransfer = mock(Transfer.class);
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getMinTransferTime()).thenReturn(90);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Map<String, Transfer>> mockTransferCollection = new HashMap<>();
        final Map<String, Transfer> innerMap = new HashMap<>();
        innerMap.put("stop id 1", mockTransfer);
        mockTransferCollection.put("stop id 1", innerMap);

        when(mockDataRepo.getTransferAll()).thenReturn(mockTransferCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN))
                .thenReturn("40");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX))
                .thenReturn("160");
        final Logger mockLogger = mock(Logger.class);

        final ValidateMinTransferTimeValue underTest =
                new ValidateMinTransferTimeValue(mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'W009 - `min_transfer_time` is" +
                " outside allowed range" + System.lineSeparator());

        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN));
        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX));

        verify(mockDataRepo, times(1)).getTransferAll();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(3)).getMinTransferTime();

        verifyNoMoreInteractions(mockTransfer, mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);
    }

    @Test
    void nullMinTransferTimeShouldNotGenerateNotice() {
        final Transfer mockTransfer = mock(Transfer.class);
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getMinTransferTime()).thenReturn(null);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Map<String, Transfer>> mockTransferCollection = new HashMap<>();
        final Map<String, Transfer> innerMap = new HashMap<>();
        innerMap.put("stop id 1", mockTransfer);
        mockTransferCollection.put("stop id 1", innerMap);

        when(mockDataRepo.getTransferAll()).thenReturn(mockTransferCollection);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN))
                .thenReturn("40");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX))
                .thenReturn("160");
        final Logger mockLogger = mock(Logger.class);

        final ValidateMinTransferTimeValue underTest =
                new ValidateMinTransferTimeValue(mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'W009 - `min_transfer_time` is" +
                " outside allowed range" + System.lineSeparator());

        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN));
        verify(mockExecParamRepo, times(1)).getExecParamValue(
                ArgumentMatchers.eq(ExecParamRepository.TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX));

        verify(mockDataRepo, times(1)).getTransferAll();
        // suppress warning regarding ignored result of method since it is not necessary here.
        //noinspection ResultOfMethodCallIgnored
        verify(mockTransfer, times(1)).getMinTransferTime();

        verifyNoMoreInteractions(mockTransfer, mockDataRepo, mockResultRepo, mockExecParamRepo, mockLogger);
    }
}
