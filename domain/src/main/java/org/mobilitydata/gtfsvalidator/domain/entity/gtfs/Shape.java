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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Shape implements Comparable<Shape> {

    @NotNull
    private final String shapeId;
    private final float shapePtLat;
    private final float shapePtLon;
    private final int shapePtSequence;

    @Nullable
    private final Float shapeDistTraveled;

    private Shape(@NotNull final String shapeId,
                  float shapePtLat,
                  float shapePtLon,
                  final int shapePtSequence,
                  @Nullable final Float shapeDistTraveled) {
        this.shapeId = shapeId;
        this.shapePtLat = shapePtLat;
        this.shapePtLon = shapePtLon;
        this.shapePtSequence = shapePtSequence;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    @NotNull
    public String getShapeId() {
        return shapeId;
    }

    public float getShapePtLat() {
        return shapePtLat;
    }

    public float getShapePtLon() {
        return shapePtLon;
    }

    @NotNull
    public Integer getShapePtSequence() {
        return shapePtSequence;
    }

    @Nullable
    public Float getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    @Override
    public int compareTo(@NotNull final Shape shape) {
        return getShapePtSequence().compareTo(shape.getShapePtSequence());
    }

    public boolean isGreaterThan(final Shape otherShape) {
        return compareTo(otherShape) > 0;
    }

    public static class ShapeBuilder {
        private String shapeId;
        private float shapePtLat;
        private float shapePtLon;
        private int shapePtSequence;

        @Nullable
        private Float shapeDistTraveled;

        public ShapeBuilder shapeId(@NotNull final String shapeId) {
            this.shapeId = shapeId;
            return this;
        }

        public ShapeBuilder shapePtLat(final float shapePtLat) {
            this.shapePtLat = shapePtLat;
            return this;
        }

        public ShapeBuilder shapePtLon(final float shapePtLon) {
            this.shapePtLon = shapePtLon;
            return this;
        }

        public ShapeBuilder shapePtSequence(final int shapePtSequence) {
            this.shapePtSequence = shapePtSequence;
            return this;
        }

        public ShapeBuilder shapeDistTraveled(@Nullable final Float shapeDistTraveled) {
            this.shapeDistTraveled = shapeDistTraveled;
            return this;
        }

        public Shape build() {
            if (shapeId == null) {
                throw new IllegalArgumentException("field shape_id can not be null in file shapes.txt");
            }

            if (shapePtLat < -90.0f || shapePtLat > 90.0f) {
                throw new IllegalArgumentException("invalid value for field shape_latitude");
            }

            if (shapePtLon < -180.0f || shapePtLon > 180.0f) {
                throw new IllegalArgumentException("invalid value for field shape_longitude");
            }

            if (shapePtSequence < 0) {
                throw new IllegalArgumentException("invalid value for field shape_pt_sequence");
            }

            if (!(shapeDistTraveled == null) && shapeDistTraveled < 0) {
                throw new IllegalArgumentException("invalid value for field shape_dist_traveled");
            }
            return new Shape(shapeId, shapePtLat, shapePtLon, shapePtSequence, shapeDistTraveled);
        }
    }
}
