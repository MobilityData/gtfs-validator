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
 * Model class for an entity defined in translations.txt with table_name!=stop_times or table_name!=feed_info
 */
public class TranslationTableSimpleKey extends TranslationTableBase {

    /**
     * @param tableName   defines the table that contains the field to be translated
     * @param fieldName   name of the field to be translated
     * @param language    language of translation
     * @param translation translated value
     * @param recordId    defines the record that corresponds to the field to be translated
     * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have a
     *                    unique ID
     */
    private TranslationTableSimpleKey(@NotNull final TableName tableName,
                                      @NotNull final String fieldName,
                                      @NotNull final String language,
                                      @NotNull final String translation,
                                      @Nullable final String recordId,
                                      @Nullable final String recordSubId,
                                      @Nullable final String fieldValue) {
        super(tableName, fieldName, language, translation, recordId, recordSubId, fieldValue);
    }

    /**
     * Builder class to create {@link TranslationTableSimpleKey} objects.
     */
    public static class TranslationTableSimpleKeyBuilder extends TableNameBaseBuilder {

        /**
         * Returns a {@link TranslationTableSimpleKey} objects from fields provided via
         * {@link TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder} methods.
         * Throws {@link IllegalArgumentException} if fields fieldName, language, translation, recordId to not meet the
         * requirements from the specification:
         * - recordId and fieldValue can not be defined at the same time
         * - recordSubId and fieldValue can not be defined at the same time
         * - recordId and fieldValue can not be undefined at the same time
         * - fieldName, language, and translation fields can not be null
         *
         * @return Entity representing a row from translations.txt with table_name!=stop_times or table_name!=feed_info
         * @throws IllegalArgumentException if fields fieldName, language, translation, recordId and recordSubId do
         *                                  not meet the requirements from the specification:
         *                                  - recordId and fieldValue can not be defined at the same time
         *                                  - recordSubId and fieldValue can not be defined at the same time
         *                                  - recordId and fieldValue can not be undefined at the same time
         *                                  - fieldName, language, and translation fields can not be null
         */
        public TranslationTableSimpleKey build() throws IllegalArgumentException {
            if (tableName == null) {
                throw new IllegalArgumentException("table_name is undefined: either null or an unexpected enum value" +
                        " has been encountered");
            }
            if (fieldValue != null) {
                if (recordId != null) {
                    throw new IllegalArgumentException("record_id and field_value can not both be defined");
                }
                if (recordSubId != null) {
                    throw new IllegalArgumentException("record_sub_id and field_value can not both be defined");
                }
            }

            if (recordId == null && fieldValue == null) {
                throw new IllegalArgumentException("record_id and field_value can not both be undefined");
            }

            if (fieldName == null) {
                throw new IllegalArgumentException("field_name can not be null");
            }

            if (language == null) {
                throw new IllegalArgumentException("language can not be null");
            }

            if (translation == null) {
                throw new IllegalArgumentException("translation can not be null");
            }

            return new TranslationTableSimpleKey(tableName, fieldName, language, translation, recordId, recordSubId,
                    fieldValue);
        }
    }
}