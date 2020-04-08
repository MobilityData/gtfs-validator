package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonExecutionParameterParser implements ExecutionParameterRepository.ExecutionParameterParser {
    private final String configFileAsString;
    private final ObjectMapper objectMapper;

    public JsonExecutionParameterParser(ObjectMapper objectMapper, String configFileAsString) {
        this.configFileAsString = configFileAsString;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, ExecutionParameter> parse() throws IOException {
        Map<String, ExecutionParameter> toReturn = new HashMap<>();

        Arrays.asList(objectMapper.readValue(configFileAsString, ExecutionParameter[].class)).forEach(executionParameter -> toReturn.put(executionParameter.getShortName(), executionParameter));

        return toReturn;
    }
}