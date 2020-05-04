package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WheelchairAccessibleStatusTest {

    @Test
    void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        final Integer nullValue = null;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(nullValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        final Integer validValue = 0;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.UNKNOWN_WHEELCHAIR_ACCESSIBILITY);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        final Integer validValue = 1;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.WHEELCHAIR_ACCESSIBLE);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        final Integer validValue = 2;
        final WheelchairAccessibleStatus underTest = WheelchairAccessibleStatus.fromInt(validValue);
        assertEquals(underTest, WheelchairAccessibleStatus.NOT_WHEELCHAIR_ACCESSIBLE);
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(WheelchairAccessibleStatus.isEnumValueValid(0));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(WheelchairAccessibleStatus.isEnumValueValid(1));
    }

    @Test
    void validValue2ShouldReturnTrue() {
        assertTrue(WheelchairAccessibleStatus.isEnumValueValid(2));
    }

    @Test
    void validValueNullShouldReturnTrue() {
        assertTrue(WheelchairAccessibleStatus.isEnumValueValid(null));
    }
}