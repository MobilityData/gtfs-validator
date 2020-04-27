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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * This use case turns a parsed entity representing a row from routes.txt into a concrete class
 */
public class ProcessParsedRoute {

    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Route.RouteBuilder builder;

    public ProcessParsedRoute(final ValidationResultRepository resultRepository,
                              final GtfsDataRepository gtfsDataRepository,
                              final Route.RouteBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from routes.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@link ParsedEntity} and creates a {@link Route} object.
     * <p>
     * If value for route_id field is null, a {@link MissingRequiredValueNotice} is created and added to the validation
     * result repository provided in the use case constructor.
     * <p>
     * If an unexpected value is passed to field route_type a {@link UnexpectedValueNotice} is created and added to the
     * validation result repository provided in the use case constructor.
     * <p>
     * In both cases a {@link IllegalArgumentException} is thrown.
     *
     * @param validatedParsedRoute entity to be processed and added to the GTFS data repository
     * @throws IllegalArgumentException if specification requirements are not met regarding values for agency_name,
     *                              route_id and route type
     */
    public void execute(final ParsedEntity validatedParsedRoute) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {

        final String routeId = (String) validatedParsedRoute.get("route_id");
        final String agencyId = (String) validatedParsedRoute.get("agency_id");
        final String routeShortName = (String) validatedParsedRoute.get("route_short_name");
        final String routeLongName = (String) validatedParsedRoute.get("route_long_name");
        final String routeDesc = (String) validatedParsedRoute.get("route_desc");
        final Integer routeType = (Integer) validatedParsedRoute.get("route_type");
        final String routeUrl = (String) validatedParsedRoute.get("route_url");
        final String routeColor = (String) validatedParsedRoute.get("route_color");
        final String routeTextColor = (String) validatedParsedRoute.get("route_text_color");
        final Integer routeSortOrder = (Integer) validatedParsedRoute.get("route_sort_order");

        try {
            builder.routeId(routeId)
                    .agencyId(agencyId)
                    .routeShortName(routeShortName)
                    .routeLongName(routeLongName)
                    .routeDesc(routeDesc)
                    .routeType(routeType)
                    .routeUrl(routeUrl)
                    .routeColor(routeColor)
                    .routeTextColor(routeTextColor)
                    .routeSortOrder(routeSortOrder);

            gtfsDataRepository.addRoute(builder.build());

        } catch (IllegalArgumentException e) {

            if (routeId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("routes.txt", "route_id",
                        validatedParsedRoute.getEntityId()));
            } else {
                resultRepository.addNotice(new UnexpectedValueNotice("routes.txt",
                        "route_type", validatedParsedRoute.getEntityId(), routeType));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("routes.txt", "route_id",
                    validatedParsedRoute.getEntityId()));
            throw e;
        }
    }
}
