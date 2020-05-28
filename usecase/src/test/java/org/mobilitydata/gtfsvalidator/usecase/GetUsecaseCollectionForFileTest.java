/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.FileSpecificUsecase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GetUsecaseCollectionForFileTest {

    @Test
    void executionMethodShouldReturnCollectionOfFileSpecificUsecase() {
        final FileSpecificUsecase fileZeroSpecificUsecase0 = mock(FileSpecificUsecase.class);
        final FileSpecificUsecase fileZeroSpecificUsecase1 = mock(FileSpecificUsecase.class);

        final Map<String, List<FileSpecificUsecase>> fileSpecificUsecaseCollection = new HashMap<>();
        final List<FileSpecificUsecase> fileZeroSpecificUsecaseCollection =
                List.of(fileZeroSpecificUsecase0, fileZeroSpecificUsecase1);
        fileSpecificUsecaseCollection.put("file0", fileZeroSpecificUsecaseCollection);

        final FileSpecificUsecase fileOneSpecificUsecase0 = mock(FileSpecificUsecase.class);
        final FileSpecificUsecase fileOneSpecificUsecase1 = mock(FileSpecificUsecase.class);
        final FileSpecificUsecase fileOneSpecificUsecase2 = mock(FileSpecificUsecase.class);
        final List<FileSpecificUsecase> fileOneSpecificUsecaseCollection =
                List.of(fileOneSpecificUsecase0, fileOneSpecificUsecase1, fileOneSpecificUsecase2);
        fileSpecificUsecaseCollection
                .put("file1", fileOneSpecificUsecaseCollection);

        final GetUsecaseCollectionForFile underTest =
                new GetUsecaseCollectionForFile(fileSpecificUsecaseCollection);

        assertEquals(fileZeroSpecificUsecaseCollection, underTest.execute("file0"));
        assertEquals(fileOneSpecificUsecaseCollection, underTest.execute("file1"));
    }
}