package org.mobilitydata.gtfsvalidator.execution;

import org.apache.commons.cli.*;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ApacheExecutionParameterRepository implements ExecutionParameterRepository {

    private final Map<String, ExecutionParameter> executionParameterCollection = new HashMap<>();
    private final String[] arguments;
    private final Options options = new Options();

    public ApacheExecutionParameterRepository(String[] arguments) {
        this.arguments = arguments;
        options.addOption("u", "url", true, "URL to GTFS zipped archive");
        options.addOption("z", "zip", true, "if --url is used, where to place the " +
                "downloaded archive." +
                "Otherwise, relative path pointing to a valid GTFS zipped archive on disk");
        options.addOption("i", "input", true, "Relative path where to extract the zip" +
                " content");
        options.addOption("o", "output", true, "Relative path where to place output" +
                " files");
        options.addOption("h", "help", false, "Print this message");
        options.addOption("p", "proto", false, "Export validation results as proto");
    }

    @Override
    public ExecutionParameter getExecutionParameterByShortName(final String executionParameterShortName) {
        return executionParameterCollection.get(executionParameterShortName);
    }

    @Override
    public Map<String, ExecutionParameter> getExecutionParameterCollection() {
        return Collections.unmodifiableMap(executionParameterCollection);
    }

    @Override
    public ExecutionParameter addExecutionParameter(final ExecutionParameter newExecutionParameter) {
        executionParameterCollection.put(newExecutionParameter.getShortName(), newExecutionParameter);
        return newExecutionParameter;
    }

    @Override
    public boolean hasExecutionParameter(String shortName) {
        return executionParameterCollection.containsKey(shortName);
    }

    public void parse(boolean fromConfigFile, String pathToConfigFile) throws IllegalArgumentException {
        final CommandLineParser parser = new DefaultParser();

        try {
            final CommandLine cmd = parser.parse(options, arguments);

            Arrays.stream(cmd.getOptions()).forEach(option ->
                    addExecutionParameter(new ExecutionParameter(option.getOpt(),
                            option.getLongOpt(),
                            option.getDescription(),
                            option.hasArg(),
                            option.getValue())));
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }
}