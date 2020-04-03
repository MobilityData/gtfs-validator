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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryGtfsDataRepository implements GtfsDataRepository {

    private final HashMap<TableName, TranslationTableBase> translationTableCollection = new HashMap<>();

    @Override
    public List<TranslationTableBase> getTranslationTableCollection() {
        return new ArrayList<>(translationTableCollection.values());
    }

    @Override
    public TranslationTableBase getTranslationTableByTableName(String tableName) {
        return translationTableCollection.get(TableName.fromString(tableName));
    }

    @Override
    public TranslationTableBase addEntity(final TranslationTableBase newTranslationTable) {
        translationTableCollection.put(newTranslationTable.getTableName(), newTranslationTable);
        return newTranslationTable;
    }
}