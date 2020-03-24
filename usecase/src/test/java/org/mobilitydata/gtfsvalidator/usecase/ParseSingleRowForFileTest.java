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
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

class ParseSingleRowForFileTest {

    @Test
    void shouldValidateAndParseOneByOne() {

        MockResultRepo resultRepo = new MockResultRepo();
        MockSpecRepo specRepo = new MockSpecRepo();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test.tst").build(),
                new MockRawFileRepo(),
                specRepo,
                resultRepo
        );

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, resultRepo.noticeList.size());
        assertEquals(1, specRepo.parser.callToValidateNumericTypesCount);
        assertEquals(1, specRepo.parser.callToParseCount);

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, resultRepo.noticeList.size());
        assertEquals(2, specRepo.parser.callToValidateNumericTypesCount);
        assertEquals(2, specRepo.parser.callToParseCount);

        assertTrue(underTest.hasNext());
        underTest.execute();
        assertEquals(0, resultRepo.noticeList.size());
        assertEquals(3, specRepo.parser.callToValidateNumericTypesCount);
        assertEquals(3, specRepo.parser.callToParseCount);

        assertFalse(underTest.hasNext());
        assertNull(underTest.execute());
    }

    @Test
    void shouldWriteNoticesToRepo() {

        MockResultRepo resultRepo = new MockResultRepo();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_invalid.tst").build(),
                new MockRawFileRepo(),
                new MockSpecRepo(),
                resultRepo
        );

        underTest.execute();
        assertEquals(3, resultRepo.noticeList.size());
        underTest.execute();
        assertEquals(6, resultRepo.noticeList.size());
        underTest.execute();
        assertEquals(9, resultRepo.noticeList.size());
    }

    @Test
    void providerErrorShouldGenerateNotice() {

        MockResultRepo resultRepo = new MockResultRepo();

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_empty.tst").build(),
                new MockRawFileRepo(),
                new MockSpecRepo(),
                resultRepo
        );

        underTest.execute();
        assertEquals(1, resultRepo.noticeList.size());
        Notice notice = resultRepo.noticeList.get(0);
        assertThat(notice, instanceOf(CannotConstructDataProviderNotice.class));
        assertEquals("E002", notice.getId());
        assertEquals("Data provider error", notice.getTitle());
        assertEquals("test_empty.tst", notice.getFilename());
        assertEquals("An error occurred while trying to access raw data for file: test_empty.tst", notice.getDescription());
    }

    private static class MockEntityParser implements GtfsSpecRepository.RawEntityParser {
        public int callToValidateNumericTypesCount = 0;
        public int callToParseCount = 0;

        Collection<ErrorNotice> fakeValidationResult;

        public MockEntityParser(Collection<ErrorNotice> fakeValidationResult) {
            this.fakeValidationResult = fakeValidationResult;
        }

        @Override
        public Collection<ErrorNotice> validateNumericTypes(RawEntity toValidate) {

            ++callToValidateNumericTypesCount;

            return fakeValidationResult;
        }

        @Override
        public ParsedEntity parse(RawEntity toParse) {

            ++callToParseCount;

            return null;
        }
    }

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {

        public MockEntityParser parser = new MockEntityParser(Collections.emptyList());

        @Override
        public List<String> getRequiredFilenameList() {
            return null;
        }

        @Override
        public List<String> getOptionalFilenameList() {
            return null;
        }

        @Override
        public List<String> getRequiredHeadersForFile(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public List<String> getOptionalHeadersForFile(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public RawEntityParser getParserForFile(RawFileInfo file) {
            if (file.getFilename().contains("invalid")) {
                ErrorNotice fakeNotice = new CannotConstructDataProviderNotice(file.getFilename());
                parser = new MockEntityParser(List.of(fakeNotice, fakeNotice, fakeNotice));
                return parser;
            }

            return parser;
        }

        @Override
        public ParsedEntityTypeValidator getValidatorForFile(RawFileInfo file) {
            return null;
        }
    }

    private static class MockEntityProvider implements RawFileRepository.RawEntityProvider {
        private int currentCount = 0;
        private List<Map<String, String>> mockEntityList;

        public MockEntityProvider(final List<Map<String, String>> mockEntityList) {
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

    private static class MockRawFileRepo implements RawFileRepository {

        @Override
        public RawFileInfo create(RawFileInfo fileInfo) {
            return null;
        }

        @Override
        public Optional<RawFileInfo> findByName(String filename) {
            return Optional.empty();
        }

        @Override
        public Collection<String> getActualHeadersForFile(RawFileInfo file) {
            return null;
        }

        @Override
        public Set<String> getFilenameAll() {
            return null;
        }

        @Override
        public Optional<RawEntityProvider> getProviderForFile(RawFileInfo file) {
            if (file.getFilename().contains("empty")) {
                return Optional.empty();
            }

            if (file.getFilename().contains("invalid")) {
                return Optional.of(new MockEntityProvider(
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

            return Optional.of(new MockEntityProvider(
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
    }

    private static class MockResultRepo implements ValidationResultRepository {

        public List<Notice> noticeList = new ArrayList<>();

        @Override
        public InfoNotice addNotice(InfoNotice newInfo) {
            return null;
        }

        @Override
        public WarningNotice addNotice(WarningNotice newWarning) {
            return null;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            noticeList.add(newError);
            return newError;
        }

        @Override
        public Notice addNotice(Notice newNotice) {
            return null;
        }

        @Override
        public Collection<Notice> getAll() {
            return null;
        }

        @Override
        public NoticeExporter getExporter(boolean outputAsProto, String outputPath) {
            return null;
        }
    }

}