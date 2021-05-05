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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.mobilitydata.gtfsvalidator.notice.DuplicatedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyColumnNameNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

/** Default implementation of {@code TableHeaderValidator}. */
public class DefaultTableHeaderValidator implements TableHeaderValidator {
  @Override
  public void validate(
      String filename,
      CsvHeader actualHeader,
      Set<String> supportedColumns,
      Set<String> requiredColumns,
      NoticeContainer noticeContainer) {
    if (actualHeader.getColumnCount() == 0) {
      // This is an empty file.
      return;
    }
    Map<String, Integer> columnIndices = new HashMap<>();
    // Sorted tree set for stable order of notices.
    TreeSet<String> missingColumns = new TreeSet<>(requiredColumns);
    for (int i = 0; i < actualHeader.getColumnCount(); ++i) {
      String column = actualHeader.getColumnName(i);
      // Column indices are zero-based. We add 1 to make them 1-based.
      if (Strings.isNullOrEmpty(column)) {
        noticeContainer.addValidationNotice(new EmptyColumnNameNotice(filename, i + 1));
        continue;
      }
      Integer prev = columnIndices.putIfAbsent(column, i);
      if (prev != null) {
        noticeContainer.addValidationNotice(
            new DuplicatedColumnNotice(filename, column, prev + 1, i + 1));
      }
      if (!supportedColumns.contains(column)) {
        noticeContainer.addValidationNotice(new UnknownColumnNotice(filename, column, i + 1));
      }
      missingColumns.remove(column);
    }
    if (!missingColumns.isEmpty()) {
      for (String column : missingColumns) {
        noticeContainer.addValidationNotice(new MissingRequiredColumnNotice(filename, column));
      }
    }
  }
}
