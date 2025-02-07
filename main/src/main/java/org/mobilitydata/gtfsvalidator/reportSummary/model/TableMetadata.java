package org.mobilitydata.gtfsvalidator.reportSummary.model;

import org.mobilitydata.gtfsvalidator.table.GtfsEntityContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;

public class TableMetadata {
  private final String filename;
  private final TableStatus tableStatus;
  private final int entityCount;

  public TableMetadata(String filename, TableStatus tableStatus, int entityCount) {

    this.filename = filename;
    this.tableStatus = tableStatus;
    this.entityCount = entityCount;
  }

  public static TableMetadata from(GtfsEntityContainer<?, ?> table) {
    return new TableMetadata(table.gtfsFilename(), table.getTableStatus(), table.entityCount());
  }

  public String getFilename() {
    return filename;
  }

  public TableStatus getTableStatus() {
    return tableStatus;
  }

  public int getEntityCount() {
    return entityCount;
  }
}
