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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ValidateAllRowLengthForFileTest {

    @Test
    void expectedLengthForAllShouldNotGenerateNotice() {

        RawFileRepository.RawEntityProvider mockProvider = mock(RawFileRepository.RawEntityProvider.class);
        when(mockProvider.hasNext()).thenReturn(true, true, true, true, false);
        RawEntity testRawEntity = new RawEntity(Map.of("testKey","testValue"), 0);
        when(mockProvider.getNext()).thenReturn(testRawEntity);
        when(mockProvider.getHeaderCount()).thenReturn(testRawEntity.size());

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.of(mockProvider));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        verify(mockProvider, times(5)).hasNext();
        verify(mockProvider, times(4)).getNext();
        verify(mockProvider, times(4)).getHeaderCount();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockFileRepo, mockResultRepo, mockProvider);
    }

    @Test
    void invalidRowsShouldGenerateError() {

        RawFileRepository.RawEntityProvider mockProvider = mock(RawFileRepository.RawEntityProvider.class);
        when(mockProvider.hasNext()).thenReturn(true,  true, true, false);
        RawEntity testRawEntity = new RawEntity(Map.of("testKey","testValue"), 0);
        when(mockProvider.getNext()).thenReturn(testRawEntity);
        int testFakeSize = testRawEntity.size() + 2;
        when(mockProvider.getHeaderCount()).thenReturn(testRawEntity.size(), testFakeSize, testFakeSize);

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.of(mockProvider));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_invalid.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        verify(mockProvider, times(4)).hasNext();
        verify(mockProvider, times(3)).getNext();
        verify(mockProvider, times(5)).getHeaderCount();
        verify(mockResultRepo, times(2)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockResultRepo, mockProvider);
    }

    @Test
    void dataProviderConstructionIssueShouldGenerateError() {

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.empty());

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllRowLengthForFile underTest = new ValidateAllRowLengthForFile(
                RawFileInfo.builder()
                        .filename("test_empty.tst")
                        .build(),
                mockFileRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockResultRepo);
    }

}