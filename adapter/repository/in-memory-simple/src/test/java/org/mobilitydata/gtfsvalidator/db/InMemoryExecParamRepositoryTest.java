package org.mobilitydata.gtfsvalidator.db;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mockito.InOrder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryExecParamRepositoryTest {
    private static final String HELP_KEY = "help";
    private static final String EXTRACT_KEY = "extract";
    private static final String OUTPUT_KEY = "output";
    private static final String PROTO_KEY = "proto";
    private static final String URL_KEY = "url";
    private static final String EXCLUSION_KEY = "exclude";
    private static final String DEFAULT_EXEC_PARAMETERS = "{\n" + "  \"help\": false,\n" + "  \"extract\": \"input\"" +
            ",\n" + "  \"output\": \"output\",\n" + "  \"proto\": false,\n" + "  \"url\": null, \n" +
            "  \"zipinput\": null,\n" + "  \"exclude\": []\n" + "}";

    @Test
    void addExecParamWithNullKeyShouldThrowException() {
        final ExecParam mockExecParam = mock(ExecParam.class);

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.addExecParam(mockExecParam));

        assertEquals("Execution parameter with key: null found in configuration file is not handled",
                exception.getMessage());
    }

    @Test
    void addExecParamWithUnhandledKeyShouldThrowException() {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setKey("unhandled key");

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.addExecParam(mockExecParam));

        assertEquals("Execution parameter with key: unhandled key found in configuration file is not handled",
                exception.getMessage());
    }

    @Test
    void addExecParamWithHandledKeyShouldAddExecParamToRepoAndReturnSameEntity() {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setKey(EXTRACT_KEY);

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        final ExecParam toCheck = underTest.addExecParam(mockExecParam);

        final InOrder inOrder = inOrder(mockExecParam);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecParam, times(2)).getKey();

        assertEquals(1, underTest.getExecParamCollection().size());

        assertEquals(toCheck, mockExecParam);
    }

    @Test
    void getExecParamByKeyShouldReturnRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByKey(HELP_KEY));
        assertEquals(mockExecParam1, underTest.getExecParamByKey(OUTPUT_KEY));
    }

    @Test
    void getExecParamCollectionShouldReturnExecParamCollection() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        final Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put(HELP_KEY, mockExecParam0);
        toCheck.put(OUTPUT_KEY, mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());
    }

    @Test
    void hasExecParamShouldReturnFalseIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefinedKey"));
    }

    @Test
    void hasExecParamShouldReturnTrueIfExecParamIsPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(HELP_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam(HELP_KEY));
        assertTrue(underTest.hasExecParam(OUTPUT_KEY));
    }

    @Test
    void getParserShouldReturnApacheExecParamParser() {
        final String[] mockString = new String[1];

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(null,
                mockString, mockLogger);
        assertTrue(toCheck instanceof ApacheExecParamParser);
    }

    @Test
    void getParserShouldReturnJsonExecParamParser() {
        final String[] mockString = new String[0];
        final String mockExecParam = "";

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        final ExecParamRepository.ExecParamParser toCheck = underTest.getParser(mockExecParam, mockString, mockLogger);
        assertTrue(toCheck instanceof JsonExecParamParser);
    }

    @Test
    void getExecParamValueShouldReturnValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        when(mockExecParam0.getValue()).thenReturn("help value");
        mockExecParam0.setKey(HELP_KEY);
        mockExecParam0.setValue(new String[]{"help value"});

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(EXTRACT_KEY);
        when(mockExecParam1.getValue()).thenReturn("input value");
        mockExecParam0.setKey(EXTRACT_KEY);
        mockExecParam0.setValue(new String[]{"input value"});

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("true", underTest.getExecParamValue(HELP_KEY));
        assertEquals("input value", underTest.getExecParamValue(EXTRACT_KEY));
    }

    @Test
    void getExecParamShouldReturnDefaultValueForMissingExecParam() {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        mockExecParam0.setKey(HELP_KEY);
        mockExecParam0.setValue(new String[]{"help value"});

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        mockExecParam1.setKey(EXTRACT_KEY);
        mockExecParam1.setValue(new String[]{"input value"});

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("true", underTest.getExecParamValue(HELP_KEY));
        assertEquals("input value", underTest.getExecParamValue(EXTRACT_KEY));
        assertEquals(System.getProperty("user.dir") + File.separator + "output",
                underTest.getExecParamValue(OUTPUT_KEY));
        assertEquals("false", underTest.getExecParamValue(PROTO_KEY));
        assertEquals("null", underTest.getExecParamValue(URL_KEY));
    }

    @Test
    void getExecParamShouldReturnDefaultValueForMissingExecParamValue() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(HELP_KEY);
        mockExecParam0.setKey(HELP_KEY);

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(EXTRACT_KEY);
        mockExecParam1.setKey(EXTRACT_KEY);

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("true", underTest.getExecParamValue(HELP_KEY));
        assertEquals(System.getProperty("user.dir") + File.separator + "input",
                underTest.getExecParamValue(EXTRACT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnTrueIfExecParamIsPresentAndParamValueFieldIsNotNull() {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        mockExecParam0.setKey(EXTRACT_KEY);
        mockExecParam0.setValue(new String[]{"input"});

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        mockExecParam1.setKey(OUTPUT_KEY);
        mockExecParam1.setValue(new String[]{"output"});

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParamValue(EXTRACT_KEY));
        assertTrue(underTest.hasExecParamValue(OUTPUT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnFalseIfExecParamIsPresentAndParamValueFieldIsNull() {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(EXTRACT_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParamValue(EXTRACT_KEY));
        assertFalse(underTest.hasExecParamValue(OUTPUT_KEY));
    }

    @Test
    void hasExecParamValueShouldReturnFalseIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(EXTRACT_KEY).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(OUTPUT_KEY).when(mockExecParam1).getKey();

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParamValue(PROTO_KEY));
    }

    @Test
    void getExecParamOnExclusionKeyShouldReturnListOfSeveralStringsAsString() {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setKey(EXCLUSION_KEY);
        mockExecParam.setValue(new String[]{"[file0, file1, file2]"});

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam);

        assertTrue(underTest.hasExecParam(EXCLUSION_KEY));
        assertTrue(underTest.hasExecParamValue(EXCLUSION_KEY));
        assertEquals("[file0, file1, file2]", underTest.getExecParamValue(EXCLUSION_KEY));
    }

    @Test
    void getExecParamOnExclusionKeyShouldReturnListOfSingleStringAsString() {
        final ExecParam mockExecParam = spy(ExecParam.class);
        mockExecParam.setKey(EXCLUSION_KEY);
        mockExecParam.setValue(new String[]{"[file0]"});

        final Logger mockLogger = mock(Logger.class);
        final ExecParamRepository underTest = new InMemoryExecParamRepository(DEFAULT_EXEC_PARAMETERS, mockLogger);

        underTest.addExecParam(mockExecParam);

        assertTrue(underTest.hasExecParam(EXCLUSION_KEY));
        assertTrue(underTest.hasExecParamValue(EXCLUSION_KEY));
        assertEquals("[file0]", underTest.getExecParamValue(EXCLUSION_KEY));
    }
}