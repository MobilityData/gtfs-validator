package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
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

class ProcessParsedShapePointTest {
    private static final String SHAPE_ID = "shape_id";
    private static final String SHAPE_PT_LAT = "shape_pt_lat";
    private static final String SHAPE_PT_LON = "shape_pt_lon";
    private static final String SHAPE_PT_SEQUENCE = "shape_pt_sequence";
    private static final String SHAPE_DIST_TRAVELED = "shape_dist_traveled";

    @Test
    public void validParsedShapeShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ShapePoint.ShapeBuilder mockBuilder = mock(ShapePoint.ShapeBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);
        final ShapePoint mockShapePoint = mock(ShapePoint.class);
        //noinspection rawtypes to avoid lind
        final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockShapePoint);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked to avoid lind
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_ID))).thenReturn(null);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LAT))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LON))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_SEQUENCE))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_DIST_TRAVELED))).thenReturn(100f);

        when(mockGtfsDataRepo.addShapePoint(mockShapePoint)).thenReturn(mockShapePoint);

        final ProcessParsedShapePoint underTest = new ProcessParsedShapePoint(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedShape);

        verify(mockParsedShape, times(1)).get(SHAPE_ID);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LAT);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LON);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_SEQUENCE);
        verify(mockParsedShape, times(1)).get(SHAPE_DIST_TRAVELED);

        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockBuilder, times(1)).build();
        verify(mockGtfsDataRepo, times(1)).addShapePoint(ArgumentMatchers.eq(mockShapePoint));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedShape, mockGenericObject);
    }

    @Test
    public void invalidParsedShapeShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ShapePoint.ShapeBuilder mockBuilder = mock(ShapePoint.ShapeBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);

        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        //noinspection rawtypes to avoid lind
        final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.isSuccess()).thenReturn(false);
        when(mockGenericObject.getData()).thenReturn(mockNoticeCollection);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        final ProcessParsedShapePoint underTest = new ProcessParsedShapePoint(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_ID))).thenReturn("test id");
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LAT))).thenReturn(190.0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LON))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_SEQUENCE))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_DIST_TRAVELED))).thenReturn(100f);

        underTest.execute(mockParsedShape);

        verify(mockParsedShape, times(1)).get(SHAPE_ID);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LAT);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LON);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_SEQUENCE);
        verify(mockParsedShape, times(1)).get(SHAPE_DIST_TRAVELED);

        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("test id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(190.0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100f));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedShape, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    public void duplicateShapeShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final ShapePoint.ShapeBuilder mockBuilder = mock(ShapePoint.ShapeBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedShape = mock(ParsedEntity.class);
        when(mockParsedShape.getEntityId()).thenReturn("shape id");
        final ShapePoint mockShapePoint = mock(ShapePoint.class);

        //noinspection rawtypes to avoid lind
        final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        when(mockGenericObject.getData()).thenReturn(mockShapePoint);

        when(mockShapePoint.getShapeId()).thenReturn(SHAPE_ID);
        //noinspection unchecked to avoid lind
        when(mockBuilder.build()).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addShapePoint(mockShapePoint)).thenReturn(null);

        final ProcessParsedShapePoint underTest = new ProcessParsedShapePoint(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_ID))).thenReturn("shape id");
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LAT))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_LON))).thenReturn(0f);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_PT_SEQUENCE))).thenReturn(1);
        when(mockParsedShape.get(ArgumentMatchers.eq(SHAPE_DIST_TRAVELED))).thenReturn(100.0f);

        underTest.execute(mockParsedShape);

        verify(mockParsedShape, times(1)).get(SHAPE_ID);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LAT);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_LON);
        verify(mockParsedShape, times(1)).get(SHAPE_PT_SEQUENCE);
        verify(mockParsedShape, times(1)).get(SHAPE_DIST_TRAVELED);
        //noinspection ResultOfMethodCallIgnored to avoid lind
        verify(mockParsedShape, times(1)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addShapePoint(ArgumentMatchers.eq(mockShapePoint));


        verify(mockBuilder, times(1)).shapeId(ArgumentMatchers.eq("shape id"));
        verify(mockBuilder, times(1)).shapePtLat(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtLon(ArgumentMatchers.eq(0f));
        verify(mockBuilder, times(1)).shapePtSequence(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).shapeDistTraveled(ArgumentMatchers.eq(100.f));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored to avoid lind
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("shapes.txt", noticeList.get(0).getFilename());
        assertEquals(SHAPE_ID, noticeList.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedShape, mockShapePoint,
                mockGenericObject);
    }
}