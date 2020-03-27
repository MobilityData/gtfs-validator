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

public class DefinedRecordIdStopTimeTranslation extends TableNameBase {

    @NotNull
    final private String recordId;

    @NotNull
    final private String recordSubId;

    private DefinedRecordIdStopTimeTranslation(@NotNull final String fieldName,
                                               @NotNull final String language,
                                               @NotNull final String translation,
                                               @NotNull final String recordId,
                                               @NotNull final String recordSubId) {
        super(fieldName, language, translation);
        this.recordId = recordId;
        this.recordSubId = recordSubId;
    }

    @NotNull
    public String getRecordId() {
        return recordId;
    }

    @NotNull
    public String getRecordSubId() {
        return recordSubId;
    }

    public static class DefinedRecordIdStopTimeTranslationBuilder extends TableNameBaseBuilder {

        @NotNull
        private String recordId;

        @NotNull
        private String recordSubId;

        public DefinedRecordIdStopTimeTranslationBuilder(@NotNull final String fieldName,
                                                         @NotNull final String language,
                                                         @NotNull final String translation,
                                                         @NotNull final String recordId,
                                                         @NotNull final String recordSubId) {
            super(fieldName, language, translation);
            this.recordId = recordId;
            this.recordSubId = recordSubId;
        }

        public DefinedRecordIdStopTimeTranslationBuilder recordId(@NotNull final String recordId) {
            this.recordId = recordId;
            return this;
        }

        public DefinedRecordIdStopTimeTranslationBuilder recordSubId(@NotNull final String recordSubId) {
            this.recordSubId = recordSubId;
            return this;
        }

        public DefinedRecordIdStopTimeTranslation build() {
            return new DefinedRecordIdStopTimeTranslation(fieldName, language, translation, recordId, recordSubId);
        }
    }
}
