package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.exception.MissingRequiredFileException;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidateFileNameTest {

    //Mock spec repo
    private static class mockSpecRepo implements GtfsSpecRepository {

        private final int homManyReq;

        public mockSpecRepo(int howManyReq) {
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
    }

    //mock raw file repo
    private static class mockRawFileRepo implements RawFileRepository {

        private final int homManyReq;
        private final int homManyOpt;

        public mockRawFileRepo(int howManyReq, int howManyOpt) {
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

        ValidateFileName underTest = new ValidateFileName(
                new mockSpecRepo(10),
                new mockRawFileRepo(10, 10)
        );

        //test will fail if an exception is raised
        underTest.validateRequiredAll();
    }

    @Test
    void shouldRaiseExceptionIfNotAllRequiredFilesFound() {

        ValidateFileName underTest = new ValidateFileName(
                new mockSpecRepo(15),
                new mockRawFileRepo(10, 10)
        );

        MissingRequiredFileException exception = assertThrows(MissingRequiredFileException.class, underTest::validateRequiredAll);

        List<String> expected = Arrays.asList("req10.req", "req11.req", "req12.req", "req13.req", "req14.req");
        assertEquals(expected, exception.missingFilenameList);
    }

    @Test
    void validateOptionalAll() {
    }
}