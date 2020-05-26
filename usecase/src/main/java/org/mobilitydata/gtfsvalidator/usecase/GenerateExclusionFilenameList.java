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

import org.mobilitydata.gtfsvalidator.domain.entity.schema.GtfsNode;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.util.*;

/**
 * Use case to create list of filename on which the GTFS validation process should not be applied to
 */
public class GenerateExclusionFilenameList {
    private final GtfsSpecRepository gtfsSpecRepo;
    private final ExecParamRepository execParamRepo;

    public GenerateExclusionFilenameList(final GtfsSpecRepository gtfsSpecRepo,
                                         final ExecParamRepository execParamRepo) {
        this.gtfsSpecRepo = gtfsSpecRepo;
        this.execParamRepo = execParamRepo;
    }

    /**
     * Use case execution method: returns the list of filename on which the GTFS validation should not be
     * applied to.
     */
    public ArrayList<String> execute() {
        final List<String> toExcludeFromValidation =
                Arrays.asList(execParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY)
                        .replace("[", "")
                        .replace("]", "")
                        .split(","));

        final List<String> gtfsFilenameList = new ArrayList<>();
        gtfsFilenameList.addAll(gtfsSpecRepo.getRequiredFilenameList());
        gtfsFilenameList.addAll(gtfsSpecRepo.getOptionalFilenameList());

        if (!gtfsFilenameList.containsAll(toExcludeFromValidation)) {
            throw new IllegalArgumentException("Some file requested to be excluded is not defined by the official " +
                    "GTFS specification: " + toExcludeFromValidation);
        }
        final GtfsNode root = gtfsSpecRepo.getGtfsRelationshipDescriptor();

        final Set<String> toReturn = new HashSet<>();
        // search for all reachable GtfsNode from each GtfsNode to exclude from validation
        for (String filename : toExcludeFromValidation) {
            toReturn.addAll(root.getChildByName(filename).DFS(new HashSet<>()));
        }
        return new ArrayList<>(toReturn);
    }
}