package org.mobilitydata.gtfsvalidator.table;

public abstract class GtfsJsonContainer<T extends GtfsEntity, D extends GtfsDescriptor<T>>
    extends GtfsContainer<T, D> {

  public GtfsJsonContainer(D descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus);
  }
}
