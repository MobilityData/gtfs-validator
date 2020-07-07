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

package org.mobilitydata.gtfsvalidator.usecase.utils;

public interface TimeUtils {
    /**
     * This method converts a time formatted as HH:MM:SS to a number of seconds elapsed since noon.
     * The time is measured from "noon minus 12h" of the service day (effectively midnight except for days on which
     * daylight savings time changes occur).
     * @param timeAsString   the time formatted as HH:MM:SS to convert as  a number of seconds elapsed since noon of day
     *                       of service.
     * @return  the number of seconds elapsed since noon of day of service. The time is measured from "noon minus 12h"
     * of the service day (effectively midnight except for days on which daylight savings time changes occur).
     */
    Integer convertHHMMSSToIntFromNoonOfDayOfService(final String timeAsString);

    /**
     * This method converts a number of seconds elapsed since noon of say of service to a string formatted as HH:MM:SS.
     *
     * @param elapsedDurationSinceNoon  the number of seconds elapsed since noon of say of service to be converted as a
     *                                  string formatted as HH:MM:SS.
     * @return  the human readable string representation of the number of seconds elapsed since noon of day of service.
     * This string is formatted as follows: HH:MM:SS.
     */
    String convertIntegerToHMMSS(final Integer elapsedDurationSinceNoon);
}
