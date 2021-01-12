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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.FeedInfoLangAndAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` and `feed_info.feed_lang`.
 * The default language may be multilingual for datasets with the original text in multiple languages. In such cases,
 * the feed_lang field should contain the language code mul defined by the norm ISO 639-2.
 * If `feed_lang` is not `mul` and does not match with `agency_lang`, that's an error
 * If there is more than one `agency_lang` and `feed_lang` isn't `mul`, that's an error
 * If `feed_lang` is `mul` and there isn't more than one `agency_lang`, that's an error

 #### References:
 * [GTFS feed_info.txt specification](http://gtfs.org/reference/static/#feed_infotxt)
 * [GTFS agency.txt specification](http://gtfs.org/reference/static/#agencytxt)
 *
 * Notice generated:
 *  * @code{FeedInfoLangAndAgencyLangMismatchNotice}
 */
@GtfsValidator
public class MatchingFeedAndAgencyLangValidator extends FileValidator{
    @Inject
    GtfsFeedInfoTableContainer feedInfoTable;
    @Inject
    GtfsAgencyTableContainer agencyTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        if (feedInfoTable.entityCount() == 0) {
            return;
        }
        // the previous early return ensures feedInfoTable is not empty
        Locale feedInfoFeedLang = feedInfoTable.getEntities().get(0).feedLang();
        Set<String> agencyLangCollection = new HashSet<>();
        agencyTable.getEntities().forEach(agency -> agencyLangCollection.add(agency.agencyLang().getISO3Language()));
        if (feedInfoFeedLang.equals(Locale.forLanguageTag("mul"))) {
            // If feed_lang is mul and there isn't more than one agency_lang, that's an error
            if (agencyLangCollection.size() <= 1) {
                noticeContainer.addNotice(
                        new FeedInfoLangAndAgencyLangMismatchNotice(
                                feedInfoFeedLang.getISO3Language(),
                                agencyLangCollection));
                return;
            }
            return;
        }
        // If there is more than one agency_lang and feed_lang isn't mul, that's an error
        if (agencyLangCollection.size() > 1) {
            noticeContainer.addNotice(
                    new FeedInfoLangAndAgencyLangMismatchNotice(
                            feedInfoFeedLang.getISO3Language(),
                            agencyLangCollection));
            return;
        }
        agencyTable.getEntities().forEach(agency -> {
            // If feed_lang is not mul and differs from agency_lang, that's an error
            if (!feedInfoFeedLang.equals(agency.agencyLang())) {
                noticeContainer.addNotice(
                        new FeedInfoLangAndAgencyLangMismatchNotice(
                                feedInfoFeedLang.getISO3Language(),
                                agencyLangCollection));
            }
        });
    }
}
