package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

public class TableMetadata {
  private final String filename;
  private final GtfsTableContainer.TableStatus tableStatus;
  private final int entityCount;

  public TableMetadata(
      String filename, GtfsTableContainer.TableStatus tableStatus, int entityCount) {

    this.filename = filename;
    this.tableStatus = tableStatus;
    this.entityCount = entityCount;
  }

  public static TableMetadata from(GtfsTableContainer<?> table) {
    return new TableMetadata(table.gtfsFilename(), table.getTableStatus(), table.entityCount());
  }

  public String getFilename() {
    return filename;
  }

  public GtfsTableContainer.TableStatus getTableStatus() {
    return tableStatus;
  }

  public int getEntityCount() {
    return entityCount;
  }
}
