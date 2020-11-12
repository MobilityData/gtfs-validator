package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotDownloadArchiveFromNetworkNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.TooBigDatasetNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DownloadArchiveFromNetworkTest {

    @Test
    void downloadDataFromValidUrlShouldNotGenerateNotice() throws IOException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.hasExecParamValue(anyString())).thenReturn(true);
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY)))
                .thenReturn(
                        Path.of("./src/test/resources/download_archive_from_network_test_directory")
                                .toString());
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY)))
                .thenReturn("https://octa.net/current/google_transit.zip");

        Logger mockLogger = mock(Logger.class);

        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), any())).thenReturn(40L);

        Path mockPath = mock(Path.class);

        DownloadArchiveFromNetwork underTest =
                new DownloadArchiveFromNetwork(mockResultRepo, mockExecParamRepo, mockLogger, mockFileUtils, mockPath);

        underTest.execute();

        verifyNoInteractions(mockResultRepo);
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .hasExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY));
        verify(mockFileUtils, times(1)).sizeOf(mockPath, CustomFileUtils.Unit.MEGABYTES);
        verify(mockLogger, times(1)).info("Downloading archive");
        verifyNoMoreInteractions(mockExecParamRepo, mockLogger, mockFileUtils, mockPath);

        // remove created files
        File toDelete = new File(
                Path.of("./src/test/resources/download_archive_from_network_test_directory").toString());
        assertTrue(toDelete.delete());
    }

    @Test
    void invalidUrlShouldGenerateAddCannotDownloadArchiveFromNetworkNoticeToResultRepoAndThrowException() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.hasExecParamValue(anyString())).thenReturn(true);
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY)))
                .thenReturn(
                        Path.of("./src/test/resources/download_archive_from_network_test_directory")
                                .toString());
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY)))
                .thenReturn("invalid url");

        Logger mockLogger = mock(Logger.class);

        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), any())).thenReturn(40L);

        Path mockPath = mock(Path.class);

        DownloadArchiveFromNetwork underTest =
                new DownloadArchiveFromNetwork(mockResultRepo, mockExecParamRepo, mockLogger, mockFileUtils, mockPath);

        assertThrows(IOException.class, underTest::execute);

        final ArgumentCaptor<CannotDownloadArchiveFromNetworkNotice> captor =
                ArgumentCaptor.forClass(CannotDownloadArchiveFromNetworkNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<CannotDownloadArchiveFromNetworkNotice> noticeList = captor.getAllValues();

        assertEquals("invalid url", noticeList.get(0).getFilename());
        assertEquals("invalid url", noticeList.get(0).getNoticeSpecific(Notice.KEY_URL_VALUE));

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .hasExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY));
        verify(mockLogger, times(1)).info("Downloading archive");
        verifyNoMoreInteractions(mockExecParamRepo, mockFileUtils, mockLogger, mockPath, mockResultRepo);
    }

    @Test
    void tooBigDatasetShouldGenerateAndAddDatasetTooBigNoticeToResultRepo() throws IOException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.hasExecParamValue(anyString())).thenReturn(true);
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY)))
                .thenReturn(
                        Path.of("./src/test/resources/download_archive_from_network_test_directory")
                                .toString());
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY)))
                .thenReturn("https://octa.net/current/google_transit.zip");

        Logger mockLogger = mock(Logger.class);

        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), any())).thenReturn(80L);

        Path mockPath = mock(Path.class);

        DownloadArchiveFromNetwork underTest =
                new DownloadArchiveFromNetwork(mockResultRepo, mockExecParamRepo, mockLogger, mockFileUtils, mockPath);

        underTest.execute();

        final ArgumentCaptor<TooBigDatasetNotice> captor =
                ArgumentCaptor.forClass(TooBigDatasetNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<TooBigDatasetNotice> noticeList = captor.getAllValues();

        assertNull(noticeList.get(0).getFilename());
        assertEquals(80f, noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_SIZE_MEGABYTES));
        assertEquals(
                RawFileRepository.MAX_RAW_INPUT_SIZE_MEGABYTES,
                noticeList.get(0).getNoticeSpecific(Notice.KEY_DATASET_MAX_SIZE_MEGABYTES));

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .hasExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY));
        verify(mockFileUtils, times(1)).sizeOf(mockPath, CustomFileUtils.Unit.MEGABYTES);
        verify(mockLogger, times(1)).info("Downloading archive");
        verifyNoMoreInteractions(mockExecParamRepo, mockFileUtils, mockLogger, mockPath, mockResultRepo);

        // remove created files
        File toDelete = new File(
                Path.of("./src/test/resources/download_archive_from_network_test_directory").toString());
        assertTrue(toDelete.delete());
    }

    @Test
    void notTooBigDatasetShouldNotGenerateNotice() throws IOException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        ExecParamRepository mockExecParamRepo = mock(ExecParamRepository.class);
        when(mockExecParamRepo.hasExecParamValue(anyString())).thenReturn(true);
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY)))
                .thenReturn(
                        Path.of("./src/test/resources/download_archive_from_network_test_directory")
                                .toString());
        when(mockExecParamRepo.getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY)))
                .thenReturn("https://octa.net/current/google_transit.zip");

        Logger mockLogger = mock(Logger.class);

        CustomFileUtils mockFileUtils = mock(CustomFileUtils.class);
        when(mockFileUtils.sizeOf(any(), any())).thenReturn(45L);

        Path mockPath = mock(Path.class);

        DownloadArchiveFromNetwork underTest =
                new DownloadArchiveFromNetwork(mockResultRepo, mockExecParamRepo, mockLogger, mockFileUtils, mockPath);

        underTest.execute();

        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .hasExecParamValue(ArgumentMatchers.eq(ExecParamRepository.URL_KEY));
        verify(mockExecParamRepo, times(1))
                .getExecParamValue(ArgumentMatchers.eq(ExecParamRepository.INPUT_KEY));
        verify(mockFileUtils, times(1)).sizeOf(mockPath, CustomFileUtils.Unit.MEGABYTES);
        verify(mockLogger, times(1)).info("Downloading archive");
        verifyNoMoreInteractions(mockExecParamRepo, mockFileUtils, mockLogger, mockPath, mockResultRepo);

        // remove created files
        File toDelete = new File(
                Path.of("./src/test/resources/download_archive_from_network_test_directory").toString());
        assertTrue(toDelete.delete());
    }
}
