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
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateAllRowLengthForFileTest {

    @Mock
    List<Notice> noticeList = new ArrayList<>();
    @InjectMocks
    ValidationResultRepository mockResultRepo;

    @Test
    void expectedLengthForAllShouldNotGenerateNotice() {

        ValidationResultRepository mockResultRepo = mockResultRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(0, noticeList.size());
    }

    @Test
    void invalidRowsShouldGenerateError() {

        ValidationResultRepository mockResultRepo = mockResultRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_invalid.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();
        assertEquals(2, noticeList.size());

        Notice notice = noticeList.get(0);
        assertThat(notice, instanceOf(InvalidRowLengthNotice.class));
        assertEquals("E004", notice.getId());
        assertEquals("Invalid row length", notice.getTitle());
        assertEquals("test_invalid.tst", notice.getFilename());
        assertEquals("Invalid length for row:2 -- expected:3 actual:2", notice.getDescription());

        notice = noticeList.get(1);
        assertThat(notice, instanceOf(InvalidRowLengthNotice.class));
        assertEquals("E004", notice.getId());
        assertEquals("Invalid row length", notice.getTitle());
        assertEquals("test_invalid.tst", notice.getFilename());
        assertEquals("Invalid length for row:4 -- expected:3 actual:4", notice.getDescription());
    }

    @Test
    void dataProviderConstructionIssueShouldGenerateError() {

        ValidationResultRepository mockResultRepo = mockResultRepository();
        RawFileRepository mockFileRepo = mockFileRepository();

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_empty.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        assertEquals(1, noticeList.size());
        Notice notice = noticeList.get(0);
        assertThat(notice, instanceOf(CannotConstructDataProviderNotice.class));
        assertEquals("E002", notice.getId());
        assertEquals("Data provider error", notice.getTitle());
        assertEquals("test_empty.tst", notice.getFilename());
        assertEquals("An error occurred while trying to access raw data for file: test_empty.tst", notice.getDescription());
    }

    private ValidationResultRepository mockResultRepository() {
        ValidationResultRepository mockResultRepo =  mock(ValidationResultRepository.class);
        when(mockResultRepo.addNotice(any(ErrorNotice.class))).thenAnswer(new Answer<Notice>() {
            public ErrorNotice answer(InvocationOnMock invocation) {
                ErrorNotice errorNotice = invocation.getArgument(0);
                noticeList.add(errorNotice);
                return errorNotice;
            }
        });

        return mockResultRepo;
    }

    private RawFileRepository mockFileRepository() {
        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.findByName(anyString())).thenReturn(Optional.empty());
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class)))
                .thenAnswer(new Answer<Optional<RawFileRepository.RawEntityProvider>>() {
                    public Optional<RawFileRepository.RawEntityProvider> answer(InvocationOnMock invocation) {
                        RawFileInfo file = invocation.getArgument(0);
                        if (file.getFilename().contains("empty")) {
                            return Optional.empty();
                        }

                        if (file.getFilename().contains("invalid")) {
                            return Optional.of(new EntityProvider(
                                    List.of(
                                            Map.of("h0", "header0_name", "h1", "header1_name",
                                                    "h2", "header2_name"),
                                            Map.of("h0", "v0", "h1", "v1"),
                                            Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                                            Map.of("h0", "v0", "h1", "v1", "h2", "v2", "h3", "v3")
                                    )
                            ));
                        }

                        return Optional.of(new EntityProvider(
                                List.of(
                                        Map.of("h0", "header0_name", "h1", "header1_name",
                                                "h2", "header2_name"),
                                        Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                                        Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                                        Map.of("h0", "v0", "h1", "v1", "h2", "v2"),
                                        Map.of("h0", "v0", "h1", "v1", "h2", "v2")
                                )
                        ));
                    }
                });

        return mockFileRepo;
    }

    private static class EntityProvider implements RawFileRepository.RawEntityProvider {
        private int currentCount = 0;
        private List<Map<String, String>> entityList;

        public EntityProvider(final List<Map<String, String>> mockEntityList) {
            this.entityList = mockEntityList;
        }

        @Override
        public boolean hasNext() {
            return currentCount < entityList.size() - 1;
        }

        @Override
        public RawEntity getNext() {
            ++currentCount;
            return new RawEntity(entityList.get(currentCount), currentCount + 1);
        }

        @Override
        public int getHeaderCount() {
            return entityList.get(0).size();
        }
    }

}