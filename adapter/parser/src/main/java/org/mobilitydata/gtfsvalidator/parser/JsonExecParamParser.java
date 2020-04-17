package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.Resources;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonExecParamParser implements ExecParamRepository.ExecParamParser {
    private final String pathToConfigFile;
    private static final ObjectReader objectReader = new ObjectMapper().readerFor(ExecParam.class);

    public JsonExecParamParser(final String pathToConfigFile) {
        this.pathToConfigFile = pathToConfigFile;
    }

    @Override
    public Map<String, ExecParam> parse() throws IOException {
        final Map<String, ExecParam> toReturn = new HashMap<>();

        //noinspection UnstableApiUsage
        final String configFileAsString =
                Resources.toString(Resources.getResource(pathToConfigFile), StandardCharsets.UTF_8);

        final List<Object> execParamCollectionAsObjectCollection = getObjectReader().readValues(configFileAsString)
                .readAll();

        for (Object object : execParamCollectionAsObjectCollection) {
            final ExecParam execParam = (ExecParam) object;
            toReturn.put(execParam.getKey(), execParam);
        }
        return toReturn;
    }

    public ObjectReader getObjectReader() {
        return objectReader;
    }
}