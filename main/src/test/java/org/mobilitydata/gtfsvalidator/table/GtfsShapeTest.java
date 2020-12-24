/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.DEFAULT_SHAPE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.DEFAULT_SHAPE_PT_LAT;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.DEFAULT_SHAPE_PT_LON;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.DEFAULT_SHAPE_PT_SEQUENCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsShape.DEFAULT_SHAPE_DIST_TRAVELED;

@RunWith(JUnit4.class)
public class GtfsShapeTest {
    @Test
    public void shouldReturnFieldValues() {
        Builder builder = new Builder();
        GtfsShape underTest = builder
                .setShapeId("shape id")
                .setShapePtLat(30d)
                .setShapePtLon(90d)
                .setShapePtSequence(2)
                .setShapeDistTraveled(20d)
                .build();

        assertThat(underTest.shapeId()).isEqualTo("shape id");
        assertThat(underTest.shapePtLat()).isEqualTo(30d);
        assertThat(underTest.shapePtLon()).isEqualTo(90d);
        assertThat(underTest.shapePtSequence()).isEqualTo(2);
        assertThat(underTest.shapeDistTraveled()).isEqualTo(20d);

        assertThat(underTest.hasShapeId()).isTrue();
        assertThat(underTest.hasShapePtLat()).isTrue();
        assertThat(underTest.hasShapePtLon()).isTrue();
        assertThat(underTest.hasShapePtSequence()).isTrue();
        assertThat(underTest.hasShapeDistTraveled()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        Builder builder = new Builder();
        GtfsShape underTest = builder
                .setShapeId(null)
                .setShapePtLat(null)
                .setShapePtLon(null)
                .setShapePtSequence(null)
                .setShapeDistTraveled(null)
                .build();

        assertThat(underTest.shapeId()).isEqualTo(DEFAULT_SHAPE_ID);
        assertThat(underTest.shapePtLat()).isEqualTo(DEFAULT_SHAPE_PT_LAT);
        assertThat(underTest.shapePtLon()).isEqualTo(DEFAULT_SHAPE_PT_LON);
        assertThat(underTest.shapePtSequence()).isEqualTo(DEFAULT_SHAPE_PT_SEQUENCE);
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);

        assertThat(underTest.hasShapeId()).isFalse();
        assertThat(underTest.hasShapePtLat()).isFalse();
        assertThat(underTest.hasShapePtLon()).isFalse();
        assertThat(underTest.hasShapePtSequence()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        Builder builder = new Builder();
        builder.setShapeId(null)
                .setShapePtLat(null)
                .setShapePtLon(null)
                .setShapePtSequence(null)
                .setShapeDistTraveled(null);
        builder.clear();
        GtfsShape underTest = builder.build();

        assertThat(underTest.shapeId()).isEqualTo(DEFAULT_SHAPE_ID);
        assertThat(underTest.shapePtLat()).isEqualTo(DEFAULT_SHAPE_PT_LAT);
        assertThat(underTest.shapePtLon()).isEqualTo(DEFAULT_SHAPE_PT_LON);
        assertThat(underTest.shapePtSequence()).isEqualTo(DEFAULT_SHAPE_PT_SEQUENCE);
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);

        assertThat(underTest.hasShapeId()).isFalse();
        assertThat(underTest.hasShapePtLat()).isFalse();
        assertThat(underTest.hasShapePtLon()).isFalse();
        assertThat(underTest.hasShapePtSequence()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        Builder builder = new Builder();
        GtfsShape underTest = builder.build();

        assertThat(underTest.shapeId()).isNull();
        assertThat(underTest.shapePtLat()).isEqualTo(DEFAULT_SHAPE_PT_LAT);
        assertThat(underTest.shapePtLon()).isEqualTo(DEFAULT_SHAPE_PT_LON);
        assertThat(underTest.shapePtSequence()).isEqualTo(DEFAULT_SHAPE_PT_SEQUENCE);
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);

        assertThat(underTest.hasShapeId()).isFalse();
        assertThat(underTest.hasShapePtLat()).isFalse();
        assertThat(underTest.hasShapePtLon()).isFalse();
        assertThat(underTest.hasShapePtSequence()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
    }
}
