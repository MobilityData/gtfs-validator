package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

public abstract class GtfsTableDescriptor<T extends GtfsEntity> {

  // True if the specified file is required in a feed.
  private boolean required;

  public abstract GtfsTableContainer createContainerForInvalidStatus(
      GtfsTableContainer.TableStatus tableStatus);

  public abstract GtfsTableContainer createContainerForHeaderAndEntities(
      CsvHeader header, List<T> entities, NoticeContainer noticeContainer);

  public abstract GtfsEntityBuilder createEntityBuilder();

  public abstract Class<T> getEntityClass();

  public abstract String gtfsFilename();

  public abstract ImmutableMap<String, GtfsFieldLoader> getFieldLoaders();

  public abstract boolean isRecommended();

  public boolean isRequired() {
    return this.required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public abstract Optional<Integer> maxCharsPerColumn();

  public abstract ImmutableList<GtfsColumnDescriptor> getColumns();
}
