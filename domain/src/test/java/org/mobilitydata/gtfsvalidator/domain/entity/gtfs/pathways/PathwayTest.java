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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathwayTest {
    private static final String PATHWAY_ID = "pathway id";
    private static final String STOP_ID_1 = "stop id 1";
    private static final String STOP_ID_0 = "stop id 0";
    private static final String FILENAME = "pathways.txt";

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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
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
}