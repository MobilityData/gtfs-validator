package org.mobilitydata.gtfsvalidator.table.locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.table.GtfsJsonContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;

public class GtfsLocationsJsonContainer
    extends GtfsJsonContainer<GtfsLocations, GtfsLocationsDescriptor> {

  private final List<GtfsLocations> entities;

  public GtfsLocationsJsonContainer(
      GtfsLocationsDescriptor descriptor, List<GtfsLocations> entities) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS);
    this.entities = entities;
  }

  public GtfsLocationsJsonContainer(GtfsLocationsDescriptor descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class getEntityClass() {
    return null;
  }

  @Override
  public List getEntities() {
    return List.of();
  }

  @Override
  public String gtfsFilename() {
    return "";
  }

  @Override
  public Optional byTranslationKey(String recordId, String recordSubId) {
    return Optional.empty();
  }
}
