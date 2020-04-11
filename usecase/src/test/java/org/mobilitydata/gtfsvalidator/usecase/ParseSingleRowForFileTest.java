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
    List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @Mock
    EntityParser mockEntityParser = new EntityParser(Collections.emptyList());
    @InjectMocks
    GtfsSpecRepository mockSpecRepo;

    @Test
    void shouldValidateAndParseOneByOne() {

        ValidationResultRepository mockResultRepo = mockResultRepository();
        GtfsSpecRepository mockSpecRepo = mockSpecRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(1, mockEntityParser.callToValidateNumericTypesCount);
        assertEquals(1, mockEntityParser.callToParseCount);

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(2, mockEntityParser.callToValidateNumericTypesCount);
        assertEquals(2, mockEntityParser.callToParseCount);

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(3, mockEntityParser.callToValidateNumericTypesCount);
        assertEquals(3, mockEntityParser.callToParseCount);

        assertFalse(underTest.hasNext());
        assertNull(underTest.execute());
    }

    @Test
    void shouldWriteNoticesToRepo() {

        ValidationResultRepository mockResultRepo = mockResultRepository();
        GtfsSpecRepository mockSpecRepo = mockSpecRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

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

        ValidationResultRepository mockResultRepo = mockResultRepository();
        GtfsSpecRepository mockSpecRepo = mockSpecRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

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

    private ValidationResultRepository mockResultRepository() {
        ValidationResultRepository mockResultRepo =  mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<Notice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                noticeList.add(errorNotice);
                return errorNotice;
            }
        });

        return mockResultRepo;
    }

    private GtfsSpecRepository mockSpecRepository() {
        GtfsSpecRepository mockSpecRepo =  mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getParserForFile(any(RawFileInfo.class))).thenAnswer(new Answer<GtfsSpecRepository.RawEntityParser>() {
            public GtfsSpecRepository.RawEntityParser answer(InvocationOnMock invocation) {
                RawFileInfo file = invocation.getArgument(0);
                if (file.getFilename().contains("invalid")) {
                    ErrorNotice fakeNotice = new CannotConstructDataProviderNotice(file.getFilename());
                    mockEntityParser = new EntityParser(List.of(fakeNotice, fakeNotice, fakeNotice));
                    return mockEntityParser;
                }
                return mockEntityParser;
            }
        });

        return mockSpecRepo;
    }

    private RawFileRepository mockFileRepository() {
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
                            return Optional.of(new EntityProvider(
                                    List.of(
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
                                    )
                            ));
                        }

                        return Optional.of(new EntityProvider(
                                List.of(
                                        Map.of("header0_string", "header0_string", "header1_float", "header1_float",
                                                "header2_integer", "header2_integer"),
                                        Map.of("header0_string", "valid_string", "header1_float", "valid_float",
                                                "header2_integer", "valid_integer"),
                                        Map.of("header0_string", "valid", "header1_float", "valid_float",
                                                "header2_integer", "valid_integer"),
                                        Map.of("header0_string", "valid", "header1_float", "invalid_float",
                                                "header2_integer", "valid_integer")
                                )
                        ));
                    }
                });

        return mockFileRepo;
    }

    private static class EntityParser implements GtfsSpecRepository.RawEntityParser {
        public int callToValidateNumericTypesCount = 0;
        public int callToParseCount = 0;

        Collection<ErrorNotice> fakeValidationResult;

        public EntityParser(Collection<ErrorNotice> fakeValidationResult) {
            this.fakeValidationResult = fakeValidationResult;
        }

        @Override
        public Collection<ErrorNotice> validateNonStringTypes(RawEntity toValidate) {

            ++callToValidateNumericTypesCount;

            return fakeValidationResult;
        }

        @Override
        public ParsedEntity parse(RawEntity toParse) {

            ++callToParseCount;

            return null;
        }
    }

    private static class EntityProvider implements RawFileRepository.RawEntityProvider {
        private int currentCount = 0;
        private List<Map<String, String>> mockEntityList;

        public EntityProvider(final List<Map<String, String>> mockEntityList) {
            this.mockEntityList = mockEntityList;
        }

        @Override
        public boolean hasNext() {
            return currentCount < mockEntityList.size() - 1;
        }

        @Override
        public RawEntity getNext() {
            ++currentCount;
            return new RawEntity(mockEntityList.get(currentCount), currentCount + 1);
        }

        @Override
        public int getHeaderCount() {
            return mockEntityList.get(0).size();
        }
    }

}