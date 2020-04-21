package org.mobilitydata.gtfsvalidator.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;

public class NoticeMemoryConsumptionTest {

    private static final Logger LOGGER = LogManager.getLogger();

    // used to provide a 10% safety margin to avoid instability due to the behavior of the garbage collector
    private static final float SAFETY_BUFFER_FACTOR = 1.15f;

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

    private void logInformation(long totalMemoryInBytes, long freeMemoryInBytes, int noticeCount) {
        LOGGER.info(String.format("Generating %s notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                noticeCount,
                totalMemoryInBytes / 1_000_000, // converting bytes to megabytes
                freeMemoryInBytes / 1_000_000, // converting bytes to megabytes
                (totalMemoryInBytes - freeMemoryInBytes) / 1_000_000) // converting bytes to megabytes
        );
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

        int noticeCount = 100;

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        generateNotices(resultRepository, noticeCount);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 7_000_000 * SAFETY_BUFFER_FACTOR);
    }

    @Test
    public void creationOf1000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        int noticeCount = 1_000;

        generateNotices(resultRepository, noticeCount);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 8_000_000 * SAFETY_BUFFER_FACTOR);
    }

    @Test
    public void creationOf10000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        int noticeCount = 10_000;

        generateNotices(resultRepository, 10_000);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 9_000_000 * SAFETY_BUFFER_FACTOR);
    }

    @Test
    public void creationOf100000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        int noticeCount = 100_000;

        generateNotices(resultRepository, noticeCount);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 28_000_000 * SAFETY_BUFFER_FACTOR);
    }

    @Test
    public void creationOf1000000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        int noticeCount = 1_000_000;

        generateNotices(resultRepository, noticeCount);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 231_000_000 * SAFETY_BUFFER_FACTOR);
    }

    @Test
    public void creationOf2000000NoticeShouldNotExceedMemoryLimit() {

        InMemoryValidationResultRepository resultRepository = new InMemoryValidationResultRepository();

        int noticeCount = 2_000_000;

        generateNotices(resultRepository, noticeCount);
        resultRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticeCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < 454_000_000 * SAFETY_BUFFER_FACTOR);
    }
}