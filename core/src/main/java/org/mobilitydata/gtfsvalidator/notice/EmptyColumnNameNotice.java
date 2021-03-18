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
 * A column name is empty. Such columns are skipped by the validator.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class EmptyColumnNameNotice extends ValidationNotice {
  public EmptyColumnNameNotice(String filename, int index) {
    super(
        ImmutableMap.of(
            "filename", filename,
            "index", index),
        SeverityLevel.WARNING);
  }
}
