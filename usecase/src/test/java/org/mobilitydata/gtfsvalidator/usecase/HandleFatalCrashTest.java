package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OutOfMemoryNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;
import org.mockito.ArgumentCaptor;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HandleFatalCrashTest {

    @Test
    void shouldGenerateAndAddOutOfMemoryNoticeToResultRepoWhenOutOfMemoryErrorIsThrown() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getNoticeCount()).thenReturn(5);
        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), anyString())).thenReturn(60L);
        Path mockPath = mock(Path.class);
        HandleFatalCrash underTest = new HandleFatalCrash(mockResultRepo, mockFileUtils, mockPath);

        underTest.execute();
        final ArgumentCaptor<OutOfMemoryNotice> captor =
                ArgumentCaptor.forClass(OutOfMemoryNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<OutOfMemoryNotice> noticeList = captor.getAllValues();

        assertNull(noticeList.get(0).getFilename());
        assertEquals("Out of memory notice", noticeList.get(0).getTitle());
        assertEquals(
                String.format("Out of memory error might have been raised because dataset was too big " +
                        "(dataset size: %f mb) or because too many notices were generated (notice count: %d)",
                        60f,
                        5),
                noticeList.get(0).getDescription());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(60f, noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_SIZE_MEGABYTES));
        assertEquals(5, noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_NOTICE_COUNT));

        verify(mockFileUtils, times(1)).sizeOf(mockPath, CustomFileUtils.MEGABYTES);
        verify(mockResultRepo, times(1)).getNoticeCount();
        verifyNoMoreInteractions(mockFileUtils, mockPath, mockResultRepo);
    }

    @Test
    void shouldGenerateAndAddValidatorCrashNoticeToResultRepoWhenExceptionIsThrown() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        Path mockPath = mock(Path.class);
        HandleFatalCrash underTest = new HandleFatalCrash(mockResultRepo, mockFileUtils, mockPath);
        Exception mockException = mock(Exception.class);
        when(mockException.getMessage()).thenReturn("exception message");
        StackTraceElement mockStackTraceElement = mock(StackTraceElement.class);
        StackTraceElement[] mockArray = {mockStackTraceElement};

        when(mockException.getStackTrace()).thenReturn(mockArray);

        underTest.execute(mockException);
        final ArgumentCaptor<ValidatorCrashNotice> captor =
                ArgumentCaptor.forClass(ValidatorCrashNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<ValidatorCrashNotice> noticeList = captor.getAllValues();

        assertNull(noticeList.get(0).getFilename());
        assertEquals("Fatal error", noticeList.get(0).getTitle());
        assertEquals(
                String.format("An exception occurred and the validator could not complete the validation process." +
                                " See detailed message for more information: %s -- stack trace: %s",
                        "exception message",
                        Arrays.toString(mockArray)),
                noticeList.get(0).getDescription());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("exception message", noticeList.get(0).getNoticeSpecific(Notice.KEY_EXCEPTION_MESSAGE));
        assertEquals(Arrays.toString(mockArray), noticeList.get(0).getNoticeSpecific(Notice.KEY_EXCEPTION_STACK_TRACE));

        verify(mockException, times(1)).getMessage();
        verify(mockException, times(1)).getStackTrace();
        verifyNoMoreInteractions(mockFileUtils, mockPath, mockResultRepo, mockException);
    }
}
