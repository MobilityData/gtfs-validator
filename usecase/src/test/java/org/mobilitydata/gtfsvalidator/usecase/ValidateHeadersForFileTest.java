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
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateHeadersForFileTest {

    private static final String REQUIRED_HEADER_0 = "requiredHeader0";
    private static final String REQUIRED_HEADER_1 = "requiredHeader1";
    private static final String REQUIRED_HEADER_2 = "requiredHeader2";
    private static final String REQUIRED_HEADER_3 = "requiredHeader3";
    private static final String REQUIRED_HEADER_4 = "requiredHeader4";

    private static final String OPTIONAL_HEADER_0 = "optionalHeader0";
    private static final String OPTIONAL_HEADER_1 = "optionalHeader1";

    private static final String EXTRA_HEADER_0 = "extraHeader0";
    private static final String EXTRA_HEADER_1 = "extraHeader1";

    private static final String TEST_TST = "test.tst";

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {
        private final List<String> mockRequiredHeaders;
        private final List<String> mockOptionalHeaders;

        public MockSpecRepo(List<String> mockRequiredHeaders, List<String> mockOptionalHeaders) {
            this.mockRequiredHeaders = mockRequiredHeaders;
            this.mockOptionalHeaders = mockOptionalHeaders;
        }

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
            return mockRequiredHeaders;
        }

        @Override
        public List<String> getOptionalHeadersForFile(RawFileInfo fileInfo) {
            return mockOptionalHeaders;
        }


        @Override
        public RawEntityParser getParserForFile(RawFileInfo file) {
            return null;
        }

        @Override
        public ParsedEntityTypeValidator getValidatorForFile(RawFileInfo file) {
            return null;
        }
    }

    //mock raw file repo
    private static class MockRawFileRepo implements RawFileRepository {
        private final Collection<String> mockHeaders;

        public MockRawFileRepo(Collection<String> mockHeaders) {
            this.mockHeaders = mockHeaders;
        }

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
            return mockHeaders;
        }

        @Override
        public Set<String> getFilenameAll() {
            return null;
        }

        @Override
        public Optional<RawEntityProvider> getProviderForFile(RawFileInfo file) {
            return Optional.empty();
        }
    }

    //mock result repo
    private static class MockResultRepo implements ValidationResultRepository {
        public List<Notice> notices = new ArrayList<>();

        @Override
        public InfoNotice addNotice(InfoNotice newInfo) {
            return null;
        }

        @Override
        public WarningNotice addNotice(WarningNotice newWarning) {
            notices.add(newWarning);
            return newWarning;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            notices.add(newError);
            return newError;
        }

        @Override
        public Collection<Notice> getAll() {
            return null;
        }

        @Override
        public Notice addNotice(Notice newNotice) {
            return null;
        }
    }

    @Test
    void expectedHeaderCountShouldNotGenerateNotice() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2), Collections.emptyList()),
                RawFileInfo.builder().build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2)),
                resultRepo
        );

        underTest.execute();

        assertEquals(0, resultRepo.notices.size());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void expectedRequiredHeaderCountAndDifferentContentShouldGenerateNotices() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2), Collections.emptyList()),
                RawFileInfo.builder().filename(TEST_TST).build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_3, REQUIRED_HEADER_4)),
                resultRepo
        );

        underTest.execute();

        assertEquals(4, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_1,
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_3)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + REQUIRED_HEADER_3 + " in file:test.tst", notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_4)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + REQUIRED_HEADER_4 + " in file:test.tst", notice.getDescription());
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessRequiredHeaderCountThanExpectedShouldGenerateOneNoticePerMissingHeader() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2, REQUIRED_HEADER_3), Collections.emptyList()),
                RawFileInfo.builder().filename(TEST_TST).build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1)),
                resultRepo
        );

        underTest.execute();

        assertEquals(2, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_3)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_3,
                notice.getDescription());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessRequiredHeaderCountThanExpectedAndNonStandardHeadersPresenceShouldGenerateNotices() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2), Collections.emptyList()),
                RawFileInfo.builder().filename(TEST_TST).build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, EXTRA_HEADER_0)),
                resultRepo
        );

        underTest.execute();

        assertEquals(3, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_1,
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_0)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_0 + " in file:test.tst", notice.getDescription());
    }

    @Test
    void presenceOfKnownOptionalHeaderShouldNotGenerateNotices() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2), Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1)),
                RawFileInfo.builder().filename(TEST_TST).build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2, OPTIONAL_HEADER_0, OPTIONAL_HEADER_1)),
                resultRepo
        );

        underTest.execute();

        assertEquals(0, resultRepo.notices.size());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void unexpectedOptionalHeaderShouldGenerateNotices() {
        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2), Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1)),
                RawFileInfo.builder().filename(TEST_TST).build(),
                new MockRawFileRepo(Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2, OPTIONAL_HEADER_0, OPTIONAL_HEADER_1, EXTRA_HEADER_0, EXTRA_HEADER_1)),
                resultRepo
        );

        underTest.execute();

        assertEquals(2, resultRepo.notices.size());
        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_0)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_0 + " in file:test.tst", notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_1 + " in file:test.tst", notice.getDescription());

    }

}