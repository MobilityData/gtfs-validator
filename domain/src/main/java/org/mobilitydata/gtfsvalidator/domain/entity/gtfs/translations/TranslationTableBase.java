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
 * Base class for all entities defined in translations.txt
 */
public abstract class TranslationTableBase {

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

    protected TranslationTableBase(@NotNull final TableName tableName,
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

    public static abstract class TableNameBaseBuilder {

        protected TableName tableName;
        protected String fieldName;
        protected String language;
        protected String translation;
        protected String recordId;
        protected String recordSubId;
        protected String fieldValue;

        public TableNameBaseBuilder tableName(@NotNull final String tableName) {
            this.tableName = TableName.fromString(tableName);
            return this;
        }

        public TableNameBaseBuilder fieldName(@NotNull final String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public TableNameBaseBuilder language(@NotNull final String language) {
            this.language = language;
            return this;
        }

        public TableNameBaseBuilder translation(@NotNull final String translation) {
            this.translation = translation;
            return this;
        }

        public TableNameBaseBuilder recordId(@Nullable final String recordId) {
            this.recordId = recordId;
            return this;
        }

        public TableNameBaseBuilder recordSubId(@Nullable final String recordSubId) {
            this.recordSubId = recordSubId;
            return this;
        }

        public TableNameBaseBuilder fieldValue(@Nullable final String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }
    }
}