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
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateAllRowLengthForFileTest {

    @Test
    void expectedLengthForAllShouldNotGenerateNotice() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test.tst")
                        .build(),
                new MockRawFileRepo(),
                resultRepo
        );

        underTest.execute();

        assertEquals(0, resultRepo.noticeList.size());
    }

    @Test
    void invalidRowsShouldGenerateError() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_invalid.tst")
                        .build(),
                new MockRawFileRepo(),
                resultRepo
        );

        underTest.execute();

        assertEquals(2, resultRepo.noticeList.size());

        Notice notice = resultRepo.noticeList.get(0);
        assertThat(notice, instanceOf(InvalidRowLengthNotice.class));
        assertEquals("E004", notice.getId());
        assertEquals("Invalid row length", notice.getTitle());
        assertEquals("test_invalid.tst", notice.getFilename());
        assertEquals("Invalid length for row:2 -- expected:3 actual:2", notice.getDescription());

        notice = resultRepo.noticeList.get(1);
        assertThat(notice, instanceOf(InvalidRowLengthNotice.class));
        assertEquals("E004", notice.getId());
        assertEquals("Invalid row length", notice.getTitle());
        assertEquals("test_invalid.tst", notice.getFilename());
        assertEquals("Invalid length for row:4 -- expected:3 actual:4", notice.getDescription());
    }

    @Test
    void dataProviderConstructionIssueShouldGenerateError() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_empty.tst")
                        .build(),
                new MockRawFileRepo(),
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
                                Map.of("h0", "header0_name", "h1", "header1_name",
                                        "h2", "header2_name"),
                                Map.of("h0", "v0", "h1", "v1"),
                                Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                                Map.of("h0", "v0", "h1", "v1", "h2", "v2", "h3", "v3")
                        )
                ));
            }

            return Optional.of(new MockEntityProvider(
                    List.of(
                            Map.of("h0", "header0_name", "h1", "header1_name",
                                    "h2", "header2_name"),
                            Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                            Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                            Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                            Map.of("h0", "v0", "h1", "v1", "h2", "v2")
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