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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedTranslationTest {
    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void validTranslationShouldCreateTranslationEntityAndBeAddedToRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Translation.TranslationBuilder mockBuilder = mock(Translation.TranslationBuilder.class, RETURNS_SELF);
        final Translation mockTranslation = mock(Translation.class);
        when(mockTranslation.getTableName()).thenReturn(TableName.FEED_INFO);
        when(mockGtfsDataRepo.addTranslation(ArgumentMatchers.any())).thenReturn(mockTranslation);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTranslation).when(mockEntityBuildResult).getData();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        final ProcessParsedTranslation underTest =
                new ProcessParsedTranslation(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTranslation = mock(ParsedEntity.class);
        when(mockParsedTranslation.get("table_name")).thenReturn("feed_info");
        when(mockParsedTranslation.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("record_id")).thenReturn(null);
        when(mockParsedTranslation.get("record_sub_id")).thenReturn(null);
        when(mockParsedTranslation.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslation);

        verify(mockParsedTranslation, times(7)).get(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).tableName(ArgumentMatchers.eq("feed_info"));
        verify(mockBuilder, times(1)).fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).recordId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).recordSubId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).fieldValue(ArgumentMatchers.eq(null));

        final InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockTranslation,
                mockEntityBuildResult);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockEntityBuildResult, times(1)).isSuccess();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockEntityBuildResult, times(1)).getData();

        inOrder.verify(mockGtfsDataRepo, times(1)).addTranslation(mockTranslation);

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockEntityBuildResult, mockTranslation);
        verifyNoInteractions(mockResultRepo);
    }

    @Test
    void invalidTranslationShouldGenerateNoticeAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Translation.TranslationBuilder mockBuilder = mock(Translation.TranslationBuilder.class, RETURNS_SELF);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        final List<Notice> mockNoticeCollection = spy(new ArrayList<>());
        final UnexpectedEnumValueNotice mockNotice = mock(UnexpectedEnumValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockEntityBuildResult).getData();
        when(mockEntityBuildResult.isSuccess()).thenReturn(false);
        final ProcessParsedTranslation underTest =
                new ProcessParsedTranslation(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTranslation = mock(ParsedEntity.class);
        when(mockParsedTranslation.get("table_name")).thenReturn("invalid table name");
        when(mockParsedTranslation.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("record_sub_id")).thenReturn(null);
        when(mockParsedTranslation.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslation);

        verify(mockParsedTranslation, times(7)).get(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1))
                .tableName(ArgumentMatchers.eq("invalid table name"));
        verify(mockBuilder, times(1)).fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).recordSubId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).fieldValue(ArgumentMatchers.eq(null));

        final InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockEntityBuildResult);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockEntityBuildResult, times(1)).isSuccess();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockEntityBuildResult, times(1)).getData();

        inOrder.verify(mockResultRepo, times(1)).addNotice(isA(UnexpectedEnumValueNotice.class));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockEntityBuildResult);

        verifyNoInteractions(mockGtfsDataRepo);
    }

    @Test
    void duplicateTranslationShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo () {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Translation.TranslationBuilder mockBuilder = mock(Translation.TranslationBuilder.class, RETURNS_SELF);
        final Translation mockTranslation = mock(Translation.class);
        when(mockTranslation.getTableName()).thenReturn(TableName.FEED_INFO);
        when(mockGtfsDataRepo.addTranslation(ArgumentMatchers.any())).thenReturn(null);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTranslation).when(mockEntityBuildResult).getData();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        final ProcessParsedTranslation underTest =
                new ProcessParsedTranslation(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTranslation = mock(ParsedEntity.class);
        when(mockParsedTranslation.get("table_name")).thenReturn("feed_info");
        when(mockParsedTranslation.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslation.get("record_id")).thenReturn(null);
        when(mockParsedTranslation.get("record_sub_id")).thenReturn(null);
        when(mockParsedTranslation.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslation);

        verify(mockParsedTranslation, times(7)).get(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).tableName(ArgumentMatchers.eq("feed_info"));
        verify(mockBuilder, times(1)).fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockBuilder, times(1)).recordId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).recordSubId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).fieldValue(ArgumentMatchers.eq(null));

        final InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockTranslation,
                mockEntityBuildResult);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockEntityBuildResult, times(1)).isSuccess();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockEntityBuildResult, times(1)).getData();

        inOrder.verify(mockGtfsDataRepo, times(1)).addTranslation(mockTranslation);

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("translations.txt", noticeList.get(0).getFilename());
        assertNull(noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockTranslation, mockEntityBuildResult, mockGtfsDataRepo);
    }
}
