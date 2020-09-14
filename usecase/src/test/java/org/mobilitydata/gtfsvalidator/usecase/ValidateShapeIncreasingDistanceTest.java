/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DecreasingShapeDistanceNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateShapeIncreasingDistanceTest {

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void strictlyIncreasingDistanceInShapeWithoutNullValuesShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(10f);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(15f);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(20f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id ";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void strictlyIncreasingDistanceInShapeWithNullValuesShouldNotGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(20f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);


        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void decreasingDistanceInShapeWithoutNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(10f);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(15f);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(13f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DecreasingShapeDistanceNotice> noticeList = captor.getAllValues();
        final DecreasingShapeDistanceNotice notice = noticeList.get(0);

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(58, notice.getCode());
        assertEquals(4, notice.getNoticeSpecific(KEY_SHAPE_PT_SEQUENCE));
        assertEquals(13f, notice.getNoticeSpecific(KEY_SHAPE_DIST_TRAVELED));
        assertEquals(3, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE));
        assertEquals(15f, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED));

        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void decreasingDistanceInShapeWithNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(2f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DecreasingShapeDistanceNotice> noticeList = captor.getAllValues();
        final DecreasingShapeDistanceNotice notice = noticeList.get(0);

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(58, notice.getCode());
        assertEquals(4, notice.getNoticeSpecific(KEY_SHAPE_PT_SEQUENCE));
        assertEquals(2f, notice.getNoticeSpecific(KEY_SHAPE_DIST_TRAVELED));
        assertEquals(1, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED));

        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void equalDistanceInStopTimeWithoutNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(10f);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(10f);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(22f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DecreasingShapeDistanceNotice> noticeList = captor.getAllValues();
        final DecreasingShapeDistanceNotice notice = noticeList.get(0);

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(58, notice.getCode());
        assertEquals(3, notice.getNoticeSpecific(KEY_SHAPE_PT_SEQUENCE));
        assertEquals(10f, notice.getNoticeSpecific(KEY_SHAPE_DIST_TRAVELED));
        assertEquals(2, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE));
        assertEquals(10f, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED));

        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void equalDistanceInStopTimeWithNullValuesShouldGenerateErrorNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(null);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(5f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DecreasingShapeDistanceNotice> noticeList = captor.getAllValues();
        final DecreasingShapeDistanceNotice notice = noticeList.get(0);

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(58, notice.getCode());
        assertEquals(4, notice.getNoticeSpecific(KEY_SHAPE_PT_SEQUENCE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_SHAPE_DIST_TRAVELED));
        assertEquals(1, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED));

        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint, mockResultRepo);
    }

    // suppressed warning regarding ignored result of method used in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void largeValueInFirstSequenceIndexShouldBeDetectedAndDecreasingDistanceShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(10f);
        final ShapePoint secondShapePoint = mock(ShapePoint.class);
        when(secondShapePoint.getShapeDistTraveled()).thenReturn(5f);
        final ShapePoint thirdShapePoint = mock(ShapePoint.class);
        when(thirdShapePoint.getShapeDistTraveled()).thenReturn(15f);
        final ShapePoint fourthShapePoint = mock(ShapePoint.class);
        when(fourthShapePoint.getShapeDistTraveled()).thenReturn(20f);

        final TreeMap<Integer, ShapePoint> shape = new TreeMap<>();
        shape.put(1, firstShapePoint);
        shape.put(2, secondShapePoint);
        shape.put(3, thirdShapePoint);
        shape.put(4, fourthShapePoint);

        final String shapeId = "shape id";
        final ValidateShapeIncreasingDistance underTest = new ValidateShapeIncreasingDistance(mockResultRepo);

        underTest.execute(shape, shapeId);

        verify(firstShapePoint, times(1)).getShapeDistTraveled();
        verify(secondShapePoint, times(1)).getShapeDistTraveled();
        verify(thirdShapePoint, times(1)).getShapeDistTraveled();
        verify(fourthShapePoint, times(1)).getShapeDistTraveled();

        final ArgumentCaptor<DecreasingShapeDistanceNotice> captor =
                ArgumentCaptor.forClass(DecreasingShapeDistanceNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DecreasingShapeDistanceNotice> noticeList = captor.getAllValues();
        final DecreasingShapeDistanceNotice notice = noticeList.get(0);

        assertEquals(1, noticeList.size());
        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("ERROR", notice.getLevel());
        assertEquals(58, notice.getCode());
        assertEquals(2, notice.getNoticeSpecific(KEY_SHAPE_PT_SEQUENCE));
        assertEquals(5f, notice.getNoticeSpecific(KEY_SHAPE_DIST_TRAVELED));
        assertEquals(1, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_PT_SEQUENCE));
        assertEquals(10f, notice.getNoticeSpecific(KEY_SHAPE_PREVIOUS_SHAPE_DIST_TRAVELED));

        verifyNoMoreInteractions(firstShapePoint, secondShapePoint, thirdShapePoint,
                fourthShapePoint, mockResultRepo);
    }
}