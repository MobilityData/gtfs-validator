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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Set;

public class FeedInfoLangAndAgencyLangMismatchNotice extends Notice {
    public FeedInfoLangAndAgencyLangMismatchNotice(String feedInfoLang,
                                                   Set<String> agencyLangCollection) {
        super(ImmutableMap.of(
                "feedInfoLang", feedInfoLang,
                "agencyLangCollection", agencyLangCollection));
    }

    @Override
    public String getCode() {
        return "feed_info_lang_and_agency_lang_mismatch";
    }
}
