package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.*;

class DirectionIdTest {

    @Test
    public void createEnumDirectionIdWithInvalidValueShouldReturnError() {
        final int invalidValue = 3;
        final DirectionId underTest = DirectionId.fromInt(invalidValue);
        assertEquals(ERROR, underTest);
    }

    @Test
    public void createEnumDirectionIdWithNullValueShouldReturnNull() {
        final Integer nullValue = null;
        @SuppressWarnings("ConstantConditions") final DirectionId underTest = DirectionId.fromInt(nullValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumDirectionIdWithValidValue0ShouldReturnOutbound() {
        final int validValue = 0;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(OUTBOUND, underTest);
    }

    @Test
    public void createEnumDirectionIdWithValidValue0ShouldReturnInbound() {
        final int validValue = 1;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(INBOUND, underTest);
    }
}