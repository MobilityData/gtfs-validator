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

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.File;
import java.util.*;

/**
 * Holds information about a GTFS CSV file that have not been through the parsing process. Provides methods to get
 * information about said file.
 * This is created when creating a new default configuration, all fields being set to their default value.
 */
public class InMemoryRawFileRepository implements RawFileRepository {

    private final Map<String, RawFileInfo> fileInfoPerFilename = new HashMap<>();

    /**
     * Builds a{@link RawFileInfo} with filename of the file to process
     *
     * @param fileInfo information regarding a file location and expected content (file name)
     * @return with filename
     */
    @Override
    public RawFileInfo create(RawFileInfo fileInfo) {
        fileInfoPerFilename.put(fileInfo.getFilename(), fileInfo);
        return fileInfo;
    }

    /**
     * Returns information regarding a file location and expected content (file name)
     *
     * @param filename the name of the file
     * @return information regarding a file location and expected content (file name)
     */
    @Override
    public Optional<RawFileInfo> findByName(String filename) {
        return Optional.ofNullable(fileInfoPerFilename.get(filename));
    }

    /**
     * Returns the collection of headers for GTFS CSV file from a {@link RawFileInfo} including duplicates
     *
     * @param file information regarding a file location and expected content (file name)
     * @return the collection of headers for a given GTFS CSV file
     */
    @Override
    public List<String> getActualHeadersForFile(RawFileInfo file) {
        File csvFile = new File(file.getPath() + File.separator + file.getFilename());

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);

        var itResult = parser.iterateRecords(csvFile);

        return Arrays.asList(itResult.getContext().headers().clone());
    }

    /**
     * Returns a collection of all filenames contained in the repository
     *
     * @return a collection of all filenames contained in the repository
     */
    @Override
    public Set<String> getFilenameAll() {
        return fileInfoPerFilename.keySet();
    }

    /**
     * Returns a data provider for a GTFS CSV file
     *
     * @param file information regarding a file to process
     * @return a data provider for a GTFS CSV file
     */
    @Override
    public Optional<RawEntityProvider> getProviderForFile(RawFileInfo file) {

        return Optional.of(new FromFileRawEntityProvider(file));
    }
}
