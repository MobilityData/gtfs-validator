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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * The values of the given key and rows of one table cannot be found a values of the given key in
 * another table.
 *
 * <p>This is the case when a foreign key of one table references a non-existing value in its
 * original table.
 */
public class ForeignKeyError extends ValidationNotice {
  public ForeignKeyError(
      String childFilename,
      String childFieldName,
      String parentFilename,
      String parentFieldName,
      String fieldValue,
      long csvRowNumber) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("childFilename", childFilename)
            .put("childFieldName", childFieldName)
            .put("parentFilename", parentFilename)
            .put("parentFieldName", parentFieldName)
            .put("fieldValue", fieldValue)
            .put("csvRowNumber", csvRowNumber)
            .build());
  }

  @Override
  public String getCode() {
    return "foreign_key_error";
  }
}
