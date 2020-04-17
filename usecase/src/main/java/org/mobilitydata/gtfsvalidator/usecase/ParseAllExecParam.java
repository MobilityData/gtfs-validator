package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;

public class ParseAllExecParam {
    private final boolean fromConfigFile;
    private final String pathToConfigFile;
    private final ExecParamRepository execParamRepository;
    private final String pathToDefaultConfigFile;

    public ParseAllExecParam(boolean fromConfigFile,
                             String pathToConfigFile,
                             ExecParamRepository execParamRepository,
                             String pathToDefaultConfigFile) {
        this.fromConfigFile = fromConfigFile;
        this.pathToConfigFile = pathToConfigFile;
        this.execParamRepository = execParamRepository;
        this.pathToDefaultConfigFile = pathToDefaultConfigFile;
    }

    public void execute(String[] args) throws IOException {
        execParamRepository
                .getParser(fromConfigFile, pathToConfigFile, args)
                .parse()
                .forEach((s, execParam) -> execParamRepository.addExecParam(execParam));

        execParamRepository.setDefaultValueOfMissingItem(pathToDefaultConfigFile);
    }
}