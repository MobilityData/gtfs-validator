package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessParsedLevelTest {

    @Test
    public void processValidLevelShouldNotThrowException() throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        Level.LevelBuilder mockBuilder = mock(Level.LevelBuilder.class, RETURNS_SELF);

        Level mockLevel = mock(Level.class);
        when(mockBuilder.build()).thenReturn(mockLevel);

        ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        when(mockParsedLevel.get("level_id")).thenReturn("test_id");
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("test");
        when(mockParsedLevel.getEntityId()).thenReturn("entity_id");

        ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedLevel);

        verify(mockParsedLevel, times(3)).get(anyString());

        verify(mockBuilder, times(1)).levelId("test_id");
        verify(mockBuilder, times(1)).levelIndex(2.0f);
        verify(mockBuilder, times(1)).levelName("test");

        InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo, mockLevel, mockResultRepo, mockParsedLevel);
        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1)).addLevel(mockLevel);

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockLevel, mockParsedLevel, mockResultRepo);
    }

    @Test
    public void processLevelEntityWithNullLevelIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        Level.LevelBuilder mockBuilder = spy(Level.LevelBuilder.class);

        ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        when(mockParsedLevel.get("level_id")).thenReturn(null);
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("test");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedLevel));

        assertEquals("field level_id can not be null", exception.getMessage());

        verify(mockParsedLevel, times(3)).get(anyString());

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).levelId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).levelIndex(ArgumentMatchers.eq(2.0f));
        verify(mockBuilder, times(1)).levelName(ArgumentMatchers.eq("test"));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedLevel, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_id", noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedLevel, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processLevelWithNullLevelIndexShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        Level.LevelBuilder mockBuilder = spy(Level.LevelBuilder.class);

        ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        when(mockParsedLevel.get("level_id")).thenReturn("test id");
        when(mockParsedLevel.get("level_index")).thenReturn(null);
        when(mockParsedLevel.get("level_name")).thenReturn("test");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedLevel));

        assertEquals("field level_index can not be null", exception.getMessage());

        verify(mockParsedLevel, times(3)).get(anyString());

        verify(mockBuilder, times(1)).levelId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).levelIndex(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).levelName(ArgumentMatchers.eq("test"));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedLevel, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_index", noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedLevel, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void duplicateLevelShouldThrowExceptionAndEntityMustBeUniqueNoticeShouldBeAddedToResultRepo()
            throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Level mockLevel = mock(Level.class);
        when(mockLevel.getLevelId()).thenReturn("test id");
        when(mockLevel.getLevelIndex()).thenReturn(2.0f);

        Level.LevelBuilder mockBuilder = mock(Level.LevelBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockLevel);

        ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        when(mockParsedLevel.get("level_id")).thenReturn("test id");
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("test");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        when(mockGtfsDataRepo.addLevel(mockLevel)).thenThrow(
                new SQLIntegrityConstraintViolationException("level must be unique in dataset"));

        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedLevel));

        assertEquals("level must be unique in dataset", exception.getMessage());

        verify(mockParsedLevel, times(3)).get(anyString());

        verify(mockBuilder, times(1)).levelId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).levelIndex(ArgumentMatchers.eq(2.0f));
        verify(mockBuilder, times(1)).levelName(ArgumentMatchers.eq("test"));

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedLevel, times(1)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addLevel(mockLevel);

        ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_id", noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedLevel, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}