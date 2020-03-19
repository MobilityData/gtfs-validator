package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.IntegerFieldValueOutOfRangeNotice;

public class NoticeMemoryConsumptionTest {

    private void generateNotices(InMemoryValidationResultRepository resultRepository, int numberOfNotices) {
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
    public void creationOf100NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory();  // bytes

        System.out.println(
                String.format("Generating 100 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 455_000_000);
    }

    @Test
    public void creationOf1000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        System.out.println(
                String.format("Generating 1000 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 33_000_000);
    }

    @Test
    public void creationOf10000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 10_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        System.out.println(
                String.format("Generating 10000 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 34_000_000);
    }

    @Test
    public void creationOf100000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 100_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        System.out.println(
                String.format("Generating 100000 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 105_000_000);
    }

    @Test
    public void creationOf1000000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1_000_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        System.out.println(
                String.format("Generating 1000000 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 418_000_000);
    }

    @Test
    public void creationOf2000000NoticeShouldNotExceedMemoryLimit() {

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 2_000_000);
        resultRepository.getAll();

        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        System.out.println(
                String.format("Generating 2000000 notices: Total memory: %s megabytes, Free memory: %s megabytes, Used memory: %s megabytes",
                        totalMemory / 1_000_000,
                        freeMemory / 1_000_000,
                        (totalMemory - freeMemory) / 1_000_000)
        );

        assert (totalMemory - freeMemory < 130_000_000);
    }
}