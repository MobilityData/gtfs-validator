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

package org.mobilitydata.gtfsvalidator.domain.entity.timeconversionutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimeConversionUtilsTest {

    @Test
    void hourInHMMSSShouldConvertToIntegerFromNoon() {
        final String threePM = "15:00:00";
        int toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(threePM);
        assertEquals(3600*3, toCheck);

        final String noon = "12:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(noon);
        assertEquals(0, toCheck);

        final String midnight = "24:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(midnight);
        assertEquals(3600*12, toCheck);

        final String sixFortyPM = "18:40:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(sixFortyPM);
        assertEquals(3600*6 + 40*60, toCheck);

        final String tenAM = "10:00:00";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(tenAM);
        assertEquals(-3600*2, toCheck);

        final String sixThirtyAM20sec = "06:30:20";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(sixThirtyAM20sec);
        assertEquals((3600*6 + 30*60 + 20) - 12 * 3600, toCheck);

        final String oneThirtyAm40sec = "25:30:40";
        toCheck = TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(oneThirtyAm40sec);
        assertEquals(3600*13 + 30*60 + 40, toCheck);

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

    @Test
    void hourAsIntShouldConvertToHHMMSS() {
        final int threePM = 3600 * 3;
        String toCheck = TimeConversionUtils.convertIntegerToHMMSS(threePM);
        assertEquals("15:00:00", toCheck);

        final int noon = 0;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(noon);
        assertEquals("12:00:00", toCheck);

        final int midnight = 3600 * 12;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(midnight);
        assertEquals("24:00:00", toCheck);

        final int sixFortyPM = 3600 * 6 + 40 * 60;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixFortyPM);
        assertEquals("18:40:00", toCheck);

        final int tenAM = - 3600 * 2;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(tenAM);
        assertEquals("10:00:00", toCheck);

        final int sixThirtyAM20sec = (3600*6+30*60+20)-(3600*12);
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixThirtyAM20sec);
        assertEquals("06:30:20", toCheck);

        final int oneThirtyAm40sec = 3600*13 + 30*60 + 40;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(oneThirtyAm40sec);
        assertEquals("25:30:40", toCheck);

        assertNull(TimeConversionUtils.convertIntegerToHMMSS(null));
    }

    @Test
    void convertHHMMSStoIntegerAndIntegerToHHMMSSShouldBeConsistent() {
        final int oneThirtyAm40sec = (3600*13 + 30*60 + 20) - 12 * 3600;
        String toCheck = TimeConversionUtils.convertIntegerToHMMSS(oneThirtyAm40sec);
        assertEquals(toCheck, TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixThirtyAM20sec = -(3600*5 + 29*60 + 40);
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixThirtyAM20sec);
        assertEquals(toCheck, TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int tenAM = - 3600 * 2;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(tenAM);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int sixFortyPM = 3600 * 6 + 40 * 60;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(sixFortyPM);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int midnight = 3600 * 12;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(midnight);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int noon = 0;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(noon);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));

        final int threePM = 3600 * 3;
        toCheck = TimeConversionUtils.convertIntegerToHMMSS(threePM);
        assertEquals(toCheck,TimeConversionUtils.convertIntegerToHMMSS(
                TimeConversionUtils.convertHHMMSSToIntFromNoonOfDayOfService(toCheck)));
    }
}
