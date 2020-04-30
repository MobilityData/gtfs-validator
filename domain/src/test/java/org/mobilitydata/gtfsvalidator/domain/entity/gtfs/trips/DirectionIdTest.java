package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.INBOUND;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.OUTBOUND;

class DirectionIdTest {

    @Test
    void createEnumDirectionIdWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final DirectionId underTest = DirectionId.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    void createEnumDirectionIdWithNullValueShouldReturnNull() {
        final Integer nullValue = null;
        @SuppressWarnings("ConstantConditions") final DirectionId underTest = DirectionId.fromInt(nullValue);
        assertNull(underTest);
    }

    @Test
    void createEnumDirectionIdWithValidValue0ShouldReturnOutbound() {
        final int validValue = 0;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(OUTBOUND, underTest);
    }

    @Test
    void createEnumDirectionIdWithValidValue0ShouldReturnInbound() {
        final int validValue = 1;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(INBOUND, underTest);
    }

    @Test
    void invalidValue23ShouldReturnFalse() {
        assertFalse(DirectionId.isEnumValueValid(23));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(1));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(0));
    }

    @Test
    void validValueNullShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(null));
    }
}