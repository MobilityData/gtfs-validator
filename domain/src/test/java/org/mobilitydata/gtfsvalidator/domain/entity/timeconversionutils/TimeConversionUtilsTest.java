/*
* Copyright (c) 2019. MobilityData IO.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.mobilitydata.gtfsvalidator.domain.entity.timeconversionutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimeConversionUtilsTest {
    private static final int HOUR_TO_SEC_CONVERSION_FACTOR = 3600;
    private static final int NOON = (12 * HOUR_TO_SEC_CONVERSION_FACTOR);
    private static final int MIN_TO_SEC_CONVERSION_FACTOR = 60;
    private static final int SEC_TO_SEC_CONVERSION_FACTOR = 1;

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void hourInHMMSSShouldConvertToIntegerFromNoon() {
        final String threePm = "15:00:00";
        int toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(threePm);
        assertEquals((15*HOUR_TO_SEC_CONVERSION_FACTOR +
                0*MIN_TO_SEC_CONVERSION_FACTOR + 0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON, toCheck);

        final String noon = "12:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(noon);
        assertEquals(((NOON + 0*MIN_TO_SEC_CONVERSION_FACTOR + 0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String midnight = "24:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(midnight);
        assertEquals(((24*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String sixFortyPm = "18:40:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(sixFortyPm);
        assertEquals(((18*3600 + 40*60 + 0) - (12*3600)), toCheck);

        final String tenAm = "10:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(tenAm);
        assertEquals(((10*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String sixThirtyAm20sec = "06:30:20";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(sixThirtyAm20sec);
        assertEquals(((6*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                20*SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String oneThirtyAm40sec = "25:30:40";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(oneThirtyAm40sec);
        assertEquals(((25*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                40*SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

         // check that null value returns null when calling method
        // TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(null));

        // check that malformed time return null when calling method
        // TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService
        String malformedTimeAsString = "1344";
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        malformedTimeAsString = "13:0:22";
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        malformedTimeAsString = "xxee";
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        malformedTimeAsString = "";
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        malformedTimeAsString = "    ";
        assertNull(TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));
    }

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void hourAsIntShouldConvertToHHMMSS() {
        final int threePm = (15*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        String toCheck = TimeConversionUtils.convertIntegerToHMMSS(threePm);
        assertEquals("15:00:00", toCheck);

        final int noon = (12*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(noon);
        assertEquals("12:00:00", toCheck);

        final int midnight = (24*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(midnight);
        assertEquals("24:00:00", toCheck);

        final int sixFortyPm = (18*HOUR_TO_SEC_CONVERSION_FACTOR + 40*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixFortyPm);
        assertEquals("18:40:00", toCheck);

        final int tenAm = (10*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(tenAm);
        assertEquals("10:00:00", toCheck);

        final int sixThirtyAm20sec = (6*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                20*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixThirtyAm20sec);
        assertEquals("06:30:20", toCheck);

        final int twentyFiveThirtyPm40sec = (25*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                40*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(twentyFiveThirtyPm40sec);
        assertEquals("25:30:40", toCheck);

        final int oneThirtyAm40sec = (1*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                40*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(oneThirtyAm40sec);
        assertEquals("01:30:40", toCheck);

        assertNull(TimeConversionUtils.convertIntegerToHMMSS(null));
    }

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void convertHHMMSStoIntegerAndIntegerToHHMMSSShouldBeConsistent() {
        final int oneThirtyAm40sec = (13*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                20*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        String toCheck = TimeConversionUtils.convertIntegerToHMMSS(oneThirtyAm40sec);
        assertEquals(toCheck, TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixThirtyAm20sec = (6*HOUR_TO_SEC_CONVERSION_FACTOR + 30*MIN_TO_SEC_CONVERSION_FACTOR +
                20*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixThirtyAm20sec);
        assertEquals(toCheck, TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int tenAm = (10*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(tenAm);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixFortyPm = (18*HOUR_TO_SEC_CONVERSION_FACTOR + 40*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixFortyPm);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int midnight = (24*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(midnight);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int noon = (12*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(noon);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int threePm = (15*HOUR_TO_SEC_CONVERSION_FACTOR + 0*MIN_TO_SEC_CONVERSION_FACTOR +
                0*SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(threePm);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));
    }
}
