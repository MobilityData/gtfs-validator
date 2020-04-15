package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonExecParamParser implements ExecParamRepository.ExecParamParser {
    private final String configFileAsString;
    private static final ObjectReader objectReader = new ObjectMapper().readerFor(ExecParam.class);

    public JsonExecParamParser(final String configFileAsString) {
        this.configFileAsString = configFileAsString;
    }

    @Override
    public Map<String, ExecParam> parse() throws IOException {
        Map<String, ExecParam> toReturn = new HashMap<>();

        List<Object> execParamCollectionAsObjectCollection = getObjectReader().readValues(configFileAsString).readAll();

        for (Object object : execParamCollectionAsObjectCollection) {
            ExecParam execParam = (ExecParam) object;
            toReturn.put(execParam.getKey(), execParam);
        }
        return toReturn;
    }

    public ObjectReader getObjectReader() {
        return objectReader;
    }
}