package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nonnull;

/**
 * This class provides some info about the different files within a GTFS dataset. Its children
 * relate to either a csv table or a GeoJSON file.
 *
 * @param <T> The entity that will be extracted from the file. For example, GtfsCalendarDate or
 *     GtfsGeoJsonFeature
 */
public abstract class GtfsFileDescriptor<T extends GtfsEntity> {

  public abstract <C extends GtfsEntityContainer> C createContainerForInvalidStatus(
      TableStatus tableStatus);

  // True if the specified file is required in a feed.
  private boolean required;

  private TableStatus tableStatus;

  public abstract boolean isRecommended();

  public abstract Class<T> getEntityClass();

  public abstract String gtfsFilename();

  public boolean isRequired() {
    return this.required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  /**
   * Get the looder for the file described by this file descriptor.
   *
   * @return the appropriate file loader.
   */
  @Nonnull
  public TableLoader getTableLoader() {
    return CsvFileLoader.getInstance();
  }
}
