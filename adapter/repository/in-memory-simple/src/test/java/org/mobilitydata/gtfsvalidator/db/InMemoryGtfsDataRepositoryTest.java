package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfsentity.Agency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class InMemoryGtfsDataRepositoryTest {

    @Test
    void agencyEntityShouldBeAddedToAgencyList() {

        Agency mockAgency = mock(Agency.class);

        InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockAgency);

        assertEquals(underTest.getAgencyList().size(), 1);
    }
}