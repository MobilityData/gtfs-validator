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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

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

    public void execute(final ParsedEntity validatedTripEntity) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

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

        try {
            builder.routeId(routeId)
                    .serviceId(serviceId)
                    .tripId(tripId)
                    .tripHeadsign(tripHeadsign)
                    .tripShortName(tripShortName)
                    .directionId(directionId)
                    .blockId(blockId)
                    .shapeId(shapeId)
                    .wheelchairAccessible(wheelchairAccessible)
                    .bikesAllowed(bikesAllowed);

            gtfsDataRepository.addTrip(builder.build());

        } catch (IllegalArgumentException e) {

            if (routeId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("trips.txt", "route_id",
                        validatedTripEntity.getEntityId()));
            }

            if (serviceId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("trips.txt", "service_id",
                        validatedTripEntity.getEntityId()));
            }

            if (tripId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("trips.txt", "trip_id",
                        validatedTripEntity.getEntityId()));
            }

            if (directionId != null && directionId != 0 && directionId != 1) {
                resultRepository.addNotice(new UnexpectedValueNotice("trips.txt",
                        "direction_id", validatedTripEntity.getEntityId(), directionId));
            }


            if (wheelchairAccessible != 0 && wheelchairAccessible != 1 && wheelchairAccessible != 2) {
                resultRepository.addNotice(new UnexpectedValueNotice("trips.txt",
                        "wheelchair_accessible", validatedTripEntity.getEntityId(), wheelchairAccessible));
            }

            if (bikesAllowed != 0 && bikesAllowed != 1 && bikesAllowed != 2) {
                resultRepository.addNotice(new UnexpectedValueNotice("trips.txt",
                        "bikes_allowed", validatedTripEntity.getEntityId(), bikesAllowed));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("trips.txt", "trip_id",
                    validatedTripEntity.getEntityId()));
            throw e;
        }
    }
}