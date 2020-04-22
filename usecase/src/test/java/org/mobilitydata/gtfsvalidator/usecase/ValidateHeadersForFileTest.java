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
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;

class ValidateHeadersForFileTest {

    private static final String REQUIRED_HEADER_0 = "requiredHeader0";
    private static final String REQUIRED_HEADER_1 = "requiredHeader1";
    private static final String REQUIRED_HEADER_2 = "requiredHeader2";
    private static final String REQUIRED_HEADER_3 = "requiredHeader3";
    private static final String REQUIRED_HEADER_4 = "requiredHeader4";

    private static final String OPTIONAL_HEADER_0 = "optionalHeader0";
    private static final String OPTIONAL_HEADER_1 = "optionalHeader1";

    private static final String EXTRA_HEADER_0 = "extraHeader0";
    private static final String EXTRA_HEADER_1 = "extraHeader1";

    private static final String TEST_TST = "test.tst";

    @Test
    void expectedHeaderCountShouldNotGenerateNotice() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        List<String> mockOptionalHeaders = Collections.emptyList();
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void expectedRequiredHeaderCountAndDifferentContentShouldGenerateNotices() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        List<String> mockOptionalHeaders = Collections.emptyList();
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_3, REQUIRED_HEADER_4);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(2)).addNotice(any(ErrorNotice.class));
        verify(mockResultRepo, times(2)).addNotice(any(WarningNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessRequiredHeaderCountThanExpectedShouldGenerateOneNoticePerMissingHeader() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2, REQUIRED_HEADER_3);
        List<String> mockOptionalHeaders = Collections.emptyList();
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(2)).addNotice(any(ErrorNotice.class));
        verify(mockResultRepo, times(0)).addNotice(any(WarningNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void lessRequiredHeaderCountThanExpectedAndNonStandardHeadersPresenceShouldGenerateNotices() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        List<String> mockOptionalHeaders = Collections.emptyList();
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, EXTRA_HEADER_0);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(2)).addNotice(any(ErrorNotice.class));
        verify(mockResultRepo, times(1)).addNotice(any(WarningNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    @Test
    void presenceOfKnownOptionalHeaderShouldNotGenerateNotices() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        List<String> mockOptionalHeaders = Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2,
                OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void unexpectedOptionalHeaderShouldGenerateNotices() {

        List<String> mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        List<String> mockOptionalHeaders = Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);
        List<String> mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2,
                OPTIONAL_HEADER_0, OPTIONAL_HEADER_1, EXTRA_HEADER_0, EXTRA_HEADER_1);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenReturn(null);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenReturn(null);

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(0)).addNotice(any(ErrorNotice.class));
        verify(mockResultRepo, times(2)).addNotice(any(WarningNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

}