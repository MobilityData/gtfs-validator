package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;

import java.io.IOException;

public class ParseAllExecutionParameter {
    private final boolean fromConfigFile;
    private final String pathToConfigFile;
    private final ExecutionParameterRepository executionParameterRepository;


    public ParseAllExecutionParameter(boolean fromConfigFile,
                                      String pathToConfigFile,
                                      ExecutionParameterRepository executionParameterRepository) {
        this.fromConfigFile = fromConfigFile;
        this.pathToConfigFile = pathToConfigFile;
        this.executionParameterRepository = executionParameterRepository;
    }

    public void execute() throws IOException {
        executionParameterRepository
                .getParser(fromConfigFile, pathToConfigFile)
                .parse()
                .forEach((s, executionParameter) -> executionParameterRepository.addExecutionParameter(executionParameter));
    }
}