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
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class ExportResultAsFileTest {

    @Test
    void jsonExportTest() throws IOException {

        ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        CannotConstructDataProviderNotice mockNotice0 = mock(CannotConstructDataProviderNotice.class);
        CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);
        when(mockResultRepo.getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.eq("testPath"))).thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));

        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, "testPath", false);

        underTest.execute();

        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.eq("testPath"));
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo);
    }

    @Test
    void protobufExportTest() throws IOException {

        ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        CannotConstructDataProviderNotice mockNotice0 = mock(CannotConstructDataProviderNotice.class);
        CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);
        when(mockResultRepo.getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.eq("testPath"))).thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));

        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, "testPath", true);

        underTest.execute();

        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.eq("testPath"));
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo);
    }

    @Test
    void emptyRepoJsonExportTest() throws IOException {

        ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        when(mockResultRepo.getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.eq("testPath"))).thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(Collections.emptyList());

        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, "testPath", false);

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.eq("testPath"));
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo);
    }

    @Test
    void emptyRepoProtobufExportTest() throws IOException {

        ValidationResultRepository.NoticeExporter mockExporter = mock(ValidationResultRepository.NoticeExporter.class);
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        when(mockResultRepo.getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.eq("testPath"))).thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(Collections.emptyList());

        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, "testPath", true);

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo);

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.eq("testPath"));
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();
        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo);
    }

}