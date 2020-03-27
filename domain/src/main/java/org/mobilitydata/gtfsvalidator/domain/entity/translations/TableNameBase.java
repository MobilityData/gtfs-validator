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

public abstract class TableNameBase {

    @NotNull
    private final String fieldName;

    @NotNull
    private final String language;

    @NotNull
    private final String translation;

    public TableNameBase(@NotNull String fieldName,
                         @NotNull String language,
                         @NotNull String translation) {
        this.fieldName = fieldName;
        this.language = language;
        this.translation = translation;
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

    public static abstract class TableNameBaseBuilder {

        @NotNull
        protected String fieldName;

        @NotNull
        protected String language;

        @NotNull
        protected String translation;

        public TableNameBaseBuilder(@NotNull final String fieldName,
                                    @NotNull final String language,
                                    @NotNull final String translation) {
            this.fieldName = fieldName;
            this.language = language;
            this.translation = translation;
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
    }
}
