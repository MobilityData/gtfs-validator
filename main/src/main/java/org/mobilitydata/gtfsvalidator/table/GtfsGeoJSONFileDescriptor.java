package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import javax.annotation.Nonnull;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * File descriptor for GeoJSON file. Contrarily to the csv file descriptor, this class is not auto
 * generated since we have only one such class.
 */
public class GtfsGeoJSONFileDescriptor extends GtfsFileDescriptor<GtfsGeoJSONFeature> {

  public GtfsGeoJSONFileDescriptor() {
    setRequired(false);
  }

  public GtfsGeoJSONFeaturesContainer createContainerForEntities(
      List<GtfsGeoJSONFeature> entities, NoticeContainer noticeContainer) {
    return new GtfsGeoJSONFeaturesContainer(this, entities, noticeContainer);
  }

  @Override
  public GtfsGeoJSONFeaturesContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsGeoJSONFeaturesContainer(this, tableStatus);
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public Class<GtfsGeoJSONFeature> getEntityClass() {
    return GtfsGeoJSONFeature.class;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }

  @Nonnull
  public TableLoader getTableLoader() {
    return new GeoJSONFileLoader();
  }
}
