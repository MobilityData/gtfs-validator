package org.mobilitydata.gtfsvalidator.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ApacheExecutionParameterParser implements ExecutionParameterRepository.ExecutionParameterParser {
    private final CommandLineParser commandLineParser;
    private final Options availableOptions;
    private final String[] arguments;

    public ApacheExecutionParameterParser(final CommandLineParser commandLineParser,
                                          final Options availableOptions,
                                          final String[] arguments) {
        this.commandLineParser = commandLineParser;
        this.arguments = arguments;
        this.availableOptions = availableOptions;

        availableOptions.addOption("u", "url", true, "URL to GTFS zipped archive");
        availableOptions.addOption("z", "zip", true, "if --url is used, where to place " +
                "the downloaded archive. Otherwise, relative path pointing to a valid GTFS zipped archive on disk");
        availableOptions.addOption("i", "input", true, "Relative path where to extract" +
                " the zip content");
        availableOptions.addOption("o", "output", true, "Relative path where to place" +
                " output files");
        availableOptions.addOption("h", "help", false, "Print this message");
        availableOptions.addOption("p", "proto", false, "Export validation results as" +
                " proto");
    }

    @Override
    public Map<String, ExecutionParameter> parse() throws IOException {
        final Map<String, ExecutionParameter> toReturn = new HashMap<>();
        try {
            CommandLine cmd = commandLineParser.parse(availableOptions, arguments);

            Arrays.stream(cmd.getOptions()).forEach(option ->
                    toReturn.put(option.getOpt(), new ExecutionParameter(option.getOpt(),
                                    option.getLongOpt(),
                                    option.getDescription(),
                                    option.hasArg(),
                                    option.getValue()
                            )
                    )
            );
            return toReturn;
        } catch (ParseException e) {
            throw new IOException();
        }
    }
}