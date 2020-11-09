/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;
import java.util.Set;

public class FeedInfoLangAgencyLangMismatchNotice extends ErrorNotice {

    public FeedInfoLangAgencyLangMismatchNotice(final String agencyId,
                                                final String agencyName,
                                                final String agencyLang,
                                                final String feedInfoFeedLang) {
        super("feed_info.txt",
                E_055,
                "Mismatching feed and agency language fields",
                String.format("Agency `agency_name`: `%s` has mismatching `agency.agency_lang`: `%s` and" +
                                " feed_info.feed_lang`: `%s`.",
                        agencyName,
                        agencyLang,
                        feedInfoFeedLang),
                agencyId
        );
        putNoticeSpecific(KEY_AGENCY_NAME, agencyName);
        putNoticeSpecific(KEY_AGENCY_AGENCY_LANG, agencyLang);
        putNoticeSpecific(KEY_FEED_INFO_FEED_LANG, feedInfoFeedLang);
    }

    public FeedInfoLangAgencyLangMismatchNotice(final Set<String> agencyLangCollection, final String feedInfoFeedLang) {
        super("feed_info.txt",
                E_055,
                "Mismatching feed and agency language fields",
                String.format("`feed_info.feed_lang` is `%s` but `agency.txt` defines more than one " +
                                "`agency.agency_lang`: `%s`",
                        feedInfoFeedLang,
                        agencyLangCollection),
                null);
        putNoticeSpecific(KEY_FEED_INFO_FEED_LANG, feedInfoFeedLang);
        putNoticeSpecific(KEY_AGENCY_AGENCY_LANG_COLLECTION, agencyLangCollection);
    }

    public FeedInfoLangAgencyLangMismatchNotice(final Set<String> agencyLangCollection) {
        super("feed_info.txt",
                E_055,
                "Mismatching feed and agency language fields",
                String.format("`feed_info.feed_lang` is `mul` but `agency.txt` only defines: `%s` as agency_lang.",
                        agencyLangCollection),
                null);
        putNoticeSpecific(KEY_AGENCY_AGENCY_LANG_COLLECTION, agencyLangCollection);
        putNoticeSpecific(KEY_FEED_INFO_FEED_LANG, "mul");
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
