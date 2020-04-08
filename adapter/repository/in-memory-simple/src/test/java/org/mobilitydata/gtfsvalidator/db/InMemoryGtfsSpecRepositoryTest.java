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

package org.mobilitydata.gtfsvalidator.db;

import com.google.common.io.Resources;
import com.google.protobuf.TextFormat;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

class InMemoryGtfsSpecRepositoryTest {

    private static final String TEST_ASCII_GTFS_FILE = "test_gtfs_spec.asciipb";
    private static final String REQUIRED_FILE_0 = "requiredFile0.txt";
    private static final String REQUIRED_FILE_1 = "requiredFile1.txt";
    private static final String OPTIONAL_FILE_0 = "optionalFile0.txt";
    private static final String OPTIONAL_FILE_1 = "optionalFile1.txt";
    private static final String REQUIRED_HEADER0 = "requiredHeader0";
    private static final String REQUIRED_HEADER_1 = "requiredHeader1";
    private static final String OPTIONAL_HEADER_0 = "optionalHeader0";
    private static final String OPTIONAL_HEADER_1 = "optionalHeader1";

    @Test
    void fileMarkedRequiredInSpecShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);
        when(mockRepository.getRequiredFilenameList()).thenReturn(csvSpecProtos.getCsvspecList()
                .stream()
                .filter(GtfsSpecificationProto.CsvSpecProto::getRequired)
                .map(GtfsSpecificationProto.CsvSpecProto::getFilename)
                .collect(Collectors.toList()));

        final Collection<String> requiredFilenameList = mockRepository.getRequiredFilenameList();

        assertEquals(2, requiredFilenameList.size());
        assertTrue(requiredFilenameList.contains(REQUIRED_FILE_0));
        assertTrue(requiredFilenameList.contains(REQUIRED_FILE_1));
    }

    @Test
    void fileMarkedOptionalInSpecShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);
        when(mockRepository.getOptionalFilenameList()).thenReturn(csvSpecProtos.getCsvspecList()
                .stream()
                .filter(file -> !file.getRequired())
                .map(GtfsSpecificationProto.CsvSpecProto::getFilename)
                .collect(Collectors.toList()));

        final Collection<String> optionalFilenameList = mockRepository.getOptionalFilenameList();

        assertEquals(2, optionalFilenameList.size());
        assertTrue(optionalFilenameList.contains(OPTIONAL_FILE_0));
        assertTrue(optionalFilenameList.contains(OPTIONAL_FILE_1));
    }

    @Test
    void headerMarkedRequiredInRequiredFileShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);
        when(mockRepository.getRequiredHeadersForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<List<String>>() {
                    public List<String> answer(InvocationOnMock invocation) {
                        RawFileInfo fileInfo = invocation.getArgument(0);
                        GtfsSpecificationProto.CsvSpecProto specForFile = csvSpecProtos.getCsvspecList().stream()
                                .filter(spec -> fileInfo.getFilename().equals(spec.getFilename()))
                                .findAny()
                                .orElse(null);
                        return specForFile.getColumnList()
                                .stream()
                                .filter(GtfsSpecificationProto.ColumnSpecProto::getRequired)
                                .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                                .collect(Collectors.toList());
                    }
                });

        final Collection<String> requiredHeaderListForRequiredFile0 = mockRepository.getRequiredHeadersForFile(
                RawFileInfo.builder().filename(REQUIRED_FILE_0).build());

        final Collection<String> requiredHeaderListForRequiredFile1 = mockRepository.getRequiredHeadersForFile(
                RawFileInfo.builder().filename(REQUIRED_FILE_1).build());

        assertEquals(1, requiredHeaderListForRequiredFile0.size());
        assertTrue(requiredHeaderListForRequiredFile0.contains(REQUIRED_HEADER0));

        assertEquals(2, requiredHeaderListForRequiredFile1.size());
        assertTrue(requiredHeaderListForRequiredFile1.contains(REQUIRED_HEADER0));
        assertTrue(requiredHeaderListForRequiredFile1.contains(REQUIRED_HEADER_1));
    }

    @Test
    void headerMarkedRequiredInOptionalFileShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);

        when(mockRepository.getRequiredHeadersForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<List<String>>() {
                    public List<String> answer(InvocationOnMock invocation) {
                        RawFileInfo fileInfo = invocation.getArgument(0);
                        GtfsSpecificationProto.CsvSpecProto specForFile = csvSpecProtos.getCsvspecList().stream()
                                .filter(spec -> fileInfo.getFilename().equals(spec.getFilename()))
                                .findAny()
                                .orElse(null);
                        return specForFile.getColumnList()
                                .stream()
                                .filter(GtfsSpecificationProto.ColumnSpecProto::getRequired)
                                .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                                .collect(Collectors.toList());
                    }
                });

        final Collection<String> requiredHeaderListForOptionalFile0 = mockRepository.getRequiredHeadersForFile(
                RawFileInfo.builder().filename(OPTIONAL_FILE_0).build());

        final Collection<String> requiredHeaderListForOptionalFile1 = mockRepository.getRequiredHeadersForFile(
                RawFileInfo.builder().filename(OPTIONAL_FILE_1).build());

        assertEquals(2, requiredHeaderListForOptionalFile0.size());
        assertTrue(requiredHeaderListForOptionalFile0.contains(REQUIRED_HEADER0));
        assertTrue(requiredHeaderListForOptionalFile0.contains(REQUIRED_HEADER_1));

        assertEquals(1, requiredHeaderListForOptionalFile1.size());
        assertTrue(requiredHeaderListForOptionalFile1.contains(REQUIRED_HEADER0));
    }

    @Test
    void headerMarkedOptionalInRequiredFileShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);

        when(mockRepository.getOptionalHeadersForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<List<String>>() {
                    public List<String> answer(InvocationOnMock invocation) {
                        RawFileInfo fileInfo = invocation.getArgument(0);
                        GtfsSpecificationProto.CsvSpecProto specForFile = csvSpecProtos.getCsvspecList().stream()
                                .filter(spec -> fileInfo.getFilename().equals(spec.getFilename()))
                                .findAny()
                                .orElse(null);
                        return specForFile.getColumnList().stream()
                                .filter(column -> !column.getRequired())
                                .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                                .collect(Collectors.toList());
                    }
                });

        final Collection<String> optionalHeaderListForRequiredFile0 = mockRepository.getOptionalHeadersForFile(
                RawFileInfo.builder().filename(REQUIRED_FILE_0).build());

        final Collection<String> optionalHeaderListForRequiredFile1 = mockRepository.getOptionalHeadersForFile(
                RawFileInfo.builder().filename(REQUIRED_FILE_1).build());

        assertEquals(2, optionalHeaderListForRequiredFile0.size());
        assertTrue(optionalHeaderListForRequiredFile0.contains(OPTIONAL_HEADER_0));
        assertTrue(optionalHeaderListForRequiredFile0.contains(OPTIONAL_HEADER_1));

        assertEquals(0, optionalHeaderListForRequiredFile1.size());
    }

    @Test
    void headerMarkedOptionalInOptionalFileShouldBeListed() throws IOException {

        final String specResourceName = TEST_ASCII_GTFS_FILE;
        GtfsSpecificationProto.CsvSpecProtos csvSpecProtos = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class);

        InMemoryGtfsSpecRepository mockRepository = mock(InMemoryGtfsSpecRepository.class);

        when(mockRepository.getOptionalHeadersForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<List<String>>() {
                    public List<String> answer(InvocationOnMock invocation) {
                        RawFileInfo fileInfo = invocation.getArgument(0);
                        GtfsSpecificationProto.CsvSpecProto specForFile = csvSpecProtos.getCsvspecList().stream()
                                .filter(spec -> fileInfo.getFilename().equals(spec.getFilename()))
                                .findAny()
                                .orElse(null);
                        return specForFile.getColumnList().stream()
                                .filter(column -> !column.getRequired())
                                .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                                .collect(Collectors.toList());
                    }
                });

        final Collection<String> optionalHeaderListForOptionalFile0 = mockRepository.getOptionalHeadersForFile(
                RawFileInfo.builder().filename(OPTIONAL_FILE_0).build());

        final Collection<String> optionalHeaderListForOptionalFile1 = mockRepository.getOptionalHeadersForFile(
                RawFileInfo.builder().filename(OPTIONAL_FILE_1).build());

        assertEquals(1, optionalHeaderListForOptionalFile0.size());
        assertTrue(optionalHeaderListForOptionalFile0.contains(OPTIONAL_HEADER_0));

        assertEquals(2, optionalHeaderListForOptionalFile1.size());
        assertTrue(optionalHeaderListForOptionalFile0.contains(OPTIONAL_HEADER_0));
        assertTrue(optionalHeaderListForOptionalFile1.contains(OPTIONAL_HEADER_1));
    }

}