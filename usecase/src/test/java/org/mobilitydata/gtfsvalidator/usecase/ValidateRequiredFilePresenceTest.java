package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.exception.MissingRequiredFileException;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidateRequiredFilePresenceTest {

    //mock spec repo
    private static class MockSpecRepo implements GtfsSpecRepository {

        private final int homManyReq;

        public MockSpecRepo(int howManyReq) {
            this.homManyReq = howManyReq;
        }

        @Override
        public List<String> getRequiredFilenameList() {

            List<String> toReturn = new ArrayList<>(this.homManyReq);

            for (int i = 0; i < this.homManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            return toReturn;
        }

        @Override
        public List<String> getExpectedHeadersForFile(RawFileInfo fileInfo) {
            return null;
        }
    }

    //mock raw file repo
    private static class MockRawFileRepo implements RawFileRepository {

        private final int homManyReq;
        private final int homManyOpt;

        public MockRawFileRepo(int howManyReq, int howManyOpt) {
            this.homManyReq = howManyReq;
            this.homManyOpt = howManyOpt;
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
            return null;
        }

        @Override
        public Set<String> getFilenameAll() {
            Set<String> toReturn = new HashSet<>(this.homManyReq + this.homManyOpt);

            for (int i = 0; i < this.homManyReq; ++i) {
                toReturn.add("req" + i + ".req");
            }

            for (int j = 0; j < this.homManyOpt; ++j) {
                toReturn.add("opt" + j + ".opt");
            }

            return toReturn;
        }
    }

    @Test
    void shouldRaiseNoExceptionIfAllRequiredFilesFound() {

        ValidateRequiredFilePresence underTest = new ValidateRequiredFilePresence(
                new MockSpecRepo(10),
                new MockRawFileRepo(10, 10)
        );

        //test will fail if an exception is raised
        underTest.execute();
    }

    @Test
    void shouldRaiseExceptionIfNotAllRequiredFilesFound() {

        ValidateRequiredFilePresence underTest = new ValidateRequiredFilePresence(
                new MockSpecRepo(15),
                new MockRawFileRepo(10, 10)
        );

        MissingRequiredFileException exception = assertThrows(MissingRequiredFileException.class, underTest::execute);

        List<String> expected = Arrays.asList("req10.req", "req11.req", "req12.req", "req13.req", "req14.req");
        assertEquals(expected, exception.missingFilenameList);
    }

    @Test
    void validateOptionalAll() {
    }
}