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
import com.univocity.parsers.common.TextParsingException;

/**
 * Parsing of a CSV file failed.
 *
 * <p>One common case of the problem is when a cell value contains more than 4096 characters.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class CsvParsingFailedNotice extends ValidationNotice {
  private String filename;
  private long charIndex;
  private long columnIndex;
  private long lineIndex;
  private String message;
  private String parsedContent;

  /**
   * Constructor used while extracting notice information.
   *
   * @param filename the name of the file
   * @param exception the exception thrown
   */
  public CsvParsingFailedNotice(String filename, TextParsingException exception) {
    super(SeverityLevel.ERROR);
    this.filename = filename;
    this.charIndex = exception.getCharIndex();
    this.columnIndex = exception.getColumnIndex();
    this.lineIndex = exception.getLineIndex();
    this.message = Strings.nullToEmpty(exception.getMessage());
    this.parsedContent = Strings.nullToEmpty(exception.getParsedContent());
  }
}
