package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BikesAllowedStatusTest {

    @Test
    public void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        final Integer nullValue = null;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(nullValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        final Integer validValue = 0;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        final Integer validValue = 1;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.BIKES_ALLOWED);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        final Integer validValue = 2;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.NO_BIKES_ALLOWED);
    }
}