package org.mobilitydata.gtfsvalidator.columns;

import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

/**
 * A GtfsEntity whose field values are not stored directly in the entity class, but instead stored
 * in an underlying {@link GtfsColumnStore}.
 */
public abstract class GtfsColumnBasedEntity implements GtfsEntity {
  protected final GtfsColumnStore store;

  /**
   * The 0-based row index (not the same as a CSV row number) indicating the row of the entity in
   * the column store.
   */
  protected final int rowIndex;

  protected GtfsColumnBasedEntity(GtfsColumnStore store, int rowIndex) {
    this.store = store;
    this.rowIndex = rowIndex;
  }

  protected abstract GtfsColumnAssignments getAssignments();
}
