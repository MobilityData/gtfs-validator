package org.mobilitydata.gtfsvalidator.execution;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecutionParameterParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;
import org.mockito.InOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryExecutionParameterRepositoryTest {

    @Test
    public void addExecutionParameterShouldAddItemToRepoAndReturnSameItem() {
        String[] mockString = new String[1];

        ExecutionParameter mockExecutionParameter = mock(ExecutionParameter.class);

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        ExecutionParameter toCheck = underTest.addExecutionParameter(mockExecutionParameter);

        InOrder inOrder = inOrder(mockExecutionParameter);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecutionParameter, times(1)).getShortName();

        assertEquals(1, underTest.getExecutionParameterCollection().size());

        assertEquals(toCheck, mockExecutionParameter);
    }

    @Test
    public void getExecutionParameterByShortNameShouldReturnRelatedExecutionParameter() {
        String[] mockString = new String[1];

        ExecutionParameter mockExecutionParameter0 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecutionParameter0).getShortName();

        ExecutionParameter mockExecutionParameter1 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecutionParameter1).getShortName();

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        underTest.addExecutionParameter(mockExecutionParameter0);
        underTest.addExecutionParameter(mockExecutionParameter1);

        assertEquals(mockExecutionParameter0, underTest.getExecutionParameterByShortName("string_test_value0"));
        assertEquals(mockExecutionParameter1, underTest.getExecutionParameterByShortName("string_test_value1"));
    }

    @Test
    public void getExecutionParameterCollectionShouldReturnExecutionParameterCollection() {
        String[] mockString = new String[1];

        ExecutionParameter mockExecutionParameter0 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecutionParameter0).getShortName();

        ExecutionParameter mockExecutionParameter1 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecutionParameter1).getShortName();

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        underTest.addExecutionParameter(mockExecutionParameter0);
        underTest.addExecutionParameter(mockExecutionParameter1);

        Map<String, ExecutionParameter> toCheck = new HashMap<>();
        toCheck.put("string_test_value0", mockExecutionParameter0);
        toCheck.put("string_test_value1", mockExecutionParameter1);

        assertEquals(toCheck, underTest.getExecutionParameterCollection());

    }

    @Test
    public void hasExecutionParameterShouldReturnFalseIfExecutionParameterIsNotPresent() {
        String[] mockString = new String[1];

        ExecutionParameter mockExecutionParameter0 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecutionParameter0).getShortName();

        ExecutionParameter mockExecutionParameter1 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecutionParameter1).getShortName();

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        underTest.addExecutionParameter(mockExecutionParameter0);
        underTest.addExecutionParameter(mockExecutionParameter1);

        assertFalse(underTest.hasExecutionParameter("undefined_execution_parameter_short_name"));
    }

    @Test
    public void hasExecutionParameterShouldReturnTrueIfExecutionParameterIsNotPresent() {
        String[] mockString = new String[1];

        ExecutionParameter mockExecutionParameter0 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value0").when(mockExecutionParameter0).getShortName();

        ExecutionParameter mockExecutionParameter1 = mock(ExecutionParameter.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn("string_test_value1").when(mockExecutionParameter1).getShortName();

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        underTest.addExecutionParameter(mockExecutionParameter0);
        underTest.addExecutionParameter(mockExecutionParameter1);

        assertTrue(underTest.hasExecutionParameter("string_test_value0"));
        assertTrue(underTest.hasExecutionParameter("string_test_value1"));
    }

    @Test
    public void getParserShouldReturnApacheExecutionParameterParser() {
        String[] mockString = new String[1];

        ExecutionParameterRepository underTest = new InMemoryExecutionParameterRepository(mockString);

        ExecutionParameterRepository.ExecutionParameterParser toCheck = underTest.getParser(false,
                null);
        //noinspection ConstantConditions
        assertTrue(toCheck instanceof ApacheExecutionParameterParser);
    }
}