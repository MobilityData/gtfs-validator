package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ParseAllExecParamTest {

    @Test
    public void allExecParamFromCommandLineShouldBeParsedAndAddedToRepoAsExecParamEntities()
            throws IOException {
        final boolean fromConfigFile = false;
        final String pathToConfigFile = null;
        final String[] mockString = new String[1];
        final ExecParamRepository mockExecParamRepository = mock(ExecParamRepository.class);

        final ParseAllExecParam underTest = new ParseAllExecParam(fromConfigFile,
                pathToConfigFile,
                mockExecParamRepository);

        final ExecParamRepository.ExecParamParser mockParser =
                spy(ExecParamRepository.ExecParamParser.class);

        final ExecParam mockHelpExecParam = spy(ExecParam.class);
        when(mockHelpExecParam.getParamKey()).thenReturn("help");

        final ExecParam mockInputExecParam = spy(ExecParam.class);
        when(mockInputExecParam.getParamKey()).thenReturn("input");

        final Map<String, ExecParam> mockExecutionParameterMap = new HashMap<>();
        mockExecutionParameterMap.put(mockHelpExecParam.getParamKey(), mockHelpExecParam);
        mockExecutionParameterMap.put(mockInputExecParam.getParamKey(), mockInputExecParam);

        when(mockExecParamRepository.getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null),
                ArgumentMatchers.eq(mockString)))
                .thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterMap);

        underTest.execute(mockString);

        final InOrder inOrder = inOrder(mockExecParamRepository, mockParser);

        inOrder.verify(mockExecParamRepository, times(1))
                .getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null),
                        ArgumentMatchers.eq(mockString));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecParamRepository, times(2))
                .addExecParam(ArgumentMatchers.isA(ExecParam.class));
    }

    @Test
    public void allExecParamFromConfigFileShouldBeParsedAndAddedToRepoAsExecParamEntities()
            throws IOException {
        final boolean fromConfigFile = true;
        final String pathToConfigFile = "test-config.json";
        final String[] mockString = new String[1];
        final ExecParamRepository mockExecParamRepository = mock(ExecParamRepository.class);

        final ParseAllExecParam underTest = new ParseAllExecParam(fromConfigFile,
                pathToConfigFile,
                mockExecParamRepository);

        final ExecParamRepository.ExecParamParser mockParser =
                spy(ExecParamRepository.ExecParamParser.class);

        final ExecParam mockHelpExecParam = spy(ExecParam.class);
        when(mockHelpExecParam.getParamKey()).thenReturn("help");

        final ExecParam mockInputExecParam = spy(ExecParam.class);
        when(mockInputExecParam.getParamKey()).thenReturn("input");

        final Map<String, ExecParam> mockExecutionParameterMap = new HashMap<>();
        mockExecutionParameterMap.put(mockHelpExecParam.getParamKey(), mockHelpExecParam);
        mockExecutionParameterMap.put(mockInputExecParam.getParamKey(), mockInputExecParam);

        when(mockExecParamRepository.getParser(ArgumentMatchers.eq(true), ArgumentMatchers.eq(pathToConfigFile),
                ArgumentMatchers.eq(mockString)))
                .thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterMap);

        underTest.execute(mockString);

        final InOrder inOrder = inOrder(mockExecParamRepository, mockParser);

        inOrder.verify(mockExecParamRepository, times(1))
                .getParser(ArgumentMatchers.eq(true), ArgumentMatchers.eq(pathToConfigFile),
                        ArgumentMatchers.eq(mockString));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecParamRepository, times(2))
                .addExecParam(ArgumentMatchers.isA(ExecParam.class));
    }
}