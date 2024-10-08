package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class MissingStopsFileValidator extends FileValidator {

  private final GtfsStopTableContainer stopTableContainer;
  private final GtfsGeoJSONFeaturesContainer geoJSONFeaturesContainer;

  @Inject
  MissingStopsFileValidator(
      GtfsStopTableContainer table, GtfsGeoJSONFeaturesContainer geoJSONFeaturesContainer) {
    this.stopTableContainer = table;
    this.geoJSONFeaturesContainer = geoJSONFeaturesContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (stopTableContainer.isMissingFile() && geoJSONFeaturesContainer.isMissingFile()) {
      noticeContainer.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    }
  }
}
