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

package org.mobilitydata.gtfsvalidator.domain.entity.translations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Model class for an entity defined in translations.txt with table_name!=stop_times or table_name!=feed_info
 */
public class TranslationDeepness01 extends TranslationBase {

    @Nullable
    final private String recordId;

    @Nullable
    final private String recordSubId;

    @Nullable
    final private String fieldValue;

    /**
     * @param tableName   defines the table that contains the field to be translated
     * @param fieldName   name of the field to be translated
     * @param language    language of translation
     * @param translation translated value
     * @param recordId    defines the record that corresponds to the field to be translated
     * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have a
     *                    unique ID
     */
    private TranslationDeepness01(@NotNull final TableName tableName,
                                  @NotNull final String fieldName,
                                  @NotNull final String language,
                                  @NotNull final String translation,
                                  @Nullable final String recordId,
                                  @Nullable final String recordSubId,
                                  @Nullable final String fieldValue) {
        super(tableName, fieldName, language, translation);
        this.recordId = recordId;
        this.recordSubId = recordSubId;
        this.fieldValue = fieldValue;
    }

    @Nullable
    public String getRecordId() {
        return recordId;
    }

    @Nullable
    public String getRecordSubId() {
        return recordSubId;
    }

    @Nullable
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Builder class to create {@link TranslationDeepness01} objects.
     */
    public static class TranslationDeepness01Builder extends TableNameBaseBuilder {

        @Nullable
        private String recordId;

        @Nullable
        private String recordSubId;

        @Nullable
        private String fieldValue;

        /**
         * Builder class to create {@link TranslationDeepness01} objects. Allows an unordered definition of the
         * different attributes of {@link TranslationDeepness01}.
         *
         * @param tableName   defines the table that contains the field to be translated
         * @param fieldName   name of the field to be translated
         * @param language    language of translation
         * @param translation translated value
         */
        public TranslationDeepness01Builder(@NotNull final String tableName,
                                            @NotNull final String fieldName,
                                            @NotNull final String language,
                                            @NotNull final String translation) {
            super(tableName, fieldName, language, translation);
        }

        /**
         * Sets field recordId value and returns this
         *
         * @param recordId defines the record that corresponds to the field to be translated
         * @return builder for future object creation
         */
        public TranslationDeepness01Builder recordId(@Nullable final String recordId) {
            this.recordId = recordId;
            return this;
        }

        /**
         * Sets field recordSubId value and returns this
         *
         * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have a
         *                    unique ID
         * @return builder for future object creation
         */
        public TranslationDeepness01Builder recordSubId(@Nullable final String recordSubId) {
            this.recordSubId = recordSubId;
            return this;
        }

        /**
         * Sets field fieldValue value and returns this
         *
         * @param fieldValue can be used to define the value which should be translated
         * @return builder for future object creation
         */
        public TranslationDeepness01Builder fieldValue(@Nullable final String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }

        /**
         * Returns a {@link TranslationDeepness01} objects from fields provided via
         * {@link TranslationDeepness01.TranslationDeepness01Builder} methods. Throws {@link IllegalArgumentException}
         * if fields fieldName, language, translation, recordId to not meet the requirements from the specification:
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
        @SuppressWarnings("ConstantConditions")
        public TranslationDeepness01 build() {
            if (fieldValue != null) {
                if (recordId != null) {
                    throw new IllegalArgumentException("recordId and fieldValue can not both be defined");
                }
                if (recordSubId != null) {
                    throw new IllegalArgumentException("recordSubId and fieldValue can not both be defined");
                }
            } else if (recordId == null && fieldValue == null) {
                throw new IllegalArgumentException("recordId and fieldValue can not both be undefined");
            } else if (fieldName == null) {
                throw new IllegalArgumentException("fieldName must be specified");
            } else if (language == null) {
                throw new IllegalArgumentException("language must be specified");
            } else if (translation == null) {
                throw new IllegalArgumentException("translation must be specified");
            }
            return new TranslationDeepness01(tableName, fieldName, language, translation, recordId, recordSubId,
                    fieldValue);
        }
    }
}