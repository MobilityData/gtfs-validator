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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IllegalFieldValueCombinationNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all entities defined in translations.txt
 */
public class Translation extends GtfsEntity {
    @NotNull
    private final TableName tableName;
    @NotNull
    private final String fieldName;
    @NotNull
    private final String language;
    @NotNull
    private final String translation;
    @Nullable
    private final String recordId;
    @Nullable
    private final String recordSubId;
    @Nullable
    private final String fieldValue;

    /**
     * Class for all entities defined in translations.txt
     *
     * @param tableName   defines the table that contains the field to be translated
     * @param fieldName   name of the field to be translated
     * @param language    language of translation
     * @param translation translated value
     * @param recordId    defines the record that corresponds to the field to be translated
     * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have a
     *                    unique ID
     * @param fieldValue  instead of defining which record should be translated by using record_id and record_sub_id,
     *                    this field can be used to define the value which should be translated
     */
    private Translation(@NotNull final TableName tableName,
                        @NotNull final String fieldName,
                        @NotNull final String language,
                        @NotNull final String translation,
                        @Nullable final String recordId,
                        @Nullable final String recordSubId,
                        @Nullable final String fieldValue) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.language = language;
        this.translation = translation;
        this.recordId = recordId;
        this.recordSubId = recordSubId;
        this.fieldValue = fieldValue;
    }

    @NotNull
    public TableName getTableName() {
        return tableName;
    }

    @NotNull
    public String getFieldName() {
        return fieldName;
    }

    @NotNull
    public String getLanguage() {
        return language;
    }

    @NotNull
    public String getTranslation() {
        return translation;
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
     * Builder class to create {@link Translation} objects. Allows an unordered definition of the different attributes
     * of {@link Translation}.
     */
    public static class TranslationBuilder {
        private TableName tableName;
        private String originalTableName;
        private String fieldName;
        private String language;
        private String translation;
        private String recordId;
        private String recordSubId;
        private String fieldValue;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field tableName and returns this
         *
         * @param tableName defines the table that contains the field to be translated
         * @return builder for future object creation
         */
        public TranslationBuilder tableName(@NotNull final String tableName) {
            this.tableName = TableName.fromString(tableName);
            this.originalTableName = tableName;
            return this;
        }

        /**
         * Sets field fieldName and returns this
         *
         * @param fieldName name of the field to be translated
         * @return builder for future object creation
         */
        public TranslationBuilder fieldName(@NotNull final String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        /**
         * Sets field language and returns this
         *
         * @param language language of translation
         * @return builder for future object creation
         */
        public TranslationBuilder language(@NotNull final String language) {
            this.language = language;
            return this;
        }

        /**
         * Sets field translation and returns this
         *
         * @param translation translated value
         * @return builder for future object creation
         */
        public TranslationBuilder translation(@NotNull final String translation) {
            this.translation = translation;
            return this;
        }

        /**
         * Sets field recordId and returns this
         *
         * @param recordId defines the record that corresponds to the field to be translated
         * @return builder for future object creation
         */
        public TranslationBuilder recordId(@Nullable final String recordId) {
            this.recordId = recordId;
            return this;
        }

        /**
         * Sets field recordSubId and returns this
         *
         * @param recordSubId helps the record that contains the field to be translated when the table doesn’t have a
         *                    unique ID
         * @return builder for future object creation
         */
        public TranslationBuilder recordSubId(@Nullable final String recordSubId) {
            this.recordSubId = recordSubId;
            return this;
        }

        /**
         * Sets field fieldValue and returns this
         *
         * @param fieldValue instead of defining which record should be translated by using record_id and
         *                   record_sub_id, this field can be used to define the value which should be translated
         * @return builder for future object creation
         */
        // suppressed warning: order of method calls triggers this warning.
        @SuppressWarnings("UnusedReturnValue")
        public TranslationBuilder fieldValue(@Nullable final String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }

        /**
         * Entity representing a row from translations.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns an entity representing a list of notices.
         *
         * @return Entity representing a row from translations.txt if the requirements from the official GTFS
         * specification are met. Otherwise, method returns an entity representing a list of notices.
         */
        public EntityBuildResult<?> build() {
            // suppressed warning for the necessity of null check. Lint is due to annotations, but the following
            // statements can be true.
            //noinspection ConstantConditions
            if (tableName == null || fieldName == null || language == null || translation == null ||
                    (recordId != null && tableName == TableName.FEED_INFO) ||
                    (recordId != null && fieldValue != null) ||
                    (recordId == null && fieldValue == null && tableName != TableName.FEED_INFO) ||
                    (recordSubId != null && fieldValue != null) ||
                    (recordSubId == null && tableName == TableName.STOP_TIMES && recordId != null) ||
                    (tableName == TableName.FEED_INFO && (recordId != null || recordSubId != null || fieldValue != null))
            ) {
                if (tableName == null) {
                    if (originalTableName == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("translations.txt",
                                "table_name", "table_name",
                                "field_name", "language",
                                "translation", originalTableName, fieldName, language, translation));
                    } else if (!TableName.isEnumValueValid(originalTableName)) {
                        noticeCollection.add(new UnexpectedEnumValueNotice("translations.txt",
                                "table_name", originalTableName,
                                "table_name", "field_name",
                                "language", "translation",
                                originalTableName, fieldName, language, translation));
                    }
                }
                if (fieldName == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("translations.txt",
                            "field_name", "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                if (language == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("translations.txt",
                            "language", "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                if (translation == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("translations.txt",
                            "translation", "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                if (tableName == TableName.FEED_INFO) {
                    if (recordId != null) {
                        noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                                "record_id", "table_name",
                                "table_name",
                                "field_name", "language",
                                "translation", originalTableName, fieldName, language,
                                translation));
                    } else if (recordSubId != null) {
                        noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                                "record_sub_id", "table_name",
                                "table_name",
                                "field_name", "language",
                                "translation", originalTableName, fieldName, language,
                                translation));
                    } else if (fieldValue != null) {
                        noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                                "field_value", "table_name",
                                "table_name",
                                "field_name", "language",
                                "translation", originalTableName, fieldName, language,
                                translation));
                    }
                }
                if (tableName != TableName.FEED_INFO && ((recordId != null && fieldValue != null) ||
                        (recordId == null && fieldValue == null))) {
                    noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                            "record_id", "field_value",
                            "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                if (recordSubId != null && fieldValue != null) {
                    noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                            "record_sub_id", "field_value",
                            "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                if (recordSubId == null && tableName == TableName.STOP_TIMES && recordId != null) {
                    noticeCollection.add(new IllegalFieldValueCombinationNotice("translations.txt",
                            "record_sub_id", "table_name",
                            "table_name",
                            "field_name", "language",
                            "translation", originalTableName, fieldName, language, translation));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Translation(tableName, fieldName, language, translation, recordId,
                        recordSubId, fieldValue));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null
         */
        public TranslationBuilder clear() {
            tableName = null;
            originalTableName = null;
            fieldName = null;
            language = null;
            translation = null;
            recordId = null;
            recordSubId = null;
            fieldValue = null;
            noticeCollection.clear();
            return this;
        }
    }
}
