package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExceptionTypeTest {

    @Test
    void createExceptionTypeWithExpectedValue1ShouldReturnAddedService() {
        final int expectedValue = 1;
        assertEquals(ExceptionType.ADDED_SERVICE, ExceptionType.fromInt(expectedValue));
    }

    @Test
    void createExceptionTypeWithExpectedValue2ShouldReturnRemovedService() {
        final int expectedValue = 2;
        assertEquals(ExceptionType.REMOVED_SERVICE, ExceptionType.fromInt(expectedValue));
    }

    @Test
    void createExceptionTypeWithUnexpectedValue3ShouldReturnNull() {
        final int unexpectedValue = 3;
        assertNull(ExceptionType.fromInt(unexpectedValue));
    }


    @Test
    void createExceptionTypeWithUnexpectedValue4ShouldReturnNull() {
        final int unexpectedValue = 4;
        assertNull(ExceptionType.fromInt(unexpectedValue));
    }

    @Test
    void createExceptionTypeWithNullValueShouldReturnNull() {
        assertNull(ExceptionType.fromInt(null));
    }
}