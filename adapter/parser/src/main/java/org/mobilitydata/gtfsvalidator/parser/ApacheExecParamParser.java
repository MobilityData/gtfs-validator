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

public class ApacheExecParamParser implements ExecParamRepository.ExecParamParser {
    private final CommandLineParser commandLineParser;
    private final Options availableOptions;
    private final String[] args;

    public ApacheExecParamParser(final CommandLineParser commandLineParser,
                                 final Options availableOptions,
                                 final String[] args) {
        this.commandLineParser = commandLineParser;
        this.args = args;
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
    public Map<String, ExecParam> parse() throws IOException {
        final Map<String, ExecParam> toReturn = new HashMap<>();
        try {
            CommandLine cmd = commandLineParser.parse(availableOptions, args);

            Arrays.stream(cmd.getOptions()).forEach(option ->
                    toReturn.put(option.getOpt(), new ExecParam(option.getOpt(),
                                    option.getLongOpt(),
                                    option.getDescription(),
                                    option.hasArg(),
                                    option.getValue()
                            )
                    )
            );
            return toReturn;
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}