package org.mobilitydata.gtfsvalidator.annotation;

/** How the rows of a GTFS table are kept in memory, see {@link GtfsTable#storage()}. */
public enum TableStorage {
  /** Rows are kept as entity objects. */
  OBJECTS,

  /**
   * Large tables are kept as primitive columns, and entities are created when they are read. The
   * table's primary key must be empty, or an indexed field and an integer sequence field.
   */
  COLUMNAR
}
