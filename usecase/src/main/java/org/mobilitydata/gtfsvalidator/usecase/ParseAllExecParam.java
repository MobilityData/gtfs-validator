package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;

public class ParseAllExecParam {
    private final boolean fromConfigFile;
    private final String pathToConfigFile;
    private final ExecParamRepository execParamRepository;


    public ParseAllExecParam(boolean fromConfigFile,
                             String pathToConfigFile,
                             ExecParamRepository execParamRepository) {
        this.fromConfigFile = fromConfigFile;
        this.pathToConfigFile = pathToConfigFile;
        this.execParamRepository = execParamRepository;
    }

    public void execute() throws IOException {
        execParamRepository
                .getParser(fromConfigFile, pathToConfigFile)
                .parse()
                .forEach((s, execParam) -> execParamRepository.addExecParam(execParam));
    }
}