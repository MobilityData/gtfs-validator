package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public class GtfsJsonDescriptor<T extends GtfsEntity> extends GtfsDescriptor<GtfsJson>
//        GtfsDescriptor<T>
{

  public GtfsJsonContainer createContainerForEntities(
      List<T> entities, NoticeContainer noticeContainer) {
    return new GtfsJsonContainer(this, entities, noticeContainer);
  }

  @Override
  public <C extends GtfsContainer> C createContainerForInvalidStatus(TableStatus tableStatus) {
    return (C) new GtfsJsonContainer(this, tableStatus);
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public Class<GtfsJson> getEntityClass() {
    return GtfsJson.class;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }
}
