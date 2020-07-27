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

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.TripIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Map;

public class ValidateStopTimeTripId {
    /**
     * Checks if `trip_id` of a {@code StopTime} refers to a record from file `trips.txt`. A new
     * notice is generated each time this condition is false.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute(final ValidationResultRepository resultRepo,
                        final StopTime stopTime,
                        final Map<String, Trip> tripCollection) {
        if (!tripCollection.containsKey(stopTime.getTripId())) {
            resultRepo.addNotice(new TripIdNotFoundNotice("stop_times.txt",
                    "trip_id",
                    "trip_id",
                    "stop_sequence",
                    stopTime.getTripId(),
                    stopTime.getStopSequence(),
                    stopTime.getTripId()
            ));
        }
    }
}
