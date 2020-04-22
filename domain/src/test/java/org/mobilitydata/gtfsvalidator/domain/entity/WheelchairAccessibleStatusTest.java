package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WheelchairAccessibleStatusTest {

    @Test
    public void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    public void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        final Integer nullValue = null;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(nullValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        final Integer validValue = 0;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        final Integer validValue = 1;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.WHEELCHAIR_ACCESSIBLE);
    }

    @Test
    public void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        final Integer validValue = 2;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.NOT_WHEELCHAIR_ACCESSIBLE);
    }
}