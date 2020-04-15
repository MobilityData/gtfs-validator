package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.util.Map;

public interface ExecParamRepository {

    ExecParam getExecParamByShortName(final String optionName);

    Map<String, ExecParam> getExecParamCollection();

    ExecParam addExecParam(final ExecParam newExecParam);

    boolean hasExecParam(String shortName);

    ExecParamParser getParser(boolean fromConfigFile, String pathToConfigFile);

    interface ExecParamParser {

        Map<String, ExecParam> parse() throws IOException;
    }
}