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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void addingNoticeShouldExtendNoticeList() {

        WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");
        List<WarningNotice> warningList = new ArrayList<>();
        ErrorNotice errorNotice = new CannotConstructDataProviderNotice(TEST_FILE_NAME);
        List<ErrorNotice> errorList = new ArrayList<>();
        List<InfoNotice> infoList = new ArrayList<>();

        ValidationResultRepository mockRepository = mock(InMemoryValidationResultRepository.class);
        when(mockRepository.addNotice(any(WarningNotice.class))).thenAnswer(new Answer<WarningNotice>() {
            public WarningNotice answer(InvocationOnMock invocation) {
                WarningNotice warningNotice = invocation.getArgument(0);
                warningList.add(warningNotice);
                return warningNotice;
            }
        });
        when(mockRepository.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<ErrorNotice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                errorList.add(errorNotice);
                return errorNotice;
            }
        });
        when(mockRepository.getAll()).thenAnswer(new Answer<Collection<Notice>>() {
            public Collection<Notice> answer(InvocationOnMock invocation) {
                return Stream.concat(
                        Stream.concat(
                            infoList.stream(),
                            warningList.stream()),
                        errorList.stream())
                        .collect(Collectors.toUnmodifiableList());
            }
        });

        //mockRepository.addNotice(warningNotice);
        //assertEquals(1, mockRepository.getAll().size());

        //WarningNotice warningNotice = new NonStandardHeaderNotice(TEST_FILE_NAME, "extra");

        //ErrorNotice errorNotice = new CannotConstructDataProviderNotice(TEST_FILE_NAME);

        //ValidationResultRepository mockRepository = new InMemoryValidationResultRepository();

        mockRepository.addNotice(warningNotice);
        assertEquals(1, mockRepository.getAll().size());

        Notice testedNotice = mockRepository.getAll().stream()
                .filter(notice -> notice.getId().equals(warningNotice.getId()))
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(NonStandardHeaderNotice.class));

        mockRepository.addNotice(errorNotice);
        assertEquals(2, mockRepository.getAll().size());

        testedNotice = mockRepository.getAll().stream()
                .filter(notice -> notice.getId().equals(errorNotice.getId()))
                .findAny()
                .get();

        assertThat(testedNotice, instanceOf(CannotConstructDataProviderNotice.class));
    }
}