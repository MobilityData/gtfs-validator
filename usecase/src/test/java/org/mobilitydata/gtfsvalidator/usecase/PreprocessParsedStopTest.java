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
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class PreprocessParsedStopTest {
    private static final String STOP_ID = "stop_id";

    @Test
    public void validParsedStopShouldNotAddNoticeToResultRepoAndShouldReturnSameParsedEntity() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ParsedEntity mockParsedStop = mock(ParsedEntity.class);

        when(mockParsedStop.getEntityId()).thenReturn(STOP_ID);

        final PreprocessParsedStop underTest = new PreprocessParsedStop(mockResultRepo);

        assertEquals(mockParsedStop, underTest.execute(mockParsedStop, Collections.emptySet()));
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStop, times(1)).getEntityId();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockParsedStop);
    }

    @Test
    public void parsedStopWithNullStopIdShouldAddNoticeToRepoAndReturnNull() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ParsedEntity mockParsedStop = mock(ParsedEntity.class);
        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        final PreprocessParsedStop underTest = new PreprocessParsedStop(mockResultRepo);

        assertNull(underTest.execute(mockParsedStop, Collections.emptySet()));
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStop, times(1)).getEntityId();
        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        MissingRequiredValueNotice notice = captor.getValue();
        assertEquals("stops.txt", notice.getFilename());
        assertEquals(STOP_ID, notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no idfftft", notice.getEntityId());

        verifyNoMoreInteractions(mockParsedStop, mockResultRepo);
    }

    @Test
    public void duplicateStopShouldAddNoticeToRepoAndReturnNull() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final ParsedEntity mockParsedStop = mock(ParsedEntity.class);
        final ArgumentCaptor<DuplicatedEntityNotice> captor =
                ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        final PreprocessParsedStop underTest = new PreprocessParsedStop(mockResultRepo);

        when(mockParsedStop.getEntityId()).thenReturn(STOP_ID);

        assertNull(underTest.execute(mockParsedStop, Set.of(STOP_ID)));
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStop, times(1)).getEntityId();
        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        DuplicatedEntityNotice notice = captor.getValue();
        assertEquals("stops.txt", notice.getFilename());
        assertEquals(STOP_ID, notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(STOP_ID, notice.getEntityId());

        verifyNoMoreInteractions(mockParsedStop, mockResultRepo);
    }
}