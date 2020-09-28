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

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.File;

/**
 * Provides methods to perform operations on rows of a GTFS CSV file. Includes feature to transform a row of a GTFS
 * CSV file into a {@link RawEntity}.
 * This is called in {@link InMemoryRawFileRepository} to retrieve the data provider for a specific GTFS CSV file.
 */
public class FromFileRawEntityProvider implements RawFileRepository.RawEntityProvider {
    private final ResultIterator<Record, ParsingContext> dataSource;
    private final int headerCount;

    public FromFileRawEntityProvider(RawFileInfo file) {

        File csvFile = new File(file.getPath() + File.separator + file.getFilename());

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);

        var itResult = parser.iterateRecords(csvFile);

        headerCount = itResult.getContext().headers().length;

        dataSource = itResult.iterator();
    }

    /**
     * Returns a boolean indicating if a GTFS CSV file has more rows that can be read. true if yes, else false.
     *
     * @return a boolean indicating if a GTFS CSV file has more rows that can be read. true if yes, else false
     */
    @Override
    public boolean hasNext() {
        return dataSource.hasNext();
    }

    /**
     * Returns the next row of a GTFS CSV file as a {@link RawEntity}
     *
     * @return the next row of a GTFS CSV file as a {@link RawEntity}
     */
    @Override
    public RawEntity getNext() {
        return new RawEntity(dataSource.next().toFieldMap(), (int) dataSource.getContext().currentLine());
    }

    /**
     * Returns the number of header of a GTFS CSV file.
     *
     * @return the number of header of a GTFS CSV file.
     */
    @Override
    public int getHeaderCount() {
        return headerCount;
    }
}
