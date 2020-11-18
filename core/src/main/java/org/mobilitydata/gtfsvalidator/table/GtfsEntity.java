package org.mobilitydata.gtfsvalidator.table;

/**
 * Basic interface for all GTFS entities: agencies, stops, routes, trips etc.
 */
public interface GtfsEntity {
    long csvRowNumber();
}
