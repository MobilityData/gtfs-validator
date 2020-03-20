package org.mobilitydata.gtfsvalidator.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.IntegerFieldValueOutOfRangeNotice;

public class NoticeMemoryConsumptionTest {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CONVERSION_FACTOR = 1_000_000;
    private static final float BUFFER = 1.10f;

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

    @BeforeEach
    public void cleanMemoryBeforeTest() {
        System.gc();
    }

    @AfterEach
    public void cleanMemoryAfterTest() {
        System.gc();
    }

    @Test
    public void creationOf100NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory();  // bytes

        LOGGER.info(String.format("Generating 100 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 7_000_000 * BUFFER);
    }

    @Test
    public void creationOf1000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 1_000);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        LOGGER.info(String.format("Generating 1000 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 8_000_000 * BUFFER);
    }

    @Test
    public void creationOf10000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 10_000);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        LOGGER.info(String.format("Generating 10 000 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 9_000_000 * BUFFER);
    }

    @Test
    public void creationOf100000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 100_000);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        LOGGER.info(String.format("Generating 100 000 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 28_000_000 * BUFFER);
    }

    @Test
    public void creationOf1000000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, CONVERSION_FACTOR);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        LOGGER.info(String.format("Generating 1 000 000 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 231_000_000 * BUFFER);
    }

    @Test
    public void creationOf2000000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, 2_000_000);
        resultRepository.getAll();

        long totalMemory = Runtime.getRuntime().totalMemory(); // bytes
        long freeMemory = Runtime.getRuntime().freeMemory(); // bytes

        LOGGER.info(String.format("Generating 2 000 000 notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                totalMemory / CONVERSION_FACTOR,
                freeMemory / CONVERSION_FACTOR,
                (totalMemory - freeMemory) / CONVERSION_FACTOR)
        );

        assert (totalMemory - freeMemory < 454_000_000 * BUFFER);
    }
}