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
    public void createTableNameWithNullValueShouldThrowException() {
        String unexpectedEnumValue = null;

        @SuppressWarnings("ConstantConditions") Object underTest = TableName.fromString(unexpectedEnumValue);

        assertNull(underTest);
    }

    @Test
    public void createTableNameWithUnexpectedValueShouldReturnNullValue() {
        String unexpectedEnumValue = "test";

        Object underTest = TableName.fromString(unexpectedEnumValue);

        assertNull(underTest);
    }

    @Test
    public void createTableNameWithExpectedValueAgencyShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "agency";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.AGENCY);
    }

    @Test
    public void createTableNameWithExpectedValueStopsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "stops";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.STOPS);
    }

    @Test
    public void createTableNameWithExpectedValueRoutesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "routes";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.ROUTES);
    }

    @Test
    public void createTableNameWithExpectedValueTripsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "trips";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.TRIPS);
    }

    @Test
    public void createTableNameWithExpectedValueStopTimesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "stop_times";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.STOP_TIMES);
    }
}