package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.*;

class DirectionIdTest {

    @Test
    public void createEnumDirectionIdWithInvalidValueShouldReturnError() {
        int invalidValue = 3;
        DirectionId underTest = DirectionId.fromInt(invalidValue);
        assertEquals(ERROR, underTest);
    }

    @Test
    public void createEnumDirectionIdWithNullValueShouldReturnNull() {
        Integer nullValue = null;
        @SuppressWarnings("ConstantConditions") DirectionId underTest = DirectionId.fromInt(nullValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumDirectionIdWithValidValue0ShouldReturnOutbound() {
        int validValue = 0;
        DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(OUTBOUND, underTest);
    }

    @Test
    public void createEnumDirectionIdWithValidValue0ShouldReturnInbound() {
        int validValue = 1;
        DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(INBOUND, underTest);
    }
}