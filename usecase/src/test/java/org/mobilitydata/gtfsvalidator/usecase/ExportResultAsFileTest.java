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
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

class ExportResultAsFileTest {

    @Test
    void resultRepoShouldBeExportedAsJsonFile() throws IOException {
        final ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        final CannotConstructDataProviderNotice mockNotice0 = mock(CannotConstructDataProviderNotice.class);
        final CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);

        when(mockResultRepo.getExporter(ArgumentMatchers.eq(false), anyString())).thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));

        when(mockExecParamRepo.getExecParamValue("output")).thenReturn("output");

        final ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, mockExecParamRepo);

        underTest.execute();

        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        verify(mockExecParamRepo, times(2))
                .getExecParamValue(ArgumentMatchers.eq("output"));

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq("proto"));

        final InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.anyString());
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo, mockExecParamRepo);
    }

    @Test
    void resultRepoShouldBeExportedAsProtoFile() throws IOException {
        final ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final CannotConstructDataProviderNotice mockNotice0 = mock(CannotConstructDataProviderNotice.class);
        final CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);

        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));
        when(mockExecParamRepo.getExecParamValue("output")).thenReturn("output");
        when(mockExecParamRepo.getExecParamValue("proto")).thenReturn(String.valueOf(true));
        when(mockResultRepo.getExporter(ArgumentMatchers.eq(true), ArgumentMatchers.anyString()))
                .thenReturn(mockExporter);

        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, mockExecParamRepo);

        underTest.execute();

        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        verify(mockExecParamRepo, times(2))
                .getExecParamValue(ArgumentMatchers.eq("output"));

        verify(mockExecParamRepo, times(3))
                .getExecParamValue(ArgumentMatchers.eq("proto"));

        InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.anyString());
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo, mockExecParamRepo);
    }
}