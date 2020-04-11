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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

class InMemoryValidationResultRepositoryTest {

    private static final String TEST_FILE_NAME = "test.tst";

    @Mock(name = "warningNotice")
    WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");
    @Mock(name = "warningList")
    List<WarningNotice> warningList = new ArrayList<>();
    @Mock(name = "errorNotice")
    ErrorNotice errorNotice = new CannotConstructDataProviderNotice(TEST_FILE_NAME);
    @Mock(name = "errorList")
    List<ErrorNotice> errorList = new ArrayList<>();
    @Mock(name = "infoList")
    List<InfoNotice> infoList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void addingNoticeShouldExtendNoticeList() {

        //WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");

        //ErrorNotice errorNotice = new CannotConstructDataProviderNotice(TEST_FILE_NAME);

        //ValidationResultRepository mockResultRepo = new InMemoryValidationResultRepository();

        ValidationResultRepository mockResultRepo = mockResultRepository();

        mockResultRepo.addNotice(warningNotice);
        assertEquals(1, mockResultRepo.getAll().size());

        Notice testedNotice = mockResultRepo.getAll().stream()
                .filter(notice -> notice.getId().equals(warningNotice.getId()))
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(NonStandardHeaderNotice.class));

        mockResultRepo.addNotice(errorNotice);
        assertEquals(2, mockResultRepo.getAll().size());

        testedNotice = mockResultRepo.getAll().stream()
                .filter(notice -> notice.getId().equals(errorNotice.getId()))
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(CannotConstructDataProviderNotice.class));
    }

    private ValidationResultRepository mockResultRepository() {
        mockResultRepo = mock(InMemoryValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(WarningNotice.class))).thenAnswer(new Answer<WarningNotice>() {
            public WarningNotice answer(InvocationOnMock invocation) {
                WarningNotice warningNotice = invocation.getArgument(0);
                warningList.add(warningNotice);
                return warningNotice;
            }
        });
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<ErrorNotice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                errorList.add(errorNotice);
                return errorNotice;
            }
        });
        when(mockResultRepo.getAll()).thenAnswer(new Answer<Collection<Notice>>() {
            public Collection<Notice> answer(InvocationOnMock invocation) {
                return Stream.concat(
                        Stream.concat(
                                infoList.stream(),
                                warningList.stream()),
                        errorList.stream())
                        .collect(Collectors.toUnmodifiableList());
            }
        });

        return mockResultRepo;
    }

}