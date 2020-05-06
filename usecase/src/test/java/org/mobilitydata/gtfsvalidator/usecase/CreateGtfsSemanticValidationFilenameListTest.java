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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateGtfsSemanticValidationFilenameListTest {

    @Test
    void executionMethodShouldReturnEmptyList() {
        final List<String> filenameCollection = List.of("file0", "file1", "file2");

        final CreateGtfsSemanticValidationFilenameList underTest =
                new CreateGtfsSemanticValidationFilenameList(filenameCollection);

        assertEquals(new ArrayList<>(), underTest.execute(filenameCollection));
    }

    @Test
    void executionMethodShouldReturnListOfSize1() {
        final List<String> filenameCollection = List.of("file0", "file1", "file2");

        final CreateGtfsSemanticValidationFilenameList underTest =
                new CreateGtfsSemanticValidationFilenameList(filenameCollection);
        assertEquals(List.of("file2"), underTest.execute(List.of("file0", "file1")));
    }

    @Test
    void executionMethodShouldReturnListOfSize2() {
        final List<String> filenameCollection = List.of("file0", "file1", "file2");

        final CreateGtfsSemanticValidationFilenameList underTest =
                new CreateGtfsSemanticValidationFilenameList(filenameCollection);
        assertEquals(List.of("file1", "file2"), underTest.execute(List.of("file0")));
    }

    @Test
    void executionMethodShouldReturnListOfSize3() {
        final List<String> filenameCollection = List.of("file0", "file1", "file2");

        final CreateGtfsSemanticValidationFilenameList underTest =
                new CreateGtfsSemanticValidationFilenameList(filenameCollection);
        assertEquals(List.of("file0", "file1", "file2"), underTest.execute(new ArrayList<>()));
    }
}