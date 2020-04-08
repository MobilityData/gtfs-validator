package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExecutionParameterRepository {

    ExecutionParameter getExecutionParameterByShortName(final String optionName);

    Map<String, ExecutionParameter> getExecutionParameterCollection();

    ExecutionParameter addExecutionParameter(final ExecutionParameter newExecutionParameter);

    boolean hasExecutionParameter(String shortName);

    ExecutionParameterParser getParser(boolean fromConfigFile, String pathToConfigFile);

    interface ExecutionParameterParser {

        List<ExecutionParameter> parse() throws IOException;
    }
}