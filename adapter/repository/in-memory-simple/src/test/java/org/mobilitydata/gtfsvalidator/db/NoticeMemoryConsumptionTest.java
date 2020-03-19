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
}