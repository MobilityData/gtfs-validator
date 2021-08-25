/*
 * Copyright 2021 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

/**
 * Validates {@code stops.stop_name} and {@code stops.stop_desc} for a single {@code GtfsStop}.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link SameNameAndDescriptionForStopNotice}
 * </ul>
 */
@GtfsValidator
public class StopNameValidator extends SingleEntityValidator<GtfsStop> {

  @Override
  public void validate(GtfsStop stop, NoticeContainer noticeContainer) {
    if (!stop.hasStopName() || !stop.hasStopDesc()) {
      return;
    }
    if (!isValidRouteDesc(stop.stopDesc(), stop.stopName())) {
      noticeContainer.addValidationNotice(
          new SameNameAndDescriptionForStopNotice(
              stop.csvRowNumber(), stop.stopId(), stop.stopDesc()));
    }
  }

  private boolean isValidRouteDesc(String stopDesc, String stopName) {
    // ignore lower case and upper case difference
    return !stopDesc.equalsIgnoreCase(stopName);
  }

  /**
   * A {@code GtfsStop} has identical value for {@code stops.route_desc} and {@code stops.stop_name}
   *
   * <p>"Do not simply duplicate the name of the location."
   * (http://gtfs.org/reference/static#stopstxt)
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class SameNameAndDescriptionForStopNotice extends ValidationNotice {

    private final long csvRowNumber;
    private final String stopId;
    private final String routeDesc;

    SameNameAndDescriptionForStopNotice(long csvRowNumber, String stopId, String routeDesc) {
      super(SeverityLevel.ERROR);
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
      this.routeDesc = routeDesc;
    }
  }
}
