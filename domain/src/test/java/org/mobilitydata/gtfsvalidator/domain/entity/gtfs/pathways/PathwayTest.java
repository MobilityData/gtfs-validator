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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousFloatValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousIntegerValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathwayTest {
    private static final String PATHWAY_ID = "pathway id";
    private static final String STOP_ID_1 = "stop id 1";
    private static final String STOP_ID_0 = "stop id 0";
    private static final String FILENAME = "pathways.txt";
    final float PATHWAY_MIN_LENGTH_KEY = 0f;
    final float PATHWAY_MAX_LENGTH_KEY = 400f;
    final int PATHWAY_MIN_TRAVERSAL_TIME_KEY = 3;
    final int PATHWAY_MAX_TRAVERSAL_TIME_KEY = 30;
    final int PATHWAY_MIN_STAIR_COUNT_KEY = 1;
    final int PATHWAY_MAX_STAIR_COUNT_KEY = 20;
    final float PATHWAY_MAX_SLOPE_KEY = 0.30f;
    final float PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY = 0.3f;
    final float PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY = 50f;


    // Field pathwayId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullPathwayIdShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(null)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("pathway_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullFromStopIdShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(null)
                .toStopId(STOP_ID_1)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("from_stop_id", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToStopIdShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(null)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("to_stop_id", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    // Field pathwayMode is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToPathwayModeShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(null)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("pathway_mode", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithInvalidToPathwayModeShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(13)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("pathway_mode", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals("13", notice.getEnumValue());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createPathwayWithInvalidIsBidirectionalShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(3)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("is_bidirectional", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals("3", notice.getEnumValue());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createPathwayWithNullIsBidirectionalShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions to avoid lint
        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(null)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("is_bidirectional", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithNegativeLengthShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(-10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("length", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(-10.0, notice.getActualValue());
        assertEquals(0, notice.getRangeMin());
        assertEquals(Float.MAX_VALUE, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithNegativeTraversalTimeShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(-2)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("traversal_time", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(-2, notice.getActualValue());
        assertEquals(0, notice.getRangeMin());
        assertEquals(Integer.MAX_VALUE, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithNegativeStairCountShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(-3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("stair_count", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(-3, notice.getActualValue());
        assertEquals(0, notice.getRangeMin());
        assertEquals(Integer.MAX_VALUE, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithNegativeMinWidthShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(-30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);
        assertEquals(FILENAME, notice.getFilename());
        assertEquals("min_width", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(-30.0, notice.getActualValue());
        assertEquals(0, notice.getRangeMin());
        assertEquals(Float.MAX_VALUE, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithSuspiciousLengthValueShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(500f)
                .traversalTime(5)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<SuspiciousFloatValueNotice> noticeCollection =
                (List<SuspiciousFloatValueNotice>) entityBuildResult.getData();
        final SuspiciousFloatValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("length", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(500, notice.getActualValue());
        assertEquals(0, notice.getRangeMin());
        assertEquals(400, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithSuspiciousTraversalTimeValueShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(30f)
                .traversalTime(60)
                .stairCount(3)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<SuspiciousIntegerValueNotice> noticeCollection =
                (List<SuspiciousIntegerValueNotice>) entityBuildResult.getData();
        final SuspiciousIntegerValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("traversal_time", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(60, notice.getActualValue());
        assertEquals(3, notice.getRangeMin());
        assertEquals(30, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithSuspiciousStairCountValueShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(30f)
                .traversalTime(20)
                .stairCount(30)
                .maxSlope(0.20f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<SuspiciousIntegerValueNotice> noticeCollection =
                (List<SuspiciousIntegerValueNotice>) entityBuildResult.getData();
        final SuspiciousIntegerValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("stair_count", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(30, notice.getActualValue());
        assertEquals(1, notice.getRangeMin());
        assertEquals(20, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithSuspiciousMaxSlopeValueShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(30f)
                .traversalTime(20)
                .stairCount(15)
                .maxSlope(0.80f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<SuspiciousFloatValueNotice> noticeCollection =
                (List<SuspiciousFloatValueNotice>) entityBuildResult.getData();
        final SuspiciousFloatValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("max_slope", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(0.8f, notice.getActualValue());
        assertEquals(-0.3f, notice.getRangeMin());
        assertEquals(0.3f, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createPathwayWithSuspiciousMinWidthValueShouldGenerateNotice() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.pathwayId(PATHWAY_ID)
                .fromStopId(STOP_ID_0)
                .toStopId(STOP_ID_1)
                .pathwayMode(1)
                .isBidirectional(1)
                .length(30f)
                .traversalTime(20)
                .stairCount(15)
                .maxSlope(0.2f)
                .minWidth(60f)
                .signpostedAs("test")
                .reversedSignpostedAs("test")
                .build(PATHWAY_MIN_LENGTH_KEY,
                        PATHWAY_MAX_LENGTH_KEY,
                        PATHWAY_MIN_TRAVERSAL_TIME_KEY,
                        PATHWAY_MAX_TRAVERSAL_TIME_KEY,
                        PATHWAY_MIN_STAIR_COUNT_KEY,
                        PATHWAY_MAX_STAIR_COUNT_KEY,
                        PATHWAY_MAX_SLOPE_KEY,
                        PATHWAY_MIN_WIDTH_LOWER_BOUND_KEY,
                        PATHWAY_MIN_WIDTH_UPPER_BOUND_KEY);

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<SuspiciousFloatValueNotice> noticeCollection =
                (List<SuspiciousFloatValueNotice>) entityBuildResult.getData();
        final SuspiciousFloatValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("min_width", notice.getFieldName());
        assertEquals(PATHWAY_ID, notice.getEntityId());
        assertEquals(60f, notice.getActualValue());
        assertEquals(0.3f, notice.getRangeMin());
        assertEquals(50f, notice.getRangeMax());

        assertEquals(1, noticeCollection.size());
    }
}