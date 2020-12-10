package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnusedShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class ShapeUsageValidatorTest {
    @Mock
    final GtfsShapeTableContainer mockShapeTable = mock(GtfsShapeTableContainer.class);
    @Mock
    final GtfsTripTableContainer mockTripTable = mock(GtfsTripTableContainer.class);
    @InjectMocks
    final ShapeUsageValidator underTest = new ShapeUsageValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void allShapeUsedShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsShape mockShape = mock(GtfsShape.class);
        when(mockShape.shapeId()).thenReturn("shape id value");
        List<GtfsShape> shapeCollection = new ArrayList<>();
        shapeCollection.add(mockShape);
        when(mockShapeTable.getEntities()).thenReturn(shapeCollection);
        GtfsTrip mockTrip = mock(GtfsTrip.class);
        List<GtfsTrip> tripCollection = new ArrayList<>();
        tripCollection.add(mockTrip);
        when(mockTripTable.byShapeId("shape id value")).thenReturn(tripCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer, mockTrip);
        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockShapeTable, times(1)).getEntities();
        verify(mockShape, times(1)).shapeId();
        verify(mockTripTable, times(1)).byShapeId("shape id value");
        verifyNoMoreInteractions(mockTrip, mockShape, mockShapeTable);
    }

    @Test
    public void unusedShapeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        GtfsShape mockShape = mock(GtfsShape.class);
        when(mockShape.shapeId()).thenReturn("shape id value");
        when(mockShape.csvRowNumber()).thenReturn(2L);
        List<GtfsShape> shapeCollection = new ArrayList<>();
        shapeCollection.add(mockShape);
        when(mockShapeTable.getEntities()).thenReturn(shapeCollection);
        List<GtfsTrip> tripCollection = new ArrayList<>();
        when(mockTripTable.byShapeId("shape id value")).thenReturn(tripCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<UnusedShapeNotice> captor =
                ArgumentCaptor.forClass(UnusedShapeNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        UnusedShapeNotice notice = captor.getValue();
        assertThat(notice.getCode()).matches("unused_shape");
        assertThat(notice.getContext()).containsEntry("shapeId", "shape id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);

        //noinspection ResultOfMethodCallIgnored stubbed method
        verify(mockShapeTable, times(1)).getEntities();
        verify(mockShape, times(1)).shapeId();
        verify(mockTripTable, times(1)).byShapeId("shape id value");
        verify(mockShape, times(1)).csvRowNumber();
        verifyNoMoreInteractions(mockShape, mockShapeTable);
    }
}
