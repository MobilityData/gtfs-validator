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

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Provides methods to perform operations on rows of a GTFS CSV file. Includes feature to transform a row of a GTFS
 * CSV file into a {@link RawEntity}.
 * This is called in {@link InMemoryRawFileRepository} to retrieve the data provider for a specific GTFS CSV file.
 */
public class FromFileRawEntityProvider implements RawFileRepository.RawEntityProvider {
    private final MappingIterator<Map<String, String>> dataSource;
    private final int headerCount;

    public FromFileRawEntityProvider(RawFileInfo file) throws IOException {
        File csvFile = new File(file.getPath() + File.separator + file.getFilename());
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        dataSource = mapper.readerFor(Map.class)
                .with(schema)
                .readValues(csvFile);

        final int[] headerCountArray = {0};

        ((CsvSchema) (dataSource.getParser().getSchema())).iterator().forEachRemaining(column -> ++headerCountArray[0]);

        headerCount = headerCountArray[0];
    }

    /**
     * Returns a boolean indicating if a GTFS CSV file has more rows that can be read. 1 if yes, else 0.
     *
     * @return a boolean indicating if a GTFS CSV file has more rows that can be read. 1 if yes, else 0.
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
        return new RawEntity(dataSource.next(), dataSource.getCurrentLocation().getLineNr());
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
