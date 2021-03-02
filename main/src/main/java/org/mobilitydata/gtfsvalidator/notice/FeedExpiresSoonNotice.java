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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * At any time, the published GTFS dataset should be valid for at least the next 7 days, and ideally
 * for as long as the operator is confident that the schedule will continue to be operated. If
 * possible, the GTFS dataset should cover at least the next 30 days of service.
 * (http://gtfs.org/best-practices/#feed_infotxt)
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class FeedExpiresSoonNotice extends ValidationNotice {
  public FeedExpiresSoonNotice(
      long csvRowNumber,
      GtfsDate currentDate,
      GtfsDate feedEndDate,
      GtfsDate suggestedExpirationDate) {
    super(
        ImmutableMap.of(
            "csvRowNumber",
            csvRowNumber,
            "currentDate",
            currentDate.toYYYYMMDD(),
            "feedEndDate",
            feedEndDate.toYYYYMMDD(),
            "suggestedExpirationDate",
            suggestedExpirationDate),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "feed_expires_soon";
  }
}
