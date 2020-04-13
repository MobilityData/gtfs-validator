package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WheelchairAccessibleStatusTest {

    @Test
    public void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        int invalidValue = 3;
        WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        Integer nullValue = null;
        WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(nullValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        Integer validValue = 0;
        WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        Integer validValue = 1;
        WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.WHEELCHAIR_ACCESSIBLE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        Integer validValue = 2;
        WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.NOT_WHEELCHAIR_ACCESSIBLE);
    }
}