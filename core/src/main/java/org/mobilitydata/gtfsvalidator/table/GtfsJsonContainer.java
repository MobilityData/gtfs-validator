package org.mobilitydata.gtfsvalidator.table;

public abstract class GtfsJsonContainer<T, D extends GtfsDescriptor<?>> extends GtfsContainer {

  public GtfsJsonContainer(D descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus);
  }
}
