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
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.ExtraFileFoundNotice;
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

class ValidateAllOptionalFilenameTest {

    @Mock(name = "reqSpecRepo")
    int reqSpecRepo;
    @Mock(name = "optSpecRepo")
    int optSpecRepo;
    @InjectMocks
    GtfsSpecRepository mockSpecRepo;

    @Mock(name = "reqFileRepo")
    int reqFileRepo;
    @Mock(name = "optFileRepo")
    int optFileRepo;
    @Mock(name = "extraFileRepo")
    int extraFileRepo;
    @InjectMocks
    RawFileRepository mockFileRepo;

    @Mock
    List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @Test
    void allExtraPresentShouldGenerateNotice() {

        reqSpecRepo = 1;
        optSpecRepo = 2;
        reqFileRepo = 1;
        optFileRepo = 2;
        extraFileRepo = 3;

        GtfsSpecRepository mockSpecRepo = mockSpecRepository();
        RawFileRepository mockFileRepo = mockFileRepository();
        ValidationResultRepository mockResultRepo = mockResultRepo();

        ValidateAllOptionalFilename underTest = new ValidateAllOptionalFilename(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo);

        List<String> result = underTest.execute();

        assertEquals(2, result.size());
        assertEquals(List.of("opt0.opt", "opt1.opt"), result);

        assertEquals(3, noticeList.size());

        Notice notice = noticeList.get(0);
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("extra0.extra", notice.getFilename());
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra0.extra found in archive", notice.getDescription());

        notice = noticeList.get(1);
        assertEquals("extra2.extra", notice.getFilename());
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra2.extra found in archive", notice.getDescription());

        notice = noticeList.get(2);
        assertEquals("extra1.extra", notice.getFilename());
        assertThat(notice, instanceOf(ExtraFileFoundNotice.class));
        assertEquals("W004", notice.getId());
        assertEquals("Non standard file found", notice.getTitle());
        assertEquals("Extra file extra1.extra found in archive", notice.getDescription());
    }

    private GtfsSpecRepository mockSpecRepository() {
        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getRequiredFilenameList()).thenAnswer(new Answer<List<String>>() {
            public List<String> answer(InvocationOnMock invocation) {
                List<String> toReturn = new ArrayList<>(reqSpecRepo);

                for (int i = 0; i < reqSpecRepo; ++i) {
                    toReturn.add("req" + i + ".req");
                }

                return toReturn;
            }
        });
        when(mockSpecRepo.getOptionalFilenameList()).thenAnswer(new Answer<List<String>>() {
            public List<String> answer(InvocationOnMock invocation) {
                List<String> toReturn = new ArrayList<>(optSpecRepo);

                for (int i = 0; i < optSpecRepo; ++i) {
                    toReturn.add("opt" + i + ".opt");
                }

                return toReturn;
            }
        });

        return mockSpecRepo;
    }

    private RawFileRepository mockFileRepository() {
        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
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

                for (int k = 0; k < extraFileRepo; ++k) {
                    toReturn.add("extra" + k + ".extra");
                }
                return toReturn;
            }
        });
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.empty());

        return mockFileRepo;
    }

    private ValidationResultRepository mockResultRepo() {
        ValidationResultRepository mockResultRepo =  mock(ValidationResultRepository.class);
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