package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;
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

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {
        private final List<String> mockHeaders;

        public MockSpecRepo(List<String> mockHeaders) {
            this.mockHeaders = mockHeaders;
        }

        @Override
        public List<String> getRequiredFilenameList() {
            return null;
        }

        @Override
        public List<String> getExpectedHeadersForFile(RawFileInfo fileInfo) {
            return mockHeaders;
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
            return null;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            notices.add(newError);
            return newError;
        }
    }

    @Test
    void expectedHeaderCountShouldNotGenerateNotice() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().build(),
                new MockRawFileRepo(Arrays.asList("header0", "header1", "header2")),
                resultRepo
        );

        underTest.execute();

        assertEquals(0, resultRepo.notices.size());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void expectedHeaderCountAndDifferentContentShouldGenerateNotices() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().filename("test.tst").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header3", "header4")),
                resultRepo
        );

        underTest.execute();

        assertEquals(4, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header1")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header1",
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header2")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header2",
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header3")).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("Unexpected header: header3 in file: test.tst", notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header4")).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("Unexpected header: header4 in file: test.tst", notice.getDescription());
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessHeaderCountThanExpectedShouldGenerateOneNoticePerMissingHeader() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2", "header3")),
                RawFileInfo.builder().filename("test.tst").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header1")),
                resultRepo
        );

        underTest.execute();

        assertEquals(2, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header2")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header2",
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header3")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header3",
                notice.getDescription());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessHeaderCountThanExpectedAndNonStandardHeadersPresenceShouldGenerateNotices() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().filename("test.tst").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header4")),
                resultRepo
        );

        underTest.execute();

        assertEquals(3, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header1")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header1",
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header2")).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("File test.tst is missing required header: header2",
                notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header4")).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("Unexpected header: header4 in file: test.tst", notice.getDescription());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void moreHeaderCountThanExpectedShouldGenerateOneNoticePerExtraHeader() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().filename("test.tst").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header1", "header2", "header3", "header4")),
                resultRepo
        );

        underTest.execute();

        assertEquals(2, resultRepo.notices.size());

        Notice notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header3")).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("Unexpected header: header3 in file: test.tst", notice.getDescription());

        notice = resultRepo.notices.stream()
                .filter(n -> n.getDescription().contains("header4")).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals("test.tst", notice.getFilename());
        assertEquals("Unexpected header: header4 in file: test.tst", notice.getDescription());
    }

}