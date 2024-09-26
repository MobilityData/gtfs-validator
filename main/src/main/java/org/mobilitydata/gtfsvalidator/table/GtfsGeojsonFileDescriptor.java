package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public class GtfsGeojsonFileDescriptor extends GtfsFileDescriptor<GtfsGeojsonFeature> {

  public GtfsGeojsonFeaturesContainer createContainerForEntities(
      List<GtfsGeojsonFeature> entities, NoticeContainer noticeContainer) {
    return new GtfsGeojsonFeaturesContainer(this, entities, noticeContainer);
  }

  @Override
  public GtfsGeojsonFeaturesContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsGeojsonFeaturesContainer(this, tableStatus);
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public Class<GtfsGeojsonFeature> getEntityClass() {
    return GtfsGeojsonFeature.class;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }

  public TableLoader getTableLoader() {
    return new GeoJsonFileLoader();
  }
}
