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
        String[] mockString = new String[1];

        ExecParam mockExecParam = mock(ExecParam.class);

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParam toCheck = underTest.addExecParam(mockExecParam);

        InOrder inOrder = inOrder(mockExecParam);

        //noinspection ResultOfMethodCallIgnored
        inOrder.verify(mockExecParam, times(1)).getKey();

        assertEquals(1, underTest.getExecParamCollection().size());

        assertEquals(toCheck, mockExecParam);
    }

    @Test
    public void getExecParamByKeyShouldReturnRelatedExecParam() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertEquals(mockExecParam0, underTest.getExecParamByKey(KEY_0));
        assertEquals(mockExecParam1, underTest.getExecParamByKey(KEY_1));
    }

    @Test
    public void getExecParamCollectionShouldReturnExecParamCollection() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        Map<String, ExecParam> toCheck = new HashMap<>();
        toCheck.put(KEY_0, mockExecParam0);
        toCheck.put(KEY_1, mockExecParam1);

        assertEquals(toCheck, underTest.getExecParamCollection());

    }

    @Test
    public void hasExecParamShouldReturnFalseIfExecParamIsNotPresent() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertFalse(underTest.hasExecParam("undefinedKey"));
    }

    @Test
    public void hasExecParamShouldReturnTrueIfExecParamIsNotPresent() {
        String[] mockString = new String[1];

        ExecParam mockExecParam0 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_0).when(mockExecParam0).getKey();

        ExecParam mockExecParam1 = mock(ExecParam.class);
        //noinspection ResultOfMethodCallIgnored
        doReturn(KEY_1).when(mockExecParam1).getKey();

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        underTest.addExecParam(mockExecParam0);
        underTest.addExecParam(mockExecParam1);

        assertTrue(underTest.hasExecParam(KEY_0));
        assertTrue(underTest.hasExecParam(KEY_1));
    }

    @Test
    public void getParserShouldReturnApacheExecParamParser() {
        String[] mockString = new String[1];

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParamRepository.ExecParamParser toCheck = underTest.getParser(false,
                null);
        assertTrue(toCheck instanceof ApacheExecParamParser);
    }

    @Test
    public void getParserShouldReturnJsonExecParamParser() {
        String[] mockString = new String[1];

        ExecParamRepository underTest = new InMemoryExecParamRepository(mockString);

        ExecParamRepository.ExecParamParser toCheck = underTest.getParser(true,
                null);
        assertTrue(toCheck instanceof JsonExecParamParser);
    }
}