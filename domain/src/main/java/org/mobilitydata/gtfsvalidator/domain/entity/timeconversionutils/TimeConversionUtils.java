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

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utility class to carry out operations related to GTFS TIME type
 */
public class TimeConversionUtils {
    // HH:MM:SS pattern to represent time. Examples of string matching the related regexp:
    // - "00:45:32"
    // - "26:45:22"
    // Examples of string that do not match the regexp:
    // - "23:90"
    // - "abcdefg"
    final static Pattern pattern = Pattern.compile("([0-9][0-9]|[0-9]):[0-5][0-9]:[0-5][0-9]");

    /**
     * Class constructor. Constructor is private to avoid instantiation of class.
     */
    private TimeConversionUtils() {
    }

    /**
     * This method converts a time formatted as HH:MM:SS to a number of seconds elapsed since noon.
     * The time is measured from "noon minus 12h" of the service day (effectively midnight except for days on which
     * daylight savings time changes occur).
     * @param timeAsString   the time formatted as HH:MM:SS to convert as  a number of seconds elapsed since noon of day
     *                       of service.
     * @return  the number of seconds elapsed since noon of day of service. The time is measured from "noon minus 12h"
     * of the service day (effectively midnight except for days on which daylight savings time changes occur).
     */
    public static Integer convertHHMMSSToIntFromNoonOfDayOfService(final String timeAsString) {
        if (timeAsString != null && pattern.matcher(timeAsString).matches()) {
            final String[] timeStringSplit = timeAsString.split(":");
            try {
                final int hourValue = Integer.parseInt(timeStringSplit[0]);
                final int minuteValue = Integer.parseInt(timeStringSplit[1]);
                final int secondValue = Integer.parseInt(timeStringSplit[2]);
                // Converting time to an Integer value representing number of seconds
                final int timeValueAsInt = hourValue * 3600 + minuteValue * 60 + secondValue;
                // Setting noon as point zero, subtracting 12 * 3600 seconds
                return timeValueAsInt - (12 * 3600);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This method converts a number of seconds elapsed since noon of say of service to a string formatted as HH:MM:SS.
     *
     * @param timeAsInt  the number of seconds elapsed since noon of say of service to be converted as a string
     *                   formatted as HH:MM:SS.
     * @return  the human readable string representation of the number of seconds elapsed since noon of day of service.
     * This string is formatted as follows: HH:MM:SS.
     */
    public static String convertIntegerToHMMSS(final Integer timeAsInt) {
        if (timeAsInt != null) {
            int hourValue = (int) (TimeUnit.SECONDS.toHours(timeAsInt) + 12);
            int minuteValue;
            int secondValue;
            if (timeAsInt > 0) {
                minuteValue = (int) (TimeUnit.SECONDS.toMinutes(timeAsInt) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeAsInt)));
                secondValue = (int) (TimeUnit.SECONDS.toSeconds(timeAsInt) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeAsInt)));
            } else {
                minuteValue = ((int) (TimeUnit.SECONDS.toMinutes(timeAsInt) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeAsInt)))) % 60;
                if (minuteValue < 0) {
                    hourValue = hourValue - 1;
                    minuteValue = 1 + ((int) -(TimeUnit.SECONDS.toMinutes(timeAsInt) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeAsInt))));
                }
                secondValue = (int) (TimeUnit.SECONDS.toSeconds(timeAsInt) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeAsInt)));
                if ((int) (TimeUnit.SECONDS.toSeconds(timeAsInt) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeAsInt))) < 0) {
                    secondValue = 60 + secondValue;
                }
            }
            return String.format("%02d:%02d:%02d", hourValue, minuteValue, secondValue);
        } else {
            return null;
        }
    }
}
