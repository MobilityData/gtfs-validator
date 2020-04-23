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

import com.google.common.io.Resources;
import com.google.protobuf.TextFormat;
import org.apache.commons.validator.routines.*;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.parser.GtfsEntityParser;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.validator.GtfsTypeValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This holds information about the GTFS specification from a {@link GtfsSpecificationProto.CsvSpecProtos}. Provides
 * methods to get information about what is defined in the official specification.
 * This is created  when creating a new default configuration.
 */
public class InMemoryGtfsSpecRepository implements GtfsSpecRepository {

    private final GtfsSpecificationProto.CsvSpecProtos inMemoryGTFSSpec;
    private final Map<String, ParsedEntityTypeValidator> validatorByFilenameCache = new HashMap<>();

    private static final String[] VALID_URL_SCHEMES = {"http", "https"};
    private static final String VALID_COLOR_REGEX_PATTERN = "[0-9a-fA-F]{6}";
    private static final String VALID_TIME_REGEXP_PATTERN = "([0-9][0-9]|[0-9]):[0-5][0-9]:[0-5][0-9]";

    /**
     * @param specResourceName the path to the GTFS schema resource
     */
    public InMemoryGtfsSpecRepository(final String specResourceName) {
        GtfsSpecificationProto.CsvSpecProtos GtfsSpec;
        try {
            //noinspection UnstableApiUsage
            GtfsSpec = TextFormat.parse(Resources.toString(Resources.getResource(specResourceName),
                    StandardCharsets.UTF_8),
                    GtfsSpecificationProto.CsvSpecProtos.class);
        } catch (IOException e) {
            GtfsSpec = null;
            e.printStackTrace();
        }
        inMemoryGTFSSpec = GtfsSpec;
    }

    /**
     * Returns the list of the files marked as required in the GTFS schema
     *
     * @return the list of the files marked as required in the GTFS schema
     */
    @Override
    public List<String> getRequiredFilenameList() {
        return inMemoryGTFSSpec.getCsvspecList().stream()
                .filter(GtfsSpecificationProto.CsvSpecProto::getRequired)
                .map(GtfsSpecificationProto.CsvSpecProto::getFilename)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of the files marked as optional in the GTFS schema
     *
     * @return the list of the files marked as optional in the GTFS schema
     */
    @Override
    public List<String> getOptionalFilenameList() {
        return inMemoryGTFSSpec.getCsvspecList().stream()
                .filter(file -> !file.getRequired())
                .map(GtfsSpecificationProto.CsvSpecProto::getFilename)
                .collect(Collectors.toList());
    }

    /**
     * Returns the list of the headers marked as required for a given file
     *
     * @param fileInfo information about the file to process: location and expected content
     * @return the list of the headers marked as required for file to analyze
     */
    @Override
    public List<String> getRequiredHeadersForFile(RawFileInfo fileInfo) {
        GtfsSpecificationProto.CsvSpecProto specForFile = getSpecForFile(fileInfo);

        if (specForFile != null) {
            return specForFile.getColumnList().stream()
                    .filter(GtfsSpecificationProto.ColumnSpecProto::getRequired)
                    .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the list of the headers marked as optional for a given GTFS CSV file
     *
     * @param fileInfo information about the file to process: location and expected content
     * @return the list of the headers marked as optional for file associated to {@code fileInfo}
     */
    @Override
    public List<String> getOptionalHeadersForFile(RawFileInfo fileInfo) {
        GtfsSpecificationProto.CsvSpecProto specForFile = getSpecForFile(fileInfo);

        if (specForFile != null) {
            return specForFile.getColumnList().stream()
                    .filter(column -> !column.getRequired())
                    .map(GtfsSpecificationProto.ColumnSpecProto::getName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the schema for a given GTFS CSV file
     *
     * @param fileInfo information about the file to process: location and expected content
     * @return the schema corresponding to the file associated to {@param fileInfo}
     */
    private GtfsSpecificationProto.CsvSpecProto getSpecForFile(RawFileInfo fileInfo) {
        return inMemoryGTFSSpec.getCsvspecList().stream()
                .filter(spec -> fileInfo.getFilename().equals(spec.getFilename()))
                .findAny()
                .orElse(null);
    }

    /**
     * Returns the parser for raw data associated to a given GTFS CSV file
     *
     * @param file information about the file to process: location and expected content
     * @return the parser for raw data associated to the file associated to {@code file}
     */
    @Override
    public RawEntityParser getParserForFile(RawFileInfo file) {
        return new GtfsEntityParser(
                Objects.requireNonNull(inMemoryGTFSSpec.getCsvspecList().stream()
                        .filter(spec -> file.getFilename().equals(spec.getFilename()))
                        .findAny()
                        .orElse(null)),
                file,
                FloatValidator.getInstance(),
                IntegerValidator.getInstance(),
                DateValidator.getInstance());
    }

    /**
     * Returns the type validator associated to a given GTFS CSV file
     *
     * @param file information about the file to process: location and expected content
     * @return the type validator associated to file associated to {@code file}
     */
    @Override
    public ParsedEntityTypeValidator getValidatorForFile(RawFileInfo file) {
        ParsedEntityTypeValidator toReturn = validatorByFilenameCache.get(file.getFilename());

        if (toReturn == null) {
            toReturn = new GtfsTypeValidator(
                    Objects.requireNonNull(inMemoryGTFSSpec.getCsvspecList().stream()
                            .filter(spec -> file.getFilename().equals(spec.getFilename()))
                            .findAny()
                            .orElse(null)),
                    FloatValidator.getInstance(),
                    IntegerValidator.getInstance(),
                    new UrlValidator(VALID_URL_SCHEMES),
                    new RegexValidator(VALID_COLOR_REGEX_PATTERN),
                    new RegexValidator(VALID_TIME_REGEXP_PATTERN),
                    // Uses IANA timezone database shipped with JDK
                    // to update without updating JDK see
                    // https://www.oracle.com/technetwork/java/javase/tzupdater-readme-136440.html
                    ZoneId.getAvailableZoneIds());
            validatorByFilenameCache.put(file.getFilename(), toReturn);
        }

        return toReturn;
    }
}
