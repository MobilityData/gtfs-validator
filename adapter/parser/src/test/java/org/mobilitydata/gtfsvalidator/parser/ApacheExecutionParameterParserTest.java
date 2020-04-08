package org.mobilitydata.gtfsvalidator.parser;

import org.apache.commons.cli.*;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecutionParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApacheExecutionParameterParserTest {

    @Test
    public void apacheCommandLineOptionShouldMapToExecutionParameterCollection() throws IOException, ParseException {
        CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        Options mockOptions = mock(Options.class);

        Option mockOption0 = mock(Option.class);
        when(mockOption0.getOpt()).thenReturn("short_name0");
        when(mockOption0.getLongOpt()).thenReturn("long_name0");
        when(mockOption0.getDescription()).thenReturn("description0");
        when(mockOption0.hasArg()).thenReturn(true);
        when(mockOption0.getValue()).thenReturn("value0");

        Option mockOption1 = mock(Option.class);
        when(mockOption0.getOpt()).thenReturn("short_name1");
        when(mockOption0.getLongOpt()).thenReturn("long_name1");
        when(mockOption0.getDescription()).thenReturn("description1");
        when(mockOption0.hasArg()).thenReturn(true);
        when(mockOption0.getValue()).thenReturn("value1");

        Collection<Option> mockOptionCollection = new ArrayList<>();
        mockOptionCollection.add(mockOption0);
        mockOptionCollection.add(mockOption1);

        String[] mockArguments = new String[1];

        CommandLine mockCommandLine = mock(CommandLine.class);
        when(mockCommandLineParser.parse(mockOptions, mockArguments)).thenReturn(mockCommandLine);
        when(mockCommandLine.getOptions()).thenReturn(mockOptionCollection.toArray(new Option[0]));

        ApacheExecutionParameterParser underTest = new ApacheExecutionParameterParser(mockCommandLineParser,
                mockOptions, mockArguments);

        Map<String, ExecutionParameter> toCheck = underTest.parse();

        verify(mockOption0, times(2)).getOpt();
        verify(mockOption0, times(1)).getLongOpt();
        verify(mockOption0, times(1)).getDescription();
        verify(mockOption0, times(1)).hasArg();
        verify(mockOption0, times(1)).getValue();

        verify(mockOption1, times(2)).getOpt();
        verify(mockOption1, times(1)).getLongOpt();
        verify(mockOption1, times(1)).getDescription();
        verify(mockOption1, times(1)).hasArg();
        verify(mockOption1, times(1)).getValue();

        assertEquals(2, toCheck.size());
    }
}