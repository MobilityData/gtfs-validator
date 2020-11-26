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

import org.mobilitydata.gtfsvalidator.notice.DuplicatedColumnNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredColumnError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A validator that checks table headers for required columns etc.
 */
public class TableHeaderValidator {
    public boolean validate(String filename, String[] actualColumns,
                            Set<String> supportedColumns, Set<String> requiredColumns,
                            NoticeContainer noticeContainer) {
        boolean isValid = true;
        if (actualColumns.length == 0) {
            // This is an empty file.
            return isValid;
        }
        Map<String, Integer> columnIndices = new HashMap<>();
        // Sorted tree set for stable order of notices.
        TreeSet<String> missingColumns = new TreeSet<>(requiredColumns);
        for (int i = 0; i < actualColumns.length; ++i) {
            String column = actualColumns[i];
            Integer prev = columnIndices.putIfAbsent(column, i);
            // Column indices are zero-based. We add 1 to make them 1-based.
            if (prev != null) {
                noticeContainer.addNotice(new DuplicatedColumnNotice(filename, column, prev + 1, i + 1));
                isValid = false;
            }
            if (!supportedColumns.contains(column)) {
                noticeContainer.addNotice(new UnknownColumnNotice(filename, column, i + 1));
            }
            missingColumns.remove(column);
        }
        if (!missingColumns.isEmpty()) {
            isValid = false;
            for (String column : missingColumns) {
                noticeContainer.addNotice(new MissingRequiredColumnError(filename, column));
            }
        }
        return isValid;
    }
}
