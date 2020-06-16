/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IllegalFieldValueCombination;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TranslationTest {
    private static final String VALID_TABLE_NAME = "agency";
    private static final String FIELD_NAME = "field name";
    private static final String LANGUAGE = "language";
    private static final String TRANSLATION = "translation";
    private static final String DEFINED_RECORD_ID = "record id";
    private static final String DEFINED_RECORD_SUB_ID = "record sub id";
    private static final String DEFINED_FIELD_VALUE = "field value";
    static final String UNDEFINED_FIELD_VALUE = null;
    static final String UNDEFINED_RECORD_SUB_ID = null;
    static final String UNDEFINED_RECORD_ID = null;
    private static final String FILENAME = "translations.txt";
    private static final String ENTITY_ID = "no id";

    @Test
    void translationWithNullTableNameShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        // suppressed warning for test purpose. Lint is generated because of NotNull annotations
        //noinspection ConstantConditions
        underTest.tableName(null)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> toCheck = (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("table_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
    }

    @Test
    void translationWithInvalidTableNameShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName("invalid table name")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId("record sub id")
                .fieldValue(null);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> toCheck = (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("table_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("invalid table name", toCheck.get(0).getNoticeSpecific(Notice.KEY_ENUM_VALUE));
    }

    @Test
    void translationWithNullFieldNameShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        // suppressed warning for test purpose. Lint is generated because of NotNull annotations
        //noinspection ConstantConditions
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(null)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> toCheck = (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("field_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
    }

    @Test
    void translationWithNullLanguageShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        // suppressed warning for test purpose. Lint is generated because of NotNull annotations
        //noinspection ConstantConditions
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(null)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> toCheck = (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals(LANGUAGE, toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
    }

    @Test
    void translationWithNullTranslationShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        // suppressed warning for test purpose. Lint is generated because of NotNull annotations
        //noinspection ConstantConditions
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(null)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> toCheck = (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals(TRANSLATION, toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
    }

    @Test
    void translationWithIllegalRecordIdTableNameFieldCombinationShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName("feed_info")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> toCheck =
                (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("table_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));
    }

    @Test
    void translationWithIllegalRecordIdFieldValueCombinationShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(DEFINED_FIELD_VALUE);

        EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        List<IllegalFieldValueCombination> toCheck = (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("field_value", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));

        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(UNDEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        toCheck = (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("field_value", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));
    }

    @Test
    void translationWithIllegalRecordSubIdTableNameCombinationShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName("feed_info")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(UNDEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> toCheck = (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_sub_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("table_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));
    }

    @Test
    void translationWithIllegalRecordSubIdFieldValueCombinationShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(UNDEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(DEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> toCheck = (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_sub_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("field_value", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));
    }

    @Test
    void translationWithIllegalRecordSubIdTableNameRecordIdShouldGenerateNotice() {
        final Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();
        underTest.tableName("stop_times")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> toCheck =
                (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        assertEquals(1, toCheck.size());
        assertEquals(FILENAME, toCheck.get(0).getFilename());
        assertEquals(ENTITY_ID, toCheck.get(0).getEntityId());
        assertEquals("record_sub_id", toCheck.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("table_name", toCheck.get(0).getNoticeSpecific(Notice.KEY_CONFLICTING_FIELD_NAME));
    }

    @Test
    void translationWithValidValuesShouldNotGenerateNotice() {
        Translation.TranslationBuilder underTest = new Translation.TranslationBuilder();

        // agency translation
        underTest.tableName(VALID_TABLE_NAME)
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof Translation);

        // levels translation
        underTest.tableName("levels")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof Translation);

        // feed_info translation
        underTest.tableName("feed_info")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(UNDEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof Translation);

        // stop_times translation with defined record id
        underTest.tableName("stop_times")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(DEFINED_RECORD_ID)
                .recordSubId(DEFINED_RECORD_SUB_ID)
                .fieldValue(UNDEFINED_FIELD_VALUE);

        entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof Translation);

        // stop_times translation with undefined record id
        underTest.tableName("stop_times")
                .fieldName(FIELD_NAME)
                .language(LANGUAGE)
                .translation(TRANSLATION)
                .recordId(UNDEFINED_RECORD_ID)
                .recordSubId(UNDEFINED_RECORD_SUB_ID)
                .fieldValue(DEFINED_FIELD_VALUE);

        entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof Translation);
    }
}
