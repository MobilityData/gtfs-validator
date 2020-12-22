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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NoticeContainer {
    private static final int MAX_EXPORTS_PER_NOTICE_TYPE = 100000;
    private static final Gson DEFAULT_GSON = new GsonBuilder().serializeNulls().create();

    private final List<Notice> notices = new ArrayList<>();

    public void addNotice(Notice notice) {
        notices.add(notice);
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public String exportJson() {
        JsonObject root = new JsonObject();
        JsonArray jsonNotices = new JsonArray();
        root.add("notices", jsonNotices);

        ListMultimap<Integer, Notice> noticesByType = getNoticesByType();
        for (Collection<Notice> noticesOfType : noticesByType.asMap().values()) {
            JsonObject noticesOfTypeJson = new JsonObject();
            jsonNotices.add(noticesOfTypeJson);
            noticesOfTypeJson.addProperty("code", noticesOfType.iterator().next().getCode());
            noticesOfTypeJson.addProperty("totalNotices", noticesOfType.size());
            JsonArray noticesArrayJson = new JsonArray();
            noticesOfTypeJson.add("notices", noticesArrayJson);
            int i = 0;
            for (Notice notice : noticesOfType) {
                ++i;
                if (i > MAX_EXPORTS_PER_NOTICE_TYPE) {
                    // Do not export too many notices for this type.
                    break;
                }
                noticesArrayJson.add(DEFAULT_GSON.toJsonTree(notice.getContext()));
            }
        }

        return DEFAULT_GSON.toJson(root);
    }

    private ListMultimap<Integer, Notice> getNoticesByType() {
        ListMultimap<Integer, Notice> noticesByType = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Notice notice : notices) {
            noticesByType.put(notice.getClass().hashCode(), notice);
        }
        return noticesByType;
    }
}
