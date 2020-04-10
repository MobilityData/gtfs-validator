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

class TranslationTableCompositeKeyTest {
    private static final String STRING_TEST_VALUE = "test_value";

    @Test
    public void createTranslationCompositeKeyWithInvalidTableNameShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        underTest.tableName("invalid_table_name")
                .fieldName(STRING_TEST_VALUE)
                .language(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("table_name is undefined: either null or an unexpected enum value has been encountered",
                exception.getMessage());
    }

    @Test
    public void createTranslationCompositeKeyWithInvalidFieldNameShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("stop_times")
                .fieldName(null)
                .language(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field_name can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationCompositeKeyWithInvalidLanguageShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("stop_times")
                .language(null)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("language can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationCompositeKeyWithInvalidTranslationShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        //noinspection ConstantConditions
        underTest.tableName("stop_times")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(null)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("translation can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationCompositeKeyWithNullRecordSubIdShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        underTest.tableName("stop_times")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(STRING_TEST_VALUE)
                .recordSubId(null)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("record_sub_id can not be null", exception.getMessage());
    }

    @Test
    public void createTranslationCompositeKeyWithNullRecordIdShouldThrowException() {
        TranslationTableCompositeKey.TranslationTableCompositeKeyBuilder underTest = new TranslationTableCompositeKey
                .TranslationTableCompositeKeyBuilder();

        underTest.tableName("stop_times")
                .language(STRING_TEST_VALUE)
                .fieldName(STRING_TEST_VALUE)
                .translation(STRING_TEST_VALUE)
                .recordId(null)
                .recordSubId(STRING_TEST_VALUE)
                .fieldValue(null);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("record_id can not be null", exception.getMessage());
    }
}