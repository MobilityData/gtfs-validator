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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.sql.SQLIntegrityConstraintViolationException;

public class ProcessParsedLevel {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Level.LevelBuilder builder;

    public ProcessParsedLevel(final ValidationResultRepository resultRepository,
                              final GtfsDataRepository gtfsDataRepository,
                              final Level.LevelBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    public void execute(final ParsedEntity validatedParsedLevel) throws IllegalArgumentException,
            SQLIntegrityConstraintViolationException {
        String levelId = (String) validatedParsedLevel.get("level_id");
        Float levelIndex = (Float) validatedParsedLevel.get("level_index");
        String levelName = (String) validatedParsedLevel.get("level_name");

        try {
            builder.levelId(levelId)
                    .levelIndex(levelIndex)
                    .levelName(levelName);

            gtfsDataRepository.addLevel(builder.build());

        } catch (IllegalArgumentException e) {

            if (levelId == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("levels.txt", "level_id",
                        validatedParsedLevel.getEntityId()));
            }
            if (levelIndex == null) {
                resultRepository.addNotice(new MissingRequiredValueNotice("levels.txt", "level_index",
                        validatedParsedLevel.getEntityId()));
            }
            throw e;
        } catch (SQLIntegrityConstraintViolationException e) {
            resultRepository.addNotice(new EntityMustBeUniqueNotice("levels.txt", "level_id",
                    validatedParsedLevel.getEntityId()));
            throw e;
        }
    }
}