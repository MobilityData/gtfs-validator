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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Model class for an entity defined in translations.txt with table_name = feed_info
 */
public class TranslationTableSingleRow extends TranslationTableBase {

    /**
     * @param tableName   defines the table that contains the field to be translated
     * @param fieldName   name of the field to be translated
     * @param language    language of translation
     * @param translation translated value
     */
    private TranslationTableSingleRow(@NotNull TableName tableName,
                                      @NotNull String fieldName,
                                      @NotNull String language,
                                      @NotNull String translation,
                                      @Nullable String recordId,
                                      @Nullable String recordSubId,
                                      @Nullable String fieldValue) {
        super(tableName, fieldName, language, translation, recordId, recordSubId, fieldValue);
    }

    /**
     * Builder class to create {@link TranslationTableSingleRow} objects.  Allows an unordered
     * definition of the different attributes of {@link TranslationTableSingleRow}.
     */
    public static class TranslationTableSingleRowBuilder extends TableNameBaseBuilder {

        /**
         * Returns a {@link TranslationTableSingleRow} object from fields provided via
         * {@link TranslationTableSingleRowBuilder} methods. Throws {@link IllegalArgumentException} if fields
         * fieldName, fieldName, language or translation are null, or if fields record_id, record_sub_id, field_value
         * are not null
         *
         * @return Entity representing a row from translations.txt with table_name = feed_info
         * @throws IllegalArgumentException if fields fieldName, fieldName, language or translation are null or if fields
         *                                  record_id, record_sub_id, field_value are not null
         */
        public TranslationTableSingleRow build() throws IllegalArgumentException {
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldName can not be null");
            }
            if (language == null) {
                throw new IllegalArgumentException("language can not be null");
            }
            if (translation == null) {
                throw new IllegalArgumentException("translation can not be null");
            }
            if (recordId != null) {
                throw new IllegalArgumentException("record_id can not be defined");
            }
            if (recordSubId != null) {
                throw new IllegalArgumentException("record_sub_id can not be defined");
            }
            if (fieldValue != null) {
                throw new IllegalArgumentException("field_value can not be defined");
            }
            return new TranslationTableSingleRow(tableName, fieldName, language, translation, recordId, recordSubId,
                    fieldValue);
        }
    }
}