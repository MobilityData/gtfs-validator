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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
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

    Map<String, Map<LocalDateTime, CalendarDate>> calendarDateCollection = new HashMap<>();

    @Override
    public Map<String, Map<LocalDateTime, CalendarDate>> getCalendarDateCollection() {
        return Collections.unmodifiableMap(calendarDateCollection);
    }

    @Override
    public CalendarDate getCalendarDateByServiceIdAndDate(final String serviceId, final LocalDateTime date) {
        return calendarDateCollection.get(serviceId).get(date);
    }

    @Override
    public CalendarDate addCalendarDate(CalendarDate newCalendarDate) throws SQLIntegrityConstraintViolationException {
        if ((calendarDateCollection.containsKey(newCalendarDate.getServiceId())) &&
                (calendarDateCollection.get(newCalendarDate.getServiceId()).containsKey(newCalendarDate.getDate()))) {
            throw new SQLIntegrityConstraintViolationException("calendar_dates based on service_id and date must be " +
                    "unique in dataset");
        } else {
            String serviceId = newCalendarDate.getServiceId();
            Map<LocalDateTime, CalendarDate> innerMap = new HashMap<>();
            innerMap.put(newCalendarDate.getDate(), newCalendarDate);
            calendarDateCollection.put(serviceId, innerMap);

            return newCalendarDate;
        }
    }
}