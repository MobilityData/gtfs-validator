package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeoJsonFeature;
import org.mobilitydata.gtfsvalidator.table.GtfsGeoJsonFeaturesContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

@GtfsValidator
public class LocationIdForeignKeyValidator extends FileValidator {
  private final GtfsGeoJsonFeaturesContainer locationGeoJSONTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  public LocationIdForeignKeyValidator(
      GtfsGeoJsonFeaturesContainer locationGeoJSONTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.locationGeoJSONTable = locationGeoJSONTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public boolean shouldCallValidate() {
    // The location_id column is optional, so we should only call validate if the column exists.
    return stopTimeTable != null
        && locationGeoJSONTable != null
        && stopTimeTable.hasColumn(GtfsStopTime.LOCATION_ID_FIELD_NAME);
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      String foreignKey = stopTime.locationId();
      if (foreignKey.isEmpty()) {
        continue;
      }
      if (!locationGeoJSONTable.byLocationIdMap().containsKey(foreignKey)) {
        noticeContainer.addValidationNotice(
            new ForeignKeyViolationNotice(
                GtfsStopTime.FILENAME,
                GtfsStopTime.LOCATION_ID_FIELD_NAME,
                GtfsGeoJsonFeature.FILENAME,
                GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME,
                foreignKey,
                stopTime.csvRowNumber()));
      }
    }
  }
}
