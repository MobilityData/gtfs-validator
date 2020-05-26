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

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GenerateFilenameListToProcessTest {

    @Test
    void excludeAllShouldReturnEmptyList() {
        final ArrayList<String> toExclude = new ArrayList<>(List.of("file0", "file1", "file2"));
        final ArrayList<String> toProcess = new ArrayList<>(List.of("file0", "file1", "file2"));
        final Logger mockLogger = mock(Logger.class);

        final GenerateFilenameListToProcess underTest = new GenerateFilenameListToProcess(mockLogger);

        assertEquals(0, underTest.execute(toExclude, toProcess).size());
    }

    @Test
    void exclude2outOf3ShouldReturnListOfSize1() {
        final ArrayList<String> toExclude = new ArrayList<>(List.of("file0", "file1"));
        final ArrayList<String> toProcess = new ArrayList<>(List.of("file0", "file1", "file2"));
        final Logger mockLogger = mock(Logger.class);

        final GenerateFilenameListToProcess underTest = new GenerateFilenameListToProcess(mockLogger);
        assertEquals(List.of("file2"), underTest.execute(toExclude, toProcess));
    }

    @Test
    void exclude1outOf3ShouldReturnListOfSize2() {
        final ArrayList<String> toExclude = new ArrayList<>(List.of("file0"));
        final ArrayList<String> toProcess = new ArrayList<>(List.of("file0", "file1", "file2"));
        final Logger mockLogger = mock(Logger.class);

        final GenerateFilenameListToProcess underTest = new GenerateFilenameListToProcess(mockLogger);
        assertEquals(List.of("file1", "file2"), underTest.execute(toExclude, toProcess));
    }

    @Test
    void exclude3outOf3ShouldReturnListOfSize0() {
        final ArrayList<String> toProcess = new ArrayList<>(List.of("file0", "file1", "file2"));
        final ArrayList<String> toExclude = new ArrayList<>();
        final Logger mockLogger = mock(Logger.class);

        final GenerateFilenameListToProcess underTest = new GenerateFilenameListToProcess(mockLogger);
        assertEquals(List.of("file0", "file1", "file2"), underTest.execute(toExclude, toProcess));
    }
}