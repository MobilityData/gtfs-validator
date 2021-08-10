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

import javax.annotation.Nullable;

/**
 * A point is too close to origin (0, 0).
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class PointNearOriginNotice extends ValidationNotice {
  private final String filename;
  private final long csvRowNumber;
  @Nullable private final String entityId;
  private final String latFieldName;
  private final double latFieldValue;
  private final String lonFieldName;
  private final double lonFieldValue;

  public PointNearOriginNotice(
      String filename,
      long csvRowNumber,
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

  public PointNearOriginNotice(
      String filename,
      long csvRowNumber,
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
