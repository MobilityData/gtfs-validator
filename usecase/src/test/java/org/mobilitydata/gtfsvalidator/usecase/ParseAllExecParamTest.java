package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
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
        final String jsonParameterString = null;
        final String[] mockString = new String[1];
        final ExecParamRepository mockExecParamRepository = mock(ExecParamRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ParseAllExecParam underTest = new ParseAllExecParam(jsonParameterString, mockExecParamRepository,
                mockLogger);

        final ExecParamRepository.ExecParamParser mockParser =
                spy(ExecParamRepository.ExecParamParser.class);

        final ExecParam mockHelpExecParam = spy(ExecParam.class);
        when(mockHelpExecParam.getKey()).thenReturn("help");

        final ExecParam mockInputExecParam = spy(ExecParam.class);
        when(mockInputExecParam.getKey()).thenReturn("input");

        final Map<String, ExecParam> mockExecutionParameterMap = new HashMap<>();
        mockExecutionParameterMap.put(mockHelpExecParam.getKey(), mockHelpExecParam);
        mockExecutionParameterMap.put(mockInputExecParam.getKey(), mockInputExecParam);

        when(mockExecParamRepository.getParser(ArgumentMatchers.eq(null),
                ArgumentMatchers.eq(mockString), ArgumentMatchers.eq(mockLogger)))
                .thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterMap);

        underTest.execute(mockString);

        final InOrder inOrder = inOrder(mockExecParamRepository, mockParser);

        inOrder.verify(mockExecParamRepository, times(1))
                .getParser(ArgumentMatchers.eq(null),
                        ArgumentMatchers.eq(mockString), ArgumentMatchers.eq(mockLogger));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecParamRepository, times(2))
                .addExecParam(ArgumentMatchers.isA(ExecParam.class));
    }

    @Test
    public void allExecParamFromConfigFileShouldBeParsedAndAddedToRepoAsExecParamEntities()
            throws IOException {
        final String testExecParam = "[{ \"key\": \"help\" },{ \"key\": \"input\", \"paramValue\": \"input\" }," +
                "{ \"key\": \"output\", \"paramValue\": \"output\" }]";
        final String[] mockString = new String[1];
        final ExecParamRepository mockExecParamRepository = mock(ExecParamRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ParseAllExecParam underTest = new ParseAllExecParam(testExecParam, mockExecParamRepository,
                mockLogger);

        final ExecParamRepository.ExecParamParser mockParser =
                spy(ExecParamRepository.ExecParamParser.class);

        final ExecParam mockHelpExecParam = spy(ExecParam.class);
        when(mockHelpExecParam.getKey()).thenReturn("help");

        final ExecParam mockInputExecParam = spy(ExecParam.class);
        when(mockInputExecParam.getKey()).thenReturn("input");

        final Map<String, ExecParam> mockExecutionParameterMap = new HashMap<>();
        mockExecutionParameterMap.put(mockHelpExecParam.getKey(), mockHelpExecParam);
        mockExecutionParameterMap.put(mockInputExecParam.getKey(), mockInputExecParam);

        when(mockExecParamRepository.getParser(ArgumentMatchers.eq(testExecParam),
                ArgumentMatchers.eq(mockString), ArgumentMatchers.eq(mockLogger)))
                .thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterMap);

        underTest.execute(mockString);

        final InOrder inOrder = inOrder(mockExecParamRepository, mockParser);

        inOrder.verify(mockExecParamRepository, times(1))
                .getParser(ArgumentMatchers.eq(testExecParam),
                        ArgumentMatchers.eq(mockString), ArgumentMatchers.eq(mockLogger));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecParamRepository, times(2))
                .addExecParam(ArgumentMatchers.isA(ExecParam.class));
    }
}