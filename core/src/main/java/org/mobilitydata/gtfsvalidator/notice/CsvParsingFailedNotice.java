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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.univocity.parsers.common.TextParsingException;

/**
 * Parsing of a CSV file failed.
 *
 * <p>One common case of the problem is when a cell value contains more than 4096 characters.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class CsvParsingFailedNotice extends ValidationNotice {
  public CsvParsingFailedNotice(String filename, TextParsingException exception) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("charIndex", exception.getCharIndex())
            .put("columnIndex", exception.getColumnIndex())
            .put("lineIndex", exception.getLineIndex())
            .put("message", Strings.nullToEmpty(exception.getMessage()))
            .put("content", Strings.nullToEmpty(exception.getParsedContent()))
            .build(),
        SeverityLevel.ERROR);
  }
}
