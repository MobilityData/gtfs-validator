package org.mobilitydata.gtfsvalidator.table;

import java.util.Optional;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainerWithMultiColumnPrimaryKey.MulitColumnKey;

/**
 * {@link GtfsTableContainer} implementations that contain an entity with a multi-column {@link
 * org.mobilitydata.gtfsvalidator.annotation.PrimaryKey} will implement this interface.
 *
 * @param <T> the GTFS entity type for this container
 */
public interface GtfsTableContainerWithMultiColumnPrimaryKey<T, K extends MulitColumnKey> {

  /**
   * Marker interface for all composite key implementation classes used by multi-column primary key
   * table containers.
   */
  interface MulitColumnKey {}

  /**
   * Finds an entity with the given primary key.
   *
   * @param key the value of the entity's composite primary key.
   * @return entity with the given primary key, if any.
   */
  Optional<T> byPrimaryKey(K key);
}
