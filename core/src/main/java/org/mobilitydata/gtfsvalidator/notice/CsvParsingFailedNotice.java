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
import org.mobilitydata.gtfsvalidator.annotation.SchemaExport;

/**
 * Parsing of a CSV file failed.
 *
 * <p>One common case of the problem is when a cell value contains more than 4096 characters.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class CsvParsingFailedNotice extends ValidationNotice {

  /**
   * Constructor used while extracting notice information.
   *
   * @param filename the name of the file
   * @param exception the exception thrown
   */
  public CsvParsingFailedNotice(String filename, TextParsingException exception) {
    this(
        filename,
        exception.getCharIndex(),
        exception.getColumnIndex(),
        exception.getLineIndex(),
        exception.getMessage(),
        exception.getParsedContent());
  }

  /**
   * Default constructor for notice.
   *
   * @param filename the name of the file
   * @param charIndex the index of character
   * @param columnIndex the index of column
   * @param lineIndex the index of line
   * @param message the exception message
   * @param parsedContent the parsed content of the exception
   */
  @SchemaExport
  public CsvParsingFailedNotice(
      String filename,
      long charIndex,
      long columnIndex,
      long lineIndex,
      String message,
      String parsedContent) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("filename", filename)
            .put("charIndex", charIndex)
            .put("columnIndex", columnIndex)
            .put("lineIndex", lineIndex)
            .put("message", Strings.nullToEmpty(message))
            .put("content", Strings.nullToEmpty(parsedContent))
            .build(),
        SeverityLevel.ERROR);
  }
}
