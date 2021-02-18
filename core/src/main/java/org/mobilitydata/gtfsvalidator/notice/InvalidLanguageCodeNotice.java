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
 * A field contains a wrong language code.
 *
 * <p>Language codes must follow <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">IETF BCP
 * 47</a>.
 *
 * <p>Example: {@code en} for English, {@code en-US} for American English or {@code de} for German.
 */
public class InvalidLanguageCodeNotice extends ValidationNotice {

  public InvalidLanguageCodeNotice(
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

  @Override
  public String getCode() {
    return "invalid_language_code";
  }
}
