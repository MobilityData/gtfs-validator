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

/*
 * This use case returns null is the passed entity has a null id, or the entity otherwise
 * Further processing of stops.txt related entities is required to validate
 * parent stations <-> child stops relationships. See ProcessParsedStopAll
 */
public class PreprocessParsedStop {

    private final ValidationResultRepository resultRepository;

    public PreprocessParsedStop(final ValidationResultRepository resultRepo) {
        this.resultRepository = resultRepo;
    }


    public ParsedEntity execute(final ParsedEntity parsedEntity, final Set<String> existingKeySet) {
        String stopId = parsedEntity.getEntityId();

        if (stopId == null) {
            resultRepository.addNotice(
                    new MissingRequiredValueNotice("stops.txt", "stop_id", null)
            );
            return null;
        }

        if (existingKeySet.contains(stopId)) {
            resultRepository.addNotice(new DuplicatedEntityNotice("stops.txt",
                    "stop_id", stopId));
            return null;
        }

        return parsedEntity;
    }
}
