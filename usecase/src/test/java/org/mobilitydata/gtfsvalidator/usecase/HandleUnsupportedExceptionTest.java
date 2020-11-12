package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class HandleUnsupportedExceptionTest {
@Test
    void shouldGenerateAndAddValidatorCrashNoticeToResultRepoWhenExceptionIsThrown() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        HandleUnsupportedException underTest = new HandleUnsupportedException(mockResultRepo);
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
        verifyNoMoreInteractions(mockResultRepo, mockException);
    }
}
