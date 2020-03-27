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
import org.mobilitydata.gtfsvalidator.domain.entity.translations.DefinedFieldValueNonFeedInfoTranslation;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.DefinedRecordIdStopTimeTranslation;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.FeedInfoTranslation;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.UndefinedFieldValueNonFeedIntoTranslation;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Objects;
import java.util.stream.Stream;

/*
 * This use case turns a parsed entity to a concrete class depending on the 'table_name', and 'record_id' fields
 */
public class ProcessParsedTranslation {

    /**
     * This enum matches types that can be found in the table_name field of translations.txt
     * // see https://gtfs.org/reference/static#translationstxt
     * It's used to decide which concrete type derived from {@link org.mobilitydata.gtfsvalidator.domain.entity.translations.TableNameBase} to instantiate
     */
    private enum TableName {
        AGENCY("agency"),
        STOPS("stops"),
        ROUTES("routes"),
        TRIPS("trips"),
        STOP_TIMES("stop_times"),
        LEVELS("levels"),
        FEED_INFO("feed_info"),
        PATHWAYS("pathways"),
        FARE_RULES("fare_rules"),
        FARE_ATTRIBUTES("fare_attributes"),
        ATTRIBUTIONS("attributions");

        private String value;

        TableName(final String value) {
            this.value = value;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        static public TableName fromString(final String tableName) {
            return Stream.of(TableName.values())
                    .filter(enumItem -> Objects.equals(enumItem.value, tableName))
                    .findAny()
                    .get(); // todo: implement solution to handle unexpected enum values
        }
    }

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;

    public ProcessParsedTranslation(final GtfsSpecRepository specRepo,
                                    final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
    }

    public void execute(final ParsedEntity validatedTranslationEntity) {

        String tableName = (String) validatedTranslationEntity.get("table_name");
        String fieldName = (String) validatedTranslationEntity.get("field_name");
        String language = (String) validatedTranslationEntity.get("language");
        String translation = (String) validatedTranslationEntity.get("translation");
        String recordId = (String) validatedTranslationEntity.get("record_id");
        String recordSubId = (String) validatedTranslationEntity.get("record_sub_id");
        String fieldValue = (String) validatedTranslationEntity.get("field_value");

        switch (TableName.fromString(tableName)) {

            case FEED_INFO: {
                FeedInfoTranslation.FeedInfoTranslationBuilder builder =
                        new FeedInfoTranslation.FeedInfoTranslationBuilder(fieldName, language, translation);

                FeedInfoTranslation feedInfoTranslation = builder.build(); // todo: add to GtfsDataRepository
                break;
            }
            case STOP_TIMES: {

                if (recordId != null) {
                    DefinedRecordIdStopTimeTranslation.DefinedRecordIdStopTimeTranslationBuilder builder =
                            new DefinedRecordIdStopTimeTranslation.DefinedRecordIdStopTimeTranslationBuilder(fieldName,
                                    language, translation, recordId, recordSubId);
                    // todo: add to GtfsDataRepository
                    DefinedRecordIdStopTimeTranslation definedRecordIdStopTimeTranslation = builder.build();
                } else {
                    DefinedFieldValueNonFeedInfoTranslation.DefinedFieldValueNonFeedInfoTranslationBuilder builder =
                            new DefinedFieldValueNonFeedInfoTranslation.DefinedFieldValueNonFeedInfoTranslationBuilder(
                                    fieldName, language, translation, fieldValue);
                }

                break;
            }
            case AGENCY:

            case ROUTES:

            case TRIPS:

            case LEVELS:

            case PATHWAYS:

            case ATTRIBUTIONS:

            case FARE_RULES:

            case FARE_ATTRIBUTES:

            case STOPS: {

                if (recordId != null) {
                    UndefinedFieldValueNonFeedIntoTranslation.UndefinedFieldValueNonFeedInfoTranslationBuilder builder =
                            new UndefinedFieldValueNonFeedIntoTranslation.UndefinedFieldValueNonFeedInfoTranslationBuilder(fieldName, language, translation, recordId);
                    // todo: add to GtfsDataRepository
                    UndefinedFieldValueNonFeedIntoTranslation undefinedRecordIdStopTimeTranslation = builder.build();
                } else {
                    DefinedFieldValueNonFeedInfoTranslation.DefinedFieldValueNonFeedInfoTranslationBuilder builder =
                            new DefinedFieldValueNonFeedInfoTranslation.DefinedFieldValueNonFeedInfoTranslationBuilder(
                                    fieldName, language, translation, fieldValue);
                }
                break;

            }
        }
    }
}