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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * Wrong foreign key.
 *
 * <p>A foreign key references the primary key of another file. A foreign key violation means that
 * the foreign key referenced from a given row (the child file) cannot be found in the corresponding
 * file (the parent file). The Foreign keys are defined in the specification under "Type" for each
 * file.
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs(FILE_REQUIREMENTS),
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class ForeignKeyViolationNotice extends ValidationNotice {

  /** The name of the file from which reference is made. */
  private final String childFilename;

  /** The name of the field that makes reference. */
  private final String childFieldName;

  /** The name of the file that is referred to. */
  private final String parentFilename;

  /** The name of the field that is referred to. */
  private final String parentFieldName;

  /** The faulty record's value. */
  private final String fieldValue;

  /** The row of the faulty record. */
  private final int csvRowNumber;

  public ForeignKeyViolationNotice(
      String childFilename,
      String childFieldName,
      String parentFilename,
      String parentFieldName,
      String fieldValue,
      int csvRowNumber) {
    super();
    this.childFilename = childFilename;
    this.childFieldName = childFieldName;
    this.parentFilename = parentFilename;
    this.parentFieldName = parentFieldName;
    this.fieldValue = fieldValue;
    this.csvRowNumber = csvRowNumber;
  }
}
