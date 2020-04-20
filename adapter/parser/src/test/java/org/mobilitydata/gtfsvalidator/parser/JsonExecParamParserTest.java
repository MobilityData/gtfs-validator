package org.mobilitydata.gtfsvalidator.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonExecParamParserTest {
    private final static String HELP_KEY = "help";
    private final static String INPUT_KEY = "input";
    private final static String OUTPUT_KEY = "output";

    @Test
    public void jsonFileShouldMapToExecutionParameterMap() throws IOException {
        final String pathToConfigFile = "test-config.json";
        final ObjectReader mockObjectReader = mock(ObjectReader.class);

        final ExecParam mockHelpExecParam = spy(ExecParam.class);
        mockHelpExecParam.setParamKey(HELP_KEY);

        final ExecParam mockInputExecParam = spy(ExecParam.class);
        mockInputExecParam.setParamKey(INPUT_KEY);
        mockInputExecParam.setParamValue("input");

        final ExecParam mockOutputExecParam = spy(ExecParam.class);
        mockOutputExecParam.setParamKey(OUTPUT_KEY);
        mockOutputExecParam.setParamValue("output");

        final List<Object> objectCollection = new ArrayList<>();

        objectCollection.add(mockHelpExecParam);
        objectCollection.add(mockInputExecParam);
        objectCollection.add(mockOutputExecParam);

        @SuppressWarnings("rawtypes") final MappingIterator mockIterator = mock(MappingIterator.class);
        //noinspection unchecked
        when(mockObjectReader.readValues(anyString())).thenReturn(mockIterator);
        when(mockIterator.readAll()).thenReturn(objectCollection);

        final JsonExecParamParser underTest = new JsonExecParamParser(pathToConfigFile, mockObjectReader);
        final Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(3, toCheck.size());
        assertTrue(toCheck.containsKey(HELP_KEY));
        assertTrue(toCheck.containsKey(INPUT_KEY));
        assertTrue(toCheck.containsKey(OUTPUT_KEY));

        ExecParam toTest = toCheck.get("help");
        assertEquals(HELP_KEY, toTest.getParamKey());
        assertNull(toTest.getParamValue());

        toTest = toCheck.get("input");
        assertEquals(INPUT_KEY, toTest.getParamKey());
        assertEquals("input", toTest.getParamValue());

        toTest = toCheck.get("output");
        assertEquals(OUTPUT_KEY, toTest.getParamKey());
        assertEquals(toTest.getParamValue(), "output");
    }
}