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

package org.mobilitydata.gtfsvalidator.usecase.port;

import org.apache.commons.cli.Options;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.util.Map;

/**
 * This holds the execution parameters passed as parameters of the main execution method from a .json file or from
 * Apache command line.
 */
public interface ExecParamRepository {
    String HELP_KEY = "help";
    String EXTRACT_KEY = "extract";
    String OUTPUT_KEY = "output";
    String PROTO_KEY = "proto";
    String URL_KEY = "url";
    String ZIP_KEY = "zipinput";
    String EXCLUSION_KEY = "exclude";
    String ROUTE__ROUTE_SORT_ORDER_LOWER_BOUND_KEY = "route__route_sort_order_lower_bound";
    String ROUTE__ROUTE_SORT_ORDER_UPPER_BOUND_KEY = "route__route_sort_order_upper_bound";

    ExecParam getExecParamByKey(final String optionName);

    Map<String, ExecParam> getExecParamCollection();

    ExecParam addExecParam(final ExecParam newExecParam) throws IllegalArgumentException;

    boolean hasExecParam(final String key);

    boolean hasExecParamValue(final String key);

    ExecParamParser getParser(final String parameterJsonString, final String[] args, final Logger logger);

    String getExecParamValue(final String key) throws IllegalArgumentException;

    Options getOptions();

    boolean isEmpty();

    interface ExecParamParser {

        Map<String, ExecParam> parse() throws IOException;
    }
}