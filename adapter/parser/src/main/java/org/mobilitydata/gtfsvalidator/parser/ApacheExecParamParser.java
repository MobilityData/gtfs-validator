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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

        availableOptions.addOption("u", "url", true, "URL to GTFS zipped archive");
        availableOptions.addOption("z", "inputzip", true, "if --url is used, where " +
                "to place the downloaded archive. Otherwise, relative path pointing to a valid GTFS zipped archive on" +
                " disk");
        availableOptions.addOption("e", "extract", true, "Relative path where to " +
                "extract the zip content");
        availableOptions.addOption("o", "output", true, "Relative path where to place" +
                " output files");
        availableOptions.addOption("h", "help", false, "Print this message");
        availableOptions.addOption("p", "proto", false, "Export validation results as" +
                " proto");
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

            Arrays.stream(cmd.getOptions()).forEach(option ->
                    toReturn.put(option.getLongOpt(), new ExecParam(option.getLongOpt(), option.getValue())));
            return toReturn;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}