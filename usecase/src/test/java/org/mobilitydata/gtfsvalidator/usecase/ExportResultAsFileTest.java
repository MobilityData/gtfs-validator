/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.info.UnsupportedGtfsTypeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportResultAsFileTest {

    private static final String FILENAME = "test.tst";
    private static final String EXTRA_HEADER = "extraHeader";
    private static final String FIELD_NAME = "fieldname";
    private static final String ENTITY_ID = "entityid";
    private static final String ID_VALUE = "idvalue";
    private static final String FOLDER_NAME = "foldername";
    private static final String TEST_OUTPUT_PATH = ".." + File.separator + "output" + File.separator + "TEST"
            + File.separator;
    private static final String PATH_TO_CLEAN_OR_CREATE = "pathToCleanOrCreate";
    private static final String MISSING_HEADER_NAME = "missingHeaderName";
    private static final String URL_VALUE = "https://openmobilitydata.org/p/mbta/64/latest/download";
    private static final String TIMEZONE_VALUE = "timezoneValue";
    private static final String COLOR_VALUE = "color value";
    private static final String RAW_VALUE = "rawValue";
    private static final float RANGE_MIN_FLOAT = 0f;
    private static final float RANGE_MAX_FLOAT = 1f;
    private static final float ACTUAL_VALUE_FLOAT = 2f;
    private static final int ACTUAL_VALUE_INT = 2;
    private static final int RANGE_MAX_INT = 1;
    private static final int RANGE_MIN_INT = 0;
    private static final int ACTUAL_LENGTH_INT = 2;
    private static final int EXPECTED_LENGTH_INT = 3;
    private static final int ROW_INDEX = 0;

    private static URL URL_AS_URL;

    static {
        try {
            URL_AS_URL = new URL(URL_VALUE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("RedundantThrows")
    private static class MockExporter implements ValidationResultRepository.NoticeExporter {

        public int callToNonStandardHeaderNoticeExport = 0;
        public int callToInputZipContainsFolderExport = 0;
        public int callToNonAsciiOrNonPrintableCharExport = 0;
        public int callToUnsupportedGtfsTypeExport = 0;
        public int callToCannotConstructDataProviderExport = 0;
        public int callToCannotDownloadArchiveFromNetworkExport = 0;
        public int callToCannotParseFloatExport = 0;
        public int callToCannotParseIntegerExport = 0;
        public int callToCannotUnzipInputArchiveExport = 0;
        public int callToFloatFieldValueOutOfRangeExport = 0;
        public int callToIntegerFieldValueOutOfRangeExport = 0;
        public int callToInvalidRowLengthExport = 0;
        public int callToInvalidTimezoneExport = 0;
        public int callToInvalidUrlExport = 0;
        public int callToMissingHeaderExport = 0;
        public int callToMissingRequiredFileExport = 0;
        public int callToMissingRequiredValueExport = 0;
        public int callToCouldNotCleanOrCreatePathExport = 0;
        public int callToInvalidColorExport = 0;

        @Override
        public String getExtension() {
            return ".tst";
        }

        @Override
        public void export(NonStandardHeaderNotice toExport, OutputStream targetStream) throws IOException {
            ++callToNonStandardHeaderNoticeExport;
        }

        @Override
        public void export(InputZipContainsFolderNotice toExport, OutputStream targetStream) throws IOException {
            ++callToInputZipContainsFolderExport;
        }

        @Override
        public void export(NonAsciiOrNonPrintableCharNotice toExport, OutputStream targetStream) throws IOException {
            ++callToNonAsciiOrNonPrintableCharExport;
        }

        @Override
        public void export(UnsupportedGtfsTypeNotice toExport, OutputStream targetStream) throws IOException {
            ++callToUnsupportedGtfsTypeExport;
        }

        @Override
        public void export(CannotConstructDataProviderNotice toExport, OutputStream targetStream) throws IOException {
            ++callToCannotConstructDataProviderExport;
        }

        @Override
        public void export(CannotDownloadArchiveFromNetworkNotice toExport, OutputStream targetStream)
                throws IOException {
            ++callToCannotDownloadArchiveFromNetworkExport;
        }

        @Override
        public void export(CannotParseFloatNotice toExport, OutputStream targetStream) throws IOException {
            ++callToCannotParseFloatExport;
        }

        @Override
        public void export(CannotParseIntegerNotice toExport, OutputStream targetStream) throws IOException {
            ++callToCannotParseIntegerExport;
        }

        @Override
        public void export(CannotUnzipInputArchiveNotice toExport, OutputStream targetStream) throws IOException {
            ++callToCannotUnzipInputArchiveExport;
        }

        @Override
        public void export(FloatFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException {
            ++callToFloatFieldValueOutOfRangeExport;
        }

        @Override
        public void export(IntegerFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException {
            ++callToIntegerFieldValueOutOfRangeExport;
        }

        @Override
        public void export(InvalidRowLengthNotice toExport, OutputStream targetStream) throws IOException {
            ++callToInvalidRowLengthExport;
        }

        @Override
        public void export(InvalidTimezoneNotice toExport, OutputStream targetStream) throws IOException {
            ++callToInvalidTimezoneExport;
        }

        @Override
        public void export(InvalidUrlNotice toExport, OutputStream targetStream) throws IOException {
            ++callToInvalidUrlExport;
        }

        @Override
        public void export(MissingHeaderNotice toExport, OutputStream targetStream) throws IOException {
            ++callToMissingHeaderExport;
        }

        @Override
        public void export(MissingRequiredFileNotice toExport, OutputStream targetStream) throws IOException {
            ++callToMissingRequiredFileExport;
        }

        @Override
        public void export(MissingRequiredValueNotice toExport, OutputStream targetStream) throws IOException {
            ++callToMissingRequiredValueExport;
        }

        @Override
        public void export(CouldNotCleanOrCreatePathNotice toExport, OutputStream targetStream) throws IOException {
            ++callToCouldNotCleanOrCreatePathExport;
        }

        @Override
        public void export(InvalidColorNotice toExport, OutputStream targetStream) throws IOException {
            ++callToInvalidColorExport;
        }
    }

    private static class MockResultRepo implements ValidationResultRepository {

        public MockExporter exporter = new MockExporter();

        public int callToGetExporterCount = 0;

        public List<Notice> noticeList = new ArrayList<>();

        @Override
        public InfoNotice addNotice(InfoNotice newInfo) {
            noticeList.add(newInfo);
            return newInfo;
        }

        @Override
        public WarningNotice addNotice(WarningNotice newWarning) {
            noticeList.add(newWarning);
            return newWarning;
        }

        @Override
        public ErrorNotice addNotice(ErrorNotice newError) {
            noticeList.add(newError);
            return newError;
        }

        @Override
        public Notice addNotice(Notice newNotice) {
            noticeList.add(newNotice);
            return newNotice;
        }

        @Override
        public Collection<Notice> getAll() {
            return noticeList;
        }

        @Override
        public NoticeExporter getExporter() {
            ++callToGetExporterCount;
            return exporter;
        }
    }

    @AfterAll
    static void cleanup() {
        new CleanOrCreatePath(TEST_OUTPUT_PATH, new MockResultRepo()).execute();
        //noinspection ResultOfMethodCallIgnored
        new File(TEST_OUTPUT_PATH).delete();
    }

    @BeforeAll
    static void setup() {
        new CleanOrCreatePath(TEST_OUTPUT_PATH, new MockResultRepo()).execute();
    }

    @Test
    void exportNonStandardHeaderNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new NonStandardHeaderNotice(FILENAME, EXTRA_HEADER));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToNonStandardHeaderNoticeExport);
    }

    @Test
    void exportInputZipContainsFolderNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new InputZipContainsFolderNotice(FILENAME, FOLDER_NAME));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToInputZipContainsFolderExport);
    }

    @Test
    void exportNonAsciiOrNonPrintableCharNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new NonAsciiOrNonPrintableCharNotice(FILENAME, FIELD_NAME, ENTITY_ID, ID_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToNonAsciiOrNonPrintableCharExport);
    }

    @Test
    void exportUnsupportedGtfsTypeNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new UnsupportedGtfsTypeNotice(FILENAME, FIELD_NAME, ENTITY_ID));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToUnsupportedGtfsTypeExport);
    }

    @Test
    void exportCannotConstructDataProviderNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CannotConstructDataProviderNotice(FILENAME));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCannotConstructDataProviderExport);
    }

    @Test
    void exportCannotDownloadArchiveFromNetworkNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CannotDownloadArchiveFromNetworkNotice(URL_AS_URL));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCannotDownloadArchiveFromNetworkExport);
    }

    @Test
    void exportCannotParseFloatNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CannotParseFloatNotice(FILENAME, FIELD_NAME, 0, RAW_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCannotParseFloatExport);
    }

    @Test
    void exportCannotParseIntegerNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CannotParseIntegerNotice(FILENAME, FIELD_NAME, 0, RAW_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCannotParseIntegerExport);
    }

    @Test
    void exportCannotUnzipInputArchiveNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CannotUnzipInputArchiveNotice(FILENAME));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCannotUnzipInputArchiveExport);
    }

    @Test
    void exportFloatFieldValueOutOfRangeNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new FloatFieldValueOutOfRangeNotice(FILENAME, FIELD_NAME, ENTITY_ID, RANGE_MIN_FLOAT,
                RANGE_MAX_FLOAT, ACTUAL_VALUE_FLOAT));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToFloatFieldValueOutOfRangeExport);
    }

    @Test
    void exportIntegerFieldValueOutOfRangeNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new IntegerFieldValueOutOfRangeNotice(FILENAME, FIELD_NAME, ENTITY_ID, RANGE_MIN_INT,
                RANGE_MAX_INT, ACTUAL_VALUE_INT));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToIntegerFieldValueOutOfRangeExport);
    }

    @Test
    void exportInvalidRowLengthNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new InvalidRowLengthNotice(FILENAME, ROW_INDEX, EXPECTED_LENGTH_INT, ACTUAL_LENGTH_INT));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToInvalidRowLengthExport);
    }

    @Test
    void exportInvalidTimezoneNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new InvalidTimezoneNotice(FILENAME, FIELD_NAME, ENTITY_ID, TIMEZONE_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToInvalidTimezoneExport);
    }

    @Test
    void exportInvalidUrlNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new InvalidUrlNotice(FILENAME, FIELD_NAME, ENTITY_ID, URL_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToInvalidUrlExport);
    }

    @Test
    void exportMissingHeaderNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new MissingHeaderNotice(FILENAME, MISSING_HEADER_NAME));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToMissingHeaderExport);
    }

    @Test
    void exportMissingRequiredFileNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new MissingRequiredFileNotice(FILENAME));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToMissingRequiredFileExport);
    }

    @Test
    void exportMissingRequiredValueNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new MissingRequiredValueNotice(FILENAME, FIELD_NAME, ENTITY_ID));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToMissingRequiredValueExport);
    }

    @Test
    void exportCouldNotCleanOrCreatePathNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new CouldNotCleanOrCreatePathNotice(PATH_TO_CLEAN_OR_CREATE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToCouldNotCleanOrCreatePathExport);
    }

    @Test
    void exportInvalidColorNotice() throws IOException {
        MockResultRepo resultRepo = new MockResultRepo();

        resultRepo.addNotice(new InvalidColorNotice(FILENAME, ENTITY_ID, ENTITY_ID, COLOR_VALUE));

        ExportResultAsFile underTest = new ExportResultAsFile(resultRepo, TEST_OUTPUT_PATH);
        underTest.execute();

        assertEquals(1, resultRepo.callToGetExporterCount);
        assertEquals(1, resultRepo.exporter.callToInvalidColorExport);
    }
}