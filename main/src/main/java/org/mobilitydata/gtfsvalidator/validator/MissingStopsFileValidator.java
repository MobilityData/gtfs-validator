package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

import javax.inject.Inject;


@GtfsValidator
public class MissingStopsFileValidator extends FileValidator {

  private final GtfsStopTableContainer stopTableContainer;
  private final GtfsGeojsonFeaturesContainer geojsonFeaturesContainer;

  @Inject
  MissingStopsFileValidator(GtfsStopTableContainer table, GtfsGeojsonFeaturesContainer geojsonFeaturesContainer) {
    this.stopTableContainer = table;
    this.geojsonFeaturesContainer = geojsonFeaturesContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (stopTableContainer.isMissingFile() && geojsonFeaturesContainer.isMissingFile()) {
        noticeContainer.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    }
  }

}
