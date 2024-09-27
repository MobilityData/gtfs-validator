package org.mobilitydata.gtfsvalidator.table;

import java.util.List;
import javax.annotation.Nonnull;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * File descriptor for geojson file. Contrarily to the csv file descriptor, this class is not auto
 * generated since we have only one such class.
 */
public class GtfsGeojsonFileDescriptor extends GtfsFileDescriptor<GtfsGeojsonFeature> {

  public GtfsGeojsonFileDescriptor() {
    setRequired(false);
  }

  public GtfsGeojsonFeaturesContainer createContainerForEntities(
      List<GtfsGeojsonFeature> entities, NoticeContainer noticeContainer) {
    return new GtfsGeojsonFeaturesContainer(this, entities, noticeContainer);
  }

  @Override
  public GtfsGeojsonFeaturesContainer createContainerForInvalidStatus(TableStatus tableStatus) {
    return new GtfsGeojsonFeaturesContainer(this, tableStatus);
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

  @Nonnull
  public TableLoader getTableLoader() {
    return new GeoJsonFileLoader();
  }
}
