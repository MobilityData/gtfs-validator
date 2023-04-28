/*
 * Copyright 2021 Google LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * A point is too close to the North or South Pole.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
@GtfsValidationNotice(
    severity = ERROR,
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class PointNearPoleNotice extends ValidationNotice {

  // The name of the affected GTFS file.
  private final String filename;

  // The row of the faulty row.
  private final int csvRowNumber;

  // The id of the faulty entity.
  @Nullable private final String entityId;

  // The name of the field that uses latitude value.
  private final String latFieldName;

  // The latitude of the faulty row.
  private final double latFieldValue;

  // The name of the field that uses longitude value.
  private final String lonFieldName;

  // The longitude of the faulty row
  private final double lonFieldValue;

  public PointNearPoleNotice(
      String filename,
      int csvRowNumber,
      String entityId,
      String latFieldName,
      double latFieldValue,
      String lonFieldName,
      double lonFieldValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = entityId;
    this.latFieldName = latFieldName;
    this.latFieldValue = latFieldValue;
    this.lonFieldName = lonFieldName;
    this.lonFieldValue = lonFieldValue;
  }

  public PointNearPoleNotice(
      String filename,
      int csvRowNumber,
      String latFieldName,
      double latFieldValue,
      String lonFieldName,
      double lonFieldValue) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.entityId = null;
    this.latFieldName = latFieldName;
    this.latFieldValue = latFieldValue;
    this.lonFieldName = lonFieldName;
    this.lonFieldValue = lonFieldValue;
  }
}
