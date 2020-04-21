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
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParseSingleRowForFileTest {

    @Mock
    private List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    private ValidationResultRepository mockResultRepo;

    @Mock
    private GtfsSpecRepository.RawEntityParser mockEntityParser = buildMockEntityParser();
    @InjectMocks
    private GtfsSpecRepository mockSpecRepo;

    private int parserCallsToValidateNumericTypesCount;
    private int parserCallsToParseCount;
    @Mock
    private Collection<ErrorNotice> fakeValidationResult = Collections.emptyList();
    @InjectMocks
    private GtfsSpecRepository.RawEntityParser mockParser;

    @Mock(name = "providerCurrentCount")
    private int providerCurrentCount;
    @Mock(name = "mockEntityList")
    private List<Map<String, String>> mockEntityList;
    @InjectMocks
    private RawFileRepository.RawEntityProvider mockProvider;

    @Test
    void shouldValidateAndParseOneByOne() {

        ValidationResultRepository mockResultRepo = buildMockResultRepository();
        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(1, parserCallsToValidateNumericTypesCount); //mockEntityParser.callToValidateNumericTypesCount
        assertEquals(1, parserCallsToParseCount); //mockEntityParser.callToParseCount

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(2, parserCallsToValidateNumericTypesCount);
        assertEquals(2, parserCallsToParseCount);

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(3, parserCallsToValidateNumericTypesCount);
        assertEquals(3, parserCallsToParseCount);

        assertFalse(underTest.hasNext());
        assertNull(underTest.execute());
    }

    @Test
    void shouldWriteNoticesToRepo() {

        ValidationResultRepository mockResultRepo = buildMockResultRepository();
        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_invalid.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(3, noticeList.size());
        underTest.execute();
        assertEquals(6, noticeList.size());
        underTest.execute();
        assertEquals(9, noticeList.size());
    }

    @Test
    void providerErrorShouldGenerateNotice() {

        ValidationResultRepository mockResultRepo = buildMockResultRepository();
        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_empty.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(1, noticeList.size());
        Notice notice = noticeList.get(0);
        assertThat(notice, instanceOf(CannotConstructDataProviderNotice.class));
        assertEquals("E002", notice.getId());
        assertEquals("Data provider error", notice.getTitle());
        assertEquals("test_empty.tst", notice.getFilename());
        assertEquals("An error occurred while trying to access raw data for file: test_empty.tst", notice.getDescription());
    }

    private ValidationResultRepository buildMockResultRepository() {
        mockResultRepo =  mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<Notice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                noticeList.add(errorNotice);
                return errorNotice;
            }
        });

        return mockResultRepo;
    }

    private GtfsSpecRepository buildMockSpecRepository() {
        mockSpecRepo =  mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getParserForFile(any(RawFileInfo.class))).thenAnswer(new Answer<GtfsSpecRepository.RawEntityParser>() {
            public GtfsSpecRepository.RawEntityParser answer(InvocationOnMock invocation) {
                RawFileInfo file = invocation.getArgument(0);
                if (file.getFilename().contains("invalid")) {
                    ErrorNotice fakeNotice = new CannotConstructDataProviderNotice(file.getFilename());
                    fakeValidationResult = List.of(fakeNotice, fakeNotice, fakeNotice);
                    return mockEntityParser;
                }
                return mockEntityParser;
            }
        });

        return mockSpecRepo;
    }

    private RawFileRepository buildMockFileRepository() {
        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.findByName(anyString())).thenReturn(Optional.empty());
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<Optional<RawFileRepository.RawEntityProvider>>() {
                    public Optional<RawFileRepository.RawEntityProvider> answer(InvocationOnMock invocation) {
                        RawFileInfo file = invocation.getArgument(0);
                        if (file.getFilename().contains("empty")) {
                            return Optional.empty();
                        }

                        if (file.getFilename().contains("invalid")) {
                            mockEntityList = List.of(
                                    Map.of("header0_string", "header0_string",
                                            "header1_float", "header1_float",
                                            "header2_integer", "header2_integer"),
                                    Map.of("header0_string", "invalid_string",
                                            "header1_float", "valid_float",
                                            "header2_integer", "invalid_integer"),
                                    Map.of("header0_string", "valid", "header1_float", "invalid_float",
                                            "header2_integer", "valid_integer"),
                                    Map.of("header0_string", "invalid", "header1_float", "invalid_float",
                                            "header2_integer", "invalid_integer")
                                    );
                            mockProvider = buildMockEntityProvider();
                            return Optional.of(mockProvider);
                        }

                        mockEntityList = List.of(
                                Map.of("header0_string", "header0_string", "header1_float", "header1_float",
                                        "header2_integer", "header2_integer"),
                                Map.of("header0_string", "valid_string", "header1_float", "valid_float",
                                        "header2_integer", "valid_integer"),
                                Map.of("header0_string", "valid", "header1_float", "valid_float",
                                        "header2_integer", "valid_integer"),
                                Map.of("header0_string", "valid", "header1_float", "invalid_float",
                                        "header2_integer", "valid_integer")
                                );
                        mockProvider = buildMockEntityProvider();
                        return Optional.of(mockProvider);
                    }
                });

        return mockFileRepo;
    }

    private GtfsSpecRepository.RawEntityParser buildMockEntityParser() {
        parserCallsToValidateNumericTypesCount = 0;
        parserCallsToParseCount = 0;
        mockParser = mock(GtfsSpecRepository.RawEntityParser.class);
        when(mockParser.validateNonStringTypes(any(RawEntity.class))).thenAnswer(new Answer<Collection<ErrorNotice>>() {
            public Collection<ErrorNotice> answer(InvocationOnMock invocation) {
                ++parserCallsToValidateNumericTypesCount;
                return fakeValidationResult;
            }
        });
        when(mockParser.parse(any(RawEntity.class))).thenAnswer(new Answer<ParsedEntity>() {
            public ParsedEntity answer(InvocationOnMock invocation) {
                ++parserCallsToParseCount;
                return null;
            }
        });

        return mockParser;
    }

    private RawFileRepository.RawEntityProvider buildMockEntityProvider() {
        providerCurrentCount = 0;
        mockProvider = mock(RawFileRepository.RawEntityProvider.class);
        when(mockProvider.hasNext()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) {
                return providerCurrentCount < mockEntityList.size() - 1;
            }
        });
        when(mockProvider.getNext()).thenAnswer(new Answer<RawEntity>() {
            public RawEntity answer(InvocationOnMock invocation) {
                ++providerCurrentCount;
                return new RawEntity(mockEntityList.get(providerCurrentCount), providerCurrentCount + 1);
            }
        });
        when(mockProvider.getHeaderCount()).thenReturn(mockEntityList.get(0).size());
        return mockProvider;
    }
    
}