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

/** Test class to avoid dependency on the real GtfsStopTableDescriptorØ and annotation processor. */
public class GtfsStopTableDescriptor extends GtfsTableDescriptor<GtfsStop> {
  @Override
  public GtfsTableContainer createContainerForInvalidStatus(
      GtfsTableContainer.TableStatus tableStatus) {
    return new GtfsStopTableContainer(tableStatus);
  }

  @Override
  public GtfsTableContainer createContainerForHeaderAndEntities(
      CsvHeader header, List<GtfsStop> entities, NoticeContainer noticeContainer) {
    return GtfsStopTableContainer.forHeaderAndEntities(header, entities, noticeContainer);
  }

  @Override
  public GtfsEntityBuilder createEntityBuilder() {
    return new GtfsStop.Builder();
  }

  @Override
  public Class getEntityClass() {
    return GtfsStop.class;
  }

  @Override
  public ImmutableList<GtfsColumnDescriptor> getColumns() {
    ImmutableList.Builder<GtfsColumnDescriptor> builder = ImmutableList.builder();
    builder.add(
        GtfsColumnDescriptor.builder()
            .setColumnName(GtfsStop.STOP_ID_FIELD_NAME)
            .setHeaderRequired(true)
            .setFieldLevel(FieldLevelEnum.REQUIRED)
            .setIsMixedCase(false)
            .setIsCached(false)
            .build());
    builder.add(
        GtfsColumnDescriptor.builder()
            .setColumnName(GtfsStop.STOP_CODE_FIELD_NAME)
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
        GtfsStop.STOP_ID_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopId(
                addToCacheIfPresent(
                    rowParser.asId(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsStop.STOP_CODE_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopCode(
                addToCacheIfPresent(
                    rowParser.asText(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsStop.STOP_NAME_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopName(
                addToCacheIfPresent(
                    rowParser.asText(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsStop.STOP_DESC_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, String>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<String> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopDesc(
                addToCacheIfPresent(
                    rowParser.asText(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsStop.STOP_LAT_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, Double>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<Double> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopLat(
                addToCacheIfPresent(
                    rowParser.asLatitude(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    builder.put(
        GtfsStop.STOP_LON_FIELD_NAME,
        new GtfsFieldLoader<GtfsStop.Builder, Double>() {
          @Override
          public void load(
              RowParser rowParser,
              int columnIndex,
              GtfsColumnDescriptor columnDescriptor,
              FieldCache<Double> fieldCache,
              GtfsStop.Builder builder) {
            builder.setStopLon(
                addToCacheIfPresent(
                    rowParser.asLongitude(columnIndex, columnDescriptor.fieldLevel()), fieldCache));
          }
        });
    return builder.build();
  }

  @Override
  public String gtfsFilename() {
    return GtfsStop.FILENAME;
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
