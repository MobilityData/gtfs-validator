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
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
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

class ValidateAllRequiredFilePresenceTest {

    @Mock(name = "reqSpecRepo")
    int reqSpecRepo;
    @InjectMocks
    GtfsSpecRepository mockSpecRepo;

    @Mock(name = "reqFileRepo")
    int reqFileRepo;
    @Mock(name = "optFileRepo")
    int optFileRepo;
    @InjectMocks
    RawFileRepository mockFileRepo;

    @Mock
    List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @Test
    void allRequiredPresentShouldNotGenerateNotice() {

        reqSpecRepo = 10;
        reqFileRepo = 10;
        optFileRepo = 10;

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo
        );

        List<String> result = underTest.execute();
        assertEquals(0, noticeList.size());
        assertEquals(10, result.size());
        assertEquals(List.of("req0.req", "req1.req", "req2.req", "req3.req", "req4.req", "req5.req", "req6.req",
                "req7.req", "req8.req", "req9.req"), result);
    }

    @Test
    void missingRequiredShouldGenerateOneNoticePerMissingFile() {

        reqSpecRepo = 15;
        reqFileRepo = 10;
        optFileRepo = 10;

        GtfsSpecRepository mockSpecRepo = buildMockSpecRepository();
        RawFileRepository mockFileRepo = buildMockFileRepository();
        ValidationResultRepository mockResultRepo = buildMockResultRepository();

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo
        );

        List<String> result = underTest.execute();

        assertEquals(5, noticeList.size());
        assertEquals(15, result.size());

        Notice notice = noticeList.get(0);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req10.req", notice.getFilename());

        notice = noticeList.get(1);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req11.req", notice.getFilename());

        notice = noticeList.get(2);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req12.req", notice.getFilename());

        notice = noticeList.get(3);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req13.req", notice.getFilename());

        notice = noticeList.get(4);
        assertThat(notice, instanceOf(MissingRequiredFileNotice.class));
        assertEquals("E003", notice.getId());
        assertEquals("Missing required file", notice.getTitle());
        assertEquals("req14.req", notice.getFilename());
    }

    private GtfsSpecRepository buildMockSpecRepository() {
        mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredFilenameList()).thenAnswer(new Answer<List<String>>() {
            public List<String> answer(InvocationOnMock invocation) {
                List<String> toReturn = new ArrayList<>(reqSpecRepo);

                for (int i = 0; i < reqSpecRepo; ++i) {
                    toReturn.add("req" + i + ".req");
                }

                return toReturn;
            }
        });

        return mockSpecRepo;
    }

    private RawFileRepository buildMockFileRepository() {
        mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.findByName(anyString())).thenReturn(Optional.empty());
        when(mockFileRepo.getFilenameAll()).thenAnswer(new Answer<Set<String>>() {
            public Set<String> answer(InvocationOnMock invocation) {
                Set<String> toReturn = new HashSet<>(reqFileRepo + optFileRepo);

                for (int i = 0; i < reqFileRepo; ++i) {
                    toReturn.add("req" + i + ".req");
                }

                for (int j = 0; j < optFileRepo; ++j) {
                    toReturn.add("opt" + j + ".opt");
                }

                return toReturn;
            }
        });
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