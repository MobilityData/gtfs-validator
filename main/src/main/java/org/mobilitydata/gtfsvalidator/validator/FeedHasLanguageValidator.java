/*
 * Copyright 2023 Google LLC
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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

/**
 * Checks that a feed has language defined in either feed_info.feed_lang or agency.agency_lang.
 *
 * <p>Note that feed_lang is a required field: if there is a feed_info entity, it must have
 * feed_lang set. agency_lang, instead, is optional. The recommended way is to provide feed_lang and
 * omit agency_lang.
 */
@GtfsValidator
public class FeedHasLanguageValidator extends FileValidator {
  private final GtfsFeedInfoTableContainer feedInfoTable;
  private final GtfsAgencyTableContainer agencyTable;

  @Inject
  FeedHasLanguageValidator(
      GtfsAgencyTableContainer agencyTable, GtfsFeedInfoTableContainer feedInfoTable) {
    this.feedInfoTable = feedInfoTable;
    this.agencyTable = agencyTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (feedInfoTable.getEntities().size() == 1 && feedInfoTable.getSingleEntity().isPresent()) {
      // The language is defined in feed_info.feed_lang field.
      return;
    }
    for (GtfsAgency agency : agencyTable.getEntities()) {
      if (agency.hasAgencyLang()) {
        // The language is defined in agency.agency_lang field.
        return;
      }
    }
    noticeContainer.addValidationNotice(new FeedHasNoLanguageNotice());
  }

  static class FeedHasNoLanguageNotice extends ValidationNotice {
    FeedHasNoLanguageNotice() {
      super(SeverityLevel.ERROR);
    }
  }
}
