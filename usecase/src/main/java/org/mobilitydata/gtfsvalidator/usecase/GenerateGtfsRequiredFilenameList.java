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

import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.util.List;

/**
 * Use case to retrieve list of filenames marked as required by the GTFS specification.
 */
public class GenerateGtfsRequiredFilenameList {
    private final GtfsSpecRepository gtfsSpecRepo;

    public GenerateGtfsRequiredFilenameList(final GtfsSpecRepository gtfsSpecRepo) {
        this.gtfsSpecRepo = gtfsSpecRepo;
    }

    /**
     * Use case execution method: returns a list of filenames marked as required by the GTFS specification.
     */
    public List<String> execute() {
        return gtfsSpecRepo.getRequiredFilenameList();
    }
}
