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

package org.mobilitydata.gtfsvalidator.usecase.crossvalidation;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import static org.mockito.Mockito.*;

class StopTimeShapeTripCrossValidatorTest {

    @Test
    void execute() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final StopTimeShapeTripCrossValidator underTest =
                spy(new StopTimeShapeTripCrossValidator(mockDataRepo, mockResultRepo, mockLogger));

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E034 - `shape_id` not found" +
                System.lineSeparator());
        verify(underTest, times(1)).checkE034();
    }
}