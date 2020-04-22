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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
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

    Map<String, Level> levelCollection = new HashMap<>();

    @Override
    public Map<String, Level> getLevelCollection() {
        return Collections.unmodifiableMap(levelCollection);
    }

    @Override
    public Level getLevelByLevelId(final String levelId) {
        return levelCollection.get(levelId);
    }

    @Override
    public Level addLevel(final Level newLevel) throws SQLIntegrityConstraintViolationException {
        if (levelCollection.containsKey(newLevel.getLevelId())) {
            throw new SQLIntegrityConstraintViolationException("level must be unique in dataset");
        } else {
            final String levelId = newLevel.getLevelId();
            levelCollection.put(levelId, newLevel);
            return newLevel;
        }
    }
}