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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.TransferType;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedTransferTest {

    @Test
    void validatedParsedTransferShouldCreateTransferEntityAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final Transfer mockTransfer = mock(Transfer.class);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);

        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTransfer).when(mockEntityBuildResult).getData();

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("stop id 0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("stop id 1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        when(mockGtfsDataRepo.addTransfer(mockTransfer)).thenReturn(mockTransfer);

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        underTest.execute(mockParsedTransfer);

        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("from_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("to_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("transfer_type"));
        verify(mockParsedTransfer, times(1))
                .get(ArgumentMatchers.eq("min_transfer_time"));

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("stop id 0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("stop id 1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));
        verify(mockBuilder, times(1)).build();

        verify(mockGtfsDataRepo, times(1)).addTransfer(ArgumentMatchers.eq(mockTransfer));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedTransfer);
    }

    @Test
    void invalidTransferShouldGenerateNoticeAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(noticeCollection).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(false);
        noticeCollection.add(mockNotice);

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn(null);
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("stop id 1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        underTest.execute(mockParsedTransfer);

        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("from_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("to_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("transfer_type"));
        verify(mockParsedTransfer, times(1))
                .get(ArgumentMatchers.eq("min_transfer_time"));

        // parameter of method .fromStopId is annotated as non null, for the purpose of this test, we suppress the
        // warning resulting form passing null value to method.
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("stop id 1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(ArgumentMatchers.eq(mockNotice));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    void duplicateTransferShouldThrowExceptionAndAddEntityMustBeUniqueNoticeToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        final Transfer mockTransfer = mock(Transfer.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTransfer).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addTransfer(mockTransfer)).thenReturn(null);

        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getTransferType()).thenReturn(TransferType.fromInt(1));

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("stop id 0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("stop id 1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        underTest.execute(mockParsedTransfer);

        verify(mockGtfsDataRepo, times(1)).addTransfer(mockTransfer);

        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("from_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("to_stop_id"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("transfer_type"));
        verify(mockParsedTransfer, times(1)).get(ArgumentMatchers.eq("min_transfer_time"));
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("stop id 0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("stop id 1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("from_stop_id;to_stop_id", noticeList.get(0).getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTransfer, mockResultRepo, mockGtfsDataRepo, mockTransfer, mockBuilder,
                mockGenericObject);
    }
}