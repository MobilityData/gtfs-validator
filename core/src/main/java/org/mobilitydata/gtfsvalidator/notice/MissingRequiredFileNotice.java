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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.TERM_DEFINITIONS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A required file is missing.
 *
 * <p>If this notice is triggered for every core file, it might be a problem with the input. To
 * create a zip file from the GTFS `.txt` files: select all the `.txt` files, right-click, and
 * compress. Do not compress the folder containing the files.
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(TERM_DEFINITIONS))
public class MissingRequiredFileNotice extends ValidationNotice {

  /** The name of the faulty file. */
  private final String filename;

  public MissingRequiredFileNotice(String filename) {
    this.filename = filename;
  }
}
