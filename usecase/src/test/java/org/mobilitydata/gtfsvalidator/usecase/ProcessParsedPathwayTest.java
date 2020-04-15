package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
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

class ProcessParsedPathwayTest {

    private final static String STRING_TEST_VALUE = "test";
    private final static Float FLOAT_TEST_VALUE = 2.0f;
    private final static int INT_TEST_VALUE = 2;

    @Test
    public void validatedParsedPathwayShouldCreatePathwayEntityAndBeAddedToGtfsDataRepository()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway mockPathway = mock(Pathway.class);

        final Pathway.PathwayBuilder mockBuilder = mock(Pathway.PathwayBuilder.class);
        when(mockBuilder.pathwayId(anyString())).thenCallRealMethod();
        when(mockBuilder.fromStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.toStopId(anyString())).thenCallRealMethod();
        when(mockBuilder.pathwayMode(anyInt())).thenCallRealMethod();
        when(mockBuilder.isBidirectional(anyInt())).thenCallRealMethod();
        when(mockBuilder.length(anyFloat())).thenCallRealMethod();
        when(mockBuilder.traversalTime(anyInt())).thenCallRealMethod();
        when(mockBuilder.stairCount(anyInt())).thenCallRealMethod();
        when(mockBuilder.maxSlope(anyFloat())).thenCallRealMethod();
        when(mockBuilder.minWidth(anyFloat())).thenCallRealMethod();
        when(mockBuilder.signpostedAs(anyString())).thenCallRealMethod();
        when(mockBuilder.reversedSignpostedAs(anyString())).thenCallRealMethod();

        when(mockBuilder.build()).thenReturn(mockPathway);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        underTest.execute(mockParsedPathway);

        verify(mockParsedPathway, times(12)).get(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        final InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1)).addPathway(ArgumentMatchers.eq(mockPathway));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockPathway);
    }

    @Test
    public void nullPathwayIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(null);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("field pathway_id can not be null", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).pathwayId(null);
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("pathway_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void nullFromStopIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(null);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("field from_stop_id can not be null", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).fromStopId(null);
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("from_stop_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void nullToStopIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(null);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("field to_stop_id can not be null", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).toStopId(null);
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("to_stop_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void nullPathwayModeShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(null);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("unexpected value for field pathway_mode", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).pathwayMode(null);
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("pathway_mode", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidPathwayModeShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(15);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("unexpected value for field pathway_mode", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(15);
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("pathway_mode", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("15", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void nullIsBidirectionalShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(null);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field is_bidirectional", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).isBidirectional(null);
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("is_bidirectional", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidIsBidirectionalShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(3);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field is_bidirectional", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(3);
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("is_bidirectional", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("3", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidLengthShouldThrowExceptionAndFloatFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(-2.0f);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field length", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(1);
        verify(mockBuilder, times(1)).length(-2.0f);
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("length", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-2.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidTraversalTimeShouldThrowExceptionAndFloatFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(-2);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field traversal_time", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(-2);
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("traversal_time", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-2, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidStairCountShouldThrowExceptionAndFloatFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(-2);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field stair_count", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(-2);
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("stair_count", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-2, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void invalidMinWidthShouldThrowExceptionAndFloatFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway.PathwayBuilder mockBuilder = spy(Pathway.PathwayBuilder.class);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedPathway = mock(ParsedEntity.class);

        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(-2f);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedPathway));

        Assertions.assertEquals("invalid value for field min_width", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockBuilder, times(1)).pathwayId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).fromStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).toStopId(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).pathwayMode(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).isBidirectional(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).length(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).traversalTime(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).stairCount(ArgumentMatchers.anyInt());
        verify(mockBuilder, times(1)).maxSlope(ArgumentMatchers.anyFloat());
        verify(mockBuilder, times(1)).minWidth(-2f);
        verify(mockBuilder, times(1)).signpostedAs(ArgumentMatchers.anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).build();
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("min_width", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-2f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedPathway, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void
    duplicatePathwayShouldThrowExceptionAndEntityMustBeUniqueNoticeShouldBeAddedToResultRepo()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Pathway mockPathway = mock(Pathway.class);
        final Pathway.PathwayBuilder mockBuilder = mock(Pathway.PathwayBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockPathway);

        when(mockPathway.getPathwayId()).thenReturn(STRING_TEST_VALUE);

        final ProcessParsedPathway underTest = new ProcessParsedPathway(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedPathway = mock(ParsedEntity.class);
        when(mockParsedPathway.get("pathway_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("from_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("to_stop_id")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("pathway_mode")).thenReturn(1);
        when(mockParsedPathway.get("is_bidirectional")).thenReturn(1);
        when(mockParsedPathway.get("length")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("traversal_tine")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("stair_count")).thenReturn(INT_TEST_VALUE);
        when(mockParsedPathway.get("max_slope")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("min_width")).thenReturn(FLOAT_TEST_VALUE);
        when(mockParsedPathway.get("signposted_as")).thenReturn(STRING_TEST_VALUE);
        when(mockParsedPathway.get("reserved_signposted_as")).thenReturn(STRING_TEST_VALUE);

        when(mockGtfsDataRepo.addPathway(mockPathway))
                .thenThrow(new SQLIntegrityConstraintViolationException("pathway must be unique in dataset"));

        final Exception exception = Assertions.assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedPathway));
        Assertions.assertEquals("pathway must be unique in dataset", exception.getMessage());

        verify(mockParsedPathway, times(12)).get(anyString());

        verify(mockGtfsDataRepo, times(1)).addPathway(ArgumentMatchers.isA(Pathway.class));

        verify(mockBuilder, times(1)).pathwayId(anyString());
        verify(mockBuilder, times(1)).fromStopId(anyString());
        verify(mockBuilder, times(1)).toStopId(anyString());
        verify(mockBuilder, times(1)).pathwayMode(anyInt());
        verify(mockBuilder, times(1)).isBidirectional(anyInt());
        verify(mockBuilder, times(1)).length(anyFloat());
        verify(mockBuilder, times(1)).traversalTime(anyInt());
        verify(mockBuilder, times(1)).stairCount(anyInt());
        verify(mockBuilder, times(1)).maxSlope(anyFloat());
        verify(mockBuilder, times(1)).minWidth(anyFloat());
        verify(mockBuilder, times(1)).signpostedAs(anyString());
        verify(mockBuilder, times(1)).reversedSignpostedAs(anyString());
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedPathway, times(1)).getEntityId();

        final ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("pathways.txt", noticeList.get(0).getFilename());
        assertEquals("pathway_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
    }
}