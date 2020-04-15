package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mockito.InOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryExecParamRepositoryTest {

    @Test
    public void addExecParamShouldAddItemToRepoAndReturnSameItem() {
        String[] mockString = new String[1];

        ExecParam mockExecParam = mock(ExecParam.class);

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParam toCheck = underTest.addExecParam(mockExecParam);

        InOrder inOrder = inOrder(mockExecParam);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecParam, times(1)).getShortName();

        assertEquals(1, underTest.getExecParamCollection().size());

        assertEquals(toCheck, mockExecParam);
    }

    @Test
    public void getExecParamByShortNameShouldReturnRelatedExecutionParameter() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecParam0).getShortName();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecParam1).getShortName();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByShortName("string_test_value0"));
        assertEquals(mockExecParam1, underTest.getExecParamByShortName("string_test_value1"));
    }

    @Test
    public void getExecParamCollectionShouldReturnExecutionParameterCollection() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecParam0).getShortName();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecParam1).getShortName();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put("string_test_value0", mockExecParam0);
        toCheck.put("string_test_value1", mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());

    }

    @Test
    public void hasExecParamShouldReturnFalseIfExecutionParameterIsNotPresent() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecParam0).getShortName();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecParam1).getShortName();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefined_execution_parameter_short_name"));
    }

    @Test
    public void hasExecParamShouldReturnTrueIfExecutionParameterIsNotPresent() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecParam0).getShortName();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecParam1).getShortName();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam("string_test_value0"));
        assertTrue(underTest.hasExecParam("string_test_value1"));
    }

    @Test
    public void getParserShouldReturnApacheExecutionParameterParser() {
        String[] mockString = new String[1];

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParamRepository.ExecParamParser toCheck = underTest.getParser(false,
                null);
        assertTrue(toCheck instanceof ApacheExecParamParser);
    }

    @Test
    public void getParserShouldReturnJsonExecutionParameterParser() {
        String[] mockString = new String[1];

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParamRepository.ExecParamParser toCheck = underTest.getParser(true,
                null);
        assertTrue(toCheck instanceof JsonExecParamParser);
    }
}