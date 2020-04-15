package org.mobilitydata.gtfsvalidator.parser;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonExecParamParserTest {

    @SuppressWarnings("UnstableApiUsage")
    @Test
    public void jsonFileMapToExecutionParameterMap() throws IOException {
        String pathToConfigFile = "test-config.json";

        String configFile = Resources.toString(Resources.getResource(pathToConfigFile), StandardCharsets.UTF_8);
        JsonExecParamParser underTest = new JsonExecParamParser(configFile);

        Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(3, toCheck.size());
        assertTrue(toCheck.containsKey("help"));
        assertTrue(toCheck.containsKey("input"));
        assertTrue(toCheck.containsKey("output"));

        ExecParam toTest = toCheck.get("help");
        assertEquals(toTest.getShortName(), "h");
        assertEquals(toTest.getLongName(), "help");
        assertEquals(toTest.getDescription(), "Print this message");
        assertFalse(toTest.hasArgument());

        toTest = toCheck.get("input");
        assertEquals(toTest.getShortName(), "i");
        assertEquals(toTest.getLongName(), "input");
        assertEquals(toTest.getDescription(), "Relative path where to place extract the zip content");
        assertTrue(toTest.hasArgument());
        assertEquals(toTest.getValue(), "input");

        toTest = toCheck.get("output");
        assertEquals(toTest.getShortName(), "o");
        assertEquals(toTest.getLongName(), "output");
        assertEquals(toTest.getDescription(), "Relative path where to place output files");
        assertTrue(toTest.hasArgument());
        assertEquals(toTest.getValue(), "output");
    }
}