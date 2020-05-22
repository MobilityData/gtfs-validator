/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.parser;

import org.apache.commons.cli.*;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApacheExecParamParserTest {

    @Test
    public void apacheCommandLineOptionShouldMapToExecutionParameterCollection() throws IOException, ParseException {
        final CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        final Options mockAvailableOptions = spy(Options.class);
        final String[] arguments = new String[]{"-p -x file0.txt,file1.txt"};
        final CommandLine mockCommandLine = mock(CommandLine.class);

        when(mockCommandLineParser.parse(mockAvailableOptions, arguments)).thenReturn(mockCommandLine);

        final Option mockProtoOption = mock(Option.class);
        when(mockProtoOption.getLongOpt()).thenReturn(ExecParamRepository.PROTO_KEY);
        when(mockProtoOption.hasArg()).thenReturn(true);
        when(mockProtoOption.getValues()).thenReturn(new String[]{"true"});

        final Option mockExcludeFileOption = mock(Option.class);
        when(mockExcludeFileOption.getLongOpt()).thenReturn(ExecParamRepository.EXCLUSION_KEY);
        when(mockExcludeFileOption.hasArg()).thenReturn(true);
        when(mockExcludeFileOption.getValues()).thenReturn(new String[]{"file0.txt,file1.txt"});

        when(mockCommandLine.getOptions()).thenReturn(new Option[]{mockProtoOption, mockExcludeFileOption});

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser, mockAvailableOptions,
                arguments);
        final Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(2, toCheck.size());
        assertEquals(mockProtoOption.getLongOpt(), toCheck.get(mockProtoOption.getLongOpt()).getKey());
        assertEquals(List.of(mockProtoOption.getValues()), toCheck.get(mockProtoOption.getLongOpt()).getValue());

        assertEquals(mockExcludeFileOption.getLongOpt(), toCheck.get(mockExcludeFileOption.getLongOpt()).getKey());
        assertEquals("file0.txt,file1.txt", toCheck.get(mockExcludeFileOption.getLongOpt()).getValue().get(0));

        verify(mockCommandLineParser, times(1)).parse(mockAvailableOptions, arguments);
        verify(mockCommandLine, times(1)).getOptions();

        verify(mockProtoOption, times(4)).getValues();
        verify(mockExcludeFileOption, times(3)).getValues();
        verify(mockExcludeFileOption, times(3)).getValues();

        verify(mockProtoOption, times(6)).getLongOpt();
        verify(mockExcludeFileOption, times(6)).getLongOpt();

        verifyNoMoreInteractions(mockCommandLineParser, mockProtoOption, mockExcludeFileOption, mockExcludeFileOption,
                mockCommandLine);
    }

    @Test
    void duplicateOptionShouldThrowException() throws ParseException {
        final CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        final Options mockAvailableOptions = spy(Options.class);
        final String[] arguments = new String[]{"-e output -e duplicate"};
        final CommandLine mockCommandLine = mock(CommandLine.class);

        when(mockCommandLineParser.parse(mockAvailableOptions, arguments)).thenReturn(mockCommandLine);

        final Option mockOutputOption = mock(Option.class);
        when(mockOutputOption.getLongOpt()).thenReturn(ExecParamRepository.EXTRACT_KEY);
        when(mockOutputOption.hasArg()).thenReturn(true);
        when(mockOutputOption.getValues()).thenReturn(new String[]{"output"});

        final Option mockDuplicateOption = mock(Option.class);
        when(mockDuplicateOption.getLongOpt()).thenReturn(ExecParamRepository.EXTRACT_KEY);
        when(mockDuplicateOption.hasArg()).thenReturn(true);
        when(mockDuplicateOption.getValues()).thenReturn(new String[]{"duplicate"});

        when(mockCommandLine.getOptions()).thenReturn(new Option[]{mockOutputOption, mockDuplicateOption});

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser, mockAvailableOptions,
                arguments);

        final Exception exception = assertThrows(IOException.class, underTest::parse);
        assertEquals("Option: extract already defined", exception.getMessage());
    }

    @Test
    void optionWithTooManyArgumentShouldThrowException() throws ParseException {
        final CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        final Options mockAvailableOptions = spy(Options.class);
        final String[] arguments = new String[]{"-e output -e duplicate"};
        final CommandLine mockCommandLine = mock(CommandLine.class);

        when(mockCommandLineParser.parse(mockAvailableOptions, arguments)).thenReturn(mockCommandLine);

        final Option mockOutputOption = mock(Option.class);
        when(mockOutputOption.getLongOpt()).thenReturn(ExecParamRepository.EXTRACT_KEY);
        when(mockOutputOption.hasArg()).thenReturn(true);
        when(mockOutputOption.getValues()).thenReturn(new String[]{"output", "duplicate"});


        when(mockCommandLine.getOptions()).thenReturn(new Option[]{mockOutputOption});

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser, mockAvailableOptions,
                arguments);

        final Exception exception = assertThrows(IOException.class, underTest::parse);
        assertEquals("Option: extract with too many arguments: [output, duplicate]", exception.getMessage());
    }
}