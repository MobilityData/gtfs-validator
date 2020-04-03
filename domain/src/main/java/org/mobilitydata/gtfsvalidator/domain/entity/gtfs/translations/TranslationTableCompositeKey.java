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
 * Model class for an entity defined in translations.txt with table_name = stop_times and record_id defined
 */
public class TranslationTableCompositeKey extends TranslationTableBase {

//    @NotNull
//    final private String recordId;
//
//    @NotNull
//    final private String recordSubId;
//
//    @Nullable
//    final private String fieldValue;

    /**
     * @param tableName   defines the table that contains the field to be translated
     * @param fieldName   name of the field to be translated
     * @param language    language of translation
     * @param translation translated value
     * @param recordId    defines the record that corresponds to the field to be translated
     * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have
     *                    a unique ID
     */
    private TranslationTableCompositeKey(@NotNull final TableName tableName,
                                         @NotNull final String fieldName,
                                         @NotNull final String language,
                                         @NotNull final String translation,
                                         @NotNull final String recordId,
                                         @NotNull final String recordSubId,
                                         @Nullable final String fieldValue) {
        super(tableName, fieldName, language, translation, recordId, recordSubId, fieldValue);
    }


    /**
     * Builder class to create {@link TranslationTableCompositeKey} objects.  Allows an unordered
     * definition of the different attributes of {@link TranslationTableCompositeKey}.
     */
    public static class TranslationTableCompositeKeyBuilder extends TableNameBaseBuilder {

        /**
         * Returns a {@link TranslationTableCompositeKey} object from fields provided via
         * {@link TranslationTableCompositeKeyBuilder} methods. Throws {@link NullPointerException} if fields
         * fieldName, language, translation, recordId, or recordSubId are null; or if field fieldValue is not null
         *
         * @return Entity representing a row from translations.txt with table_name = stop_times and record_id defined
         * @throws NullPointerException if fields fieldName, language, translation, recordId, or recordSubId are
         *                              null; or if field fieldValue is not null
         */
        public TranslationTableCompositeKey build() throws NullPointerException {
            if (fieldName == null) {
                throw new NullPointerException("field_name must be specified");
            } else if (language == null) {
                throw new NullPointerException("language must be specified");
            } else if (translation == null) {
                throw new NullPointerException("translation must be specified");
            } else if (recordSubId == null) {
                throw new NullPointerException("record_sub_id must be specified");
            } else if (recordId == null) {
                throw new NullPointerException("record_id must be specified");
            }
            if (fieldValue != null) {
                throw new NullPointerException("field_value must be null");
            }
            return new TranslationTableCompositeKey(tableName, fieldName, language, translation, recordId, recordSubId,
                    fieldValue);
        }
    }
}