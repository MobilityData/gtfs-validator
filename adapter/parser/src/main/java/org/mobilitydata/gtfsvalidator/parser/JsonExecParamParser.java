package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonExecParamParser implements ExecParamRepository.ExecParamParser {
    private final String configFileAsString;
    private final ObjectMapper objectMapper;

    public JsonExecParamParser(final ObjectMapper objectMapper, final String configFileAsString) {
        this.configFileAsString = configFileAsString;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, ExecParam> parse() throws IOException {
        Map<String, ExecParam> toReturn = new HashMap<>();

        Arrays.asList(objectMapper.readValue(configFileAsString, ExecParam[].class))
                .forEach(executionParameter -> toReturn.put(executionParameter.getShortName(), executionParameter));

        return toReturn;
    }
}