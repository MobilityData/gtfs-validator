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

public class DefinedFieldValueNonFeedInfoTranslation extends TableNameBase {

    @NotNull
    private final String fieldValue;

    @NotNull
    public String getFieldValue() {
        return fieldValue;
    }

    public DefinedFieldValueNonFeedInfoTranslation(@NotNull final String fieldName,
                                                   @NotNull final String language,
                                                   @NotNull final String translation,
                                                   @NotNull final String fieldValue) {
        super(fieldName, language, translation);
        this.fieldValue = fieldValue;
    }

    public static class DefinedFieldValueNonFeedInfoTranslationBuilder extends TableNameBaseBuilder {

        private String fieldValue;

        public DefinedFieldValueNonFeedInfoTranslationBuilder(@NotNull final String fieldName,
                                                              @NotNull final String language,
                                                              @NotNull final String translation,
                                                              @NotNull final String fieldValue) {
            super(fieldName, language, translation);
            this.fieldValue = fieldValue;
        }

        public DefinedFieldValueNonFeedInfoTranslationBuilder fieldValue(@NotNull final String fieldValue) {
            this.fieldValue = fieldValue;
            return this;
        }

        public DefinedFieldValueNonFeedInfoTranslation build() {
            return new DefinedFieldValueNonFeedInfoTranslation(fieldName, language, translation, fieldValue);
        }
    }
}