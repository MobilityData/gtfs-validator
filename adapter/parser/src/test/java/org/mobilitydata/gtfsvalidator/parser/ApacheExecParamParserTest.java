package org.mobilitydata.gtfsvalidator.parser;

import org.apache.commons.cli.*;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApacheExecParamParserTest {

    @Test
    public void apacheCommandLineOptionShouldMapToExecutionParameterCollection() throws IOException, ParseException {
        final CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        final Options mockOptions = mock(Options.class);

        final Option mockOption0 = mock(Option.class);
        when(mockOption0.getOpt()).thenReturn("short_name0");
        when(mockOption0.getLongOpt()).thenReturn("long_name0");
        when(mockOption0.getDescription()).thenReturn("description0");
        when(mockOption0.hasArg()).thenReturn(true);
        when(mockOption0.getValue()).thenReturn("value0");

        final Option mockOption1 = mock(Option.class);
        when(mockOption0.getOpt()).thenReturn("short_name1");
        when(mockOption0.getLongOpt()).thenReturn("long_name1");
        when(mockOption0.getDescription()).thenReturn("description1");
        when(mockOption0.hasArg()).thenReturn(true);
        when(mockOption0.getValue()).thenReturn("value1");

        final Collection<Option> mockOptionCollection = new ArrayList<>();
        mockOptionCollection.add(mockOption0);
        mockOptionCollection.add(mockOption1);

        final String[] mockArguments = new String[1];

        final CommandLine mockCommandLine = mock(CommandLine.class);
        when(mockCommandLineParser.parse(mockOptions, mockArguments)).thenReturn(mockCommandLine);
        when(mockCommandLine.getOptions()).thenReturn(mockOptionCollection.toArray(new Option[0]));

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser,
                mockOptions, mockArguments);

        final Map<String, ExecParam> toCheck = underTest.parse();

        verify(mockOptions, times(5))
                .addOption(anyString(), anyString(), ArgumentMatchers.eq(true), anyString());
        verify(mockOptions, times(1))
                .addOption(anyString(), anyString(), ArgumentMatchers.eq(false), anyString());

        verify(mockCommandLineParser, times(1)).parse(mockOptions, mockArguments);

        verify(mockOption0, times(1)).getOpt();
        verify(mockOption0, times(3)).getLongOpt();
        verify(mockOption0, times(1)).getDescription();
        verify(mockOption0, times(1)).hasArg();
        verify(mockOption0, times(1)).getValue();

        verify(mockOption1, times(1)).getOpt();
        verify(mockOption1, times(3)).getLongOpt();
        verify(mockOption1, times(1)).getDescription();
        verify(mockOption1, times(1)).hasArg();
        verify(mockOption1, times(1)).getValue();

        assertEquals(2, toCheck.size());

        verifyNoMoreInteractions(mockCommandLineParser, mockOptions, mockOption0, mockOption1);
    }
}