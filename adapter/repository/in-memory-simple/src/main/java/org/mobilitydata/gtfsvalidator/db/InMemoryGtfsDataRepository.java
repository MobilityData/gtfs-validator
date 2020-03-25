package org.mobilitydata.gtfsvalidator.db;

import org.mobilitydata.gtfsvalidator.usecase.entity.Agency;
import org.mobilitydata.gtfsvalidator.usecase.entity.Entity;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryGtfsDataRepository implements GtfsDataRepository {

    public List<Agency> getAgencyList() {
        return agencyList;
    }

    private final List<Agency> agencyList = new ArrayList<>();

    @Override
    public Agency addEntity(Agency newAgency) {
        agencyList.add(newAgency);
        return newAgency;
    }

    public Entity addEntity(Entity newEntity) {
        return newEntity.solveType(this);
    }
}
