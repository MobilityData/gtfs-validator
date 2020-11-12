package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OutOfMemoryNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;
import org.mockito.ArgumentCaptor;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HandleOutOfMemoryErrorTest {

    @Test
    void shouldGenerateAndAddOutOfMemoryNoticeToResultRepoWhenOutOfMemoryErrorIsThrown() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.getNoticeCount()).thenReturn(5);
        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), any())).thenReturn(60L);
        Path mockPath = mock(Path.class);
        HandleOutOfMemoryError underTest = new HandleOutOfMemoryError(mockResultRepo, mockFileUtils, mockPath);

        underTest.execute();
        final ArgumentCaptor<OutOfMemoryNotice> captor =
                ArgumentCaptor.forClass(OutOfMemoryNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<OutOfMemoryNotice> noticeList = captor.getAllValues();

        assertNull(noticeList.get(0).getFilename());
        assertEquals("Out of memory", noticeList.get(0).getTitle());
        assertEquals(
                String.format("Out of memory error might have been raised because dataset was too big " +
                        "(dataset size: %f mb) or because too many notices were generated (notice count: %d)",
                        60f,
                        5),
                noticeList.get(0).getDescription());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(60f, noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_SIZE_MEGABYTES));
        assertEquals(5, noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_NOTICE_COUNT));

        verify(mockFileUtils, times(1)).sizeOf(mockPath, CustomFileUtils.Unit.MEGABYTES);
        verify(mockResultRepo, times(1)).getNoticeCount();
        verifyNoMoreInteractions(mockFileUtils, mockPath, mockResultRepo);
    }
}
