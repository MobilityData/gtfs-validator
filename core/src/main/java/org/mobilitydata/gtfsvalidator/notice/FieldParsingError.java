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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * The values in the given column of the input rows do not represent valid values according to the
 * column type, or have values that conflict with others according to the requirements on the input.
 */
public class FieldParsingError extends ValidationNotice {

  public FieldParsingError(
      String filename,
      long csvRowNumber,
      String fieldName,
      String fieldType,
      @Nullable String fieldValue) {
    super(createContext(filename, csvRowNumber, fieldName, fieldType, fieldValue));
  }

  private static Map<String, Object> createContext(
      String filename,
      long csvRowNumber,
      String fieldName,
      String fieldType,
      @Nullable String fieldValue) {
    // ImmutableMap does not support null values, so we have to use a HashMap here.
    Map<String, Object> map = new HashMap<>();
    map.put("filename", filename);
    map.put("csvRowNumber", csvRowNumber);
    map.put("fieldName", fieldName);
    map.put("fieldType", fieldType);
    map.put("fieldValue", fieldValue);
    return map;
  }

  @Override
  public String getCode() {
    return "field_parsing_error";
  }
}
