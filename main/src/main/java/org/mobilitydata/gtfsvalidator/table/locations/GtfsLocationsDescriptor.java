package org.mobilitydata.gtfsvalidator.table.locations;

import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

public class GtfsLocationsDescriptor extends GtfsJsonDescriptor<GtfsLocations> {

  @Override
  public GtfsJsonContainer createContainerForEntities(
      List<GtfsLocations> entities, NoticeContainer noticeContainer) {
    return GtfsLocationsDescriptor.forEntities(this, entities, noticeContainer);
  }

  /** Creates a table with entities */
  public static GtfsLocationsJsonContainer forEntities(
      GtfsLocationsDescriptor descriptor,
      List<GtfsLocations> entities,
      NoticeContainer noticeContainer) {
    //    TODO review indices with the notice container
    return new GtfsLocationsJsonContainer(descriptor, entities);
  }

  public GtfsLocationsDescriptor() {
    super();
  }

  @Override
  public GtfsJsonContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsLocationsJsonContainer(this, tableStatus);
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public Class<GtfsLocations> getEntityClass() {
    return null;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }
}
