package org.mobilitydata.gtfsvalidator.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoticeMemoryConsumptionTest {

    private static final Logger LOGGER = LogManager.getLogger();

    // used to provide a 10% safety margin to avoid instability due to the behavior of the garbage collector
    private static final float SAFETY_BUFFER_FACTOR = 1.10f;

    private void generateNotices(ValidationResultRepository resultRepository, int numberOfNotices) {
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

    private void logInformation(long totalMemoryInBytes, long freeMemoryInBytes, int noticesCount) {
        LOGGER.info(String.format("Generating %s notices: Total memory: %s megabytes, Free memory: %s megabytes," +
                        " Used memory: %s megabytes",
                noticesCount,
                totalMemoryInBytes / 1_000_000, // converting bytes to megabytes
                freeMemoryInBytes / 1_000_000, // converting bytes to megabytes
                (totalMemoryInBytes - freeMemoryInBytes) / 1_000_000) // converting bytes to megabytes
        );
    }

    private void memoryLimitTest(int noticesCount, int maxMemoryLimit) {

        ValidationResultRepository underTest = new InMemoryValidationResultRepository();

        generateNotices(underTest, noticesCount);
        underTest.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticesCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assertTrue(totalMemoryInBytes - freeMemoryInBytes < maxMemoryLimit * SAFETY_BUFFER_FACTOR);
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
    @Order(1)
    public void memoryLimitTest_100notices() {
        memoryLimitTest(100, 10_000_000);
    }

    @Test
    @Order(2)
    public void memoryLimitTest_1000notices() {
        memoryLimitTest(1000, 10_000_000);
    }

    @Test
    @Order(3)
    public void memoryLimitTest_10_000notices() {
        memoryLimitTest(10_000, 11_000_000);
    }

    @Test
    @Order(4)
    public void memoryLimitTest_100_000notices() {
        memoryLimitTest(100_000, 30_000_000);
    }

    @Test
    @Order(5)
    public void memoryLimitTest_1_000_000notices() {
        memoryLimitTest(1_000_000, 231_000_000);
    }

    @Test
    @Order(6)
    public void memoryLimitTest_2_000_000notices() {
        memoryLimitTest(2_000_000, 454_000_000);
    }

}