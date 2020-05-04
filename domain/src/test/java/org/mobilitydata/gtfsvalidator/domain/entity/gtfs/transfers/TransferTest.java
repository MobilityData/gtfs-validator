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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TransferTest {
    private final static String FROM_STOP_ID = "stop id 0";
    private final static String TO_STOP_ID = "stop id 1";
    private final static int VALID_TRANSFER_TYPE_VALUE = 1;
    private final static int VALID_MIN_TRANSFER_TIME_VALUE = 20;

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createTransferWithNullFromStopIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.fromStopId(null)
                .toStopId(TO_STOP_ID)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("from_stop_id", noticeList.get(0).getFieldName());
        assertEquals("null;stop id 1", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createTransferWithNullToStopIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.fromStopId(FROM_STOP_ID)
                .toStopId(null)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("to_stop_id", noticeList.get(0).getFieldName());
        assertEquals("stop id 0;null", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createTransferWithUnexpectedTransferTypeValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder(mockNoticeCollection);

        underTest.fromStopId(FROM_STOP_ID)
                .toStopId(TO_STOP_ID)
                .transferType(55)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("transfer_type", noticeList.get(0).getFieldName());
        assertEquals("stop id 0;stop id 1", noticeList.get(0).getEntityId());
        assertEquals("55", noticeList.get(0).getEnumValue());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createTransferWithInvalidMinTransferTimeValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder(mockNoticeCollection);

        underTest.fromStopId(FROM_STOP_ID)
                .toStopId(TO_STOP_ID)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(-20);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("min_transfer_time", noticeList.get(0).getFieldName());
        assertEquals("stop id 0;stop id 1", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-20, noticeList.get(0).getActualValue());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createTransferWithNullMinTransferTimeValueShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder(mockNoticeCollection);

        underTest.fromStopId(FROM_STOP_ID)
                .toStopId(TO_STOP_ID)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        verify(mockNoticeCollection, times(1)).clear();

        assertTrue(entityBuildResult.getData() instanceof Transfer);
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}