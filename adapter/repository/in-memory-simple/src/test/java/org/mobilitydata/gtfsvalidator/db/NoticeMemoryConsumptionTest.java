package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoticeMemoryConsumptionTest {

    private void generateNotices(MockValidationResultRepo mockValidationResultRepo, int numberOfNotices) {
        for (int i = 0; i < numberOfNotices; i++) {
            mockValidationResultRepo.addNotice(new IntegerFieldValueOutOfRangeNotice("filename",
                    "fieldname",
                    "entity_id",
                    0,
                    100,
                    101)
            );
        }
    }

    @Test
    public void creationOf100NoticeShouldNotRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        generateNotices(mockValidationResultRepo, 100);
        mockValidationResultRepo.getAll();
    }

    @Test
    public void creationOf1000NoticeShouldNotRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        generateNotices(mockValidationResultRepo, 1_000);
        mockValidationResultRepo.getAll();

    }

    @Test
    public void creationOf10000NoticeShouldNotRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        generateNotices(mockValidationResultRepo, 10_000);
        mockValidationResultRepo.getAll();

    }

    @Test
    public void creationOf100000NoticeShouldNotRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        generateNotices(mockValidationResultRepo, 100_000);
        mockValidationResultRepo.getAll();

    }

    @Test
    public void creationOf1_000_000NoticeShouldNotRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        generateNotices(mockValidationResultRepo, 1_000_000);
        mockValidationResultRepo.getAll();
    }

    @Test
    public void creationOf3_000_000NoticeShouldRaiseException() {

        MockValidationResultRepo mockValidationResultRepo = new MockValidationResultRepo();

        try {
            for (int i = 0; i < 3_000_000; i++) {
                mockValidationResultRepo.addNotice(new IntegerFieldValueOutOfRangeNotice("filename",
                        "fieldname",
                        "entity_id",
                        0,
                        100,
                        101)
                );
            }
            mockValidationResultRepo.getAll();

        } catch (OutOfMemoryError outOfMemory) {
            System.out.println("Catching out of memory error");
        }
    }

    //mock validation result repo
    private static class MockValidationResultRepo implements ValidationResultRepository {
        private final List<InfoNotice> infoNoticeList = new ArrayList<>();
        private final List<WarningNotice> warningNoticeList = new ArrayList<>();
        private final List<ErrorNotice> errorNoticeList = new ArrayList<>();

        @Override
        public InfoNotice addNotice(InfoNotice newInfo) {
            infoNoticeList.add(newInfo);
            return newInfo;
        }

        @Override
        public WarningNotice addNotice(WarningNotice newWarning) {
            warningNoticeList.add(newWarning);
            return newWarning;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            errorNoticeList.add(newError);
            return newError;
        }

        @Override
        public Collection<Notice> getAll() {
            return Stream.concat(
                    Stream.concat(
                            infoNoticeList.stream(),
                            warningNoticeList.stream()),
                    errorNoticeList.stream())
                    .collect(Collectors.toUnmodifiableList());
        }

        @Override
        public Notice addNotice(Notice newNotice) {
            return newNotice.visit(this);
        }
    }
}
