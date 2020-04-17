package org.mobilitydata.gtfsvalidator.db;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryExecParamRepository implements ExecParamRepository {
    private final Map<String, ExecParam> execParamCollection = new HashMap<>();

    @Override
    public ExecParam getExecParamByKey(final String execParamShortName) {
        return execParamCollection.get(execParamShortName);
    }

    @Override
    public Map<String, ExecParam> getExecParamCollection() {
        return Collections.unmodifiableMap(execParamCollection);
    }

    @Override
    public ExecParam addExecParam(final ExecParam newExecParam) {
        execParamCollection.put(newExecParam.getKey(), newExecParam);
        return newExecParam;
    }

    @Override
    public boolean hasExecParam(final String key) {
        return execParamCollection.containsKey(key);
    }

    @Override
    public ExecParamParser getParser(boolean fromConfigFile, final String pathToConfigFile, final String[] args) {
        if (!fromConfigFile) {
            return new ApacheExecParamParser(new DefaultParser(), new Options(), args);
        } else {
            return new JsonExecParamParser(pathToConfigFile);
        }
    }

    @Override
    public String getExecParamValue(String key) {
        return execParamCollection.get(key).getValue();
    }

    @Override
    public void setExecParamDefaultValue(String key, String value) {
        execParamCollection.get(key).setValue(value);
    }

    @Override
    public void setExecParamDefaultValue(String key, Boolean value) {
        execParamCollection.get(key).setValue(value);
    }

    @Override
    public String getExecParamDefaultValue(String key) {
        return execParamCollection.get(key).getDefaultValue();
    }

    @Override
    public void setDefaultValueOfMissingItem(String pathToDefaultConfigFile) throws IOException {
        Map<String, ExecParam> defaultValueCollection = new JsonExecParamParser(pathToDefaultConfigFile).parse();

        for (String key : execParamCollection.keySet()) {
            final ExecParam execParam = execParamCollection.get(key);

            if (execParam.getValue() == null) {
                //noinspection ResultOfMethodCallIgnored
                defaultValueCollection.get(key).getValue();
                execParam.setDefaultValue(defaultValueCollection.get(key).getDefaultValue());
            }
        }
        for (Map.Entry<String, ExecParam> entry : defaultValueCollection.entrySet()) {
            final String key = entry.getKey();
            if (!execParamCollection.containsKey(key)) {
                execParamCollection.put(key, entry.getValue());
            }
        }
    }
}