/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousRouteSortOrderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to semantically validate the value of field route_sort_order in routes.txt
 */
public class ValidateRouteSortSemanticValue {
    private final ExecParamRepository execParamRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final ValidationResultRepository validationResultRepository;

    public ValidateRouteSortSemanticValue(final ExecParamRepository execParamRepository,
                                          final GtfsDataRepository gtfsDataRepository,
                                          final ValidationResultRepository validationResultRepository) {
        this.execParamRepository = execParamRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.validationResultRepository = validationResultRepository;
    }

    /**
     * Use case execution method. Verifies if the value of field route_sort_order is between the numeric bounds
     * corresponding to this field.
     */
    public void execute() {
        final Collection<Route> routePerId = gtfsDataRepository.getRouteAll();
        final int routeSortOrderLowerBound = Integer.parseInt(execParamRepository
                .getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY));
        final int routeSortOrderUpperBound = Integer.parseInt(execParamRepository
                .getExecParamValue(ExecParamRepository.ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY));
        routePerId.forEach(route -> {
            final Integer routeSortOrder = route.getRouteSortOrder();
            if (routeSortOrder != null) {
                if (routeSortOrder < routeSortOrderLowerBound || routeSortOrder > routeSortOrderUpperBound) {
                    validationResultRepository.addNotice(new SuspiciousRouteSortOrderNotice("routes.txt",
                            "route_sort_order", route.getRouteId(), routeSortOrderLowerBound,
                            routeSortOrderUpperBound, routeSortOrder));
                }
            }
        });
    }
}
