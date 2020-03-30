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
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationDeepness00;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationDeepness01;
import org.mobilitydata.gtfsvalidator.domain.entity.translations.TranslationDeepness02;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/*
 * This use case turns a parsed entity to a concrete class depending on the 'table_name', and 'record_id' fields
 */
public class ProcessParsedTranslation {

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

        if (tableName.equals("feed_info")) {
            TranslationDeepness00.TranslationDeepness00Builder builder =
                    new TranslationDeepness00.TranslationDeepness00Builder(tableName, fieldName, language, translation);

            TranslationDeepness00 translationDeepness00 = builder.build();
        } else if (tableName.equals("stop_times") && recordId.equals("")) {
            TranslationDeepness02.TranslationDeepness02Builder builder =
                    new TranslationDeepness02.TranslationDeepness02Builder(tableName, fieldName, language, translation,
                            recordId, recordSubId);

            TranslationDeepness02 translationDeepness02 = builder.build();
        } else {
            TranslationDeepness01.TranslationDeepness01Builder builder =
                    new TranslationDeepness01.TranslationDeepness01Builder(tableName, fieldName, language, translation);
            builder.recordId(recordId)
                    .recordSubId(recordSubId)
                    .fieldValue(fieldValue);

            TranslationDeepness01 translationDeepness01 = builder.build();
        }
    }
}