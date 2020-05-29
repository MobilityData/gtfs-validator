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
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedTransferTest {

    @Test
    void validatedParsedTransferShouldCreateTransferEntityAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME)).thenReturn("0");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME)).thenReturn("40");
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final Transfer mockTransfer = mock(Transfer.class);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockEntityBuildResult = mock(EntityBuildResult.class);

        //noinspection unchecked
        when(mockBuilder.build(0, 40))
                .thenReturn(mockEntityBuildResult);
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        when(mockEntityBuildResult.getData()).thenReturn(mockTransfer);

        when(mockParsedTransfer.get("from_stop_id")).thenReturn("stop id 0");
        when(mockParsedTransfer.get("to_stop_id")).thenReturn("stop id 1");
        when(mockParsedTransfer.get("transfer_type")).thenReturn(1);
        when(mockParsedTransfer.get("min_transfer_time")).thenReturn(20);

        when(mockGtfsDataRepo.addTransfer(mockTransfer)).thenReturn(mockTransfer);

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockExecParamRepo, mockBuilder);

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
        verify(mockBuilder, times(1))
                .build(0, 40);

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME);
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME);

        verify(mockGtfsDataRepo, times(1)).addTransfer(ArgumentMatchers.eq(mockTransfer));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedTransfer, mockExecParamRepo);
    }

    @Test
    void invalidTransferShouldGenerateNoticeAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME)).thenReturn("0");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME)).thenReturn("40");
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.isSuccess()).thenReturn(false);
        noticeCollection.add(mockNotice);

        //noinspection unchecked
        when(mockBuilder.build(0, 40)).thenReturn(mockGenericObject);

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockExecParamRepo, mockBuilder);

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

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("stop id 1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));
        verify(mockBuilder, times(1))
                .build(0, 40);

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(ArgumentMatchers.eq(mockNotice));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME);
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME);

        verifyNoMoreInteractions(mockParsedTransfer, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject,
                mockExecParamRepo);
    }

    @Test
    void duplicateTransferShouldThrowExceptionAndAddEntityMustBeUniqueNoticeToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME)).thenReturn("0");
        when(mockExecParamRepo.getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME)).thenReturn("40");
        final Transfer.TransferBuilder mockBuilder = mock(Transfer.TransferBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTransfer = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        final Transfer mockTransfer = mock(Transfer.class);
        when(mockGenericObject.getData()).thenReturn(mockTransfer);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBuilder.build(0, 40)).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addTransfer(mockTransfer)).thenReturn(null);

        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");
        when(mockTransfer.getTransferType()).thenReturn(TransferType.fromInt(1));

        final ProcessParsedTransfer underTest = new ProcessParsedTransfer(mockResultRepo, mockGtfsDataRepo,
                mockExecParamRepo, mockBuilder);

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
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTransfer, times(1)).getEntityId();

        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.eq("stop id 0"));
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.eq("stop id 1"));
        verify(mockBuilder, times(1)).transferType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).minTransferTime(ArgumentMatchers.eq(20));
        verify(mockBuilder, times(1)).build(0, 40);

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("transfers.txt", noticeList.get(0).getFilename());
        assertEquals("from_stop_id;to_stop_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.UPPER_BOUND_MIN_TRANSFER_TIME);
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ExecParamRepository.LOWER_BOUND_MIN_TRANSFER_TIME);

        verifyNoMoreInteractions(mockParsedTransfer, mockResultRepo, mockGtfsDataRepo, mockTransfer, mockBuilder,
                mockGenericObject, mockExecParamRepo);
    }
}