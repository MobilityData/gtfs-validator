package org.mobilitydata.gtfsvalidator.db;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mockito.InOrder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("UnstableApiUsage")
class InMemoryExecParamRepositoryTest {
    private static final String HELP_KEY = "help";
    private static final String INPUT_KEY = "extract";
    private static final String OUTPUT_KEY = "output";
    private static final String PROTO_KEY = "proto";
    private static final String URL_KEY = "url";

    @Test
    void addExecParamWithNullKeyShouldThrowException() throws IOException {
        final ExecParam mockExecParam = mock(ExecParam.class);

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.addExecParam(mockExecParam));

        assertEquals("Execution parameter with key: null found in configuration file is not handled",
                exception.getMessage());
    }

    @Test
    void addExecParamWithUnhandledKeyShouldThrowException() throws IOException {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setParamKey("unhandled key");

        final Logger mockLogger = mock(Logger.class);

        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.addExecParam(mockExecParam));

        assertEquals("Execution parameter with key: unhandled key found in configuration file is not handled",
                exception.getMessage());
    }

    @Test
    void addExecParamWithHandledKeyShouldAddExecParamToRepoAndReturnSameEntity() throws IOException {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setParamKey(INPUT_KEY);

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        final ExecParam toCheck = underTest.addExecParam(mockExecParam);

        final InOrder inOrder = inOrder(mockExecParam);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecParam, times(2)).getParamKey();

        assertEquals(1, underTest.getExecParamCollection().size());

        assertEquals(toCheck, mockExecParam);
    }

    @Test
    void getExecParamByKeyShouldReturnRelatedExecParam() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByKey(HELP_KEY));
        assertEquals(mockExecParam1, underTest.getExecParamByKey(OUTPUT_KEY));
    }

    @Test
    void getExecParamCollectionShouldReturnExecParamCollection() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        final Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put(HELP_KEY, mockExecParam0);
        toCheck.put(OUTPUT_KEY, mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());

    }

    @Test
    void hasExecParamShouldReturnFalseIfExecParamIsNotPresent() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefinedKey"));
    }

    @Test
    void hasExecParamShouldReturnTrueIfExecParamIsPresent() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam(HELP_KEY));
        assertTrue(underTest.hasExecParam(OUTPUT_KEY));
    }

    @Test
    void getParserShouldReturnApacheExecParamParser() throws IOException {
        final String[] mockString = new String[1];

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(null,
                mockString, mockLogger);
        assertTrue(toCheck instanceof ApacheExecParamParser);
    }

    @Test
    void getParserShouldReturnJsonExecParamParser() throws IOException {
        final String[] mockString = new String[0];
        final String mockExecParam = "";

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(mockExecParam, mockString, mockLogger);
        assertTrue(toCheck instanceof JsonExecParamParser);
    }

    @Test
    void getExecParamValueShouldReturnValueOfRelatedExecParam() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getParamKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getParamValue()).thenReturn("help value");
        mockExecParam0.setParamKey(HELP_KEY);
        mockExecParam0.setParamValue("help value");

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getParamKey()).thenReturn(INPUT_KEY);
        when(mockExecParam1.getParamValue()).thenReturn("input value");
        mockExecParam0.setParamKey(INPUT_KEY);
        mockExecParam0.setParamValue("input value");

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("true", underTest.getExecParamValue(HELP_KEY));
        assertEquals("input value", underTest.getExecParamValue(INPUT_KEY));
    }

    @Test
    void getExecParamShouldReturnDefaultValueForMissingExecParam() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getParamKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getParamValue()).thenReturn("help value");
        mockExecParam0.setParamKey(HELP_KEY);
        mockExecParam0.setParamValue("help value");

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getParamKey()).thenReturn(INPUT_KEY);
        when(mockExecParam1.getParamValue()).thenReturn("input value");
        mockExecParam0.setParamKey(INPUT_KEY);
        mockExecParam0.setParamValue("input value");

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("true", underTest.getExecParamValue(HELP_KEY));
        assertEquals("input value", underTest.getExecParamValue(INPUT_KEY));
        assertEquals(System.getProperty("user.dir") + File.separator + "test output value",
                underTest.getExecParamValue(OUTPUT_KEY));
        assertEquals("test proto value", underTest.getExecParamValue(PROTO_KEY));
        assertEquals("test url value", underTest.getExecParamValue(URL_KEY));
    }

    @Test
    void getExecParamShouldReturnDefaultValueForMissingExecParamValue() throws IOException {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getParamKey()).thenReturn(HELP_KEY);
        mockExecParam0.setParamKey(HELP_KEY);

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getParamKey()).thenReturn(INPUT_KEY);
        mockExecParam0.setParamKey(INPUT_KEY);

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("test help value", underTest.getExecParamValue(HELP_KEY));
        assertEquals(System.getProperty("user.dir") + File.separator + "test input value",
                underTest.getExecParamValue(INPUT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnTrueIfExecParamIsPresentAndParamValueFieldIsNotNull() throws IOException {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(INPUT_KEY).when(mockExecParam0).getParamKey();
        mockExecParam0.setParamValue("input");

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();
        mockExecParam1.setParamValue("output");

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParamValue(INPUT_KEY));
        assertTrue(underTest.hasExecParamValue(OUTPUT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnFalseIfExecParamIsPresentAndParamValueFieldIsNull() throws IOException {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(INPUT_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParamValue(INPUT_KEY));
        assertFalse(underTest.hasExecParamValue(OUTPUT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnFalseIfExecParamIsNotPresent() throws IOException {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(INPUT_KEY).when(mockExecParam0).getParamKey();

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getParamKey();

        final Logger mockLogger = mock(Logger.class);
        final String defaultParameterJson = Resources.toString(
                Resources.getResource("test-default-execution-parameters.json"),
                StandardCharsets.UTF_8);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(defaultParameterJson, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParamValue(PROTO_KEY));
    }
}