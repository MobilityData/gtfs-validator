package org.mobilitydata.gtfsvalidator.usecase.entity;

import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

public abstract class Entity {

    public abstract Entity solveType(GtfsDataRepository gtfsDataRepository);
}