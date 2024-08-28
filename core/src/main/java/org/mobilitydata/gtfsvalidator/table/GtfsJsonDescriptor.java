package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public abstract class GtfsJsonDescriptor<T extends GtfsEntity> extends GtfsDescriptor<T> {

  public abstract GtfsJsonContainer createContainerForEntities(
      List<T> entities, NoticeContainer noticeContainer);
}
