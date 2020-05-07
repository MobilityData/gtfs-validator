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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Attribution;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * This holds an internal representation of gtfs entities: each row of each file from a GTFS dataset is represented here
 */
public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    private final Map<String, Agency> agencyCollection = new HashMap<>();
    private final Map<String, Route> routeCollection = new HashMap<>();
    private final Map<String, Attribution> attributionCollection = new HashMap<>();

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
     * Add an Attribution representing a row from attributions.txt to this. Return the entity added to the repository if
     * the uniqueness constraint of rows f attributions.txt is respected, if this requirement is not met, returns null.
     *
     * @param newAttribution the internal representation of a row from attributions.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of rows f attributions.txt is respected,
     * if this requirement is not met, returns null.
     */
    @Override
    public Attribution addAttribution(final Attribution newAttribution) throws IllegalArgumentException {
        if (newAttribution != null) {
            final String key = newAttribution.getAttributionId() + newAttribution.getAgencyId() +
                    newAttribution.getRouteId() + newAttribution.getTripId() + newAttribution.getOrganizationName() +
                    newAttribution.isProducer() + newAttribution.isOperator() + newAttribution.isAuthority();
            if (attributionCollection.containsKey(key)) {
                return null;
            } else {
                attributionCollection.put(key, newAttribution);
                return newAttribution;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null attribution to data repository");
        }
    }

    /**
     * Return the Attribution representing a row from attributions.txt related to the composite key provided as
     * parameter
     *
     * @param attributionId    identifies an attribution for the dataset or a subset of it
     * @param agencyId         agency to which the attribution applies
     * @param routeId          route to which the attribution applies
     * @param tripId           trip to which the attribution applies
     * @param organizationName name of the organization that the dataset is attributed to
     * @param isProducer       the role of the organization if producer
     * @param isOperator       the role of the organization if operator
     * @param isAuthority      the role of the organization if authority
     * @param attributionUrl   URL of the organization
     * @param attributionEmail email of the organization
     * @param attributionPhone phone number of the organization
     * @return the Attribution representing a row from attributions.txt related to the composite key provided as
     * parameter
     */
    @Override
    public Attribution getAttribution(final String attributionId, final String agencyId, final String routeId,
                                      final String tripId, final String organizationName, final Integer isProducer,
                                      final Integer isOperator, final Integer isAuthority, final String attributionUrl,
                                      final String attributionEmail, final String attributionPhone) {
        return attributionCollection.get(attributionId + agencyId + routeId + tripId + organizationName + isProducer + isOperator
                + isAuthority + attributionUrl + attributionEmail + attributionPhone);
    }
}