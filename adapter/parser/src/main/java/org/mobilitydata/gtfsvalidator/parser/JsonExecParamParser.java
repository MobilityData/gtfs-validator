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

package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.Resources;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This provides context to go from execution parameters contained in an .json file to an internal representation using
 * {@code ExecParam}.
 */
public class JsonExecParamParser implements ExecParamRepository.ExecParamParser {
    private final String pathToConfigFile;
    private final ObjectReader objectReader;

    /**
     * @param pathToConfigFile path to the .json file containing the execution parameters to parse
     * @param objectReader     reader from jackson library to parse the configuration .json file
     */
    public JsonExecParamParser(final String pathToConfigFile, ObjectReader objectReader) {
        this.pathToConfigFile = pathToConfigFile;
        this.objectReader = objectReader;
    }

    /**
     * This method allows parsing execution parameters found in a .json file to an internal representation using
     * {@code ExecParam}. Returns a collection of the extracted {@link ExecParam} mapped on their keys. They key of each
     * {@link ExecParam} is associated with the field paramKey of each object to be parsed from the .json file.
     * This method throws IOException if the parsing operation could not be executed
     *
     * @return a collection of {@link ExecParam} mapped on the name associated to the execution parameter they
     * represent
     * @throws IOException if the parsing operation could not be executed
     */
    @Override
    public Map<String, ExecParam> parse() throws IOException {
        final Map<String, ExecParam> toReturn = new HashMap<>();

        //noinspection UnstableApiUsage
        final String configFileAsString =
                Resources.toString(Resources.getResource(pathToConfigFile), StandardCharsets.UTF_8);

        final List<Object> execParamCollectionAsObjectCollection = objectReader.readValues(configFileAsString)
                .readAll();

        for (Object object : execParamCollectionAsObjectCollection) {
            final ExecParam execParam = (ExecParam) object;
            toReturn.put(execParam.getParamKey(), execParam);
        }
        return toReturn;
    }
}