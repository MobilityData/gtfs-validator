package org.mobilitydata.gtfsvalidator.table;

/**
 * This class provides soem info about the different files within a GTFS dataset. Its children
 * relate to either a csv table or a geojson file.
 *
 * @param <T> The entity that will be extracted from the file. For example, GtfsCalendarDate or
 *     {@link GtfsGeojsonFeature}
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
}
