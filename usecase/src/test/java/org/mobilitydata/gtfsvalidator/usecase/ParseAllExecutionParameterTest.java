package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecutionParameterRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;

class ParseAllExecutionParameterTest {

    @Test
    public void allExecutionParameterFromCommandLineShouldBeParsedAndAddedToRepoAsExecutionParameterEntities() throws IOException {
        boolean fromConfigFile = false;
        String pathToConfigFile = null;
        ExecutionParameterRepository mockExecutionParameterRepository = mock(ExecutionParameterRepository.class);

        ParseAllExecutionParameter underTest = new ParseAllExecutionParameter(fromConfigFile,
                pathToConfigFile,
                mockExecutionParameterRepository);

        ExecutionParameterRepository.ExecutionParameterParser mockParser =
                mock(ExecutionParameterRepository.ExecutionParameterParser.class);

        ExecutionParameter mockExecutionParameter0 = mock(ExecutionParameter.class);
        when(mockExecutionParameter0.getShortName()).thenReturn("short_name0");
        ExecutionParameter mockExecutionParameter1 = mock(ExecutionParameter.class);
        when(mockExecutionParameter1.getShortName()).thenReturn("short_name1");

        Collection<ExecutionParameter> mockExecutionParameterCollection = new ArrayList<>();
        mockExecutionParameterCollection.add(mockExecutionParameter0);
        mockExecutionParameterCollection.add(mockExecutionParameter1);

        when(mockExecutionParameterRepository.getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null))).thenReturn(mockParser);
        when(mockParser.parse()).thenReturn(mockExecutionParameterCollection);

        underTest.execute();

        InOrder inOrder = inOrder(mockExecutionParameterRepository, mockParser);

        inOrder.verify(mockExecutionParameterRepository, times(1)).getParser(ArgumentMatchers.eq(false), ArgumentMatchers.eq(null));
        inOrder.verify(mockParser, times(1)).parse();
        inOrder.verify(mockExecutionParameterRepository, times(2)).addExecutionParameter(ArgumentMatchers.isA(ExecutionParameter.class));
    }
}