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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfsentity.Agency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InMemoryGtfsDataRepositoryTest {

    @Test
    void agencyEntityShouldBeAddedToAgencyCollection() {

        Agency mockAgency = mock(Agency.class);

        InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockAgency);

        assertEquals(underTest.getAgencyCollection().size(), 1);

        //noinspection ResultOfMethodCallIgnored
        verify(mockAgency, times(1)).getAgencyId();

        verifyNoMoreInteractions(mockAgency);
    }
}