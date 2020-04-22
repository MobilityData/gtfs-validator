package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Shape;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
class ProcessParsedShapeTest {

    @Test
    public void validatedShapeEntityWithNullShapeIdShouldThrowExceptionAndAddMissingRequiredValueNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn(null);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("field shape_id can not be null in file shapes.txt", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedShape, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void validatedShapeEntityWithTooBigLatShouldThrowExceptionAndAddFloatFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(190.0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_latitude", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(190.0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_pt_lat", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(-90.0f, noticeList.get(0).getRangeMin());
        assertEquals(90.0f, noticeList.get(0).getRangeMax());
        assertEquals(190.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void validatedShapeEntityWithTooSmallLatShouldThrowExceptionAndAddFloatFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(-190.0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_latitude", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(-190.0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_pt_lat", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(-90.0f, noticeList.get(0).getRangeMin());
        assertEquals(90.0f, noticeList.get(0).getRangeMax());
        assertEquals(-190.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void validatedShapeEntityWithTooBigLongShouldThrowExceptionAndAddFloatFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(190.0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_longitude", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(190.0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_pt_lon", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(-180.0f, noticeList.get(0).getRangeMin());
        assertEquals(180.0f, noticeList.get(0).getRangeMax());
        assertEquals(190.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void validatedShapeEntityWithTooSmallLongShouldThrowExceptionAndAddFloatFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(-190.f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_longitude", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(-190.0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_pt_lon", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(-180.0f, noticeList.get(0).getRangeMin());
        assertEquals(180.0f, noticeList.get(0).getRangeMax());
        assertEquals(-190.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void validatedShapeWitNegShapePtSequenceShouldThrowExceptionAndAddIntegerFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(-1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_pt_sequence", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(-1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_pt_sequence", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-1, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void shapeWitNegShapeDistTraveledShouldThrowExceptionAndAddIntegerFieldValueOutOfRangeNoticeToRepo() {

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Shape.ShapeBuilder mockBuilder = spy(Shape.ShapeBuilder.class);

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(-100f);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("invalid value for field shape_dist_traveled", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(-100.0f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_dist_traveled", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-100.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void duplicateShapeShouldThrowExceptionAndAddShapeMustBeUniqueNoticeToRepo()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Shape.ShapeBuilder mockBuilder = mock(Shape.ShapeBuilder.class, RETURNS_SELF);

        final Shape mockShape = mock(Shape.class);
        when(mockBuilder.build()).thenReturn(mockShape);
        when(mockGtfsDataRepo.addShape(mockShape))
                .thenThrow(new SQLIntegrityConstraintViolationException("shape must be unique in dataset"));

        final ProcessParsedShape underTest = new ProcessParsedShape(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        when(mockParsedShape.get(ArgumentMatchers.eq("shape_id"))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lat"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_lon"))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_pt_sequence"))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq("shape_dist_traveled"))).thenReturn(100.0f);

        final Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedShape));

        assertEquals("shape must be unique in dataset", exception.getMessage());

        verify(mockParsedShape, times(5)).get(anyString());

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100.f));

        verify(mockBuilder, times(1)).build();

        verify(mockParsedShape, times(1)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addShape(ArgumentMatchers.isA(Shape.class));

        final ArgumentCaptor<EntityMustBeUniqueNotice> captor =
                ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals("shape_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}