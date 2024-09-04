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
 * Location id from locations.geojson already exists.
 *
 * <p>The id of one of the features of the locations.geojson file already exists in stops.txt or
 * location_groups.txt
 */
@GtfsValidationNotice(
    severity = ERROR,
    sections = @SectionRefs(FILE_REQUIREMENTS),
    urls = {
      @UrlRef(
          label = "Original Python validator implementation",
          url = "https://github.com/google/transitfeed")
    })
public class UniqueLocationIdViolationNotice extends ValidationNotice {

  /** The id that already exists. */
  private final String id;

  /** The name of the file that already has this id. */
  private final String fileWithIdAlreadyPresent;

  /** The name of the field that contains this id. */
  private final String fieldNameInFile;

  /** The row of the record in the file where the id is already present. */
  private final int csvRowNumber;

  public UniqueLocationIdViolationNotice(
      String id, String fileWithIdAlreadyPresent, String fieldNameInFile, int csvRowNumber) {

    this.id = id;
    this.fileWithIdAlreadyPresent = fileWithIdAlreadyPresent;
    this.fieldNameInFile = fieldNameInFile;
    this.csvRowNumber = csvRowNumber;
  }
}
