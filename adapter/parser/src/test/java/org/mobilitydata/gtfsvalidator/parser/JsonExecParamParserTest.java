package org.mobilitydata.gtfsvalidator.parser;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonExecParamParserTest {
    private final static String HELP_KEY = "help";
    private final static String INPUT_KEY = "input";
    private final static String OUTPUT_KEY = "output";

    @Test
    public void jsonFileShouldMapToExecutionParameterMap() throws IOException {
        final String pathToConfigFile = "test-config.json";
        final JsonExecParamParser underTest = new JsonExecParamParser(pathToConfigFile);

        final Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(3, toCheck.size());
        assertTrue(toCheck.containsKey(HELP_KEY));
        assertTrue(toCheck.containsKey(INPUT_KEY));
        assertTrue(toCheck.containsKey(OUTPUT_KEY));

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