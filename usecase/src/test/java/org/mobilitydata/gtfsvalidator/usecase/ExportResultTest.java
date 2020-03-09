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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportResultTest {

    private static final String FILENAME = "filename";
    private static final String EXTRA_HEADER = "extraHeader";
    private static final String FIELD_NAME = "fieldname";
    private static final String ENTITY_ID = "entityid";
    private static final String ID_VALUE = "idvalue";
    private static final String FOLDER_NAME = "foldername";
    private static final String TEST_OUTPUT_PATH = "testOutputPath";
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
        public void export(NonStandardHeaderNotice toExport) {
            ++callToNonStandardHeaderNoticeExport;
        }

        @Override
        public void export(InputZipContainsFolderNotice toExport) {
            ++callToInputZipContainsFolderExport;
        }

        @Override
        public void export(NonAsciiOrNonPrintableCharNotice toExport) {
            ++callToNonAsciiOrNonPrintableCharExport;

        }

        @Override
        public void export(UnsupportedGtfsTypeNotice toExport) {
            ++callToUnsupportedGtfsTypeExport;
        }

        @Override
        public void export(CannotConstructDataProviderNotice toExport) {
            ++callToCannotConstructDataProviderExport;
        }

        @Override
        public void export(CannotDownloadArchiveFromNetworkNotice toExport) {
            ++callToCannotDownloadArchiveFromNetworkExport;
        }

        @Override
        public void export(CannotParseFloatNotice toExport) {
            ++callToCannotParseFloatExport;
        }

        @Override
        public void export(CannotParseIntegerNotice toExport) {
            ++callToCannotParseIntegerExport;
        }

        @Override
        public void export(CannotUnzipInputArchiveNotice toExport) {
            ++callToCannotUnzipInputArchiveExport;
        }

        @Override
        public void export(FloatFieldValueOutOfRangeNotice toExport) {
            ++callToFloatFieldValueOutOfRangeExport;
        }

        @Override
        public void export(IntegerFieldValueOutOfRangeNotice toExport) {
            ++callToIntegerFieldValueOutOfRangeExport;
        }

        @Override
        public void export(InvalidRowLengthNotice toExport) {
            ++callToInvalidRowLengthExport;
        }

        @Override
        public void export(InvalidTimezoneNotice toExport) {
            ++callToInvalidTimezoneExport;
        }

        @Override
        public void export(InvalidUrlNotice toExport) {
            ++callToInvalidUrlExport;
        }

        @Override
        public void export(MissingHeaderNotice toExport) {
            ++callToMissingHeaderExport;
        }

        @Override
        public void export(MissingRequiredFileNotice toExport) {
            ++callToMissingRequiredFileExport;
        }

        @Override
        public void export(MissingRequiredValueNotice toExport) {
            ++callToMissingRequiredValueExport;
        }

        @Override
        public void export(CouldNotCleanOrCreatePathNotice toExport) {
            ++callToCouldNotCleanOrCreatePathExport;
        }

        @Override
        public void export(InvalidColorNotice toExport) {
            ++callToInvalidColorExport;

        }
    }

    private static class MockResultRepo implements ValidationResultRepository {

        public MockExporter exporter = new MockExporter();

        public int callToGetExportCount = 0;

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
        public NoticeExporter getExporter(String outputPath) {
            ++callToGetExportCount;
            return exporter;
        }
    }

    @Test
    void callingExportShouldCallGetExporterAndInterfaceExportMethod() {

        MockResultRepo resultRepo = new MockResultRepo();

        NonStandardHeaderNotice nonStandardHeaderNotice0 = new NonStandardHeaderNotice(FILENAME, EXTRA_HEADER);
        resultRepo.addNotice(nonStandardHeaderNotice0);

        NonStandardHeaderNotice nonStandardHeaderNotice1 = new NonStandardHeaderNotice(FILENAME, EXTRA_HEADER);
        resultRepo.addNotice(nonStandardHeaderNotice1);

        NonAsciiOrNonPrintableCharNotice nonAsciiOrNonPrintableCharNotice = new NonAsciiOrNonPrintableCharNotice(FILENAME, FIELD_NAME, ENTITY_ID, ID_VALUE);
        resultRepo.addNotice(nonAsciiOrNonPrintableCharNotice);

        InputZipContainsFolderNotice inputZipContainsFolderNotice0 = new InputZipContainsFolderNotice(FILENAME, FOLDER_NAME);
        resultRepo.addNotice(inputZipContainsFolderNotice0);

        InputZipContainsFolderNotice inputZipContainsFolderNotice1 = new InputZipContainsFolderNotice(FILENAME, FOLDER_NAME);
        resultRepo.addNotice(inputZipContainsFolderNotice1);

        UnsupportedGtfsTypeNotice unsupportedGtfsTypeNotice = new UnsupportedGtfsTypeNotice(FILENAME, FIELD_NAME, ENTITY_ID);
        resultRepo.addNotice(unsupportedGtfsTypeNotice);

        CannotConstructDataProviderNotice cannotConstructDataProviderNotice = new CannotConstructDataProviderNotice(FILENAME);
        resultRepo.addNotice(cannotConstructDataProviderNotice);

        CannotDownloadArchiveFromNetworkNotice cannotDownloadArchiveFromNetworkNotice = new CannotDownloadArchiveFromNetworkNotice(URL_AS_URL);
        resultRepo.addNotice(cannotDownloadArchiveFromNetworkNotice);

        CannotParseFloatNotice cannotParseFloatNotice = new CannotParseFloatNotice(FILENAME, FIELD_NAME, 0, RAW_VALUE);
        resultRepo.addNotice(cannotParseFloatNotice);

        CannotParseIntegerNotice cannotParseIntegerNotice = new CannotParseIntegerNotice(FILENAME, FIELD_NAME, 0, RAW_VALUE);
        resultRepo.addNotice(cannotParseIntegerNotice);

        CannotUnzipInputArchiveNotice cannotUnzipInputArchiveNotice = new CannotUnzipInputArchiveNotice(FILENAME);
        resultRepo.addNotice(cannotUnzipInputArchiveNotice);

        FloatFieldValueOutOfRangeNotice floatFieldValueOutOfRangeNotice = new FloatFieldValueOutOfRangeNotice(FILENAME, FIELD_NAME, ENTITY_ID, RANGE_MIN_FLOAT, RANGE_MAX_FLOAT, ACTUAL_VALUE_FLOAT);
        resultRepo.addNotice(floatFieldValueOutOfRangeNotice);

        IntegerFieldValueOutOfRangeNotice integerFieldValueOutOfRangeNotice = new IntegerFieldValueOutOfRangeNotice(FILENAME, FIELD_NAME, ENTITY_ID, RANGE_MIN_INT, RANGE_MAX_INT, ACTUAL_VALUE_INT);
        resultRepo.addNotice(integerFieldValueOutOfRangeNotice);

        InvalidRowLengthNotice invalidRowLengthNotice = new InvalidRowLengthNotice(FILENAME, ROW_INDEX, EXPECTED_LENGTH_INT, ACTUAL_LENGTH_INT);
        resultRepo.addNotice(invalidRowLengthNotice);

        InvalidTimezoneNotice invalidTimezoneNotice = new InvalidTimezoneNotice(FILENAME, FIELD_NAME, ENTITY_ID, TIMEZONE_VALUE);
        resultRepo.addNotice(invalidTimezoneNotice);

        InvalidUrlNotice invalidUrlNotice = new InvalidUrlNotice(FILENAME, FIELD_NAME, ENTITY_ID, URL_VALUE);
        resultRepo.addNotice(invalidUrlNotice);

        MissingHeaderNotice missingHeaderNotice = new MissingHeaderNotice(FILENAME, MISSING_HEADER_NAME);
        resultRepo.addNotice(missingHeaderNotice);

        MissingRequiredFileNotice missingRequiredFileNotice = new MissingRequiredFileNotice(FILENAME);
        resultRepo.addNotice(missingRequiredFileNotice);

        MissingRequiredValueNotice missingRequiredValueNotice = new MissingRequiredValueNotice(FILENAME, FIELD_NAME, ENTITY_ID);
        resultRepo.addNotice(missingRequiredValueNotice);

        CouldNotCleanOrCreatePathNotice couldNotCleanOrCreatePathNotice = new CouldNotCleanOrCreatePathNotice(PATH_TO_CLEAN_OR_CREATE);
        resultRepo.addNotice(couldNotCleanOrCreatePathNotice);

        InvalidColorNotice invalidColorNotice = new InvalidColorNotice(FILENAME, ENTITY_ID, ENTITY_ID, COLOR_VALUE);
        resultRepo.addNotice(invalidColorNotice);


        ExportResult underTest = new ExportResult(resultRepo, TEST_OUTPUT_PATH);

        underTest.execute();

        assertEquals(1, resultRepo.callToGetExportCount);

        assertEquals(2, resultRepo.exporter.callToNonStandardHeaderNoticeExport);
        assertEquals(1, resultRepo.exporter.callToNonAsciiOrNonPrintableCharExport);
        assertEquals(2, resultRepo.exporter.callToInputZipContainsFolderExport);
        assertEquals(1, resultRepo.exporter.callToUnsupportedGtfsTypeExport);
        assertEquals(1, resultRepo.exporter.callToCannotConstructDataProviderExport);
        assertEquals(1, resultRepo.exporter.callToCannotDownloadArchiveFromNetworkExport);
        assertEquals(1, resultRepo.exporter.callToCannotParseFloatExport);
        assertEquals(1, resultRepo.exporter.callToCannotParseIntegerExport);
        assertEquals(1, resultRepo.exporter.callToCannotUnzipInputArchiveExport);
        assertEquals(1, resultRepo.exporter.callToFloatFieldValueOutOfRangeExport);
        assertEquals(1, resultRepo.exporter.callToIntegerFieldValueOutOfRangeExport);
        assertEquals(1, resultRepo.exporter.callToInvalidRowLengthExport);
        assertEquals(1, resultRepo.exporter.callToInvalidTimezoneExport);
        assertEquals(1, resultRepo.exporter.callToInvalidUrlExport);
        assertEquals(1, resultRepo.exporter.callToMissingHeaderExport);
        assertEquals(1, resultRepo.exporter.callToMissingRequiredFileExport);
        assertEquals(1, resultRepo.exporter.callToMissingRequiredValueExport);
        assertEquals(1, resultRepo.exporter.callToCouldNotCleanOrCreatePathExport);
        assertEquals(1, resultRepo.exporter.callToInvalidColorExport);
    }

}