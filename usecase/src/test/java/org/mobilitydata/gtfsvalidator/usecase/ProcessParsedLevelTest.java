package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedLevelTest {

    @Test
    public void validatedParsedLevelShouldCreateLevelEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Level.LevelBuilder mockBuilder = mock(Level.LevelBuilder.class, RETURNS_SELF);
        final Level mockLevel = mock(Level.class);
        final ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockLevel);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedLevel.get("level_id")).thenReturn("level id");
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("level name");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        when(mockGtfsDataRepo.addLevel(mockLevel)).thenReturn(mockLevel);

        final ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedLevel);

        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_id"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_index"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_name"));

        verify(mockBuilder, times(1)).levelId("level id");
        verify(mockBuilder, times(1)).levelIndex(2.0f);
        verify(mockBuilder, times(1)).levelName("level name");
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();
        verify(mockGenericObject, times(1)).isSuccess();

        verify(mockGtfsDataRepo, times(1)).addLevel(ArgumentMatchers.eq(mockLevel));

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockLevel, mockParsedLevel, mockResultRepo);
    }

    @Test
    public void invalidLevelShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Level.LevelBuilder mockBuilder = mock(Level.LevelBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedLevel = mock(ParsedEntity.class);

        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.isSuccess()).thenReturn(false);
        when(mockGenericObject.getData()).thenReturn(mockNoticeCollection);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        final ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedLevel.get("level_id")).thenReturn("level id");
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("level name");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        underTest.execute(mockParsedLevel);

        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_id"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_index"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_name"));

        verify(mockBuilder, times(1)).levelId(ArgumentMatchers.eq("level id"));
        verify(mockBuilder, times(1)).levelIndex(ArgumentMatchers.eq(2.0f));
        verify(mockBuilder, times(1)).levelName(ArgumentMatchers.eq("level name"));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedLevel, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    public void duplicateLevelShouldThrowExceptionAndEntityMustBeUniqueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Level.LevelBuilder mockBuilder = mock(Level.LevelBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedLevel = mock(ParsedEntity.class);
        final Level mockLevel = mock(Level.class);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        when(mockGenericObject.getData()).thenReturn(mockLevel);


        when(mockLevel.getLevelId()).thenReturn("level id");
        when(mockLevel.getLevelIndex()).thenReturn(2.0f);
        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        final ProcessParsedLevel underTest = new ProcessParsedLevel(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedLevel.get("level_id")).thenReturn("level id");
        when(mockParsedLevel.get("level_index")).thenReturn(2.0f);
        when(mockParsedLevel.get("level_name")).thenReturn("level name");
        when(mockParsedLevel.getEntityId()).thenReturn("entity id");

        underTest.execute(mockParsedLevel);

        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_id"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_index"));
        verify(mockParsedLevel, times(1)).get(ArgumentMatchers.eq("level_name"));

        verify(mockBuilder, times(1)).levelId(ArgumentMatchers.eq("level id"));
        verify(mockBuilder, times(1)).levelIndex(ArgumentMatchers.eq(2.0f));
        verify(mockBuilder, times(1)).levelName(ArgumentMatchers.eq("level name"));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedLevel, times(1)).getEntityId();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockGtfsDataRepo, times(1)).addLevel(ArgumentMatchers.eq(mockLevel));

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_id", noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedLevel, mockLevel,
                mockGenericObject);
    }
}