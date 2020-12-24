/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_TABLE_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_LANGUAGE;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_TRANSLATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_RECORD_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_RECORD_SUB_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTranslation.DEFAULT_FIELD_VALUE;

@RunWith(JUnit4.class)
public class GtfsTranslationTest {
    @Test
    public void shouldReturnFieldValues() {
        Builder builder = new Builder();
        GtfsTranslation underTest = builder
                .setTableName("table name")
                .setFieldName("field name")
                .setLanguage(Locale.CANADA)
                .setTranslation("translation")
                .setRecordId("record id")
                .setRecordSubId("record sub ud")
                .setFieldValue("field value")
                .build();

        assertThat(underTest.tableName()).isEqualTo("table name");
        assertThat(underTest.fieldName()).isEqualTo("field name");
        assertThat(underTest.language()).isEqualTo(Locale.CANADA);
        assertThat(underTest.translation()).isEqualTo("translation");
        assertThat(underTest.recordId()).isEqualTo("record id");
        assertThat(underTest.recordSubId()).isEqualTo("record sub ud");
        assertThat(underTest.fieldValue()).isEqualTo("field value");

        assertThat(underTest.hasTableName()).isTrue();
        assertThat(underTest.hasFieldName()).isTrue();
        assertThat(underTest.hasLanguage()).isTrue();
        assertThat(underTest.hasTranslation()).isTrue();
        assertThat(underTest.hasRecordId()).isTrue();
        assertThat(underTest.hasRecordSubId()).isTrue();
        assertThat(underTest.hasFieldValue()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        Builder builder = new Builder();
        GtfsTranslation underTest = builder
                .setTableName(null)
                .setFieldName(null)
                .setLanguage(null)
                .setTranslation(null)
                .setRecordId(null)
                .setRecordSubId(null)
                .setFieldValue(null)
                .build();

        assertThat(underTest.tableName()).isEqualTo(DEFAULT_TABLE_NAME);
        assertThat(underTest.fieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(underTest.language()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(underTest.translation()).isEqualTo(DEFAULT_TRANSLATION);
        assertThat(underTest.recordId()).isEqualTo(DEFAULT_RECORD_ID);
        assertThat(underTest.recordSubId()).isEqualTo(DEFAULT_RECORD_SUB_ID);
        assertThat(underTest.fieldValue()).isEqualTo(DEFAULT_FIELD_VALUE);

        assertThat(underTest.hasTableName()).isFalse();
        assertThat(underTest.hasFieldName()).isFalse();
        assertThat(underTest.hasLanguage()).isFalse();
        assertThat(underTest.hasTranslation()).isFalse();
        assertThat(underTest.hasRecordId()).isFalse();
        assertThat(underTest.hasRecordSubId()).isFalse();
        assertThat(underTest.hasFieldValue()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        Builder builder = new Builder();
        builder.setTableName("table name")
                .setFieldName("field name")
                .setLanguage(Locale.CANADA)
                .setTranslation("translation")
                .setRecordId("record id")
                .setRecordSubId("record sub ud")
                .setFieldValue("field value");
        builder.clear();

        GtfsTranslation underTest = builder.build();

        assertThat(underTest.tableName()).isEqualTo(DEFAULT_TABLE_NAME);
        assertThat(underTest.fieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(underTest.language()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(underTest.translation()).isEqualTo(DEFAULT_TRANSLATION);
        assertThat(underTest.recordId()).isEqualTo(DEFAULT_RECORD_ID);
        assertThat(underTest.recordSubId()).isEqualTo(DEFAULT_RECORD_SUB_ID);
        assertThat(underTest.fieldValue()).isEqualTo(DEFAULT_FIELD_VALUE);

        assertThat(underTest.hasTableName()).isFalse();
        assertThat(underTest.hasFieldName()).isFalse();
        assertThat(underTest.hasLanguage()).isFalse();
        assertThat(underTest.hasTranslation()).isFalse();
        assertThat(underTest.hasRecordId()).isFalse();
        assertThat(underTest.hasRecordSubId()).isFalse();
        assertThat(underTest.hasFieldValue()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        Builder builder = new Builder();
        GtfsTranslation underTest = builder.build();

        assertThat(underTest.tableName()).isNull();
        assertThat(underTest.fieldName()).isNull();
        assertThat(underTest.language()).isNull();
        assertThat(underTest.translation()).isNull();
        assertThat(underTest.recordId()).isNull();
        assertThat(underTest.recordSubId()).isNull();
        assertThat(underTest.fieldValue()).isNull();

        assertThat(underTest.hasTableName()).isFalse();
        assertThat(underTest.hasFieldName()).isFalse();
        assertThat(underTest.hasLanguage()).isFalse();
        assertThat(underTest.hasTranslation()).isFalse();
        assertThat(underTest.hasRecordId()).isFalse();
        assertThat(underTest.hasRecordSubId()).isFalse();
        assertThat(underTest.hasFieldValue()).isFalse();
    }
}
