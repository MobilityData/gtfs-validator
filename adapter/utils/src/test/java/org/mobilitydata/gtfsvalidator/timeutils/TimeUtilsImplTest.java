/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.timeutils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeUtilsImplTest {
    private static final int HOUR_TO_SEC_CONVERSION_FACTOR = 3600;
    private static final int NOON = (12 * HOUR_TO_SEC_CONVERSION_FACTOR);
    private static final int MIN_TO_SEC_CONVERSION_FACTOR = 60;
    private static final int SEC_TO_SEC_CONVERSION_FACTOR = 1;
    private static final TimeUtilsImpl TIME_CONVERSION_UTILS = TimeUtilsImpl.getInstance();

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void hourInHHMMSSShouldConvertToIntegerFromNoon() {
        final String threePm = "15:00:00";
        int toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(threePm);
        assertEquals((15 * HOUR_TO_SEC_CONVERSION_FACTOR +
                0 * MIN_TO_SEC_CONVERSION_FACTOR + 0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON, toCheck);

        final String noon = "12:00:00";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(noon);
        assertEquals(((NOON + 0 * MIN_TO_SEC_CONVERSION_FACTOR + 0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String midnight = "24:00:00";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(midnight);
        assertEquals(((24 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String sixFortyPm = "18:40:00";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(sixFortyPm);
        assertEquals(((18 * HOUR_TO_SEC_CONVERSION_FACTOR + 40 * MIN_TO_SEC_CONVERSION_FACTOR + 0) - NOON), toCheck);

        final String tenAm = "10:00:00";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(tenAm);
        assertEquals(((10 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String fiveTenAm34sec = "05:10:34";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(fiveTenAm34sec);
        assertEquals(((5 * HOUR_TO_SEC_CONVERSION_FACTOR + 10 * MIN_TO_SEC_CONVERSION_FACTOR +
                34 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String sixThirtyAm20sec = "06:30:20";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(sixThirtyAm20sec);
        assertEquals(((6 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                20 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String sixOneAm = "06:01:00";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(sixOneAm);
        assertEquals(((6 * HOUR_TO_SEC_CONVERSION_FACTOR + 1 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);

        final String oneThirtyAm40sec = "25:30:40";
        toCheck = TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(oneThirtyAm40sec);
        assertEquals(((25 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                40 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON), toCheck);
    }

    @Test
    void convertHHMMSSToIntFromNoonOfDayOfServiceOnNullValueShouldReturnNull() {
        // check that null value returns null when calling method convertHHMMSSToIntFromNoonOfDayOfService
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(null));
    }

    @Test
    void convertHHMMSSToIntFromNoonOfDayOfServiceOnMalformedValueShouldReturnNull() {
        // check that malformed time return null when calling method
        // TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService
        String malformedTimeAsString = "1344";
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        malformedTimeAsString = "13:0:22";
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));

        //noinspection SpellCheckingInspection
        malformedTimeAsString = "xxee";
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(malformedTimeAsString));
    }

    @Test
    void convertHHMMSSToIntFromNoonOfDayOfServiceOnEmptyValueShouldReturnNull() {
        final String emptyString = "";
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(emptyString));
    }

    @Test
    void convertHHMMSSToIntFromNoonOfDayOfServiceOnBlankValueShouldReturnNull() {
        final String blankString = "    ";
        assertNull(TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(blankString));
    }

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void hourAsIntShouldConvertToHHMMSS() {
        final int threePm = (15 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        String toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(threePm);
        assertEquals("15:00:00", toCheck);

        final int noon = (12 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(noon);
        assertEquals("12:00:00", toCheck);

        final int midnight = (24 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(midnight);
        assertEquals("24:00:00", toCheck);

        final int sixFortyPm = (18 * HOUR_TO_SEC_CONVERSION_FACTOR + 40 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixFortyPm);
        assertEquals("18:40:00", toCheck);

        final int sixAmOne = (6 * HOUR_TO_SEC_CONVERSION_FACTOR + 1 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixAmOne);
        assertEquals("06:01:00", toCheck);

        final int fiveTenAm34sec = (5 * HOUR_TO_SEC_CONVERSION_FACTOR + 10 * MIN_TO_SEC_CONVERSION_FACTOR +
                34 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(fiveTenAm34sec);
        assertEquals("05:10:34", toCheck);

        final int tenAm = (10 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(tenAm);
        assertEquals("10:00:00", toCheck);

        final int sixThirtyAm20sec = (6 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                20 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixThirtyAm20sec);
        assertEquals("06:30:20", toCheck);

        final int twentyFiveThirtyPm40sec = (25 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                40 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(twentyFiveThirtyPm40sec);
        assertEquals("25:30:40", toCheck);

        final int oneThirtyAm40sec = (1 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                40 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(oneThirtyAm40sec);
        assertEquals("01:30:40", toCheck);
    }

    @Test
    void convertIntegerToHHMMSSOnNullValueShouldThrowException() {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(null));
        Assertions.assertEquals("elapsedDurationSinceNoonInSeconds cannot be null", exception.getMessage());
    }

    // Remove warning "PointlessArithmeticExpression" since they these expressions are written to ease code
    // comprehension
    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void convertHHMMSStoIntegerAndIntegerToHHMMSSShouldBeConsistent() {
        final int oneThirtyAm40sec = (1 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                40 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        String toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(oneThirtyAm40sec);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixThirtyAm20sec = (6 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                20 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixThirtyAm20sec);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixAmOne = (6 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixAmOne);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int tenAm = (10 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(tenAm);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixFortyPm = (18 * HOUR_TO_SEC_CONVERSION_FACTOR + 40 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(sixFortyPm);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int midnight = (24 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(midnight);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int noon = (12 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(noon);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int threePm = (15 * HOUR_TO_SEC_CONVERSION_FACTOR + 0 * MIN_TO_SEC_CONVERSION_FACTOR +
                0 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(threePm);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int twentyFiveThirtyPm40sec = (25 * HOUR_TO_SEC_CONVERSION_FACTOR + 30 * MIN_TO_SEC_CONVERSION_FACTOR +
                40 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(twentyFiveThirtyPm40sec);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int twentyEightFortySevenPm20sec = (28 * HOUR_TO_SEC_CONVERSION_FACTOR + 47 * MIN_TO_SEC_CONVERSION_FACTOR +
                20 * SEC_TO_SEC_CONVERSION_FACTOR) - NOON;
        toCheck = TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(twentyEightFortySevenPm20sec);
        assertEquals(toCheck, TIME_CONVERSION_UTILS.convertIntegerToHHMMSS(
                TIME_CONVERSION_UTILS.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));
    }

    @Test
    void commonFirstPeriodLastTimeAndSecondPeriodFirstTimeShouldReturnFalse() {
        //   a |--------|         07:00 am -> 10:00 am
        //   b          |----|    10:00 am -> 11:00 am
        // period `b` partially overlaps with period `a`
        assertFalse(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1000,
                1000, 1100));

        // permute periods `a` and `b` in method call
        //   b          |----|    10:00 am -> 11:00 am
        //   a |--------|         07:00 am -> 10:00 am
        assertFalse(TIME_CONVERSION_UTILS.arePeriodsOverlapping(1000, 1100,
                700, 1000));
    }

    @Test
    void partialOverlapWithOneCommonBoundaryShouldReturnTrue() {
        //   a |--------|         07:00 am -> 10:00 am
        //   b     |----|         09:00 am -> 10:00 am
        // period `b` partially overlaps with period `a`
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1000,
                900, 1000));

        // permute periods `a` and `b` in method call
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(900, 1000,
                700, 1000));

        //   a |--------|         07:00 am -> 10:00 am
        //   b |----|             07:00 am -> 09:00 am
        // period `b` partially overlaps with period `a`
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1000,
                700, 900));

        // permute periods `a` and `b` in method call
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 900,
                700, 1000));
    }

    @Test
    void partialOverlapPeriodsShouldReturnTrue() {
        //   a |--------|       07:00 am -> 10:00 am
        //     b      |----|    08:00 am -> 12:00 am
        // period `b` partially overlaps with period `a`
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1000,
                800, 1200));

        // permute periods `a` and `b` in method call
        //     b      |----|    08:00 am -> 12:00 am
        //   a |--------|       07:00 am -> 10:00 am
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(800, 1200,
                700, 1000));
    }

    @Test
    void firstPeriodContainedWithTheSecondOneShouldReturnTrueWhenArePeriodsOverlappingIsCalled() {
        //   a |--------|       07:00 am -> 12:00 am
        //   b   |----|         08:00 am -> 11:00 am
        // period `b` is contained within period `a`
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1200,
                800, 1100));
    }

    @Test
    void secondPeriodContainedWithinTheFirstOneShouldReturnTrueWhenArePeriodsOverlappingIsCalled() {
        //   a   |----|         08:00 am -> 11:00 am
        //   b |--------|       07:00 am -> 12:00 am
        // period `a` is contained within period `b`
        assertTrue(TIME_CONVERSION_UTILS.arePeriodsOverlapping(800, 1100,
                700, 1200));
    }

    @Test
    void disjointPeriodsShouldReturnFalse() {
        // Disjoint periods of time, with `a` being the first period and `b` the second one
        // a |--------|                 07:00 am -> 10:00 am
        //             b  |----|        11:00 am -> 12:00 am
        assertFalse(TIME_CONVERSION_UTILS.arePeriodsOverlapping(700, 1000,
                1100, 1200));
        // permute periods a and b in method call
        //             b  |----|        11:00 am -> 12:00 am
        // a |--------|                 07:00 am -> 10:00 am
        assertFalse(TIME_CONVERSION_UTILS.arePeriodsOverlapping(1100, 1200,
                700, 1000));
    }
}
