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
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerateGtfsRequiredFilenameListTest {

    @Test
    void allRequiredFilenameShouldBeReturned() {
        final RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        final Set<String> testSet = Set.of("req0.req", "req1.req", "req2.req", "req3.req", "req4.req",
                "req5.req", "req6.req", "req7.req", "req8.req", "req9.req",
                "opt0.opt", "opt1.opt", "opt2.opt", "opt3.opt", "opt4.opt",
                "opt5.opt", "opt6.opt", "opt7.opt", "opt8.opt", "opt9.opt");
        when(mockFileRepo.getFilenameAll()).thenReturn(testSet);

        final GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        List<String> testRequiredList = List.of("req0.req", "req1.req", "req2.req", "req3.req", "req4.req",
                "req5.req", "req6.req", "req7.req", "req8.req", "req9.req");
        when(mockSpecRepo.getRequiredFilenameList()).thenReturn(testRequiredList);

        GenerateGtfsRequiredFilenameList underTest = new GenerateGtfsRequiredFilenameList(mockSpecRepo);

        List<String> result = underTest.execute();
        assertEquals(10, result.size());
        assertEquals(testRequiredList, result);
    }
}
