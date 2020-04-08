package org.mobilitydata.gtfsvalidator.execution;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecutionParameterParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryExecutionParameterRepository implements ExecutionParameterRepository {

    private final Map<String, ExecutionParameter> executionParameterCollection = new LinkedHashMap<>();
    private final String[] arguments;

    public InMemoryExecutionParameterRepository(String[] arguments) {
        this.arguments = arguments;
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

    @Override
    public ApacheExecutionParameterParser getParser(boolean fromConfigFile, String pathToConfigFile) {
        if (!fromConfigFile) {
            return new ApacheExecutionParameterParser(new DefaultParser(), new Options(), arguments);
        }
        return null;
    }
}