package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class MissingStopsFileValidator extends FileValidator {

  private final GtfsStopTableContainer stopTableContainer;
  private final GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer;

  @Inject
  MissingStopsFileValidator(
      GtfsStopTableContainer table, GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer) {
    this.stopTableContainer = table;
    this.geoJsonFeaturesContainer = geoJsonFeaturesContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (stopTableContainer.isMissingFile() && geoJsonFeaturesContainer.isMissingFile()) {
      noticeContainer.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    }
  }
}
