package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableCompositeKey;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableSimpleKey;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableSingleRow;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

class ProcessParsedTranslationTest {

    private final String STRING_TEST_VALUE = "test_value";

    @Test
    public void validTranslationWithFeedInfoAsTableNameShouldCreateTranslationTableSingleRowAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                spy(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                mock(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSingleRow = mock(ParsedEntity.class);
        when(mockParsedTranslationSingleRow.get("table_name")).thenReturn("feed_info");
        when(mockParsedTranslationSingleRow.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSingleRow.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSingleRow.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSingleRow.get("record_id")).thenReturn(null);
        when(mockParsedTranslationSingleRow.get("record_sub_id")).thenReturn(null);
        when(mockParsedTranslationSingleRow.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSingleRow);

        verify(mockParsedTranslationSingleRow, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSingleRowBuilder, times(1))
                .tableName(ArgumentMatchers.eq("feed_info"));
        verify(mockTranslationSingleRowBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSingleRowBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSingleRowBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSingleRowBuilder, times(1))
                .recordId(ArgumentMatchers.eq(null));
        verify(mockTranslationSingleRowBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(null));
        verify(mockTranslationSingleRowBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSingleRow> captor = ArgumentCaptor.forClass(TranslationTableSingleRow.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSingleRowBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSingleRowBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSingleRowBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForStopTimesAndDefinedRecordIdShouldCreateTranslationTableCompositeKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                mock(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                spy(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationCompositeKey = mock(ParsedEntity.class);
        when(mockParsedTranslationCompositeKey.get("table_name")).thenReturn("stop_times");
        when(mockParsedTranslationCompositeKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationCompositeKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationCompositeKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationCompositeKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationCompositeKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationCompositeKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationCompositeKey);

        verify(mockParsedTranslationCompositeKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("stop_times"));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationCompositeKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableCompositeKey> captor = ArgumentCaptor.forClass(TranslationTableCompositeKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationCompositeKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationCompositeKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationCompositeKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForTripsShouldCreateTranslationTableSimpleKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                spy(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSimpleKey = mock(ParsedEntity.class);
        when(mockParsedTranslationSimpleKey.get("table_name")).thenReturn("trips");
        when(mockParsedTranslationSimpleKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSimpleKey);

        verify(mockParsedTranslationSimpleKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("trips"));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSimpleKey> captor = ArgumentCaptor.forClass(TranslationTableSimpleKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSimpleKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForAgencyShouldCreateTranslationTableSimpleKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                spy(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSimpleKey = mock(ParsedEntity.class);
        when(mockParsedTranslationSimpleKey.get("table_name")).thenReturn("agency");
        when(mockParsedTranslationSimpleKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSimpleKey);

        verify(mockParsedTranslationSimpleKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("agency"));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSimpleKey> captor = ArgumentCaptor.forClass(TranslationTableSimpleKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSimpleKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForStopsShouldCreateTranslationTableSimpleKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                spy(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSimpleKey = mock(ParsedEntity.class);
        when(mockParsedTranslationSimpleKey.get("table_name")).thenReturn("stops");
        when(mockParsedTranslationSimpleKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSimpleKey);

        verify(mockParsedTranslationSimpleKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("stops"));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSimpleKey> captor = ArgumentCaptor.forClass(TranslationTableSimpleKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSimpleKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForRoutesShouldCreateTranslationTableSimpleKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                spy(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSimpleKey = mock(ParsedEntity.class);
        when(mockParsedTranslationSimpleKey.get("table_name")).thenReturn("routes");
        when(mockParsedTranslationSimpleKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSimpleKey);

        verify(mockParsedTranslationSimpleKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("routes"));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSimpleKey> captor = ArgumentCaptor.forClass(TranslationTableSimpleKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSimpleKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }

    @Test
    public void validTranslationForLevelsShouldCreateTranslationTableSimpleKeyAndBeAddedToRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        TranslationTableSingleRow.TranslationTableSingleRowBuilder mockTranslationSingleRowBuilder =
                mock(TranslationTableSingleRow.TranslationTableSingleRowBuilder.class);

        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder mockTranslationSimpleKeyBuilder =
                spy(TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder.class);

        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder mockTranslationCompositeKeyBuilder =
                mock(TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder.class);

        ProcessParsedTranslation underTest = new ProcessParsedTranslation(mockTranslationSingleRowBuilder,
                mockTranslationSimpleKeyBuilder,
                mockTranslationCompositeKeyBuilder,
                mockResultRepo,
                mockGtfsDataRepo);

        ParsedEntity mockParsedTranslationSimpleKey = mock(ParsedEntity.class);
        when(mockParsedTranslationSimpleKey.get("table_name")).thenReturn("levels");
        when(mockParsedTranslationSimpleKey.get("field_name")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("language")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("translation")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("record_sub_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedTranslationSimpleKey.get("field_value")).thenReturn(null);

        underTest.execute(mockParsedTranslationSimpleKey);

        verify(mockParsedTranslationSimpleKey, times(7))
                .get(ArgumentMatchers.anyString());
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .tableName(ArgumentMatchers.eq("levels"));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldName(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .translation(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .language(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .recordSubId(ArgumentMatchers.eq(STRING_TEST_VALUE));
        verify(mockTranslationSimpleKeyBuilder, times(1))
                .fieldValue(ArgumentMatchers.eq(null));

        ArgumentCaptor<TranslationTableSimpleKey> captor = ArgumentCaptor.forClass(TranslationTableSimpleKey.class);

        verify(mockGtfsDataRepo, times(1)).
                addEntity(captor.capture());

        InOrder inOrder = inOrder(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockTranslationSimpleKeyBuilder, times(1)).build();

        verifyNoMoreInteractions(mockTranslationSimpleKeyBuilder, mockResultRepo, mockGtfsDataRepo);
    }
}