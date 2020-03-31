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
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationTableCompositeKey;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationTableSimpleKey;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationTableSingleRow;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IncoherentValuesForFields;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Arrays;

/**
 * This use case turns a parsed entity into a concrete class depending on the 'table_name', and 'record_id' fields
 */
@SuppressWarnings("ConstantConditions")
public class ProcessParsedTranslation {

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;

    public ProcessParsedTranslation(final GtfsSpecRepository specRepo,
                                    final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method to go from a row from translations.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@link ParsedEntity} and creates a
     * {@link org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationTableBase} object. If the
     * required conditions to create such object are not met, the execution is aborted and a
     * {@link IncoherentValuesForFields} or {@link MissingRequiredValueNotice} is created and added to the validation
     * result repository provided in the use case constructor.
     *
     * @param validatedTranslationEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedTranslationEntity) {

        String tableName = (String) validatedTranslationEntity.get("table_name");
        String fieldName = (String) validatedTranslationEntity.get("field_name");
        String language = (String) validatedTranslationEntity.get("language");
        String translation = (String) validatedTranslationEntity.get("translation");
        String recordId = (String) validatedTranslationEntity.get("record_id");
        String recordSubId = (String) validatedTranslationEntity.get("record_sub_id");
        String fieldValue = (String) validatedTranslationEntity.get("field_value");

        //noinspection SuspiciousMethodCalls
        if (!Arrays.asList(TableName.values()).contains(tableName)) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,", "table_name",
                    validatedTranslationEntity.getEntityId()));
            return;
        }
        if (fieldName.equals(null)) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "field_name", validatedTranslationEntity.getEntityId()));
            return;
        }
        if (language.equals(null)) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "language", validatedTranslationEntity.getEntityId()));
            return;
        }
        if (translation.equals(null)) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "translation", validatedTranslationEntity.getEntityId()));
            return;
        }

        if (tableName.equals("feed_info")) {
            TranslationTableSingleRow.TranslationTableSingleRowBuilder builder;

            builder = new TranslationTableSingleRow.TranslationTableSingleRowBuilder(tableName, fieldName, language,
                    translation);

            TranslationTableSingleRow translationTableSingleRow = builder.build();

        } else if (tableName.equals("stop_times") && recordId.equals(null)) {
            TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder builder;

            try {
                builder = new TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder(tableName, fieldName,
                        language, translation, recordId, recordSubId);
                TranslationTableCompositeKey translationTableCompositeKey = builder.build();

            } catch (IllegalArgumentException e) {
                if (recordSubId.equals(null)) {
                    resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt",
                            "record_sub_id", validatedTranslationEntity.getEntityId()));
                }
                if (recordId.equals(null)) {
                    resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt",
                            "record_id", validatedTranslationEntity.getEntityId()));
                }
            }

        } else {
            try {
                TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder builder;
                builder = new TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder(tableName, fieldName, language,
                        translation);
                builder.recordId(recordId)
                        .recordSubId(recordSubId)
                        .fieldValue(fieldValue);
                TranslationTableSimpleKey translationTableSimpleKey = builder.build();

            } catch (IllegalArgumentException e) {
                if (!fieldValue.equals(null)) {
                    if (!recordId.equals(null)) {
                        resultRepo.addNotice(new IncoherentValuesForFields("translations.txt",
                                "field_value", "record_id",
                                validatedTranslationEntity.getEntityId()));
                        return;
                    }
                    if (!recordSubId.equals(null)) {
                        resultRepo.addNotice(new IncoherentValuesForFields("translations.txt",
                                "field_value", "record_sub_id",
                                validatedTranslationEntity.getEntityId()));
                        return;
                    }
                }
                if (!recordId.equals(null) && !fieldValue.equals(null)) {
                    resultRepo.addNotice(new IncoherentValuesForFields("translations.txt",
                            "field_value", "record_id",
                            validatedTranslationEntity.getEntityId()));
                }
            }
        }
    }
}