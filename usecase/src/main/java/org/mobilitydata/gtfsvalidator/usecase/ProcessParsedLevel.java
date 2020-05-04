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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Level;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity representing a row from levels.txt into a concrete class
 */
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

    /**
     * Use case execution method to go from a row from levels.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Level} object if the requirements
     * from the official GTFS specification are met. When these requirements are mot met, related notices generated in
     * {@code Level.LevelBuilder} are added to the result repository provided to the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * route entities is not respected.
     *
     * @param validatedParsedLevel entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedParsedLevel) {
        final String levelId = (String) validatedParsedLevel.get("level_id");
        final Float levelIndex = (Float) validatedParsedLevel.get("level_index");
        final String levelName = (String) validatedParsedLevel.get("level_name");

        builder.levelId(levelId)
                .levelIndex(levelIndex)
                .levelName(levelName);
        @SuppressWarnings("rawtypes") final EntityBuildResult level = builder.build();

        if (level.isSuccess()) {
            if (gtfsDataRepository.addLevel((Level) level.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("levels.txt",
                        "level_id", validatedParsedLevel.getEntityId()));
            }
        } else {
            //noinspection unchecked
            ((List<Notice>) level.getData()).forEach(resultRepository::addNotice);
        }
    }
}