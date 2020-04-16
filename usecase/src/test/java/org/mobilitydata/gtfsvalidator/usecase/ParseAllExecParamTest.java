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
        boolean fromConfigFile = false;
        String pathToConfigFile = null;
        String[] mockString = new String[1];
        ExecParamRepository mockExecParamRepository = mock(ExecParamRepository.class);

        ParseAllExecParam underTest = new ParseAllExecParam(fromConfigFile,
                pathToConfigFile,
                mockExecParamRepository);

        ExecParamRepository.ExecParamParser mockParser =
                mock(ExecParamRepository.ExecParamParser.class);

        ExecParam mockExecParam0 = mock(ExecParam.class);
        when(mockExecParam0.getShortName()).thenReturn("short_name0");
        ExecParam mockExecParam1 = mock(ExecParam.class);
        when(mockExecParam1.getShortName()).thenReturn("short_name1");

        Map<String, ExecParam> mockExecutionParameterMap = new HashMap<>();
        mockExecutionParameterMap.put(mockExecParam0.getShortName(), mockExecParam0);
        mockExecutionParameterMap.put(mockExecParam1.getShortName(), mockExecParam1);

        when(mockExecParamRepository.getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null),
                ArgumentMatchers.eq(mockString)))
                .thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterMap);

        underTest.execute(mockString);

        InOrder inOrder = inOrder(mockExecParamRepository, mockParser);

        inOrder.verify(mockExecParamRepository, times(1))
                .getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null),
                        ArgumentMatchers.eq(mockString));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecParamRepository, times(2))
                .addExecParam(ArgumentMatchers.isA(ExecParam.class));
    }
}