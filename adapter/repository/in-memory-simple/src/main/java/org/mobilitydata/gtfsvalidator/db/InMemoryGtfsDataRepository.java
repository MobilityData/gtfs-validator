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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This holds an internal representation of gtfs entities: each row of each file from a GTFS dataset is represented here
 */
public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    private final Map<String, Agency> agencyCollection = new HashMap<>();
    private final Map<String, Route> routeCollection = new HashMap<>();

    // Map containing Pathway entities. Entities are mapped on the value found in column pathway_id of GTFS file
    // pathways.txt
    private final Map<String, Pathway> pathwayPerId = new HashMap<>();

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
            if (agencyCollection.containsKey(newAgency.getAgencyId())) {
                return null;
            } else {
                agencyCollection.put(newAgency.getAgencyId(), newAgency);
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
        return agencyCollection.get(agencyId);
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
            if (routeCollection.containsKey(newRoute.getRouteId())) {
                return null;
            } else {
                routeCollection.put(newRoute.getRouteId(), newRoute);
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
        return routeCollection.values();
    }

    /**
     * Return the Route representing a row from routes.txt related to the id provided as parameter
     *
     * @param routeId the key from routes.txt related to the Route to be returned
     * @return the Route representing a row from routes.txt related to the id provided as parameter
     */
    @Override
    public Route getRouteById(final String routeId) {
        return routeCollection.get(routeId);
    }

    /**
     * Add a Pathway representing a row from pathways.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint of route based on pathway_id is respected,
     * if this requirement is not met, returns null.
     *
     * @param newPathway the internal representation of a row from routes.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on pathway_id is
     * respected, if this requirement is not met returns null.
     */
    @Override
    public Pathway addPathway(final Pathway newPathway) throws IllegalArgumentException {
        if (newPathway != null) {
            if (pathwayPerId.containsKey(newPathway.getPathwayId())) {
                return null;
            } else {
                String pathwayId = newPathway.getPathwayId();
                pathwayPerId.put(pathwayId, newPathway);
                return newPathway;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null pathway to data repository");
        }
    }

    /**
     * Return the Pathway representing a row from pathways.txt related to the id provided as parameter
     *
     * @param pathwayId the key from pathways.txt related to the Pathway to be returned
     * @return the Pathway representing a row from pathways.txt related to the id provided as parameter
     */
    @Override
    public Pathway getPathwayById(final String pathwayId) {
        return pathwayPerId.get(pathwayId);
    }
}