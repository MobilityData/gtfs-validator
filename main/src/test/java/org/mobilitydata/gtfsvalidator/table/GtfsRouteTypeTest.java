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

@RunWith(JUnit4.class)
public class GtfsRouteTypeTest {
    @Test
    public void shouldReturnEnumValue() {
        assertThat(GtfsRouteType.forNumber(0)).isEqualTo(GtfsRouteType.LIGHT_RAIL);
        assertThat(GtfsRouteType.forNumber(1)).isEqualTo(GtfsRouteType.SUBWAY);
        assertThat(GtfsRouteType.forNumber(2)).isEqualTo(GtfsRouteType.RAIL);
        assertThat(GtfsRouteType.forNumber(3)).isEqualTo(GtfsRouteType.BUS);
        assertThat(GtfsRouteType.forNumber(4)).isEqualTo(GtfsRouteType.FERRY);
        assertThat(GtfsRouteType.forNumber(5)).isEqualTo(GtfsRouteType.CABLE_TRAM);
        assertThat(GtfsRouteType.forNumber(6)).isEqualTo(GtfsRouteType.AERIAL_LIFT);
        assertThat(GtfsRouteType.forNumber(7)).isEqualTo(GtfsRouteType.FUNICULAR);
        assertThat(GtfsRouteType.forNumber(11)).isEqualTo(GtfsRouteType.TROLLEYBUS);
        assertThat(GtfsRouteType.forNumber(12)).isEqualTo(GtfsRouteType.MONORAIL);
    }

    @Test
    public void shouldReturnNull() {
        assertThat(GtfsRouteType.forNumber(-1)).isNull();
        assertThat(GtfsRouteType.forNumber(14)).isNull();
    }
}
