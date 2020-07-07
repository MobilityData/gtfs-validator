/*
 * Copyright (c) 2020. MobilityData IO.
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

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from stop_times.txt into a concrete class
 */
public class ProcessParsedStopTime {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final TimeUtils timeUtils;
    private final StopTime.StopTimeBuilder builder;

    public ProcessParsedStopTime(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final TimeUtils timeUtils,
                                 final StopTime.StopTimeBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.timeUtils = timeUtils;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from stop_times.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code StopTime} object if the
     * requirements from the official GTFS specification are met. When these requirements are mot met, related notices
     * generated in {@code StopTime.StopTimeBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * route entities is not respected.
     *
     * @param validatedParsedStopTime entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedStopTime) {
        final String tripId = (String) validatedParsedStopTime.get("trip_id");
        final Integer arrivalTime = timeUtils.convertHHMMSSToIntFromNoonOfDayOfService(
                (String) validatedParsedStopTime.get("arrival_time"));
        final Integer departureTime = timeUtils.convertHHMMSSToIntFromNoonOfDayOfService(
                (String) validatedParsedStopTime.get("departure_time"));
        final String stopId = (String) validatedParsedStopTime.get("stop_id");
        final Integer stopSequence = (Integer) validatedParsedStopTime.get("stop_sequence");
        final String stopHeadsign = (String) validatedParsedStopTime.get("stop_headsign");
        final Integer pickupType = (Integer) validatedParsedStopTime.get("pickup_type");
        final Integer dropOffType = (Integer) validatedParsedStopTime.get("drop_off_type");
        final Integer continuousPickup = (Integer) validatedParsedStopTime.get("continuous_pickup");
        final Integer continuousDropOff = (Integer) validatedParsedStopTime.get("continuous_drop_off");
        final Float shapeDistTraveled = (Float) validatedParsedStopTime.get("shape_dist_traveled");
        final Integer timepoint = (Integer) validatedParsedStopTime.get("timepoint");

        builder.clear()
                .tripId(tripId)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .stopId(stopId)
                .stopSequence(stopSequence)
                .stopHeadsign(stopHeadsign)
                .pickupType(pickupType)
                .dropOffType(dropOffType)
                .continuousPickup(continuousPickup)
                .continuousDropOff(continuousDropOff)
                .shapeDistTraveled(shapeDistTraveled)
                .timepoint(timepoint);

        final EntityBuildResult<?> stopTime = builder.build();

        if (stopTime.isSuccess()) {
            if (gtfsDataRepository.addStopTime((StopTime) stopTime.getData()) == null) {
                resultRepository.addNotice(
                        new DuplicatedEntityNotice(
                                "stop_times.txt",
                        "trip_id",
                        "stop_sequence",
                                tripId,
                                stopSequence)
                );
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) stopTime.getData()).forEach(resultRepository::addNotice);
        }
    }
}
