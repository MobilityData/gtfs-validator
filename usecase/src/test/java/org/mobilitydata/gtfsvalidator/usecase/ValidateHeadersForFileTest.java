package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.NonStandardHeadersNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.MissingHeadersNotice;
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
            return null;
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
    }

    @Test
    void expectedHeaderCountAndContentShouldNotGenerateNotice() {

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


    @Test
    void lessHeaderCountThanExpectedShouldGenerateError() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().filename("test").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header1")),
                resultRepo
        );

        underTest.execute();

        assertEquals(1, resultRepo.notices.size());
        Notice notice = resultRepo.notices.get(0);
        assertThat(notice, instanceOf(MissingHeadersNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Invalid headers", notice.getTitle());
        assertEquals("test", notice.getFilename());
        assertEquals("expected: [header0, header1, header2]  actual: [header0, header1]",
                notice.getDescription());
    }

    @Test
    void moreHeaderCountThanExpectedShouldGenerateWarning() {

        MockResultRepo resultRepo = new MockResultRepo();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                new MockSpecRepo(Arrays.asList("header0", "header1", "header2")),
                RawFileInfo.builder().filename("test").build(),
                new MockRawFileRepo(Arrays.asList("header0", "header1", "header2", "header3")),
                resultRepo
        );

        underTest.execute();

        assertEquals(1, resultRepo.notices.size());
        Notice notice = resultRepo.notices.get(0);
        assertThat(notice, instanceOf(NonStandardHeadersNotice.class));
        assertEquals("W001", notice.getId());
        assertEquals("Non standard headers", notice.getTitle());
        assertEquals("test", notice.getFilename());
        assertEquals("extra: [header3]", notice.getDescription());
    }
}