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
package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapePointTest {

    // Field shapeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createShapePointWithNullIdShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.shapeId(null)
                .shapePtLat(112f)
                .shapePtLon(110f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_id", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
    }

    @Test
    void createShapePointWithNullLatitudeShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(null)
                .shapePtLon(110f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_lat", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    void createShapePointWithNullLongitudeShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(null)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_lon", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    void createShapePointWithNullShapePtSequenceShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(0f)
                .shapePtSequence(null)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked  to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_sequence", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    void createShapePointWithInvalidShapePtSequenceShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(0f)
                .shapePtSequence(-3)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_sequence", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", notice.getEntityId());
        assertEquals(0, notice.getNoticeSpecific(Notice.KEY_RANGE_MIN));
        assertEquals(-3, notice.getNoticeSpecific(Notice.KEY_ACTUAL_VALUE));
        assertEquals(Integer.MAX_VALUE, notice.getNoticeSpecific(Notice.KEY_RANGE_MAX));
    }

    @Test
    void createShapePointWithInvalidShapeDistTraveledShouldGenerateNotice() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(0f)
                .shapePtSequence(0)
                .shapeDistTraveled(-2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_dist_traveled", notice.getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("shape id", notice.getEntityId());
        assertEquals(0.0f, notice.getNoticeSpecific(Notice.KEY_RANGE_MIN));
        assertEquals(-2.0f, notice.getNoticeSpecific(Notice.KEY_ACTUAL_VALUE));
        assertEquals(Float.MAX_VALUE, notice.getNoticeSpecific(Notice.KEY_RANGE_MAX));
    }

    @Test
    void shapePointShouldBeComparableByShapePtSequenceAscending() {
        final ShapePoint.ShapeBuilder underTest = new ShapePoint.ShapeBuilder();

        underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(0f)
                .shapePtSequence(0)
                .shapeDistTraveled(0f);

        final ShapePoint firstShapePointInSequence = (ShapePoint) underTest.build().getData();

        underTest.shapePtSequence(2);

        final ShapePoint secondShapePointInSequence = (ShapePoint) underTest.build().getData();

        assertTrue(secondShapePointInSequence.isGreaterThan(firstShapePointInSequence));
    }
}
