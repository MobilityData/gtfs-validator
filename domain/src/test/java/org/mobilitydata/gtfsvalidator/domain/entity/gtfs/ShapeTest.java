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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapeTest {

    // Field shapeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createShapeWithNullIdShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
    }

    @Test
    void createShapeWithNullLatitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_pt_lat", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    void createShapeWithNullLongitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_pt_lon", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    void createShapeWithNullShapePtSequenceShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_pt_sequence", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
    }

    @Test
    public void createShapeWithTooBigLatitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(120f)
                .shapePtLon(0f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("shape_pt_lat", notice.getFieldName());
        assertEquals(-90f, notice.getRangeMin());
        assertEquals(120f, notice.getActualValue());
        assertEquals(90f, notice.getRangeMax());
    }

    @Test
    public void createShapeWithTooSmallLatitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(-120f)
                .shapePtLon(0f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_lat", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
        assertEquals(-90f, notice.getRangeMin());
        assertEquals(-120f, notice.getActualValue());
        assertEquals(90f, notice.getRangeMax());
    }

    @Test
    public void createShapeWithTooSmallLongitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(-270f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape id", notice.getEntityId());
        assertEquals("shape_pt_lon", notice.getFieldName());
        assertEquals(-180f, notice.getRangeMin());
        assertEquals(-270f, notice.getActualValue());
        assertEquals(180f, notice.getRangeMax());
    }

    @Test
    public void createShapeWithTooBigLongitudeShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(270f)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("shapes.txt", notice.getFilename());
        assertEquals("shape_pt_lon", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
        assertEquals(-180f, notice.getRangeMin());
        assertEquals(270f, notice.getActualValue());
        assertEquals(180f, notice.getRangeMax());
    }

    @Test
    public void createShapeWithInvalidShapePtSequenceShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_pt_sequence", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
        assertEquals(0, notice.getRangeMin());
        assertEquals(-3, notice.getActualValue());
        assertEquals(Integer.MAX_VALUE, notice.getRangeMax());
    }

    @Test
    public void createShapeWithInvalidShapeDistTraveledShouldGenerateNotice() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

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
        assertEquals("shape_dist_traveled", notice.getFieldName());
        assertEquals("shape id", notice.getEntityId());
        assertEquals(0, notice.getRangeMin());
        assertEquals(-2.0f, notice.getActualValue());
        assertEquals(Float.MAX_VALUE, notice.getRangeMax());
    }

    @Test
    public void shapeShouldBeAbleToBeComparedToOtherShape() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("shape id")
                .shapePtLat(0f)
                .shapePtLon(0f)
                .shapePtSequence(0)
                .shapeDistTraveled(0f);

        final Shape firstShapeInSequence = (Shape) underTest.build().getData();

        underTest.shapePtSequence(2);

        final Shape secondShapeInSequence = (Shape) underTest.build().getData();

        assertTrue(secondShapeInSequence.isGreaterThan(firstShapeInSequence));
    }
}