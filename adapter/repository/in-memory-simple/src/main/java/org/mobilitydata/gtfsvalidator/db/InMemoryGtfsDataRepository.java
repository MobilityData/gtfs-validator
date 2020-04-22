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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryGtfsDataRepository implements GtfsDataRepository {
    private final Map<String, Agency> agencyCollection = new HashMap<>();

    @Override
    public Agency addAgency(final Agency newAgency) throws SQLIntegrityConstraintViolationException {
        final String agencyId = newAgency.getAgencyId();
        if (agencyCollection.containsKey(newAgency.getAgencyId())) {
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

    private final Map<String, Route> routeCollection = new HashMap<>();

    @Override
    public Route getRouteById(final String routeId) {
        return routeCollection.get(routeId);
    }

    @Override
    public Route addRoute(final Route newRoute) throws SQLIntegrityConstraintViolationException {
        if (routeCollection.containsKey(newRoute.getRouteId())) {
            throw new SQLIntegrityConstraintViolationException("route must be unique in dataset");
        } else {
            String routeId = newRoute.getRouteId();
            routeCollection.put(routeId, newRoute);
            return newRoute;
        }
    }

    private final Map<String, Map<String, Transfer>> transferCollection = new HashMap<>();

    @Override
    public Map<String, Map<String, Transfer>> getTransferCollection() {
        return Collections.unmodifiableMap(transferCollection);
    }

    @Override
    public Transfer getTransferByStopPair(final String fromStopId, final String toStopId) {
        return transferCollection.get(fromStopId).get(toStopId);
    }

    @Override
    public Transfer addTransfer(final Transfer newTransfer) throws SQLIntegrityConstraintViolationException {
        if ((transferCollection.containsKey(newTransfer.getFromStopId())) &&
                (transferCollection.get(newTransfer.getFromStopId()).containsKey(newTransfer.getToStopId()))) {
            throw new SQLIntegrityConstraintViolationException("transfer must be unique in dataset");
        } else {
            String fromStopId = newTransfer.getFromStopId();
            String toStopId = newTransfer.getToStopId();
            Map<String, Transfer> innerMap = new HashMap<>();
            innerMap.put(toStopId, newTransfer);
            transferCollection.put(fromStopId, innerMap);
            return newTransfer;
        }
    }
}