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
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock(name = "mockRequiredHeaders")
    List<String> mockRequiredHeaders;
    @Mock(name = "mockOptionalHeaders")
    List<String> mockOptionalHeaders;
    @InjectMocks
    GtfsSpecRepository mockSpecRepo;

    @Mock
    Collection<String> mockHeaders;
    @InjectMocks
    RawFileRepository mockFileRepo;

    @Mock
    List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @Test
    void expectedHeaderCountShouldNotGenerateNotice() {

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        mockOptionalHeaders = Collections.emptyList();
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(0, noticeList.size());

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

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        mockOptionalHeaders = Collections.emptyList();
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_3, REQUIRED_HEADER_4);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(4, noticeList.size());

        Notice notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_1,
                notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_3)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + REQUIRED_HEADER_3 + " in file:test.tst", notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_4)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + REQUIRED_HEADER_4 + " in file:test.tst", notice.getDescription());

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

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2, REQUIRED_HEADER_3);
        mockOptionalHeaders = Collections.emptyList();
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(2, noticeList.size());

        Notice notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_3)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_3,
                notice.getDescription());

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

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        mockOptionalHeaders = Collections.emptyList();
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, EXTRA_HEADER_0);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(3, noticeList.size());

        Notice notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_1,
                notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(REQUIRED_HEADER_2)).findAny().get();
        assertThat(notice, instanceOf(MissingHeaderNotice.class));
        assertEquals("E001", notice.getId());
        assertEquals("Missing required header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("File test.tst is missing required header: " + REQUIRED_HEADER_2,
                notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_0)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_0 + " in file:test.tst", notice.getDescription());

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

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        mockOptionalHeaders = Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2,
                OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(0, noticeList.size());

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

        mockRequiredHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2);
        mockOptionalHeaders = Arrays.asList(OPTIONAL_HEADER_0, OPTIONAL_HEADER_1);
        mockHeaders = Arrays.asList(REQUIRED_HEADER_0, REQUIRED_HEADER_1, REQUIRED_HEADER_2,
                OPTIONAL_HEADER_0, OPTIONAL_HEADER_1, EXTRA_HEADER_0, EXTRA_HEADER_1);

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();


        ValidateHeadersForFile underTest = new ValidateHeadersForFile(
                mockSpecRepo,
                RawFileInfo.builder().filename(TEST_TST).build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(2, noticeList.size());

        Notice notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_0)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_0 + " in file:test.tst", notice.getDescription());

        notice = noticeList.stream()
                .filter(n -> n.getDescription().contains(EXTRA_HEADER_1)).findAny().get();
        assertThat(notice, instanceOf(NonStandardHeaderNotice.class));
        assertEquals("W002", notice.getId());
        assertEquals("Non standard header", notice.getTitle());
        assertEquals(TEST_TST, notice.getFilename());
        assertEquals("Unexpected header:" + EXTRA_HEADER_1 + " in file:test.tst", notice.getDescription());

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getRequiredHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getOptionalHeadersForFile(any(RawFileInfo.class));
        inOrder.verify(mockFileRepo, times(1)).getActualHeadersForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(0)).addNotice(any(ErrorNotice.class));
        verify(mockResultRepo, times(2)).addNotice(any(WarningNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    private GtfsSpecRepository buildMockSpecRepository() {
        mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredHeadersForFile(any(RawFileInfo.class))).thenReturn(mockRequiredHeaders);
        when(mockSpecRepo.getOptionalHeadersForFile(any(RawFileInfo.class))).thenReturn(mockOptionalHeaders);
        return mockSpecRepo;
    }

    private RawFileRepository buildMockFileRepository() {
        mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.findByName(anyString())).thenReturn(Optional.empty());
        when(mockFileRepo.getActualHeadersForFile(any(RawFileInfo.class))).thenReturn(mockHeaders);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.empty());
        return mockFileRepo;
    }

    private ValidationResultRepository buildMockResultRepository() {
        mockResultRepo =  mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(InfoNotice.class))).thenAnswer(new Answer<Notice>() {
            public InfoNotice answer(InvocationOnMock invocation) {
                InfoNotice infoNotice = invocation.getArgument(0);
                noticeList.add(infoNotice);
                return infoNotice;
            }
        });
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenAnswer(new Answer<Notice>() {
            public WarningNotice answer(InvocationOnMock invocation) {
                WarningNotice warningNotice = invocation.getArgument(0);
                noticeList.add(warningNotice);
                return warningNotice;
            }
        });
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<Notice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                noticeList.add(errorNotice);
                return errorNotice;
            }
        });

        return mockResultRepo;
    }

}