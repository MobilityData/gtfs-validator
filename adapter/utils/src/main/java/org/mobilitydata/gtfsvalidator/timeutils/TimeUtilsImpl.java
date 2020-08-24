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

import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Utility class to carry out operations related to GTFS TIME type
 */
public class TimeUtilsImpl implements TimeUtils {
    // Matches any time string representing time in HH:MM:SS format with:
    // - hours between 0 and 99h,
    // - minutes between 0 and 60min
    // - seconds between 0 and 0sec.
    // Examples of string matching the related regexp: "00:45:32" or "26:45:22".
    // Examples of string that do not match the regexp: "23:90", "23:90:62" or "abcdefg"
    final static Pattern pattern = Pattern.compile("([0-9][0-9]|[0-9]):[0-5][0-9]:[0-5][0-9]");
    private static TimeUtilsImpl TIME_UTILS = null;

    private TimeUtilsImpl() {
    }

    /**
     * Implement singleton pattern
     *
     * @return a unique instance of {@code TimeUtilsImpl}
     */
    public static TimeUtilsImpl getInstance() {
        if (TIME_UTILS == null) {
            TIME_UTILS = new TimeUtilsImpl();
        }
        return TIME_UTILS;
    }

    /**
     * This method converts a time formatted as HH:MM:SS to a number of seconds elapsed since noon.
     * The time is measured from "noon minus 12h" of the service day (effectively midnight except for days on which
     * daylight savings time changes occur).
     *
     * @param timeAsString the time formatted as HH:MM:SS to convert as  a number of seconds elapsed since noon of day
     *                     of service.
     * @return the number of seconds elapsed since noon of day of service. The time is measured from "noon minus 12h"
     * of the service day (effectively midnight except for days on which daylight savings time changes occur).
     */
    public Integer convertHHMMSSToIntFromNoonOfDayOfService(final String timeAsString) {
        if (timeAsString != null && pattern.matcher(timeAsString).matches()) {
            final String[] timeStringSplit = timeAsString.split(":");
            try {
                final int hourValue = Integer.parseInt(timeStringSplit[0]);
                final int minuteValue = Integer.parseInt(timeStringSplit[1]);
                final int secondValue = Integer.parseInt(timeStringSplit[2]);
                // Converting time to an Integer value representing number of seconds
                final int timeValueAsInt = (int) (TimeUnit.HOURS.toSeconds(hourValue) +
                        TimeUnit.MINUTES.toSeconds(minuteValue) + secondValue);
                // Setting noon as point zero, subtracting 12 * 3600 seconds
                return timeValueAsInt - (int) (TimeUnit.HOURS.toSeconds(12));
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
     * @param elapsedDurationSinceNoon the number of seconds elapsed since noon of say of service to be converted as a
     *                                 string formatted as HH:MM:SS.
     * @return the human readable string representation of the number of seconds elapsed since noon of day of service.
     * This string is formatted as follows: HH:MM:SS.
     */
    public String convertIntegerToHMMSS(final Integer elapsedDurationSinceNoon) {
        if (elapsedDurationSinceNoon != null) {
            final LocalTime noon = LocalTime.NOON;
            final boolean isTimePM = elapsedDurationSinceNoon >= 0;
            // elapsedDurationSinceNoon > 0 for times after noon, e.g 14:00PM is represented a +2*3600s=+7200s
            // elapsedDurationSinceNoon < 0 for times before noon, e.g 11:00AM is represented as -1*3600s=-3600s
//            LocalTime toReturn = elapsedDurationSinceNoon < 24*3600 ?
            LocalTime toReturn = noon.plusHours(TimeUnit.SECONDS.toHours(elapsedDurationSinceNoon));
            final boolean isTimeGreaterThan24hours = elapsedDurationSinceNoon >= TimeUnit.HOURS.toSeconds(12);

            if (isTimePM) {
                toReturn = toReturn.plusMinutes(
                        TimeUnit.SECONDS.toMinutes(elapsedDurationSinceNoon) -
                                TimeUnit.HOURS.toMinutes(toReturn.getHour() - noon.getHour()));
                toReturn = toReturn.plusSeconds(elapsedDurationSinceNoon -
                        TimeUnit.HOURS.toSeconds(toReturn.getHour() - noon.getHour()) -
                        TimeUnit.MINUTES.toSeconds(toReturn.getMinute()));
            } else {
                toReturn = toReturn.plusMinutes(
                        (TimeUnit.SECONDS.toMinutes(
                                elapsedDurationSinceNoon) - TimeUnit.HOURS.toMinutes(toReturn.getHour())) % 60);
                toReturn = toReturn.plusSeconds(elapsedDurationSinceNoon -
                        TimeUnit.HOURS.toSeconds(toReturn.getHour() - noon.getHour()) -
                        TimeUnit.MINUTES.toSeconds(toReturn.getMinute()));
            }
            return String.format("%02d:%02d:%02d",
                    isTimeGreaterThan24hours ? toReturn.getHour() + 24 : toReturn.getHour(),
                    toReturn.getMinute(),
                    toReturn.getSecond());
        } else {
            return null;
        }
    }
}
