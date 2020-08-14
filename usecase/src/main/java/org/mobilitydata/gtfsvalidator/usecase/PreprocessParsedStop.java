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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Set;

/**
 * This use case checks basic validity of rows from stops.txt
 * Further processing of stops.txt related entities is done in {@link ProcessParsedStopAll}
 **/
public class PreprocessParsedStop {

    private final ValidationResultRepository resultRepository;

    public PreprocessParsedStop(final ValidationResultRepository resultRepo) {
        this.resultRepository = resultRepo;
    }

    /**
     * Use case execution method to check a stop row basic validity
     * <p>
     * This use case returns null if the passed entity has a null id or if the id is found in existingIdSet.
     * Otherwise it returns the entity.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * trip entities is not respected.
     *
     * @param parsedEntity  entity to be checked for basic validity
     * @param existingIdSet a set of ids against which the non null entity id is checked
     * @return null if the passed entity doesn't have an id or if its id was found in existingIdSet.
     * Otherwise, the entity itself
     */
    public ParsedEntity execute(final ParsedEntity parsedEntity, final Set<String> existingIdSet) {
        final String stopId = parsedEntity.getEntityId();

        if (stopId == null) {
            resultRepository.addNotice(
                    new MissingRequiredValueNotice("stops.txt", "stop_id", null)
            );
            return null;
        }

        if (existingIdSet.contains(stopId)) {
            resultRepository.addNotice(new DuplicatedEntityNotice("stops.txt",
                    "stop_id", stopId));
            return null;
        }
        return parsedEntity;
    }
}
