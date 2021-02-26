/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.FeedInfoLangAndAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

/**
 * Validates that {@code agency.agency_lang} and {@code feed_info.feed_lang} are matching.
 *
 * <p>{@code agency.agency_lang} is optional, so it is acceptable to omit that field.
 *
 * <p>The default language may be multilingual for datasets with the original text in multiple
 * languages. In such cases, the {@code feed_lang} field should contain the language code {@code
 * mul} defined by the norm ISO 639-2. If {@code feed_lang} is not {@code mul} and does not match
 * with {@code agency_lang}, a notice should be generated.
 *
 * <p>References:
 *
 * <ul>
 *   <li>&lt;a href="http://gtfs.org/reference/static/#feed_infotxt")"&gt;GTFS feed_info.txt
 *       specification&lt;/a&gt;
 *   <li>&lt;a href="http://gtfs.org/reference/static/#agencytxt"&gt;GTFS agency.txt
 *       specification&lt;/a&gt;
 * </ul>
 *
 * <p>Generated notice: {@link FeedInfoLangAndAgencyLangMismatchNotice}.
 */
@GtfsValidator
public class MatchingFeedAndAgencyLangValidator extends FileValidator {

  @Inject GtfsFeedInfoTableContainer feedInfoTable;
  @Inject GtfsAgencyTableContainer agencyTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // If there are no feed info entries or if no feed lang has been specified, we don't do any
    // validation.
    if (feedInfoTable.entityCount() == 0 || !feedInfoTable.getSingleEntity().hasFeedLang()) {
      return;
    }
    final Locale feedLang = feedInfoTable.getSingleEntity().feedLang();
    if (feedLang.equals(Locale.forLanguageTag("mul"))) {
      // A multilanguage feed may have different agency_lang.
      return;
    }
    for (GtfsAgency agency : agencyTable.getEntities()) {
      if (agency.hasAgencyLang() && !feedLang.equals(agency.agencyLang())) {
        noticeContainer.addValidationNotice(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                agency.csvRowNumber(),
                agency.agencyId(),
                agency.agencyName(),
                agency.agencyLang().toLanguageTag(),
                feedLang.toLanguageTag()));
      }
    }
  }
}
