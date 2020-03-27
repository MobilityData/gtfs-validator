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

public class UndefinedFieldValueNonFeedIntoTranslation extends TableNameBase {

    @NotNull
    private final String recordId;

    @Nullable
    private final String recordSubId;

    @NotNull
    public String getRecordId() {
        return recordId;
    }

    @Nullable
    public String getRecordSubId() {
        return recordSubId;
    }

    public UndefinedFieldValueNonFeedIntoTranslation(@NotNull final String fieldName,
                                                     @NotNull final String language,
                                                     @NotNull final String translation,
                                                     @NotNull final String recordId,
                                                     @Nullable final String recordSubId) {
        super(fieldName, language, translation);
        this.recordId = recordId;
        this.recordSubId = recordSubId;
    }

    public static class UndefinedFieldValueNonFeedInfoTranslationBuilder extends TableNameBaseBuilder {

        private String recordId;
        private String recordSubId;

        public UndefinedFieldValueNonFeedInfoTranslationBuilder(@NotNull final String fieldName,
                                                                @NotNull final String language,
                                                                @NotNull final String translation,
                                                                @NotNull final String recordId) {
            super(fieldName, language, translation);
            this.recordId = recordId;

        }

        public UndefinedFieldValueNonFeedInfoTranslationBuilder recordId(@NotNull final String recordId) {
            this.recordId = recordId;
            return this;
        }

        public UndefinedFieldValueNonFeedInfoTranslationBuilder recordSubId(@Nullable final String recordSubId) {
            this.recordSubId = recordSubId;
            return this;
        }

        public UndefinedFieldValueNonFeedIntoTranslation build() {
            return new UndefinedFieldValueNonFeedIntoTranslation(fieldName, language, translation, recordId,
                    recordSubId);
        }
    }
}