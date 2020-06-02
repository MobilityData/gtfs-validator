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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FareRule;
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

    // Map containing FareRule entities. Entities are mapped on a composite key made of the values found in the
    // columns of GTFS file fare_rules.txt:
    // - fare_id
    // - route_id
    // - origin_id
    // - destination_id
    // - contains_id
    private final Map<String, FareRule> fareRuleCollection = new HashMap<>();

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
     * Add a FareRule representing a row from fare_rules.txt to this {@link GtfsDataRepository}.
     * Return the entity added to the repository if the uniqueness constraint on rows from fare_rules.txt is respected,
     * if this requirement is not met, returns null.
     *
     * @param newFareRule the internal representation of a row from fare_rules.txt to be added to the repository.
     * @return Return the entity added to the repository if the uniqueness constraint on rows from fare_rules.txt
     * is respected, if this requirement is not met, returns null.
     */
    @Override
    public FareRule addFareRule(final FareRule newFareRule) throws IllegalArgumentException {
        if (newFareRule != null) {
            final String key = newFareRule.getFareRuleMappingKey();
            if (fareRuleCollection.containsKey(key)) {
                return null;
            } else {
                fareRuleCollection.put(key, newFareRule);
                return newFareRule;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null FareRule to data repository");
        }
    }

    /**
     * Return the FareRule representing a row from fare_rules.txt related to the id provided as parameter
     *
     * @param fareId        1st part of the composite key: identifies a fare class
     * @param routeId       2nd part of the composite key: identifies a route associated with the fare class
     * @param originId      3rd part of the composite key: identifies an origin zone
     * @param destinationId 4th part of the composite key: identifies a destination zone
     * @param containsId    5th part ot the composite key: identifies the zones that a rider will enter while using a
     *                      given fare class
     * @return the FareRule representing a row from fare_rules.txt related to the id provided as parameter
     */
    @Override
    public FareRule getFareRule(final String fareId, final String routeId, final String originId,
                                final String destinationId, final String containsId) {
        return fareRuleCollection.get(fareId + routeId + originId + destinationId + containsId);
    }
}