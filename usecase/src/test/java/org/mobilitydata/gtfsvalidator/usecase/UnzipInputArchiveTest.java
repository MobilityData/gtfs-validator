package org.mobilitydata.gtfsvalidator.usecase;
import com.google.common.io.Resources;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnzipInputArchiveTest {

    @Test
    void unzipFolderThatContainsMacOsXFolderShouldNotGenerateNoticeAndShouldNotFillRawFileRepo()
            throws IOException {
        final RawFileRepository mockRawFileRepo = mock(RawFileRepository.class);
        final Path mockZipExtractPath = mock(Path.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        ;
        final ZipFile mockZipFile =
                new ZipFile(Resources.getResource("zip_with_invalid_content_sample.zip").getPath());
        final RawFileInfo.RawFileInfoBuilder mockBuilder = mock(RawFileInfo.RawFileInfoBuilder.class);

        final UnzipInputArchive underTest =
                new UnzipInputArchive(mockRawFileRepo, mockZipExtractPath, mockResultRepo, mockLogger, mockZipFile,
                        mockBuilder);

        underTest.execute();

        verify(mockLogger, times(1)).info("Unzipping archive");

        verifyNoInteractions(mockRawFileRepo, mockZipExtractPath, mockResultRepo);
        verifyNoMoreInteractions(mockLogger);
    }

    @Test
    void unzipFolderThatContainsFolderShouldGenerateNotice() throws IOException {
        final RawFileRepository mockRawFileRepo = mock(RawFileRepository.class);
        final Path mockZipExtractPath = mock(Path.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);
        final RawFileInfo.RawFileInfoBuilder mockBuilder = mock(RawFileInfo.RawFileInfoBuilder.class);

        final ZipFile mockZipFile =
                new ZipFile(Resources.getResource("nested_folders_sample.zip").getPath());

        final UnzipInputArchive underTest =
                new UnzipInputArchive(mockRawFileRepo, mockZipExtractPath, mockResultRepo, mockLogger, mockZipFile,
                        mockBuilder);

        underTest.execute();

        verify(mockLogger, times(1)).info("Unzipping archive");

        final ArgumentCaptor<InputZipContainsFolderNotice> captor =
                ArgumentCaptor.forClass(InputZipContainsFolderNotice.class);
        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<InputZipContainsFolderNotice> noticeList = captor.getAllValues();

        assertEquals("WARNING", noticeList.get(0).getLevel());
        assertEquals(1, noticeList.get(0).getCode());
        assertEquals(
                Resources.getResource("nested_folders_sample.zip").getPath(),
                noticeList.get(0).getFilename()
        );
        assertEquals("empty folder/", noticeList.get(0).getNoticeSpecific(Notice.KEY_FOLDER_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());
    }

    @Test
    void validZipShouldNotGenerateNoticeAndFillRawFileRepo() throws IOException {
        final RawFileRepository mockRawFileRepo = mock(RawFileRepository.class);
        final Path mockZipExtractPath = mock(Path.class);
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final RawFileInfo.RawFileInfoBuilder mockBuilder = mock(RawFileInfo.RawFileInfoBuilder.class);
        final RawFileInfo mockRawFileInfo = mock(RawFileInfo.class);
        when(mockBuilder.filename(anyString())).thenCallRealMethod();
        when(mockBuilder.path(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenReturn(mockRawFileInfo);

        final Logger mockLogger = mock(Logger.class);

        when(mockZipExtractPath.toAbsolutePath()).thenReturn(
                Path.of(Resources.getResource("valid_zip_sample.zip").getPath()));
        when(mockZipExtractPath.resolve("agency.txt")).thenReturn(Path.of("./agency.txt"));
        when(mockZipExtractPath.resolve("transfers.txt")).thenReturn(Path.of("./transfers.txt"));

        final ZipFile mockZipFile = new ZipFile(Resources.getResource("valid_zip_sample.zip").getPath());

        final UnzipInputArchive underTest =
                new UnzipInputArchive(mockRawFileRepo, mockZipExtractPath, mockResultRepo, mockLogger, mockZipFile,
                        mockBuilder);

        underTest.execute();

        verify(mockLogger, times(1)).info("Unzipping archive");

        verify(mockZipExtractPath, times(1)).resolve("agency.txt");
        verify(mockZipExtractPath, times(1)).resolve("transfers.txt");
        verify(mockZipExtractPath, times(2)).toAbsolutePath();

        verify(mockRawFileRepo, times(2)).create(mockRawFileInfo);
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockLogger, mockRawFileRepo, mockZipExtractPath);

        // remove files created for test purpose. If files are not removed, an Exception will be thrown. Said exception
        // will be caught and a CannotUnzipFileNotice will be generated and added to the validation  result repository.
        File toDelete = new File("./agency.txt");
        toDelete.delete();
        toDelete = new File("./transfers.txt");
        toDelete.delete();
    }
}
