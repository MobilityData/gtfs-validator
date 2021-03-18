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
 * A field contains a wrong currency code.
 *
 * <p>Currency code must follow <a href="https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO
 * 4217</a>.
 *
 * <p>Example: {@code CAD} for Canadian dollars, {@code EUR} for euros or {@code JPY} for Japanese
 * yen.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class InvalidCurrencyNotice extends ValidationNotice {

  public InvalidCurrencyNotice(
      String filename, long csvRowNumber, String fieldName, String fieldValue) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "csvRowNumber",
            csvRowNumber,
            "fieldName",
            fieldName,
            "fieldValue",
            fieldValue),
        SeverityLevel.ERROR);
  }
}
