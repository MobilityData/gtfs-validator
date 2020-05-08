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

        final Option mockOption0 = mock(Option.class);
        when(mockOption0.getLongOpt()).thenReturn("proto");
        when(mockOption0.hasArg()).thenReturn(true);
        when(mockOption0.getValues()).thenReturn(new String[]{"true"});
        when(mockOption0.getValue()).thenReturn("true");

        final Option mockOption1 = mock(Option.class);
        when(mockOption1.getLongOpt()).thenReturn("exclude");
        when(mockOption1.hasArg()).thenReturn(true);
        when(mockOption1.getValues()).thenReturn(new String[]{"file0.txt", "file1.txt"});

        when(mockCommandLine.getOptions()).thenReturn(new Option[]{mockOption0, mockOption1});

        final ApacheExecParamParser underTest = new ApacheExecParamParser(mockCommandLineParser, mockAvailableOptions,
                arguments);
        final Map<String, ExecParam> toCheck = underTest.parse();

        assertEquals(2, toCheck.size());
        assertEquals(mockOption0.getLongOpt(), toCheck.get(mockOption0.getLongOpt()).getKey());
        assertEquals(mockOption0.getValue(), toCheck.get(mockOption0.getLongOpt()).getValue());

        assertEquals(mockOption1.getLongOpt(), toCheck.get(mockOption1.getLongOpt()).getKey());
        assertEquals(Arrays.asList(mockOption1.getValues()).toString(), toCheck.get(mockOption1.getLongOpt()).getValue());

        verify(mockOption0, times(5)).getLongOpt();
        verify(mockOption0, times(1)).getValues();
        verify(mockOption0, times(1)).getValue();

        verify(mockOption1, times(5)).getLongOpt();
        verify(mockOption1, times(2)).getValues();
        verify(mockCommandLineParser, times(1))
                .parse(ArgumentMatchers.eq(mockAvailableOptions), ArgumentMatchers.eq(arguments));

        verify(mockAvailableOptions, times(7))
                .addOption(anyString(), anyString(), anyBoolean(), anyString());
        verify(mockCommandLine, times(1)).getOptions();
        verifyNoMoreInteractions(mockCommandLineParser, mockOption0, mockOption1, mockCommandLine);
    }
}