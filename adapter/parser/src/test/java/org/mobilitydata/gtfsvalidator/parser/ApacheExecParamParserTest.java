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
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApacheExecParamParserTest {

    @Test
    public void apacheCommandLineOptionShouldMapToExecutionParameterCollection() throws IOException, ParseException {
        final CommandLineParser mockCommandLineParser = mock(CommandLineParser.class);
        final Options mockAvailableOptions = spy(Options.class);
        final String[] arguments = new String[]{"-p -x file0.txt -x file1.txt"};
        final CommandLine mockCommandLine = mock(CommandLine.class);

        when(mockCommandLineParser.parse(mockAvailableOptions, arguments)).thenReturn(mockCommandLine);

        final Option mockProtoOption = mock(Option.class);
        when(mockProtoOption.getLongOpt()).thenReturn(ExecParamRepository.PROTO_KEY);
        when(mockProtoOption.hasArg()).thenReturn(true);
        when(mockProtoOption.getValues()).thenReturn(new String[]{"true"});
        when(mockProtoOption.getValue()).thenReturn("true");

        final Option mockExcludeOption = mock(Option.class);
        when(mockExcludeOption.getLongOpt()).thenReturn(ExecParamRepository.EXCLUSION_KEY);
        when(mockExcludeOption.hasArg()).thenReturn(true);
        when(mockExcludeOption.getValues()).thenReturn(new String[]{"file0.txt", "file1.txt"});

        when(mockCommandLine.getOptions()).thenReturn(new Option[]{mockProtoOption, mockExcludeOption});

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser, mockAvailableOptions,
                arguments);
        final Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(2, toCheck.size());
        assertEquals(mockProtoOption.getLongOpt(), toCheck.get(mockProtoOption.getLongOpt()).getKey());
        assertEquals(mockProtoOption.getValue(), toCheck.get(mockProtoOption.getLongOpt()).getValue());

        assertEquals(mockExcludeOption.getLongOpt(), toCheck.get(mockExcludeOption.getLongOpt()).getKey());
        assertEquals(Arrays.asList(mockExcludeOption.getValues()).toString(),
                toCheck.get(mockExcludeOption.getLongOpt()).getValue());

        verify(mockProtoOption, times(7)).getLongOpt();
        verify(mockProtoOption, times(3)).getValues();
        verify(mockProtoOption, times(1)).getValue();

        verify(mockExcludeOption, times(7)).getLongOpt();
        verify(mockExcludeOption, times(2)).getValues();
        verify(mockCommandLineParser, times(1))
                .parse(ArgumentMatchers.eq(mockAvailableOptions), ArgumentMatchers.eq(arguments));

        verify(mockCommandLine, times(1)).getOptions();
        verifyNoMoreInteractions(mockCommandLineParser, mockProtoOption, mockExcludeOption, mockCommandLine);
    }
}