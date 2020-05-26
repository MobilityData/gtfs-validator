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

import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This provides context to go from execution parameters contained in an Apache command line to an internal
 * representation using {@code ExecParam}.
 */
public class ApacheExecParamParser implements ExecParamRepository.ExecParamParser {
    private final CommandLineParser commandLineParser;
    private final Options availableOptions;
    private final String[] args;

    /**
     * @param commandLineParser parser from the apache collection transforming {@param args} into {@code Options}
     * @param availableOptions  options handled by the {@code ExecParamRepository}
     * @param args              command line to parse
     */
    public ApacheExecParamParser(final CommandLineParser commandLineParser,
                                 final Options availableOptions,
                                 final String[] args) {
        this.commandLineParser = commandLineParser;
        this.args = args;
        this.availableOptions = availableOptions;
    }

    /**
     * This method allows parsing of command line to an internal representation using {@code ExecParam}. Returns a
     * collection of the extracted {@link ExecParam} mapped on their keys. They key is the long name associated to
     * an option from the command line.
     * This method throws IOException if the parsing operation could not be executed
     *
     * @return a collection of {@link ExecParam} mapped on the longName associated to the command line option they
     * represent
     * @throws IOException if the parsing operation could not be executed
     */
    @Override
    public Map<String, ExecParam> parse() throws IOException {
        final Map<String, ExecParam> toReturn = new HashMap<>();
        try {
            final CommandLine cmd = commandLineParser.parse(availableOptions, args);
            for (Option option : cmd.getOptions()) {
                verifyOptionArgumentLength(option);
                if (!isOptionAlreadyDefined(toReturn, option)) {
                    toReturn.put(option.getLongOpt(), new ExecParam(option.getLongOpt(), option.getValues()));
                }
            }
            return toReturn;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Method verifies if all options only have one argument. Throws a ParseException if this requirement is not met.
     *
     * @param option option to analyze
     * @throws ParseException if an option has more than one argument
     */
    private void verifyOptionArgumentLength(@NotNull final Option option) throws ParseException {
        if (option.getValues() != null && option.getValues().length > 1) {
            throw new ParseException("Option: " + option.getLongOpt() + " with too many arguments: "
                    + Arrays.toString(option.getValues()));
        }
    }

    /**
     * Method verifies if all options have been declared once. Throws a ParseException if this requirement is not met
     *
     * @param execParamCollection option collection
     * @param option              option to analyze
     * @throws ParseException if an option has been declared more than once
     */
    @SuppressWarnings("SameReturnValue") // to avoid lint
    private boolean isOptionAlreadyDefined(@NotNull final Map<String, ExecParam> execParamCollection,
                                           @NotNull final Option option) throws ParseException {
        if (execParamCollection.containsKey(option.getLongOpt())) {
            throw new ParseException("Option: " + option.getLongOpt() + " already defined");
        } else {
            return false;
        }
    }
}