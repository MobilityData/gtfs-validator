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
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This provides context to go from execution parameters contained in an .json file to an internal representation using
 * {@code ExecParam}.
 */
public class JsonExecParamParser implements ExecParamRepository.ExecParamParser {
    private final String parameterJsonString;
    private final ObjectReader objectReader;
    private final Logger logger;

    /**
     * @param parameterJsonString the .json containing the execution parameters to parse, as string
     * @param objectReader        reader from jackson library to parse the configuration .json file
     * @param logger              logger to log relevant information
     */
    public JsonExecParamParser(final String parameterJsonString,
                               final ObjectReader objectReader,
                               final Logger logger) {
        this.parameterJsonString = parameterJsonString;
        this.objectReader = objectReader;
        this.logger = logger;
    }

    /**
     * This method allows parsing execution parameters found in a .json file to an internal representation using
     * {@code ExecParam}. Returns a collection of the extracted {@link ExecParam} mapped on their keys. They key of each
     * {@link ExecParam} is associated with the field paramKey of each object to be parsed from the .json file.
     *
     * @return a collection of {@link ExecParam} mapped on the name associated to the execution parameter they
     * represent
     */
    @Override
    public Map<String, ExecParam> parse() {
        final Map<String, ExecParam> toReturn = new HashMap<>();
        try {
            objectReader.readTree(parameterJsonString).fields()
                    .forEachRemaining(field -> {
                        // field.getValue().asText() contains the value related to a given key for an ExecParam, as
                        // a string.
                        // This string needs to be converted to a String[] as the second argument for ExecParam has
                        // type String[]
                        final List<String> value = new ArrayList<>();
                        if (field.getKey().equals(ExecParamRepository.EXCLUSION_KEY)) {
                            field.getValue().elements().forEachRemaining(elem -> value.add(elem.asText()));
                        } else {
                            value.addAll(List.of(field.getValue().asText()));
                        }
                        final ExecParam execParam =
                                new ExecParam(field.getKey(), value.toArray(new String[0]));
                        toReturn.put(execParam.getKey(), execParam);
                    });

            return toReturn;
        } catch (IOException e) {
            logger.info("could not find execution-parameters.json file  -- will consider default values for" +
                    " execution parameters");
            return toReturn;
        }
    }
}