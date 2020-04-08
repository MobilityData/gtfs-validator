package org.mobilitydata.gtfsvalidator.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    private ValidationResultRepository getMockRepository() {
        List<Notice> noticeList = new ArrayList<>();

        ValidationResultRepository mockRepository = mock(InMemoryValidationResultRepository.class);
        when(mockRepository.addNotice(any(Notice.class))).thenAnswer(new Answer<Notice>() {
            public Notice answer(InvocationOnMock invocation) {
                Notice notice = invocation.getArgument(0);
                noticeList.add(notice);
                return notice;
            }
        });
        when(mockRepository.getAll()).thenAnswer(new Answer<Collection<Notice>>() {
            public Collection<Notice> answer(InvocationOnMock invocation) {
                return noticeList.stream().collect(Collectors.toUnmodifiableList());
            }
        });
        return mockRepository;
    }

    private void memoryLimitTest(boolean isMock, int noticesCount, int maxMemoryLimit) {

        ValidationResultRepository testRepository;

        if (isMock == true) {
            testRepository = getMockRepository();
        } else {
            testRepository = new InMemoryValidationResultRepository();
        }

        generateNotices(testRepository, noticesCount);
        testRepository.getAll();

        long totalMemoryInBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryInBytes = Runtime.getRuntime().freeMemory();

        logInformation(totalMemoryInBytes, freeMemoryInBytes, noticesCount);

        // assert used memory is less than the average used memory (in bytes) while taking a safety margin (given by
        // SAFETY_BUFFER_FACTOR) into account
        assert (totalMemoryInBytes - freeMemoryInBytes < maxMemoryLimit * SAFETY_BUFFER_FACTOR);
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
    public void realMemoryLimitTest_100notices() {
        memoryLimitTest(false,100, 8_000_000);
    }

    @Test
    @Order(2)
    public void realMemoryLimitTest_1000notices() {
        System.gc();
        memoryLimitTest(false,1000, 9_000_000);
    }

    @Test
    @Order(3)
    public void realMemoryLimitTest_10_000notices() {
        memoryLimitTest(false,10_000, 10_000_000);
    }

    @Test
    @Order(4)
    public void realMemoryLimitTest_100_000notices() {
        memoryLimitTest(false,100_000, 28_000_000);
    }

    @Test
    @Order(5)
    public void realMemoryLimitTest_1_000_000notices() {
        memoryLimitTest(false,1_000_000, 231_000_000);
    }

    @Test
    @Order(6)
    public void realMemoryLimitTest_2_000_000notices() {
        memoryLimitTest(false,2_000_000, 454_000_000);
    }

    @Test
    @Order(7)
    public void mockMemoryLimitTest_100notices() {
        memoryLimitTest(true,100, 10_000_000);
    }

    @Test
    @Order(8)
    public void mockMemoryLimitTest_1000notices() {
        memoryLimitTest(true, 1000, 20_000_000);
    }

    @Test
    @Order(9)
    public void mockMemoryLimitTest_10_000notices() {
        memoryLimitTest(true,10_000, 50_000_000);
    }

    @Test
    @Order(10)
    public void mockMemoryLimitTest_100_000notices() {
        memoryLimitTest(true,100_000, 100_000_000);
    }
}