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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParseSingleRowForFileTest {

    @Test
    void shouldValidateAndParseOneByOne() {

        RawFileRepository.RawEntityProvider mockProvider = mock(RawFileRepository.RawEntityProvider.class);
        when(mockProvider.hasNext()).thenReturn(true, true, true, false);
        RawEntity testRawEntity = new RawEntity(Map.of("testKey","testValue"), 0);
        when(mockProvider.getNext()).thenReturn(testRawEntity);

        GtfsSpecRepository.RawEntityParser mockParser = mock(GtfsSpecRepository.RawEntityParser.class);
        when(mockParser.validateNonStringTypes(any(RawEntity.class))).thenReturn(Collections.emptyList());

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.of(mockProvider));

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getParserForFile(any(RawFileInfo.class))).thenReturn(mockParser);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        underTest.execute();
        underTest.execute();
        underTest.execute();
        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getParserForFile(any(RawFileInfo.class));
        verify(mockParser, times(3)).validateNonStringTypes(any(RawEntity.class));
        verify(mockParser, times(3)).parse(any(RawEntity.class));
        verify(mockProvider, times(4)).hasNext();
        verify(mockProvider, times(3)).getNext();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo, mockParser, mockProvider);
    }

    @Test
    void shouldWriteNoticesToRepo() {

        RawFileRepository.RawEntityProvider mockProvider = mock(RawFileRepository.RawEntityProvider.class);
        when(mockProvider.hasNext()).thenReturn(true);
        RawEntity testRawEntity = new RawEntity(Map.of("testKey","testValue"), 0);
        when(mockProvider.getNext()).thenReturn(testRawEntity);

        GtfsSpecRepository.RawEntityParser mockParser = mock(GtfsSpecRepository.RawEntityParser.class);
        ErrorNotice testNotice = new CannotConstructDataProviderNotice("testName");
        when(mockParser.validateNonStringTypes(any(RawEntity.class))).thenReturn(List.of(testNotice, testNotice, testNotice));

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.of(mockProvider));

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        when(mockSpecRepo.getParserForFile(any(RawFileInfo.class))).thenReturn(mockParser);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_invalid.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        underTest.execute();
        underTest.execute();
        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        inOrder.verify(mockSpecRepo, times(1)).getParserForFile(any(RawFileInfo.class));
        verify(mockParser, times(3)).validateNonStringTypes(any(RawEntity.class));
        verify(mockParser, times(3)).parse(any(RawEntity.class));
        verify(mockProvider, times(3)).hasNext();
        verify(mockProvider, times(3)).getNext();
        verify(mockResultRepo, times(9)).addNotice(any(CannotConstructDataProviderNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo, mockParser, mockProvider);
    }

    @Test
    void providerErrorShouldGenerateNotice() {

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        when(mockFileRepo.getProviderForFile(any(RawFileInfo.class))).thenReturn(Optional.empty());

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ParseSingleRowForFile underTest = new ParseSingleRowForFile(
                RawFileInfo.builder().filename("test_empty.tst").build(),
                mockFileRepo,
                mockSpecRepo,
                mockResultRepo
        );

        underTest.execute();

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockResultRepo);

        inOrder.verify(mockFileRepo, times(1)).getProviderForFile(any(RawFileInfo.class));
        inOrder.verify(mockResultRepo, times(1)).addNotice(any(CannotConstructDataProviderNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

}