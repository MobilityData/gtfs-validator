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

package org.mobilitydata.gtfsvalidator.parsing;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

/**
 * Reading support for a CSV file in GTFS feed. The file normally has headers and 0 or several data
 * rows.
 */
public class CsvFile implements Iterable<CsvRow> {
  private final boolean isEmpty;
  private final CsvParser parser;
  private final HashMap<String, Integer> columnIndices = new HashMap<>();
  private final String filename;
  private String[] columnNames;

  public CsvFile(InputStream inputStream, String filename) {
    this.filename = filename;

    // Only UTF-8 is supported according to GTFS reference. We may add optional support for other
    // encodings later.
    final BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8);
    final CharsetDecoder decoder =
        StandardCharsets.UTF_8
            .newDecoder()
            .replaceWith("\uFFFD")
            .onMalformedInput(CodingErrorAction.REPLACE);
    final BufferedReader reader =
        new BufferedReader(new InputStreamReader(bomInputStream, decoder));

    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    settings.getFormat().setDelimiter(',');
    settings.setHeaderExtractionEnabled(true);
    // Explicitly disable trimming of whitespaces because we will emit notices for them.
    settings.setIgnoreLeadingWhitespacesInQuotes(false);
    settings.setIgnoreTrailingWhitespacesInQuotes(false);
    parser = new CsvParser(settings);
    parser.beginParsing(reader);

    columnNames = parser.getContext().headers();
    isEmpty = columnNames == null;
    if (isEmpty) {
      // Do not leave them as null.
      columnNames = new String[] {};
      return;
    }
    for (int i = 0; i < columnNames.length; ++i) {
      columnIndices.putIfAbsent(columnNames[i], i);
    }
  }

  /**
   * Tells if the file is empty, i.e. it has no rows and even no headers.
   *
   * @return true if the file is empty, false otherwise
   */
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * Returns an iterator over this CSV file.
   *
   * <p>Note that you can iterate over the file only once. Any subsequent call to this function
   * returns an iterator that starts from the previous position.
   *
   * @return an iterator over this CSV file.
   */
  @Override
  public Iterator<CsvRow> iterator() {
    return new CsvFileIterator();
  }

  /**
   * Advances to the next row.
   *
   * @return the next {@link CsvRow} or null if end of file was reached.
   */
  @Nullable
  private CsvRow nextResult() {
    String[] columnValues = parser.parseNext();
    if (columnValues == null) {
      return null;
    }
    return new CsvRow(this, parser.getContext().currentLine(), columnValues);
  }

  /**
   * Base name of the file, e.g., "stops.txt".
   *
   * @return file name
   */
  public String getFileName() {
    return filename;
  }

  public String[] getColumnNames() {
    return columnNames;
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public int getColumnIndex(String columnName) {
    return columnIndices.getOrDefault(columnName, -1);
  }

  @Nullable
  public String getColumnName(int columnIndex) {
    if (columnNames == null || columnIndex < 0 || columnIndex >= columnNames.length) {
      return null;
    }
    return columnNames[columnIndex];
  }

  class CsvFileIterator implements Iterator<CsvRow> {
    boolean started = false;
    CsvRow nextRow = null;

    @Override
    public boolean hasNext() {
      if (started) {
        return nextRow != null;
      }
      started = true;
      nextRow = nextResult();
      return nextRow != null;
    }

    @Override
    @Nullable
    public CsvRow next() {
      if (!started) {
        hasNext();
      }
      CsvRow out = nextRow;
      nextRow = nextResult();
      return out;
    }
  }
}
