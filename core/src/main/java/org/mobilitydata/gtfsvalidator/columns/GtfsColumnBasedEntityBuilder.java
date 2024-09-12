package org.mobilitydata.gtfsvalidator.columns;

import org.mobilitydata.gtfsvalidator.table.GtfsEntityBuilder;

public abstract class GtfsColumnBasedEntityBuilder<T extends GtfsColumnBasedEntity>
    implements GtfsEntityBuilder<T> {

  protected final GtfsColumnStore store;

  protected int rowIndex = -1;

  public GtfsColumnBasedEntityBuilder(GtfsColumnStore store) {
    this.store = store;
  }

  protected abstract GtfsColumnAssignments getAssignments();

  public abstract GtfsColumnBasedCollectionFactory<T> getCollectionFactory();

  @Override
  public void clear() {
    this.rowIndex++;
  }

  @Override
  public void close() {}
}
