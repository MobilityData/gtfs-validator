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
 * A row in the input file has only spaces.
 *
 * <p>Some CSV parsers, such as Univocity, may misinterpret it as a non-empty row that has a single
 * column which is empty, hence we show a warning.
 */
public class EmptyRowNotice extends ValidationNotice {

  public EmptyRowNotice(String filename, long csvRowNumber) {
    super(
        ImmutableMap.of(
            "filename", filename,
            "csvRowNumber", csvRowNumber),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "empty_row";
  }
}
