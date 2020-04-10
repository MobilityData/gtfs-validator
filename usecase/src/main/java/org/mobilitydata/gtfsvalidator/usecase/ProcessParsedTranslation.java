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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableCompositeKey;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableSimpleKey;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableSingleRow;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IncoherentValuesForFieldsNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedDefinedFieldNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * This use case turns a parsed entity into a concrete class depending on the 'table_name', and 'record_id' fields
 */
@SuppressWarnings("ConstantConditions")
public class ProcessParsedTranslation {

    private final TranslationTableSingleRow.TranslationTableSingleRowBuilder singleRowTranslationBuilder;
    private final TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder simpleKeyTranslationBuilder;
    private final TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder compositeKeyTranslationBuilder;
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository gtfsDataRepo;

    public ProcessParsedTranslation(final TranslationTableSingleRow.TranslationTableSingleRowBuilder singleRowTranslationBuilder,
                                    final TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder simpleKeyTranslationBuilder,
                                    final TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder compositeKeyTranslationBuilder,
                                    final ValidationResultRepository resultRepo,
                                    final GtfsDataRepository gtfsDataRepo) {
        this.singleRowTranslationBuilder = singleRowTranslationBuilder;
        this.simpleKeyTranslationBuilder = simpleKeyTranslationBuilder;
        this.compositeKeyTranslationBuilder = compositeKeyTranslationBuilder;

        this.resultRepo = resultRepo;
        this.gtfsDataRepo = gtfsDataRepo;
    }

    /**
     * Use case execution method to go from a row from translations.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@link ParsedEntity} and creates a
     * {@link org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableBase} object. If the
     * required conditions to create such object are not met, the execution is aborted and a
     * {@link IncoherentValuesForFieldsNotice} or {@link MissingRequiredValueNotice} is created and added to the
     * validation result repository provided in the use case constructor.
     *
     * @param validatedTranslationEntity entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedTranslationEntity) throws IllegalArgumentException {

        final String tableName = (String) validatedTranslationEntity.get("table_name");
        final String fieldName = (String) validatedTranslationEntity.get("field_name");
        final String language = (String) validatedTranslationEntity.get("language");
        final String translation = (String) validatedTranslationEntity.get("translation");
        final String recordId = (String) validatedTranslationEntity.get("record_id");
        final String recordSubId = (String) validatedTranslationEntity.get("record_sub_id");
        final String fieldValue = (String) validatedTranslationEntity.get("field_value");

        try {
            TableName.fromString(tableName);
        } catch (IllegalArgumentException e) {
            resultRepo.addNotice(new UnexpectedDefinedFieldNotice("translations.txt,", "table_name",
                    tableName, validatedTranslationEntity.getEntityId()));
            throw e;
        }

        if (tableName.equals("feed_info")) {

            try {
                singleRowTranslationBuilder.tableName(tableName)
                        .fieldName(fieldName)
                        .language(language)
                        .translation(translation)
                        .recordId(recordId)
                        .recordSubId(recordSubId)
                        .fieldValue(fieldValue);

                TranslationTableSingleRow translationTableSingleRow = singleRowTranslationBuilder.build();
                gtfsDataRepo.addEntity(translationTableSingleRow);

            } catch (IllegalArgumentException e) {

                checkRequiredFields(validatedTranslationEntity, fieldName, language, translation);
                if (fieldValue != null) {
                    resultRepo.addNotice(new UnexpectedDefinedFieldNotice("translations.txt",
                            "field_value", fieldValue, validatedTranslationEntity.getEntityId()));
                } else if (recordId != null) {
                    resultRepo.addNotice(new UnexpectedDefinedFieldNotice("translations.txt",
                            "record_id_value", recordId, validatedTranslationEntity.getEntityId()));
                } else if (recordSubId != null) {
                    resultRepo.addNotice(new UnexpectedDefinedFieldNotice("translations.txt",
                            "record_sub_id", recordSubId, validatedTranslationEntity.getEntityId()));
                }

                throw e;
            }

        } else if (tableName.equals("stop_times") & !recordId.equals(null)) {

            try {
                compositeKeyTranslationBuilder.tableName(tableName)
                        .fieldName(fieldName)
                        .language(language)
                        .translation(translation)
                        .recordId(recordId)
                        .recordSubId(recordSubId)
                        .fieldValue(fieldValue);

                TranslationTableCompositeKey translationTableCompositeKey = compositeKeyTranslationBuilder.build();
                gtfsDataRepo.addEntity(translationTableCompositeKey);

            } catch (IllegalArgumentException e) {

                checkRequiredFields(validatedTranslationEntity, fieldName, language, translation);

                if (recordSubId == null) {
                    resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt",
                            "record_sub_id", validatedTranslationEntity.getEntityId()));
                }
                if (recordId == null) {
                    resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt",
                            "record_id", validatedTranslationEntity.getEntityId()));
                }
                if (fieldValue != null) {

                    resultRepo.addNotice(new UnexpectedDefinedFieldNotice("translations.txt",
                            "field_value", fieldValue, validatedTranslationEntity.getEntityId()));
                }
                throw e;
            }

        } else {
            try {
                simpleKeyTranslationBuilder.tableName(tableName)
                        .fieldName(fieldName)
                        .language(language)
                        .translation(translation)
                        .recordId(recordId)
                        .recordSubId(recordSubId)
                        .fieldValue(fieldValue);

                TranslationTableSimpleKey translationTableSimpleKey = simpleKeyTranslationBuilder.build();
                gtfsDataRepo.addEntity(translationTableSimpleKey);

            } catch (IllegalArgumentException e) {

                checkRequiredFields(validatedTranslationEntity, fieldName, language, translation);

                if (recordId == null && fieldValue == null) {
                    resultRepo.addNotice(new IncoherentValuesForFieldsNotice("translations.txt",
                            "record_id", "field_value",
                            validatedTranslationEntity.getEntityId()));
                }

                if (fieldValue != null) {
                    if (recordId != null) {
                        resultRepo.addNotice(new IncoherentValuesForFieldsNotice("translations.txt",
                                "record_id", "field_value",
                                validatedTranslationEntity.getEntityId()));
                    }
                    if (recordSubId != null) {
                        resultRepo.addNotice(new IncoherentValuesForFieldsNotice("translations.txt",
                                "record_sub_id", "field_value",
                                validatedTranslationEntity.getEntityId()));
                    }
                }
            }
        }
    }

    private void checkRequiredFields(ParsedEntity validatedTranslationEntity, String fieldName, String language,
                                     String translation) {
        if (fieldName == null) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "field_name", validatedTranslationEntity.getEntityId()));
        } else if (language == null) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "language", validatedTranslationEntity.getEntityId()));
        } else if (translation == null) {
            resultRepo.addNotice(new MissingRequiredValueNotice("translations.txt,",
                    "translation", validatedTranslationEntity.getEntityId()));
        }
    }
}