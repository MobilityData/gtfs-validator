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

/**
 * Container for validation notices (errors and warnings).
 *
 * <p>This class is not intentionally not thread-safe to increase performance. Each thread has it's
 * own NoticeContainer, and after execution is complete the results are merged.
 */
public class NoticeContainer {
  private static final int MAX_EXPORTS_PER_NOTICE_TYPE = 100000;
  private static final Gson DEFAULT_GSON = new GsonBuilder().serializeNulls().create();

  private final List<ValidationNotice> validationNotices = new ArrayList<>();
  private final List<SystemError> systemErrors = new ArrayList<>();

  public void addValidationNotice(ValidationNotice notice) {
    validationNotices.add(notice);
  }

  public void addSystemError(SystemError error) {
    systemErrors.add(error);
  }

  public List<ValidationNotice> getValidationNotices() {
    return validationNotices;
  }

  public List<SystemError> getSystemErrors() {
    return systemErrors;
  }

  public String exportValidationNotices() {
    return exportJson(validationNotices);
  }

  public String exportSystemErrors() {
    return exportJson(systemErrors);
  }

  private static <T extends Notice> String exportJson(List<T> notices) {
    JsonObject root = new JsonObject();
    JsonArray jsonNotices = new JsonArray();
    root.add("notices", jsonNotices);

    ListMultimap<Integer, T> noticesByType = groupNoticesByType(notices);
    for (Collection<T> noticesOfType : noticesByType.asMap().values()) {
      JsonObject noticesOfTypeJson = new JsonObject();
      jsonNotices.add(noticesOfTypeJson);
      noticesOfTypeJson.addProperty("code", noticesOfType.iterator().next().getCode());
      noticesOfTypeJson.addProperty("totalNotices", noticesOfType.size());
      JsonArray noticesArrayJson = new JsonArray();
      noticesOfTypeJson.add("notices", noticesArrayJson);
      int i = 0;
      for (T notice : noticesOfType) {
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

  private static <T extends Notice> ListMultimap<Integer, T> groupNoticesByType(List<T> notices) {
    ListMultimap<Integer, T> noticesByType = MultimapBuilder.treeKeys().arrayListValues().build();
    for (T notice : notices) {
      noticesByType.put(notice.getClass().hashCode(), notice);
    }
    return noticesByType;
  }

  /**
   * Adds all validation notices and system errors from another container.
   *
   * <p>This is useful for multithreaded validation: each thread has its own notice container which
   * is merged into the global container when the thread finishes.
   *
   * @param otherContainer a container to take the notices from
   */
  public void addAll(NoticeContainer otherContainer) {
    validationNotices.addAll(otherContainer.validationNotices);
    systemErrors.addAll(otherContainer.systemErrors);
  }
}
