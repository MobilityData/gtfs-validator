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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;

/**
 * A point is too close to the North or South Pole.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class PointNearPoleNotice extends ValidationNotice {

  @SchemaExport
  public PointNearPoleNotice(
      String filename,
      long csvRowNumber,
      String entityId,
      String latFieldName,
      double latFieldValue,
      String lonFieldName,
      double lonFieldValue) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("entityId", entityId)
            .put("latFieldName", latFieldName)
            .put("latFieldValue", latFieldValue)
            .put("lonFieldName", lonFieldName)
            .put("lonFieldValue", lonFieldValue)
            .build(),
        SeverityLevel.ERROR);
  }

  public PointNearPoleNotice(
      String filename,
      long csvRowNumber,
      String latFieldName,
      double latFieldValue,
      String lonFieldName,
      double lonFieldValue) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("csvRowNumber", csvRowNumber)
            .put("latFieldName", latFieldName)
            .put("latFieldValue", latFieldValue)
            .put("lonFieldName", lonFieldName)
            .put("lonFieldValue", lonFieldValue)
            .build(),
        SeverityLevel.ERROR);
  }
}
