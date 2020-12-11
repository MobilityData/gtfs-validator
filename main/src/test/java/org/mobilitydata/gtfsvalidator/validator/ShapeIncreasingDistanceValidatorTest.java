/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.DecreasingShapeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ShapeIncreasingDistanceValidatorTest {
    @Mock
    final GtfsShapeTableContainer mockShapeTable = mock(GtfsShapeTableContainer.class);
    @InjectMocks
    final ShapeIncreasingDistanceValidator underTest = new ShapeIncreasingDistanceValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void increasingDistanceAlongShapeShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsShape> shapeCollection = ArrayListMultimap.create();
        GtfsShape mockShape0 = mock(GtfsShape.class);
        when(mockShape0.shapePtSequence()).thenReturn(0);
        when(mockShape0.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape0.shapeDistTraveled()).thenReturn(10.0);

        GtfsShape mockShape1 = mock(GtfsShape.class);
        when(mockShape1.shapePtSequence()).thenReturn(20);
        when(mockShape1.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape1.shapeDistTraveled()).thenReturn(45.0);

        GtfsShape mockShape2 = mock(GtfsShape.class);
        when(mockShape2.shapePtSequence()).thenReturn(30);
        when(mockShape2.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape2.shapeDistTraveled()).thenReturn(64.0);

        shapeCollection.put("shape id value", mockShape0);
        shapeCollection.put("shape id value", mockShape1);
        shapeCollection.put("shape id value", mockShape2);
        when(mockShapeTable.byShapeIdMap()).thenReturn(shapeCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockShape0, times(1)).hasShapeDistTraveled();
        verify(mockShape1, times(2)).hasShapeDistTraveled();
        verify(mockShape2, times(1)).hasShapeDistTraveled();
        verify(mockShape0, times(1)).shapeDistTraveled();
        verify(mockShape1, times(2)).shapeDistTraveled();
        verify(mockShape2, times(1)).shapeDistTraveled();
        verify(mockShapeTable, times(1)).byShapeIdMap();
        verifyNoMoreInteractions(mockShape0, mockShape1, mockShape2, mockShapeTable);
    }

    @Test
    public void lastShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsShape> shapeCollection = ArrayListMultimap.create();
        GtfsShape mockShape0 = mock(GtfsShape.class);
        when(mockShape0.shapePtSequence()).thenReturn(0);
        when(mockShape0.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape0.shapeDistTraveled()).thenReturn(10.0);

        GtfsShape mockShape1 = mock(GtfsShape.class);
        when(mockShape1.shapeId()).thenReturn("shape id value");
        when(mockShape1.shapePtSequence()).thenReturn(20);
        when(mockShape1.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape1.shapeDistTraveled()).thenReturn(45.0);
        when(mockShape1.csvRowNumber()).thenReturn(1L);

        GtfsShape mockShape2 = mock(GtfsShape.class);
        when(mockShape2.shapeId()).thenReturn("shape id value");
        when(mockShape2.shapePtSequence()).thenReturn(30);
        when(mockShape2.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape2.shapeDistTraveled()).thenReturn(33.0);
        when(mockShape2.csvRowNumber()).thenReturn(2L);

        shapeCollection.put("shape id value", mockShape0);
        shapeCollection.put("shape id value", mockShape1);
        shapeCollection.put("shape id value", mockShape2);
        when(mockShapeTable.byShapeIdMap()).thenReturn(shapeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockNoticeContainer, times(1)).addValidationNotice(captor.capture());
        DecreasingShapeDistanceNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("decreasing_shape_distance");
        assertThat(notice.getContext()).containsEntry("shapeId", "shape id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(notice.getContext()).containsEntry("shapeDistTraveled", 33.0);
        assertThat(notice.getContext()).containsEntry("shapePtSequence", 30);
        assertThat(notice.getContext()).containsEntry("prevCsvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("prevShapeDistTraveled", 45.0);
        assertThat(notice.getContext()).containsEntry("prevShapePtSequence", 20);

        verify(mockShape0, times(1)).hasShapeDistTraveled();
        verify(mockShape1, times(2)).hasShapeDistTraveled();
        verify(mockShape2, times(1)).hasShapeDistTraveled();
        verify(mockShape0, times(1)).shapeDistTraveled();
        verify(mockShape1, times(3)).shapeDistTraveled();
        verify(mockShape2, times(2)).shapeDistTraveled();
        verify(mockShape1, times(1)).csvRowNumber();
        verify(mockShape1, times(1)).shapePtSequence();
        verify(mockShape2, times(1)).csvRowNumber();
        verify(mockShape2, times(1)).shapeId();
        verify(mockShape2, times(1)).shapePtSequence();
        verify(mockShapeTable, times(1)).byShapeIdMap();
        verifyNoMoreInteractions(mockShape0, mockShape1, mockShape2, mockShapeTable);
    }

    @Test
    public void oneIntermediateShapeWithDecreasingDistanceAlongShapeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsShape> shapeCollection = ArrayListMultimap.create();
        GtfsShape mockShape0 = mock(GtfsShape.class);
        when(mockShape0.shapePtSequence()).thenReturn(0);
        when(mockShape0.shapeId()).thenReturn("shape id value");
        when(mockShape0.csvRowNumber()).thenReturn(0L);
        when(mockShape0.shapePtSequence()).thenReturn(2);
        when(mockShape0.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape0.shapeDistTraveled()).thenReturn(10.0);

        GtfsShape mockShape1 = mock(GtfsShape.class);
        when(mockShape1.shapeId()).thenReturn("shape id value");
        when(mockShape1.shapePtSequence()).thenReturn(20);
        when(mockShape1.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape1.shapeDistTraveled()).thenReturn(9.0);
        when(mockShape1.csvRowNumber()).thenReturn(1L);

        GtfsShape mockShape2 = mock(GtfsShape.class);
        when(mockShape2.shapePtSequence()).thenReturn(30);
        when(mockShape2.hasShapeDistTraveled()).thenReturn(true);
        when(mockShape2.shapeDistTraveled()).thenReturn(33.0);

        shapeCollection.put("shape id value", mockShape0);
        shapeCollection.put("shape id value", mockShape1);
        shapeCollection.put("shape id value", mockShape2);
        when(mockShapeTable.byShapeIdMap()).thenReturn(shapeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockNoticeContainer, times(1)).addValidationNotice(captor.capture());
        DecreasingShapeDistanceNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("decreasing_shape_distance");
        assertThat(notice.getContext()).containsEntry("shapeId", "shape id value");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("shapeDistTraveled", 9.0);
        assertThat(notice.getContext()).containsEntry("shapePtSequence", 20);
        assertThat(notice.getContext()).containsEntry("prevCsvRowNumber", 0L);
        assertThat(notice.getContext()).containsEntry("prevShapeDistTraveled", 10.0);
        assertThat(notice.getContext()).containsEntry("prevShapePtSequence", 2);

        verify(mockShape0, times(1)).hasShapeDistTraveled();
        verify(mockShape1, times(2)).hasShapeDistTraveled();
        verify(mockShape2, times(1)).hasShapeDistTraveled();
        verify(mockShape0, times(2)).shapeDistTraveled();
        verify(mockShape0, times(1)).csvRowNumber();
        verify(mockShape0, times(1)).shapePtSequence();
        verify(mockShape1, times(3)).shapeDistTraveled();
        verify(mockShape2, times(1)).shapeDistTraveled();
        verify(mockShape1, times(1)).csvRowNumber();
        verify(mockShape1, times(1)).shapePtSequence();
        verify(mockShape1, times(1)).shapeId();
        verify(mockShapeTable, times(1)).byShapeIdMap();
        verifyNoMoreInteractions(mockShape0, mockShape1, mockShape2, mockShapeTable);
    }
}
