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

/**
 * Use case to load into memory GTFS entities. This use case is called after parsing a row from a GTFS .txt file.
 */
public class LoadAllEntityIntoMemory {
    private final ProcessParsedAgency processParsedAgency;
    private final ProcessParsedRoute processParsedRoute;

    public LoadAllEntityIntoMemory(final ProcessParsedAgency processParsedAgency,
                                   final ProcessParsedRoute processParsedRoute) {
        this.processParsedAgency = processParsedAgency;
        this.processParsedRoute = processParsedRoute;
    }

    /**
     * Use case execution method: calls to other use cases to transform a {@code ParsedEntity} into an internal
     * representation.
     *
     * @param parsedEntity entity whose internal representation will be added to the repository that internally
     *                     stores GTFS data
     */
    public void execute(final ParsedEntity parsedEntity) {
        switch (parsedEntity.getRawFileInfo().getFilename()) {
            case "agency.txt": {
                processParsedAgency.execute(parsedEntity);
                break;
            }
            case "routes.txt": {
                processParsedRoute.execute(parsedEntity);
                break;
            }
        }
    }
}
