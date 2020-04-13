package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BikesAllowedStatusTest {

    @Test
    public void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        int invalidValue = 3;
        BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        Integer nullValue = null;
        BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(nullValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        Integer validValue = 0;
        BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        Integer validValue = 1;
        BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.BIKES_ALLOWED);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        Integer validValue = 2;
        BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.NO_BIKES_ALLOWED);
    }
}