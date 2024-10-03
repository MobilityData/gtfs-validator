package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

public abstract class GtfsTableDescriptor<T extends GtfsEntity> extends GtfsFileDescriptor<T> {

  @Override
  public abstract GtfsTableContainer createContainerForInvalidStatus(TableStatus tableStatus);

  public abstract GtfsTableContainer createContainerForHeaderAndEntities(
      CsvHeader header, List<T> entities, NoticeContainer noticeContainer);

  public abstract GtfsEntityBuilder createEntityBuilder();

  public abstract ImmutableMap<String, GtfsFieldLoader> getFieldLoaders();

  public abstract Optional<Integer> maxCharsPerColumn();

  public abstract ImmutableList<GtfsColumnDescriptor> getColumns();
}
