package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;

import java.util.Map;

public interface ExecutionParameterRepository {

    ExecutionParameter getExecutionParameterByShortName(final String optionName);

    Map<String, ExecutionParameter> getExecutionParameterCollection();

    ExecutionParameter addExecutionParameter(final ExecutionParameter newExecutionParameter);

    boolean hasExecutionParameter(String shortName);

    void parse(boolean fromConfigFile, String pathToConfigFile) throws IllegalArgumentException;
}