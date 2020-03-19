package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.IntegerFieldValueOutOfRangeNotice;

public class NoticeMemoryConsumptionTest {

    private void generateNotices(MockValidationResultRepo mockValidationResultRepo, int numberOfNotices) {
        for (int i = 0; i < numberOfNotices; i++) {
            resultRepository.addNotice(new IntegerFieldValueOutOfRangeNotice("filename",
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

        long freeMemory = Runtime.getRuntime().freeMemory();  // bytes

        generateNotices(mockValidationResultRepo, 100);
        mockValidationResultRepo.getAll();
    }

    @Test
    public void creationOf1000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1_000);
        resultRepository.getAll();

    }

    @Test
    public void creationOf10000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 10_000);
        resultRepository.getAll();

    }

    @Test
    public void creationOf100000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 100_000);
        resultRepository.getAll();

    }

    @Test
    public void creationOf2000000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 2_000_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        generateNotices(mockValidationResultRepo, 1_000_000);
        mockValidationResultRepo.getAll();
    }
}