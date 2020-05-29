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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
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

    // Map containing Transfer entities. Entities are mapped on a first key which is the value found in the column
    // from_stop_id of GTFS file transfers.txt; the second key is the value found in the column to_stop_id of the same
    // file.
    private final Map<String, Map<String, Transfer>> transferCollection = new HashMap<>();

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
     * Add a Transfer representing a row from transfers.txt to this {@link GtfsDataRepository}. Return the entity added
     * to the repository if the uniqueness constraint of route based on composite key from_stop_id and to_stop_id is
     * respected, if this requirement is not met, returns null.
     *
     * @param newTransfer the internal representation of a row from transfers.txt to be added to the repository.
     * @return the entity added to the repository if the uniqueness constraint of route based on composite key
     * from_stop_id and to_stop_id is respected, if this requirement is not met, returns null.
     */
    @Override
    public Transfer addTransfer(final Transfer newTransfer) throws IllegalArgumentException {
        if (newTransfer != null) {
            // check that that from_stop_id is not already in collection. It if is, check that to_stop_id is not in the
            // associated map
            if ((transferCollection.containsKey(newTransfer.getFromStopId())) &&
                    (transferCollection.get(newTransfer.getFromStopId()).containsKey(newTransfer.getToStopId()))) {
                return null;
            } else {
                final String fromStopId = newTransfer.getFromStopId();
                final String toStopId = newTransfer.getToStopId();
                if (!transferCollection.containsKey(newTransfer.getFromStopId())) {
                    final Map<String, Transfer> innerMap = new HashMap<>();
                    innerMap.put(toStopId, newTransfer);
                    transferCollection.put(fromStopId, innerMap);
                } else {
                    transferCollection.get(newTransfer.getFromStopId()).put(newTransfer.getToStopId(), newTransfer);
                }
                return newTransfer;
            }
        } else {
            throw new IllegalArgumentException("Cannot add null transfer to data repository");
        }
    }

    /**
     * Return the Transfer representing a row from transfers.txt related to the composite key provided as parameter.
     *
     * @param fromStopId first part of the composite key: identifies a stop or station where a connection between
     *                   routes begins. Querying on {@param fromStopId}, the method will will only return records
     *                   containing {@param fromStopId} in the from_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {@param fromStopId} is in the to_stop_id column of the
     *                   same pre-mentioned GTFS file.
     *
     * @param toStopId   second part of the composite key: identifies a stop or station where a connection between
     *                   routes ends. Querying on {@param toStopId}, the method will will only return records
     *                   containing {@param toStopId} in the to_stop_id column of transfers.txt GTFS file. This
     *                   method will not return any records where {@param toStopId} is in the from_stop_id column
     *                   of the same pre-mentioned GTFS file.
     * @return the Transfer representing a row from transfers.txt related to the composite key provided as parameter
     */
    @Override
    public Transfer getTransferByStopPair(final String fromStopId, final String toStopId) {
        return transferCollection.get(fromStopId).get(toStopId);
    }
}