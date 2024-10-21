package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import javax.annotation.Nonnull;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * File descriptor for GeoJSON file. Contrarily to the csv file descriptor, this class is not auto
 * generated since we have only one such class.
 */
public class GtfsGeoJsonFileDescriptor extends GtfsFileDescriptor<GtfsGeoJsonFeature> {

  public GtfsGeoJsonFileDescriptor() {
    setRequired(false);
  }

  public GtfsGeoJsonFeaturesContainer createContainerForEntities(
      List<GtfsGeoJsonFeature> entities, NoticeContainer noticeContainer) {
    return new GtfsGeoJsonFeaturesContainer(this, entities, noticeContainer);
  }

  @Override
  public GtfsGeoJsonFeaturesContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsGeoJsonFeaturesContainer(this, tableStatus);
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public Class<GtfsGeoJsonFeature> getEntityClass() {
    return GtfsGeoJsonFeature.class;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }

  @Nonnull
  public TableLoader getTableLoader() {
    return new GeoJsonFileLoader();
  }
}
