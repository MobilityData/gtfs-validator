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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    private final Map<String, Agency> agencyCollection = new HashMap<>();

    public Map<String, Agency> getAgencyCollection() {
        return agencyCollection;
    }

    @Override
    public Agency addEntity(final Agency newAgency) throws SQLIntegrityConstraintViolationException {
        String agencyId = newAgency.getAgencyId();
        if (isPresent(newAgency)) {
            throw new SQLIntegrityConstraintViolationException("agency must be unique in dataset");
        } else {
            agencyCollection.put(agencyId, newAgency);
            return newAgency;
        }
    }

    @Override
    public Agency getAgencyById(final String agencyId) {
        return agencyCollection.get(agencyId);
    }

    @Override
    public boolean isPresent(Agency agency) {
        return agencyCollection.containsKey(agency.getAgencyId());
    }

    private final Map<String, Route> routeCollection = new HashMap<>();

    public Map<String, Route> getRouteCollection() {
        return Collections.unmodifiableMap(routeCollection);
    }

    @Override
    public Route getRouteById(String routeId) {
        return routeCollection.get(routeId);
    }

    @Override
    public Route addEntity(final Route newRoute) throws SQLIntegrityConstraintViolationException {
        if (routeCollection.containsKey(newRoute.getRouteId())) {
            throw new SQLIntegrityConstraintViolationException("route must be unique in dataset");
        } else {
            String routeId = newRoute.getRouteId();
            routeCollection.put(routeId, newRoute);
            return newRoute;
        }
    }

    private final Map<String, Trip> tripCollection = new HashMap<>();

    @Override
    public Map<String, Trip> getTripCollection() {
        return Collections.unmodifiableMap(tripCollection);
    }

    @Override
    public Trip getTripById(final String tripId) {
        return tripCollection.get(tripId);
    }

    @Override
    public Trip addTrip(final Trip newTrip) throws SQLIntegrityConstraintViolationException {
        if (tripCollection.containsKey(newTrip.getTripId())) {
            throw new SQLIntegrityConstraintViolationException("trip must be unique in dataset");
        } else {
            String tripId = newTrip.getTripId();
            tripCollection.put(tripId, newTrip);
            return newTrip;
        }
    }
}