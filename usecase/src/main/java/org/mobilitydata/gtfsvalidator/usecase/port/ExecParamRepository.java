package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.util.Map;

public interface ExecParamRepository {

    ExecParam getExecParamByKey(final String optionName);

    Map<String, ExecParam> getExecParamCollection();

    ExecParam addExecParam(final ExecParam newExecParam);

    boolean hasExecParam(String key);

    ExecParamParser getParser(boolean fromConfigFile, String pathToConfigFile, String[] args);

    String getExecParamValue(String key) throws IllegalArgumentException, IOException;

    Options getOptions();

    String getExecParamDefaultValue(String key);

    void setDefaultValueOfMissingItem(String pathToDefaultConfigFile) throws IOException;

    interface ExecParamParser {

        Map<String, ExecParam> parse() throws IOException;
    }
}