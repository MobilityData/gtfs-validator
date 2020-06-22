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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;

/**
 * This use case turns a parsed entity representing a row from trips.txt into a concrete class
 */
public class ProcessParsedTrip {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Trip.TripBuilder builder;

    public ProcessParsedTrip(final ValidationResultRepository resultRepository,
                             final GtfsDataRepository gtfsDataRepository,
                             final Trip.TripBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from trips.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Trip} object if the requirements
     * from the official GTFS specification are met. When these requirements are mot met, related notices generated in
     * {@code Trip.TripBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * trip entities is not respected.
     *
     * @param validatedTripEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedTripEntity) {
        final String routeId = (String) validatedTripEntity.get("route_id");
        final String serviceId = (String) validatedTripEntity.get("service_id");
        final String tripId = (String) validatedTripEntity.get("trip_id");
        final String tripHeadsign = (String) validatedTripEntity.get("trip_headsign");
        final String tripShortName = (String) validatedTripEntity.get("trip_short_name");
        final Integer directionId = (Integer) validatedTripEntity.get("direction_id");
        final String blockId = (String) validatedTripEntity.get("block_id");
        final String shapeId = (String) validatedTripEntity.get("shape_id");
        final Integer wheelchairAccessible = (Integer) validatedTripEntity.get("wheelchair_accessible");
        final Integer bikesAllowed = (Integer) validatedTripEntity.get("bikes_allowed");

        builder.clearFieldAll()
                .routeId(routeId)
                .serviceId(serviceId)
                .tripId(tripId)
                .tripHeadsign(tripHeadsign)
                .tripShortName(tripShortName)
                .directionId(directionId)
                .blockId(blockId)
                .shapeId(shapeId)
                .wheelchairAccessible(wheelchairAccessible)
                .bikesAllowed(bikesAllowed);

        final EntityBuildResult<?> trip = builder.build();

        if (trip.isSuccess()) {
            if (gtfsDataRepository.addTrip((Trip) trip.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("trips.txt", "trip_id",
                        validatedTripEntity.getEntityId()));
            }
        } else {
            // At this step of the process, we are certain that calling .getData() on trip will return a list of notices
            // Therefore, the warning produced by casting can be safely ignored.
            //noinspection unchecked
            ((ArrayList<Notice>) trip.getData()).forEach(resultRepository::addNotice);
        }
    }
}