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

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor.RelationshipDescriptor;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Use case to create list of filename on which the GTFS validation process should not be applied to
 */
public class GenerateExclusionFilenameList {
    private final GtfsSpecRepository gtfsSpecRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    public GenerateExclusionFilenameList(final GtfsSpecRepository gtfsSpecRepo,
                                         final ExecParamRepository execParamRepo,
                                         final Logger logger) {
        this.gtfsSpecRepo = gtfsSpecRepo;
        this.execParamRepo = execParamRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: returns the list of filename on which the GTFS validation should not be
     * applied to.
     */
    public ArrayList<String> execute() {
        final String rawFilenameListToExcludeAsString =
                execParamRepo.getExecParamValue(ExecParamRepository.EXCLUSION_KEY);
        final ArrayList<String> toExcludeFromValidation =
                rawFilenameListToExcludeAsString != null ?
                new ArrayList<>(List.of(rawFilenameListToExcludeAsString
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")))
                : null;

        final List<String> gtfsFilenameList = new ArrayList<>();
        gtfsFilenameList.addAll(gtfsSpecRepo.getRequiredFilenameList());
        gtfsFilenameList.addAll(gtfsSpecRepo.getOptionalFilenameList());

        if(toExcludeFromValidation == null){
            logger.info("No file to exclude -- will execute validation process on all files");
            return new ArrayList<>();
        }
        else if (!gtfsFilenameList.containsAll(toExcludeFromValidation)) {
            logger.info("Some file requested to be excluded is not defined by the official GTFS specification: "
                    + toExcludeFromValidation + " -- will execute validation process on all files");
            toExcludeFromValidation.clear();
        }
        final RelationshipDescriptor root = gtfsSpecRepo.getGtfsRelationshipDescriptor();

        final Set<String> toReturn = new HashSet<>();
        // search for all reachable GtfsNode from each GtfsNode to exclude from validation
        for (String filename : toExcludeFromValidation) {
            toReturn.addAll(root.getChildByName(filename).DFS(new HashSet<>()));
        }
        return new ArrayList<>(toReturn);
    }
}