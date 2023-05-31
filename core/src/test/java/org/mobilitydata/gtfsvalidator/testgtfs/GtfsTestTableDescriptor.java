package org.mobilitydata.gtfsvalidator.testgtfs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.*;

public class GtfsTestTableDescriptor extends GtfsTableDescriptor<GtfsTestEntity> {
  @Override
  public GtfsTableContainer createContainerForInvalidStatus(
      GtfsTableContainer.TableStatus tableStatus) {
    return new GtfsTestTableContainer(tableStatus);
  }

  @Override
  public GtfsTableContainer createContainerForHeaderAndEntities(
      CsvHeader header, List<GtfsTestEntity> entities, NoticeContainer noticeContainer) {
    return GtfsTestTableContainer.forHeaderAndEntities(header, entities, noticeContainer);
  }

  @Override
  public GtfsEntityBuilder createEntityBuilder() {
    return new GtfsTestEntity.Builder();
  }

  @Override
  public Class getEntityClass() {
    return GtfsTestEntity.class;
  }

  @Override
  public ImmutableList<GtfsColumnDescriptor> getColumns() {
    ImmutableList.Builder<GtfsColumnDescriptor> builder = ImmutableList.builder();
    builder.add(
        GtfsColumnDescriptor.builder()
            .setColumnName(GtfsTestEntity.ID_FIELD_NAME)
            .setHeaderRequired(true)
            .setHeaderRecommended(false)
            .setFieldLevel(FieldLevelEnum.REQUIRED)
            .setIsMixedCase(false)
            .setIsCached(false)
            .build());
    builder.add(
        GtfsColumnDescriptor.builder()
            .setColumnName(GtfsTestEntity.CODE_FIELD_NAME)
            .setHeaderRequired(false)
            .setHeaderRecommended(false)
            .setFieldLevel(FieldLevelEnum.OPTIONAL)
            .setIsMixedCase(false)
            .setIsCached(false)
            .build());
    return builder.build();
  }

  @Override
  public ImmutableMap<String, GtfsFieldLoader> getFieldLoaders() {
    ImmutableMap.Builder<String, GtfsFieldLoader> builder = ImmutableMap.builder();
    builder.put(
        GtfsTestEntity.ID_FIELD_NAME,
        new GtfsFieldLoader<GtfsTestEntity.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsTestEntity.Builder builder) {
            builder.setId(
                addToCacheIfPresent(
                    rowParser.asId(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsTestEntity.CODE_FIELD_NAME,
        new GtfsFieldLoader<GtfsTestEntity.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsTestEntity.Builder builder) {
            builder.setCode(
                addToCacheIfPresent(
                    rowParser.asText(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    return builder.build();
  }

  @Override
  public String gtfsFilename() {
    return GtfsTestEntity.FILENAME;
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public boolean isRequired() {
    return true;
  }
}
