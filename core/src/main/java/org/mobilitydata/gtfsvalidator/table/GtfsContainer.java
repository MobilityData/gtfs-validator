package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import java.util.Optional;

public abstract class GtfsContainer<T extends GtfsEntity, D extends GtfsDescriptor<T>> {

  private final D descriptor;
  private final TableStatus tableStatus;

  public GtfsContainer(D descriptor, TableStatus tableStatus) {
    this.tableStatus = tableStatus;
    this.descriptor = descriptor;
  }

  public TableStatus getTableStatus() {
    return tableStatus;
  }

  public D getDescriptor() {
    return descriptor;
  }

  public abstract Class<T> getEntityClass();

  public int entityCount() {
    return getEntities().size();
  }

  public abstract List<T> getEntities();

  public abstract String gtfsFilename();

  public abstract Optional<T> byTranslationKey(String recordId, String recordSubId);

  public boolean isMissingFile() {
    return tableStatus == TableStatus.MISSING_FILE;
  }

  public boolean isParsedSuccessfully() {
    switch (tableStatus) {
      case PARSABLE_HEADERS_AND_ROWS:
        return true;
      case MISSING_FILE:
        return !descriptor.isRequired();
      default:
        return false;
    }
  }
}
