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
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_PATHWAY_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_FROM_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_TO_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_IS_BIDIRECTIONAL;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_LENGTH;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_TRAVERSAL_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_STAIR_COUNT;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_MAX_SLOPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_MIN_WIDTH;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_SIGNPOSTED_AS;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathway.DEFAULT_REVERSED_SIGNPOSTED_AS;

@RunWith(JUnit4.class)
public class GtfsPathwayTest {
    @Test
    public void shouldReturnFieldValues() {
        Builder builder = new Builder();
        GtfsPathway underTest = builder
                .setPathwayId("pathway id")
                .setFromStopId("from stop id")
                .setToStopId("to stop id")
                .setPathwayMode(2)
                .setIsBidirectional(1)
                .setLength(20d)
                .setTraversalTime(30)
                .setStairCount(40)
                .setMaxSlope(40d)
                .setMinWidth(30d)
                .setSignpostedAs("signpost")
                .setReversedSignpostedAs("reverse signpost")
                .build();

        assertThat(underTest.pathwayId()).isEqualTo("pathway id");
        assertThat(underTest.fromStopId()).isEqualTo("from stop id");
        assertThat(underTest.toStopId()).isEqualTo("to stop id");
        assertThat(underTest.pathwayMode()).isEqualTo(GtfsPathwayMode.STAIRS);
        assertThat(underTest.isBidirectional()).isEqualTo(1);
        assertThat(underTest.length()).isEqualTo(20d);
        assertThat(underTest.traversalTime()).isEqualTo(30);
        assertThat(underTest.stairCount()).isEqualTo(40);
        assertThat(underTest.maxSlope()).isEqualTo(40d);
        assertThat(underTest.minWidth()).isEqualTo(30d);
        assertThat(underTest.signpostedAs()).isEqualTo("signpost");
        assertThat(underTest.reversedSignpostedAs()).isEqualTo("reverse signpost");

        assertThat(underTest.hasPathwayId()).isTrue();
        assertThat(underTest.hasFromStopId()).isTrue();
        assertThat(underTest.hasToStopId()).isTrue();
        assertThat(underTest.hasPathwayMode()).isTrue();
        assertThat(underTest.hasIsBidirectional()).isTrue();
        assertThat(underTest.hasLength()).isTrue();
        assertThat(underTest.hasTraversalTime()).isTrue();
        assertThat(underTest.hasStairCount()).isTrue();
        assertThat(underTest.hasMaxSlope()).isTrue();
        assertThat(underTest.hasMinWidth()).isTrue();
        assertThat(underTest.hasSignpostedAs()).isTrue();
        assertThat(underTest.hasReversedSignpostedAs()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        Builder builder = new Builder();
        GtfsPathway underTest = builder
                .setPathwayId(null)
                .setFromStopId(null)
                .setToStopId(null)
                .setPathwayMode(null)
                .setIsBidirectional(null)
                .setLength(null)
                .setTraversalTime(null)
                .setStairCount(null)
                .setMaxSlope(null)
                .setMinWidth(null)
                .setSignpostedAs(null)
                .setReversedSignpostedAs(null)
                .build();

        assertThat(underTest.pathwayId()).isEqualTo(DEFAULT_PATHWAY_ID);
        assertThat(underTest.fromStopId()).isEqualTo(DEFAULT_FROM_STOP_ID);
        assertThat(underTest.toStopId()).isEqualTo(DEFAULT_TO_STOP_ID);
        assertThat(underTest.pathwayMode()).isEqualTo(GtfsPathwayMode.UNRECOGNIZED);
        assertThat(underTest.isBidirectional()).isEqualTo(DEFAULT_IS_BIDIRECTIONAL);
        assertThat(underTest.length()).isEqualTo(DEFAULT_LENGTH);
        assertThat(underTest.traversalTime()).isEqualTo(DEFAULT_TRAVERSAL_TIME);
        assertThat(underTest.stairCount()).isEqualTo(DEFAULT_STAIR_COUNT);
        assertThat(underTest.maxSlope()).isEqualTo(DEFAULT_MAX_SLOPE);
        assertThat(underTest.minWidth()).isEqualTo(DEFAULT_MIN_WIDTH);
        assertThat(underTest.signpostedAs()).isEqualTo(DEFAULT_SIGNPOSTED_AS);
        assertThat(underTest.reversedSignpostedAs()).isEqualTo(DEFAULT_REVERSED_SIGNPOSTED_AS);

        assertThat(underTest.hasPathwayId()).isFalse();
        assertThat(underTest.hasFromStopId()).isFalse();
        assertThat(underTest.hasToStopId()).isFalse();
        assertThat(underTest.hasPathwayMode()).isFalse();
        assertThat(underTest.hasIsBidirectional()).isFalse();
        assertThat(underTest.hasLength()).isFalse();
        assertThat(underTest.hasTraversalTime()).isFalse();
        assertThat(underTest.hasStairCount()).isFalse();
        assertThat(underTest.hasMaxSlope()).isFalse();
        assertThat(underTest.hasMinWidth()).isFalse();
        assertThat(underTest.hasSignpostedAs()).isFalse();
        assertThat(underTest.hasReversedSignpostedAs()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        Builder builder = new Builder();
        builder.setPathwayId("pathway id")
                .setFromStopId("from stop id")
                .setToStopId("to stop id")
                .setPathwayMode(2)
                .setIsBidirectional(1)
                .setLength(20d)
                .setTraversalTime(30)
                .setStairCount(40)
                .setMaxSlope(40d)
                .setMinWidth(30d)
                .setSignpostedAs("signpost")
                .setReversedSignpostedAs("reverse signpost");
        builder.clear();
        GtfsPathway underTest = builder.build();

        assertThat(underTest.pathwayId()).isEqualTo(DEFAULT_PATHWAY_ID);
        assertThat(underTest.fromStopId()).isEqualTo(DEFAULT_FROM_STOP_ID);
        assertThat(underTest.toStopId()).isEqualTo(DEFAULT_TO_STOP_ID);
        assertThat(underTest.pathwayMode()).isEqualTo(GtfsPathwayMode.UNRECOGNIZED);
        assertThat(underTest.isBidirectional()).isEqualTo(DEFAULT_IS_BIDIRECTIONAL);
        assertThat(underTest.length()).isEqualTo(DEFAULT_LENGTH);
        assertThat(underTest.traversalTime()).isEqualTo(DEFAULT_TRAVERSAL_TIME);
        assertThat(underTest.stairCount()).isEqualTo(DEFAULT_STAIR_COUNT);
        assertThat(underTest.maxSlope()).isEqualTo(DEFAULT_MAX_SLOPE);
        assertThat(underTest.minWidth()).isEqualTo(DEFAULT_MIN_WIDTH);
        assertThat(underTest.signpostedAs()).isEqualTo(DEFAULT_SIGNPOSTED_AS);
        assertThat(underTest.reversedSignpostedAs()).isEqualTo(DEFAULT_REVERSED_SIGNPOSTED_AS);

        assertThat(underTest.hasPathwayId()).isFalse();
        assertThat(underTest.hasFromStopId()).isFalse();
        assertThat(underTest.hasToStopId()).isFalse();
        assertThat(underTest.hasPathwayMode()).isFalse();
        assertThat(underTest.hasIsBidirectional()).isFalse();
        assertThat(underTest.hasLength()).isFalse();
        assertThat(underTest.hasTraversalTime()).isFalse();
        assertThat(underTest.hasStairCount()).isFalse();
        assertThat(underTest.hasMaxSlope()).isFalse();
        assertThat(underTest.hasMinWidth()).isFalse();
        assertThat(underTest.hasSignpostedAs()).isFalse();
        assertThat(underTest.hasReversedSignpostedAs()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        Builder builder = new Builder();
        GtfsPathway underTest = builder.build();

        assertThat(underTest.pathwayId()).isNull();
        assertThat(underTest.fromStopId()).isNull();
        assertThat(underTest.toStopId()).isNull();
        assertThat(underTest.pathwayMode()).isEqualTo(GtfsPathwayMode.UNRECOGNIZED);
        assertThat(underTest.isBidirectional()).isEqualTo(DEFAULT_IS_BIDIRECTIONAL);
        assertThat(underTest.length()).isEqualTo(DEFAULT_LENGTH);
        assertThat(underTest.traversalTime()).isEqualTo(DEFAULT_TRAVERSAL_TIME);
        assertThat(underTest.stairCount()).isEqualTo(DEFAULT_STAIR_COUNT);
        assertThat(underTest.maxSlope()).isEqualTo(DEFAULT_MAX_SLOPE);
        assertThat(underTest.minWidth()).isEqualTo(DEFAULT_MIN_WIDTH);
        assertThat(underTest.signpostedAs()).isNull();
        assertThat(underTest.reversedSignpostedAs()).isNull();

        assertThat(underTest.hasPathwayId()).isFalse();
        assertThat(underTest.hasFromStopId()).isFalse();
        assertThat(underTest.hasToStopId()).isFalse();
        assertThat(underTest.hasPathwayMode()).isFalse();
        assertThat(underTest.hasIsBidirectional()).isFalse();
        assertThat(underTest.hasLength()).isFalse();
        assertThat(underTest.hasTraversalTime()).isFalse();
        assertThat(underTest.hasStairCount()).isFalse();
        assertThat(underTest.hasMaxSlope()).isFalse();
        assertThat(underTest.hasMinWidth()).isFalse();
        assertThat(underTest.hasSignpostedAs()).isFalse();
        assertThat(underTest.hasReversedSignpostedAs()).isFalse();
    }
}
