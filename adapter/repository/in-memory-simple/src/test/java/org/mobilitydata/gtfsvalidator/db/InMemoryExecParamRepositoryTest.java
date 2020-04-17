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
    private static final String KEY_0 = "key0";
    private static final String KEY_1 = "key1";

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
        doReturn(KEY_0).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByKey(KEY_0));
        assertEquals(mockExecParam1, underTest.getExecParamByKey(KEY_1));
    }

    @Test
    public void getExecParamCollectionShouldReturnExecParamCollection() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        final Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put(KEY_0, mockExecParam0);
        toCheck.put(KEY_1, mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());

    }

    @Test
    public void hasExecParamShouldReturnFalseIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefinedKey"));
    }

    @Test
    public void hasExecParamShouldReturnTrueIfExecParamIsNotPresent() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam(KEY_0));
        assertTrue(underTest.hasExecParam(KEY_1));
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
        when(mockExecParam0.getKey()).thenReturn(KEY_0);
        when(mockExecParam0.getValue()).thenReturn("value0");
        mockExecParam0.setKey(KEY_0);
        mockExecParam0.setValue("value0");

        final ExecParam mockExecParam1 = spy(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(KEY_1);
        when(mockExecParam1.getValue()).thenReturn("value1");
        mockExecParam0.setKey(KEY_1);
        mockExecParam0.setValue("value1");

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("value0", underTest.getExecParamValue(KEY_0));
        assertEquals("value1", underTest.getExecParamValue(KEY_1));
    }

    @Test
    void setExecParamDefaultValueShouldSetValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(KEY_0);
        when(mockExecParam0.getValue()).thenReturn("value0");
        when(mockExecParam0.getDefaultValue()).thenReturn("default value0");
        mockExecParam0.setKey(KEY_0);
        mockExecParam0.setValue("value0");
        mockExecParam0.setDefaultValue("default value0");

        final ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(KEY_1);
        when(mockExecParam1.getValue()).thenReturn("value1");
        when(mockExecParam1.getDefaultValue()).thenReturn("true");
        mockExecParam0.setKey(KEY_1);
        mockExecParam0.setValue("value1");
        mockExecParam0.setDefaultValue(true);

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("default value0", underTest.getExecParamDefaultValue(KEY_0));
        assertEquals("true", underTest.getExecParamDefaultValue(KEY_1));
    }

    @Test
    public void getExecParamDefaultValueShouldReturnDefaultValueOfRelatedExecParam() {
        final ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getKey()).thenReturn(KEY_0);
        when(mockExecParam0.getValue()).thenReturn("value0");
        when(mockExecParam0.getDefaultValue()).thenReturn("default value0");
        mockExecParam0.setKey(KEY_0);
        mockExecParam0.setValue("value0");
        mockExecParam0.setDefaultValue("default value0");


        final ExecParam mockExecParam1 = spy(ExecParam.class);
        when(mockExecParam1.getKey()).thenReturn(KEY_1);
        when(mockExecParam1.getValue()).thenReturn("value1");
        when(mockExecParam1.getDefaultValue()).thenReturn("default value1");
        mockExecParam0.setKey(KEY_1);
        mockExecParam0.setValue("value1");
        mockExecParam0.setDefaultValue("default value1");

        final ExecParamRepository underTest = new InMemoryExecParamRepository();

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals("default value0", underTest.getExecParamDefaultValue(KEY_0));
        assertEquals("default value1", underTest.getExecParamDefaultValue(KEY_1));
    }
}