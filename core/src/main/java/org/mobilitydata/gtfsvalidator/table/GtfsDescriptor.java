package org.mobilitydata.gtfsvalidator.table;

// TODO: review class name maybe GtfsFileDescriptor
public abstract class GtfsDescriptor<T extends GtfsEntity> {

  public abstract <C extends GtfsContainer> C createContainerForInvalidStatus(
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
