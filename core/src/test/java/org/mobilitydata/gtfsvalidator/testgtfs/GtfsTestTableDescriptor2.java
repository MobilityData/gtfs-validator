package org.mobilitydata.gtfsvalidator.testgtfs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.*;

// We need a second test table descriptor to test multi file contaioners
public class GtfsTestTableDescriptor2 extends GtfsTableDescriptor<GtfsTestEntity> {
  @Override
  public GtfsTableContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsTestTableContainer2(tableStatus);
  }

  @Override
  public GtfsTableContainer createContainerForHeaderAndEntities(
      CsvHeader header, List<GtfsTestEntity> entities, NoticeContainer noticeContainer) {
    return GtfsTestTableContainer2.forHeaderAndEntities(this, header, entities, noticeContainer);
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
            .setFieldLevel(FieldLevelEnum.REQUIRED)
            .setIsMixedCase(false)
            .setIsCached(false)
            .build());
    builder.add(
        GtfsColumnDescriptor.builder()
            .setColumnName(GtfsTestEntity.CODE_FIELD_NAME)
            .setHeaderRequired(false)
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
                addToCacheIfPresent(rowParser.asId(columnIndex, columnDescriptor), fieldCache));
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
                addToCacheIfPresent(rowParser.asText(columnIndex, columnDescriptor), fieldCache));
          }
        });
    return builder.build();
  }

  @Override
  public String gtfsFilename() {
    return GtfsTestEntity.FILENAME + "2";
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public boolean isRequired() {
    return true;
  }

  @Override
  public Optional<Integer> maxCharsPerColumn() {
    return Optional.empty();
  }
}
