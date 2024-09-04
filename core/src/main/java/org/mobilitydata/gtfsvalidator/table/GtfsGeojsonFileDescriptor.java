package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public class GtfsGeojsonFileDescriptor<T extends GtfsEntity>
    extends GtfsFileDescriptor<GtfsGeojsonFeature> {

  public GtfsGeojsonFeaturesContainer createContainerForEntities(
      List<T> entities, NoticeContainer noticeContainer) {
    return new GtfsGeojsonFeaturesContainer(this, entities, noticeContainer);
  }

  @Override
  public <C extends GtfsEntityContainer> C createContainerForInvalidStatus(
      TableStatus tableStatus) {
    return (C) new GtfsGeojsonFeaturesContainer(this, tableStatus);
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
}
