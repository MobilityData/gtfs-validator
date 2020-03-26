package org.mobilitydata.gtfsvalidator.db;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfsentity.Agency;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryGtfsDataRepository implements GtfsDataRepository {

    public List<Agency> getAgencyList() {
        return agencyList;
    }

    private final List<Agency> agencyList = new ArrayList<>();

    @Override
    public Agency addEntity(final Agency newAgency) {
        agencyList.add(newAgency);
        return newAgency;
    }

    @Override
    public Agency getAgencyById(final String agencyId) {
        return agencyList.stream()
                .filter(agency -> {
                    assert agency.getAgencyId() != null;
                    return agency.getAgencyId().equals(agencyId);
                })
                .findAny()
                .orElse(null);
    }
}