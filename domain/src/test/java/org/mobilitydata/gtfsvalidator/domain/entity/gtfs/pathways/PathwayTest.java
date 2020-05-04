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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PathwayTest {
    private static final String PATHWAY_ID = "pathway id";
    private static final String STOP_ID_1 = "stop id 1";
    private static final String STOP_ID_0 = "stop id 0";
    private static final String FILENAME = "pathways.txt";

    // Field pathwayId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullPathwayIdShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        @SuppressWarnings("unchecked") final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.pathwayId(null)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("pathway_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullFromStopIdShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(null)
                .toStopId("stop id")
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("from_stop_id", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToStopIdShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(null)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("to_stop_id", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field pathwayMode is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToPathwayModeShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(null)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("pathway_mode", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createPathwayWithInvalidToPathwayModeShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(13)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("pathway_mode", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals("13", noticeList.get(0).getEnumValue());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createPathwayWithInvalidIsBidirectionalShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(3)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("is_bidirectional", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals("3", noticeList.get(0).getEnumValue());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createPathwayWillNullIsBidirectionalShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(null)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("is_bidirectional", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createPathwayWithInvalidLengthBidirectionalShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(-10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("length", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals(-10.0, noticeList.get(0).getActualValue());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createPathwayWithInvalidTraversalTimeShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(-2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("traversal_time", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals(-2, noticeList.get(0).getActualValue());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createPathwayWithInvalidStairCountShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(-3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("stair_count", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals(-3, noticeList.get(0).getActualValue());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createPathwayWithInvalidMinWidthShouldGenerateNotice() {
        @SuppressWarnings("rawtypes") final List mockNoticeCollection = mock(List.class);
        //noinspection unchecked
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder(mockNoticeCollection);

        underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(-30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        //noinspection unchecked
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("min_width", noticeList.get(0).getFieldName());
        assertEquals(PATHWAY_ID, noticeList.get(0).getEntityId());
        assertEquals(-30.0, noticeList.get(0).getActualValue());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}