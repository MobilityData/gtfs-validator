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

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;

class ExportResultAsFileTest {

    @Test
    void resultRepoShouldBeExportedAsJsonFile() throws IOException {
        final NoticeExporter mockExporter =
                mock(NoticeExporter.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final MissingHeaderNotice mockNotice0 = mock(MissingHeaderNotice.class);
        final CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);
        final Timestamp mockTimestamp = mock(Timestamp.class);

        when(mockResultRepo.getExporter(ArgumentMatchers.eq(false), ArgumentMatchers.anyString()))
                .thenReturn(mockExporter);
        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));

        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.OUTPUT_KEY))
                .thenReturn(mockExecParamRepo.OUTPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.PROTO_KEY)).thenReturn("false");
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.PROTO_KEY)).thenReturn(false);
        when(mockGtfsDataRepo.getFeedPublisherName()).thenReturn("feed publisher name");

        Logger mockLogger = mock(Logger.class);

        final ExportResultAsFile underTest =
                new ExportResultAsFile(mockResultRepo, mockExecParamRepo, mockGtfsDataRepo, mockTimestamp, mockLogger);

        underTest.execute();

        verify(mockExecParamRepo, times(2)).getExecParamValue(mockExecParamRepo.PROTO_KEY);

        verify(mockLogger, times(1))
                .info(ArgumentMatchers.eq("Results are exported as JSON by default"));
        verify(mockLogger, times(1))
                .info(ArgumentMatchers.contains(
                        "Computed relative path for report file: output/feed_publisher_name__Mock_for_Timestamp"
                ));
        verify(mockLogger, times(1))
                .info(ArgumentMatchers.eq("Exporting validation repo content:" + mockResultRepo.getAll()));

        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(mockExecParamRepo.OUTPUT_KEY));

        verify(mockExecParamRepo, times(2))
                .getExecParamValue(ArgumentMatchers.eq(mockExecParamRepo.PROTO_KEY));


        final InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo, mockGtfsDataRepo);
        inOrder.verify(mockGtfsDataRepo, times(1)).getFeedPublisherName();
        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(false),
                ArgumentMatchers.anyString());
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();

        verify(mockNotice0, times(1)).export(mockExporter);
        verify(mockNotice1, times(1)).export(mockExporter);

        verify(mockResultRepo, times(3)).getAll();

        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo, mockExecParamRepo, mockLogger);
    }

    @Test
    void resultRepoShouldBeExportedAsProtoFile() throws IOException {
        final NoticeExporter mockExporter =
                mock(NoticeExporter.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final MissingHeaderNotice mockNotice0 = mock(MissingHeaderNotice.class);
        final CannotUnzipInputArchiveNotice mockNotice1 = mock(CannotUnzipInputArchiveNotice.class);
        final ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Timestamp mockTimestamp = mock(Timestamp.class);

        when(mockResultRepo.getAll()).thenReturn(List.of(mockNotice0, mockNotice1));
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.OUTPUT_KEY))
                .thenReturn(mockExecParamRepo.OUTPUT_KEY);
        when(mockExecParamRepo.getExecParamValue(mockExecParamRepo.PROTO_KEY)).thenReturn(String.valueOf(true));
        when(mockExecParamRepo.hasExecParamValue(mockExecParamRepo.PROTO_KEY)).thenReturn(true);
        when(mockResultRepo.getExporter(ArgumentMatchers.eq(true), ArgumentMatchers.anyString())).thenReturn(mockExporter);
        when(mockGtfsDataRepo.getFeedPublisherName()).thenReturn("feed publisher name");

        Logger mockLogger = mock(Logger.class);
        ExportResultAsFile underTest = new ExportResultAsFile(mockResultRepo, mockExecParamRepo, mockGtfsDataRepo,
                mockTimestamp, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info(ArgumentMatchers.eq("-p provided, exporting results as proto"));
        verify(mockLogger, times(1))
                .info(ArgumentMatchers.contains(
                        "Computed relative path for report file: output/feed_publisher_name__Mock_for_Timestamp"
                ));
        verify(mockLogger, times(1))
                .info(ArgumentMatchers.eq("Exporting validation repo content:" + mockResultRepo.getAll()));
        verify(mockNotice0, times(1)).export(ArgumentMatchers.eq(mockExporter));
        verify(mockNotice1, times(1)).export(ArgumentMatchers.eq(mockExporter));

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(mockExecParamRepo.OUTPUT_KEY));

        verify(mockExecParamRepo, times(2))
                .getExecParamValue(ArgumentMatchers.eq(mockExecParamRepo.PROTO_KEY));

        final InOrder inOrder = Mockito.inOrder(mockExporter, mockResultRepo, mockGtfsDataRepo);
        inOrder.verify(mockGtfsDataRepo, times(1)).getFeedPublisherName();

        inOrder.verify(mockResultRepo, times(1)).getExporter(ArgumentMatchers.eq(true),
                ArgumentMatchers.anyString());
        inOrder.verify(mockExporter, times(1)).exportBegin();
        inOrder.verify(mockResultRepo, times(1)).getAll();

        verify(mockNotice0, times(1)).export(mockExporter);
        verify(mockNotice1, times(1)).export(mockExporter);
        verify(mockResultRepo, times(3)).getAll();
        verify(mockExecParamRepo, times(2)).getExecParamValue(mockExecParamRepo.PROTO_KEY);

        verify(mockExporter, times(1)).exportEnd();
        verifyNoMoreInteractions(mockExporter, mockResultRepo, mockExecParamRepo, mockLogger);
    }
}
