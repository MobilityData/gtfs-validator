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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateAllOptionalFilenameTest {

    @Test
    void allExtraPresentShouldGenerateNotice() {

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        Set<String> testSet = Set.of("req0.req","opt0.opt","opt1.opt", "extra0.extra", "extra1.extra", "extra2.extra");
        when(mockFileRepo.getFilenameAll()).thenReturn(testSet);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        List<String> testRequiredList = List.of("req0.req");
        when(mockSpecRepo.getRequiredFilenameList()).thenReturn(testRequiredList);
        List<String> testOptionalList = List.of("opt0.opt","opt1.opt");
        when(mockSpecRepo.getOptionalFilenameList()).thenReturn(testOptionalList);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllOptionalFilename underTest = new ValidateAllOptionalFilename(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo);

        List<String> result = underTest.execute();
        assertEquals(2, result.size());

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockSpecRepo, times(1)).getOptionalFilenameList();
        inOrder.verify(mockSpecRepo, times(1)).getRequiredFilenameList();
        inOrder.verify(mockFileRepo, times(2)).getFilenameAll();
        verify(mockResultRepo, times(3)).addNotice(any(ExtraFileFoundNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

}