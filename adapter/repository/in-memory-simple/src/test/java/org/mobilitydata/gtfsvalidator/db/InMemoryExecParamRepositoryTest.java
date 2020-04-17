package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryExecParamRepositoryTest {
    private static final String HELP_KEY = "help";
    private static final String INPUT_KEY = "input";
    private static final String OUTPUT_KEY = "output";
    private static final String PROTO_KEY = "proto";
    private static final String URL_KEY = "url";
    private static final String ZIP_KEY = "zip";

    @Test
    public void addExecParamShouldAddItemToRepoAndReturnSameItem() {
        final ExecParam mockExecParam = mock(ExecParam.class);

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        final ExecParam toCheck = underTest.addExecParam(mockExecParam);

        final InOrder inOrder = inOrder(mockExecParam);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecParam, times(1)).getKey();

        assertEquals(1, underTest.getExecParamCollection().size());

        assertEquals(toCheck, mockExecParam);
    }

    @Test
    public void getExecParamByKeyShouldReturnRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByKey(HELP_KEY));
        assertEquals(mockExecParam1, underTest.getExecParamByKey(OUTPUT_KEY));
    }

    @Test
    public void getExecParamCollectionShouldReturnExecParamCollection() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        final Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put(HELP_KEY, mockExecParam0);
        toCheck.put(OUTPUT_KEY, mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());

    }

    @Test
    public void hasExecParamShouldReturnFalseIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefinedKey"));
    }

    @Test
    public void hasExecParamShouldReturnTrueIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam(HELP_KEY));
        assertTrue(underTest.hasExecParam(OUTPUT_KEY));
    }

    @Test
    public void getParserShouldReturnApacheExecParamParser() {
        final String[] mockString = new String[1];

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(false, null,
                mockString);
        assertTrue(toCheck instanceof ApacheExecParamParser);
    }

    @Test
    public void getParserShouldReturnJsonExecParamParser() {
        final String[] mockString = new String[1];

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(true, null,
                mockString);
        assertTrue(toCheck instanceof JsonExecParamParser);
    }

    @Test
    public void getExecParamValueShouldReturnValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getValue()).thenReturn("value0");
        mockExecParam0.setKey(HELP_KEY);
        mockExecParam0.setValue("value0");

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(INPUT_KEY);
        when(mockExecParam1.getValue()).thenReturn("value1");
        mockExecParam0.setKey(INPUT_KEY);
        mockExecParam0.setValue("value1");

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("value0", underTest.getExecParamValue(HELP_KEY));
        assertEquals("value1", underTest.getExecParamValue(INPUT_KEY));
    }

    @Test
    void setExecParamDefaultValueShouldSetValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getValue()).thenReturn("value0");
        when(mockExecParam0.getDefaultValue()).thenReturn("default value0");
        mockExecParam0.setKey(HELP_KEY);
        mockExecParam0.setValue("value0");
        mockExecParam0.setDefaultValue("default value0");

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(INPUT_KEY);
        when(mockExecParam1.getValue()).thenReturn("value1");
        when(mockExecParam1.getDefaultValue()).thenReturn("true");
        mockExecParam0.setKey(INPUT_KEY);
        mockExecParam0.setValue("value1");
        mockExecParam0.setDefaultValue(true);

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("default value0", underTest.getExecParamDefaultValue(HELP_KEY));
        assertEquals("true", underTest.getExecParamDefaultValue(INPUT_KEY));
    }

    @Test
    public void getExecParamDefaultValueShouldReturnDefaultValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getValue()).thenReturn("value0");
        when(mockExecParam0.getDefaultValue()).thenReturn("default value0");
        mockExecParam0.setKey(HELP_KEY);
        mockExecParam0.setValue("value0");
        mockExecParam0.setDefaultValue("default value0");


        final ExecParam mockExecParam1 = spy(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(INPUT_KEY);
        when(mockExecParam1.getValue()).thenReturn("value1");
        when(mockExecParam1.getDefaultValue()).thenReturn("default value1");
        mockExecParam0.setKey(INPUT_KEY);
        mockExecParam0.setValue("value1");
        mockExecParam0.setDefaultValue("default value1");

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("default value0", underTest.getExecParamDefaultValue(HELP_KEY));
        assertEquals("default value1", underTest.getExecParamDefaultValue(INPUT_KEY));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void completeExecParamRepoShouldNotChangeWhenCallingGetMissingValues() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        final ExecParam mockExecParam1 = mock(ExecParam.class);
        final ExecParam mockExecParam2 = mock(ExecParam.class);
        final ExecParam mockExecParam3 = mock(ExecParam.class);
        final ExecParam mockExecParam4 = mock(ExecParam.class);
        final ExecParam mockExecParam5 = mock(ExecParam.class);

        doReturn(HELP_KEY).when(mockExecParam0).getKey();
        doReturn("help").when(mockExecParam0).getValue();
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();
        doReturn("output").when(mockExecParam1).getValue();
        doReturn(INPUT_KEY).when(mockExecParam2).getKey();
        doReturn("input").when(mockExecParam2).getValue();
        doReturn(PROTO_KEY).when(mockExecParam3).getKey();
        doReturn("false").when(mockExecParam3).getValue();
        doReturn(URL_KEY).when(mockExecParam4).getKey();
        doReturn("test url").when(mockExecParam4).getValue();
        doReturn(ZIP_KEY).when(mockExecParam5).getKey();
        doReturn("test zip").when(mockExecParam5).getValue();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);
        underTest.addExecParam(mockExecParam2);
        underTest.addExecParam(mockExecParam3);
        underTest.addExecParam(mockExecParam4);
        underTest.addExecParam(mockExecParam5);

        underTest.setDefaultValueOfMissingItem("test-default-config.json");

        assertEquals(6, underTest.getExecParamCollection().size());

        assertTrue(underTest.hasExecParam("help"));
        assertTrue(underTest.hasExecParam("input"));
        assertTrue(underTest.hasExecParam("output"));
        assertTrue(underTest.hasExecParam("proto"));
        assertTrue(underTest.hasExecParam("url"));
        assertTrue(underTest.hasExecParam("zip"));

        assertEquals("help", underTest.getExecParamByKey("help").getValue());
        assertEquals("input", underTest.getExecParamByKey("input").getValue());
        assertEquals("output", underTest.getExecParamByKey("output").getValue());
        assertEquals("false", underTest.getExecParamByKey("proto").getValue());
        assertEquals("test url", underTest.getExecParamByKey("url").getValue());
        assertEquals("test zip", underTest.getExecParamByKey("zip").getValue());
    }

    @Test
    void missingValueFromExecParamShouldPlaceDefaultValues() throws IOException {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        mockExecParam0.setKey(HELP_KEY);

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        mockExecParam1.setKey(INPUT_KEY);

        final ExecParam mockExecParam2 = spy(ExecParam.class);
        mockExecParam2.setKey(OUTPUT_KEY);

        final ExecParam mockExecParam3 = spy(ExecParam.class);
        mockExecParam3.setKey(PROTO_KEY);

        final ExecParam mockExecParam4 = spy(ExecParam.class);
        mockExecParam4.setKey(URL_KEY);

        final ExecParam mockExecParam5 = spy(ExecParam.class);
        mockExecParam5.setKey(ZIP_KEY);

        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        when(mockExecParam1.getKey()).thenReturn(INPUT_KEY);
        when(mockExecParam2.getKey()).thenReturn(OUTPUT_KEY);
        when(mockExecParam3.getKey()).thenReturn(PROTO_KEY);
        when(mockExecParam4.getKey()).thenReturn(URL_KEY);
        when(mockExecParam5.getKey()).thenReturn(ZIP_KEY);

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);
        underTest.addExecParam(mockExecParam2);
        underTest.addExecParam(mockExecParam3);
        underTest.addExecParam(mockExecParam4);
        underTest.addExecParam(mockExecParam5);

        underTest.setDefaultValueOfMissingItem("test-default-config.json");

        underTest.getExecParamCollection();

        assertEquals(6, underTest.getExecParamCollection().size());

        assertTrue(underTest.hasExecParam("help"));
        assertTrue(underTest.hasExecParam("input"));
        assertTrue(underTest.hasExecParam("output"));
        assertTrue(underTest.hasExecParam("proto"));
        assertTrue(underTest.hasExecParam("url"));
        assertTrue(underTest.hasExecParam("zip"));

        assertEquals("test help value", underTest.getExecParamByKey("help").getDefaultValue());
        assertEquals("test input value", underTest.getExecParamByKey("input").getDefaultValue());
        assertEquals("test output value", underTest.getExecParamByKey("output").getDefaultValue());
        assertEquals("test proto value", underTest.getExecParamByKey("proto").getDefaultValue());
        assertEquals("test url value", underTest.getExecParamByKey("url").getDefaultValue());
        assertEquals("test zip value", underTest.getExecParamByKey("zip").getDefaultValue());
    }

    @Test
    void setDefaultValuesOnEmptyExecParamRepoShouldSetDefaultValuesFromDefaultConfigFile() throws IOException {
        final ExecParamRepository underTest = new InMemoryExecParamRepository();
        underTest.setDefaultValueOfMissingItem("test-default-config.json");

        assertEquals(6, underTest.getExecParamCollection().size());

        assertTrue(underTest.hasExecParam("help"));
        assertTrue(underTest.hasExecParam("input"));
        assertTrue(underTest.hasExecParam("output"));
        assertTrue(underTest.hasExecParam("proto"));
        assertTrue(underTest.hasExecParam("url"));
        assertTrue(underTest.hasExecParam("zip"));

        assertEquals("test help value", underTest.getExecParamByKey("help").getDefaultValue());
        assertEquals("test input value", underTest.getExecParamByKey("input").getDefaultValue());
        assertEquals("test output value", underTest.getExecParamByKey("output").getDefaultValue());
        assertEquals("test proto value", underTest.getExecParamByKey("proto").getDefaultValue());
        assertEquals("test url value", underTest.getExecParamByKey("url").getDefaultValue());
        assertEquals("test zip value", underTest.getExecParamByKey("zip").getDefaultValue());
    }
}