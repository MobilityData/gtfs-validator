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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

/**
 * This use case turns a parsed entity into a concrete class depending on the 'table_name', and 'record_id' fields
 */
public class ProcessParsedTranslation {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final Translation.TranslationBuilder builder;

    public ProcessParsedTranslation(final ValidationResultRepository resultRepository,
                                    final GtfsDataRepository gtfsDataRepository,
                                    final Translation.TranslationBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from translations.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code Translation} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code Translation.TranslationBuilder} are added to the result repository provided in the
     * constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * translation entities is not respected.
     *
     * @param validatedTranslation  entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedTranslation) throws IllegalArgumentException {
        final String tableName = (String) validatedTranslation.get("table_name");
        final String fieldName = (String) validatedTranslation.get("field_name");
        final String language = (String) validatedTranslation.get("language");
        final String translation = (String) validatedTranslation.get("translation");
        final String recordId = (String) validatedTranslation.get("record_id");
        final String recordSubId = (String) validatedTranslation.get("record_sub_id");
        final String fieldValue = (String) validatedTranslation.get("field_value");

        builder.tableName(tableName)
                .fieldName(fieldName)
                .language(language)
                .translation(translation)
                .recordId(recordId)
                .recordSubId(recordSubId)
                .fieldValue(fieldValue);

        final EntityBuildResult<?> translationEntity = builder.build();

        if (translationEntity.isSuccess()) {
            if (gtfsDataRepository.addTranslation((Translation) translationEntity.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("translations.txt",
                        null, validatedTranslation.getEntityId(),
                        "table_name",
                        "field_name", "language",
                        "translation", tableName, fieldName, language, translation));
            }
        } else {
            // at this step it is certain that calling getData method will return a list of notices, therefore there is
            // no need for cast check
            //noinspection unchecked
            ((List<Notice>) translationEntity.getData()).forEach(resultRepository::addNotice);
        }
    }
}
