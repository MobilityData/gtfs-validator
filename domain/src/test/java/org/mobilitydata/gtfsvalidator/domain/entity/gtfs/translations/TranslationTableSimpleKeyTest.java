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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TranslationTableSimpleKeyTest {
    private static final String STRING_TEST_VALUE = "test_value";

    @Test
    public void createTranslationSimpleKeyWithInvalidTableNameShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        underTest.tableName("invalid_table_name")
                .fieldName(STRING_TEST_VALUE)
                .language(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(null)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("table_name is undefined: either null or an unexpected enum value has been encountered",
                exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithInvalidFieldNameShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("agency")
                .fieldName(null)
                .language(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(null)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field_name can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithInvalidLanguageShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("agency")
                .language(null)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(null)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("language can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithInvalidTranslationShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("agency")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(null)
                .recordId(null)
                .recordSubId(null)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("translation can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithInvalidFieldValueAndRecordIdShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        underTest.tableName("agency")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(null)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("record_id and field_value can not both be defined", exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithInvalidFieldValueAndRecordSubIdShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        underTest.tableName("agency")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("record_sub_id and field_value can not both be defined", exception.getMessage());
    }

    @Test
    public void createTranslationSimpleKeyWithNullFieldValueAndNullRecordIdShouldThrowException() {
        TranslationTableSimpleKey.TranslationTableSimpleKeyBuilder underTest = new TranslationTableSimpleKey
                .TranslationTableSimpleKeyBuilder();

        underTest.tableName("agency")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("record_id and field_value can not both be undefined", exception.getMessage());
    }
}