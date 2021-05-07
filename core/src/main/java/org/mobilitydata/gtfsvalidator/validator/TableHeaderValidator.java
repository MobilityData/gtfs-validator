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

package org.mobilitydata.gtfsvalidator.validator;

import java.util.Set;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

/** A validator that checks table headers for required columns etc. */
public interface TableHeaderValidator {
  /** Validates header of a single GTFS CSV table and adds errors and warnings to the container. */
  void validate(
      String filename,
      CsvHeader actualHeader,
      Set<String> supportedColumns,
      Set<String> requiredColumns,
      NoticeContainer noticeContainer);
}
