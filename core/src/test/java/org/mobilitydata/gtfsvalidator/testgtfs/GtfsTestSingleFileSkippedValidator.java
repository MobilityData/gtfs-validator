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

package org.mobilitydata.gtfsvalidator.testgtfs;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;

public class GtfsTestSingleFileSkippedValidator extends FileValidator {

  private final GtfsTestTableContainer table;
  private final CountryCode countryCode;
  private final DateForValidation dateForValidation;

  @Inject
  public GtfsTestSingleFileSkippedValidator(
      GtfsTestTableContainer table, CountryCode countryCode, DateForValidation dateForValidation) {
    this.table = table;
    this.countryCode = countryCode;
    this.dateForValidation = dateForValidation;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {}

  public GtfsTestTableContainer getStopTable() {
    return table;
  }

  public CountryCode getCountryCode() {
    return countryCode;
  }

  public DateForValidation getDateForValidation() {
    return dateForValidation;
  }

  @Override
  public boolean shouldCallValidate() {
    return false;
  }
}
