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

package org.mobilitydata.gtfsvalidator.db;

import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This holds an internal representation of gtfs entities: each row of each file from a GTFS dataset is represented here
 */
public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    // Map containing Agency entities. Entities are mapped on the value found in the column agency_id of GTFS file
    // agency.txt
    private final Map<String, Agency> agencyPerId = new HashMap<>();

    // Map containing Route entities. Entities are mapped on the value found in the column route_id of GTFS file
    // routes.txt
    private final Map<String, Route> routePerId = new HashMap<>();

    // Map containing Trip entities. Entities are mapped on the value found in column trip_id of GTFS file trips.txt
    private final Map<String, Trip> tripPerId = new HashMap<>();

    /**
     * Add an Agency representing a row from agency.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of agency based on agency_id is respected, if this requirement is not met, returns null.
     *
     * @param newAgency the internal representation of a row from agency.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of agency based on agency_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Agency addAgency(@NotNull final Agency newAgency) throws IllegalArgumentException {
        //noinspection ConstantConditions
        if (newAgency != null) {
            if (agencyPerId.containsKey(newAgency.getAgencyId())) {
                return null;
            } else {
                agencyPerId.put(newAgency.getAgencyId(), newAgency);
                return newAgency;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null agency to data repository");
        }
    }

    /**
     * Return the Agency representing a row from agency.txt related to the id provided as parameter
     *
     * @param agencyId the key from agency.txt related to the Agency to be returned
     * @return the Agency representing a row from agency.txt related to the id provided as parameter
     */
    @Override
    public Agency getAgencyById(final String agencyId) {
        return agencyPerId.get(agencyId);
    }

    /**
     * Add a Route representing a row from routes.txt to this. Return the entity added to the repository if the
     * uniqueness constraint of route based on route_id is respected, if this requirement is not met, returns null.
     *
     * @param newRoute the internal representation of a row from routes.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on route_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Route addRoute(@NotNull final Route newRoute) throws IllegalArgumentException {
        //noinspection ConstantConditions
        if (newRoute != null) {
            if (routePerId.containsKey(newRoute.getRouteId())) {
                return null;
            } else {
                routePerId.put(newRoute.getRouteId(), newRoute);
                return newRoute;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null route to data repository");
        }
    }

    /**
     * Return a collection of Route objects representing all the rows from routes.txt
     *
     * @return a collection of Route objects representing all the rows from routes.txt
     */
    @Override
    public Collection<Route> getRouteAll() {
        return routePerId.values();
    }

    /**
     * Return the Route representing a row from routes.txt related to the id provided as parameter
     *
     * @param routeId the key from routes.txt related to the Route to be returned
     * @return the Route representing a row from routes.txt related to the id provided as parameter
     */
    @Override
    public Route getRouteById(final String routeId) {
        return routePerId.get(routeId);
    }

    /**
     * Add a trip representing a row from trip.txt to this {@link GtfsDataRepository}. Return the entity added to the
     * repository if the uniqueness constraint of trip based on trip_id is respected, if this requirement is not met,
     * returns null.
     *
     * @param newTrip the internal representation of a row from trips.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of trip based on trip_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Trip addTrip(final Trip newTrip) throws IllegalArgumentException {
        if (newTrip != null) {
            if (tripPerId.containsKey(newTrip.getTripId())) {
                return null;
            } else {
                final String tripId = newTrip.getTripId();
                tripPerId.put(tripId, newTrip);
                return newTrip;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null trip to data repository");
        }
    }

    /**
     * Return the Trip representing a row from trips.txt related to the id provided as parameter
     *
     * @param tripId the key from trips.txt related to the Trip to be returned
     * @return the Trip representing a row from trips.txt related to the id provided as parameter
     */
    @Override
    public Trip getTripById(final String tripId) {
        return tripPerId.get(tripId);
    }
}