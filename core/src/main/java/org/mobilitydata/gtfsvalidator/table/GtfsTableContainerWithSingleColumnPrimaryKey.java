package org.mobilitydata.gtfsvalidator.table;

import java.util.Optional;

/**
 * {@link GtfsTableContainer} implementations that contain an entity with a single-column {@link
 * org.mobilitydata.gtfsvalidator.annotation.PrimaryKey} will implement this interface.
 *
 * @param <T> the GTFS entity type for this container
 */
public interface GtfsTableContainerWithSingleColumnPrimaryKey<T> {

  /**
   * Finds an entity with the given primary key.
   *
   * @param id the value of the entity's primary key. * @return entity with the given primary key,
   *     if any
   */
  Optional<T> byPrimaryKey(String id);
}
