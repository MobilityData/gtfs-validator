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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryGtfsDataRepositoryTest {

    @Test
    public void getTranslationTableCollectionShouldReturnTranslationTableCollection() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockTranslation0);
        underTest.addEntity(mockTranslation1);

        Map<TableName, TranslationTableBase> toCheck = underTest.getTranslationTableCollection();

        assertEquals(2, toCheck.size());

        assertTrue(toCheck.containsKey(TableName.AGENCY));
        assertTrue(toCheck.containsKey(TableName.STOPS));
    }

    @Test
    public void getTranslationTableByTableNameShouldReturnRelatedTranslationTableEntity() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockTranslation0);
        underTest.addEntity(mockTranslation1);

        TranslationTableBase toCheck = underTest.getTranslationTableByTableName("agency");
        assertEquals(TableName.AGENCY, toCheck.getTableName());

        toCheck = underTest.getTranslationTableByTableName("stops");
        assertEquals(TableName.STOPS, toCheck.getTableName());
    }

    @Test
    public void addEntityShouldAddEntityToRepoAndReturnSameEntity() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase toCheck = underTest.addEntity(mockTranslation0);

        assertEquals(mockTranslation0, toCheck);
        assertEquals(1, underTest.getTranslationTableCollection().size());

        toCheck = underTest.addEntity(mockTranslation1);

        assertEquals(mockTranslation1, toCheck);
        assertEquals(2, underTest.getTranslationTableCollection().size());
    }

    @Test
    public void isPresentShouldReturnFalseIfEntityIsNotInRepo() {

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        assertFalse(underTest.isPresent(mockTranslation0));
    }

    @Test
    public void isPresentShouldReturnTrueIfEntityIsInRepo() {

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        underTest.addEntity(mockTranslation0);

        assertTrue(underTest.isPresent(mockTranslation0));
    }
}