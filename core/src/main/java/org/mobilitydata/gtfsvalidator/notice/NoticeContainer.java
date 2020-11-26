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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NoticeContainer {
    private static final int MAX_EXPORTS_PER_NOTICE_TYPE = 100000;
    private final ObjectMapper mapper = new ObjectMapper();

    private List<Notice> notices = new ArrayList<>();

    public void addNotice(Notice notice) {
        notices.add(notice);
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public String exportJson() throws JsonProcessingException {
        ObjectNode root = new ObjectNode(new JsonNodeFactory(true));
        ArrayNode jsonNotices = root.withArray("notices");

        ListMultimap<Integer, Notice> noticesByType = getNoticesByType();
        for (Collection<Notice> noticesOfType : noticesByType.asMap().values()) {
            ObjectNode noticesOfTypeJson = jsonNotices.addObject();
            noticesOfTypeJson.put("code", noticesOfType.iterator().next().getCode());
            noticesOfTypeJson.put("totalNotices", noticesOfType.size());
            ArrayNode noticesArrayJson = noticesOfTypeJson.withArray("notices");
            int i = 0;
            for (Notice notice : noticesOfType) {
                ++i;
                if (i > MAX_EXPORTS_PER_NOTICE_TYPE) {
                    // Do not export too many notices for this type.
                    break;
                }
                ObjectNode noticeJson = noticesArrayJson.addObject();
                for (Map.Entry<String, Object> kv : notice.getContext().entrySet()) {
                    noticeJson.putPOJO(kv.getKey(), kv.getValue());
                }
            }
        }

        return mapper.writeValueAsString(root);
    }

    private ListMultimap<Integer, Notice> getNoticesByType() {
        ListMultimap<Integer, Notice> noticesByType = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Notice notice : notices) {
            noticesByType.put(notice.getClass().hashCode(), notice);
        }
        return noticesByType;
    }
}
