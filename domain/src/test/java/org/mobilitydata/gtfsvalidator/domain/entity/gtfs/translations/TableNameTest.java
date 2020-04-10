package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableNameTest {

    @Test
    public void createTableNameWithNullValueShouldThrowException() {

        String unexpectedEnumValue = null;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> TableName.fromString(unexpectedEnumValue));

        assertEquals("Field table_name in translations.txt can not be null", exception.getMessage());
    }

    @Test
    public void createTableNameWithUnexpectedValueShouldThrowException() {

        String unexpectedEnumValue = "test";

        Exception exception = assertThrows(NullPointerException.class, () -> TableName.fromString(unexpectedEnumValue));

        assertEquals("Unexpected value for field table_name in translations.txt", exception.getMessage());
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