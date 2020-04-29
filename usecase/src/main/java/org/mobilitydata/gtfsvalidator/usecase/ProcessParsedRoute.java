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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

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
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Route} object if the requirements
     * from the official GTFS specification are met. When these requirements are mot met, related notices generated in
     * {@code Route.RouteBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code EntityMustBeUniqueNotice} to said repository if the uniqueness constraint on
     * route entities is not respected.
     *
     * @param validatedParsedRoute entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedRoute, final List<Notice> noticeCollection) {

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

        final var route = builder.build(noticeCollection);

        if (route.isSuccess()) {
            if (gtfsDataRepository.addRoute((Route) route.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("routes.txt",
                        "route_id", validatedParsedRoute.getEntityId()));
            }
        } else {
            //noinspection unchecked
            ((List<Notice>) route.getData()).forEach(resultRepository::addNotice);
        }
    }
}