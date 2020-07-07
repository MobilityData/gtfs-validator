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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TableNameTest {

    @Test
    public void createTableNameWithNullValueShouldReturnNull() {
        assertNull(TableName.fromString(null));
    }

    @Test
    public void createTableNameWithUnexpectedValueShouldReturnNullValue() {
        final String unexpectedEnumValue = "test";
        assertNull(TableName.fromString(unexpectedEnumValue));
    }

    @Test
    public void createTableNameWithExpectedValueAgencyShouldNotThrowExceptionAndReturnCorrectRouteType() {
        assertEquals(TableName.AGENCY, TableName.fromString("agency"));
    }

    @Test
    public void createTableNameWithExpectedValueStopsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        assertEquals(TableName.STOPS, TableName.fromString("stops"));
    }

    @Test
    public void createTableNameWithExpectedValueRoutesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        assertEquals(TableName.ROUTES, TableName.fromString("routes"));
    }

    @Test
    public void createTableNameWithExpectedValueTripsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        assertEquals(TableName.TRIPS, TableName.fromString("trips"));
    }

    @Test
    public void createTableNameWithExpectedValueStopTimesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        assertEquals(TableName.STOP_TIMES, TableName.fromString("stop_times"));
    }
}
