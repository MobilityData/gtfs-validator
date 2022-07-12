package org.mobilitydata.gtfsvalidator.table;

import java.util.Optional;

/**
 * {@link GtfsTableContainer} implementations that contain an entity with a single row will
 * implement this interface.
 *
 * @param <T> the GTFS entity type for this container
 */
public interface GtfsTableContainerWithSingleRow<T> {

  /** Returns the single entity contained within table, if present. */
  Optional<T> getSingleEntity();
}
