package org.mobilitydata.gtfsvalidator.table;

/** Basic interface for builders of all GTFS entities: agencies, stops, routes, trips etc. */
public interface GtfsEntityBuilder<T extends GtfsEntity> {
  /** Builds a new GTFS entity. */
  T build();

  /**
   * Clears all fields.
   *
   * <p>This method should be called if the same builder instance is reused for building another
   * GTFS entity. This is cheaper to reuse an existing builder instance than constructing a new one.
   */
  void clear();

  /** Row number in a CSV file. */
  int csvRowNumber();

  /** Sets row number in a CSV file. Returns self. */
  GtfsEntityBuilder<T> setCsvRowNumber(int value);
}
