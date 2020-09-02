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
import java.time.temporal.ChronoUnit;
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
    @Override
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
     * @param elapsedDurationSinceNoonInSeconds the number of seconds elapsed since noon of day of service to be
     *                                          converted as a string formatted as HH:MM:SS.
     * @return the human readable string representation of the number of seconds elapsed since noon of day of service.
     * This string is formatted as follows: HH:MM:SS.
     */
    public String convertIntegerToHHMMSS(final Integer elapsedDurationSinceNoonInSeconds) throws
            IllegalArgumentException {
        if (elapsedDurationSinceNoonInSeconds == null) {
            throw new IllegalArgumentException("elapsedDurationSinceNoonInSeconds cannot be null");
        }
        final LocalTime noon = LocalTime.NOON;
        // elapsedDurationSinceNoonInSeconds > 0 for times after noon, e.g 14:00PM is represented a +2*3600s=+7200s
        // elapsedDurationSinceNoonInSeconds < 0 for times before noon, e.g 11:00AM is represented as -1*3600s=-3600s
        final boolean isTimeGreaterThan24hours =
                elapsedDurationSinceNoonInSeconds >= TimeUnit.HOURS.toSeconds(12);

        if (!isTimeGreaterThan24hours) {
            return String.format("%02d:%02d:%02d", noon.plusSeconds(elapsedDurationSinceNoonInSeconds).getHour(),
                    noon.plusSeconds(elapsedDurationSinceNoonInSeconds).getMinute(),
                    noon.plusSeconds(elapsedDurationSinceNoonInSeconds).getSecond());
        } else {
            // This wraps around midnight
            LocalTime timeAfterMidnight = noon.plusSeconds(elapsedDurationSinceNoonInSeconds);
            // Get the number of hours, minutes, and seconds between midnight and timeAfterMidnight, which is the
            // time to add to 24:00:00
            long hoursAfterMidnight = LocalTime.MIDNIGHT.until(timeAfterMidnight, ChronoUnit.HOURS);
            long minutesAfterMidnight = LocalTime.MIDNIGHT.until(timeAfterMidnight, ChronoUnit.MINUTES) % 60;
            long secondsAfterMidnight = LocalTime.MIDNIGHT.until(timeAfterMidnight, ChronoUnit.SECONDS) % 60;
            return String.format("%02d:%02d:%02d",
                    24 + hoursAfterMidnight,
                    minutesAfterMidnight,
                    secondsAfterMidnight);
        }
    }

    /**
     * This method checks if two periods of times overlap:
     * a |--------|            07:00 am to 10:00 am     here, method returns false according to the Python Validator im-
     * b          |----|       10:00 am to 11:00 am     plementation                                                                                                 implementation
     * <p>
     * <p>
     * a |--------|            07:00 am to 10:00 am     here, method returns false
     * b              |----|   11:00 am to 12:00 am
     * <p>
     * a |--------|            07:00 am to 10:00 am     here, method returns true
     * b     |----|            09:00 am to 10:00 am
     * <p>
     * a |--------|            07:00 am to 10:00 am     here, method returns true
     * b |----|                07:00 am to 09:00 am
     * <p>
     * a |--------|            07:00 am to 10:00 am     here, method returns true
     * b       |----|          08:00 am to 12:00 am
     * <p>
     * a |--------|            07:00 am to 12:00 am     here, method returns true
     * b   |----|              08:00 am to 11:00 am
     *
     * @param firstPeriodFirstTimeSecs  lower bound of the first period of time in number of seconds elapsed since noon
     * @param firstPeriodLastTimeSecs   upper bound of the first period of time in number of seconds elapsed since noon
     * @param secondPeriodFirstTimeSecs lower bound of the second period of time in number of seconds elapsed since noon
     * @param secondPeriodLastTimeSecs  upper bound of the second period of time in number of seconds elapsed since noon
     * @return true if the two periods of time overlap, otherwise returns false
     */
    @Override
    public boolean arePeriodsOverlapping(final int firstPeriodFirstTimeSecs,
                                         final int firstPeriodLastTimeSecs,
                                         final int secondPeriodFirstTimeSecs,
                                         final int secondPeriodLastTimeSecs) {
        if (firstPeriodFirstTimeSecs > secondPeriodFirstTimeSecs) {
            return arePeriodsOverlapping(secondPeriodFirstTimeSecs,
                    secondPeriodLastTimeSecs,
                    firstPeriodFirstTimeSecs,
                    firstPeriodLastTimeSecs);
        }
        return !(firstPeriodFirstTimeSecs <= secondPeriodLastTimeSecs &&
                firstPeriodLastTimeSecs <= secondPeriodFirstTimeSecs);
    }
}
