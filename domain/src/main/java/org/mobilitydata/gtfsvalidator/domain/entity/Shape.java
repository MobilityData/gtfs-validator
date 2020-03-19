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

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Shape {

    @NotNull
    private final String shapeId;

    @NotNull
    private final Float shapePtLat;

    @NotNull
    private final Float shapePtLon;

    @NotNull
    private final Integer shapePtSequence;

    private final int shapeDistTraveled;


    public Shape(@NotNull String shapeId,
                 @NotNull Float shapePtLat,
                 @NotNull Float shapePtLon,
                 @NotNull Integer shapePtSequence,
                 int shapeDistTraveled) {
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

    @NotNull
    public Float getShapePtLat() {
        return shapePtLat;
    }

    @NotNull
    public Float getShapePtLon() {
        return shapePtLon;
    }

    @NotNull
    public Integer getShapePtSequence() {
        return shapePtSequence;
    }

    public int getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    public static class ShapeBuilder {

        private String shapeId;
        private Float shapePtLat;
        private Float shapePtLon;
        private Integer shapePtSequence;
        private Integer shapeDistTraveled;

        public ShapeBuilder(@NotNull String shapeId,
                            @NotNull Float shapePtLat,
                            @NotNull Float shapePtLon,
                            @NotNull Integer shapePtSequence) {
            this.shapeId = shapeId;
            this.shapePtLat = shapePtLat;
            this.shapePtLon = shapePtLon;
            this.shapePtSequence = shapePtSequence;
        }

        public ShapeBuilder shapeDistTraveled(@Nullable Integer shapeDistTraveled) {
            this.shapeDistTraveled = shapeDistTraveled;
            return this;
        }

        public Shape build() {
            return new Shape(shapeId, shapePtLat, shapePtLon, shapePtSequence, shapeDistTraveled);
        }
    }
}
