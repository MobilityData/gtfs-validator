/*
 * Copyright 2020 Google LLC
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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;

/**
 * Validates that there is enough contrast between route_color and route_text_color in "routes.txt".
 *
 * <p>Generated notice: {@link RouteColorContrastNotice}.
 */
@GtfsValidator
public class RouteColorContrastValidator extends SingleEntityValidator<GtfsRoute> {
  /**
   * The maximum difference between the luma of the route display color and text color, beyond which
   * a warning is produced. http://www.w3.org/TR/2000/WD-AERT-20000426#color-contrast recommends a
   * threshold of 125, but that is for normal text and too harsh for big colored logos like line
   * names, so we allow a tighter threshold.
   */
  private static final int MAX_ROUTE_COLOR_LUMA_DIFFERENCE = 72;

  @Override
  public void validate(GtfsRoute entity, NoticeContainer noticeContainer) {
    if (!entity.hasRouteColor() || !entity.hasRouteTextColor()) {
      // Some of the colors is not given explicitly.
      return;
    }
    if (Math.abs(entity.routeColor().rec601Luma() - entity.routeTextColor().rec601Luma())
        < MAX_ROUTE_COLOR_LUMA_DIFFERENCE) {
      noticeContainer.addValidationNotice(
          new RouteColorContrastNotice(
              entity.routeId(),
              entity.csvRowNumber(),
              entity.routeColor(),
              entity.routeTextColor()));
    }
  }

  /**
   * "The color difference between route_color and route_text_color should provide sufficient
   * contrast when viewed on a black and white screen." (http://gtfs.org/best-practices/#routestxt)
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class RouteColorContrastNotice extends ValidationNotice {
    @NoticeExport
    RouteColorContrastNotice(
        String routeId, long csvRowNumber, GtfsColor routeColor, GtfsColor routeTextColor) {
      super(
          ImmutableMap.of(
              "routeId",
              routeId,
              "csvRowNumber",
              csvRowNumber,
              "routeColor",
              routeColor.toHtmlColor(),
              "routeTextColor",
              routeTextColor.toHtmlColor()),
          SeverityLevel.WARNING);
    }
  }
}
