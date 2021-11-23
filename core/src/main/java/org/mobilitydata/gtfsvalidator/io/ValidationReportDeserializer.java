/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.io;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * Used to deserialize a JSON validation report. This represents a validation report as a list of {@code
 * NoticeReport} which provides information about each notice generated during a GTFS dataset
 * validation.
 */
public class ValidationReportDeserializer implements JsonDeserializer<ValidationReport> {

  private static final Gson GSON =
      new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
  private static final String NOTICES_MEMBER_NAME = "notices";

  /**
   * Return the sorted set of error codes from a list of {@code NoticeReport}.
   *
   * @return the sorted set of error codes from a list of {@code NoticeReport}.
   */
  private static ImmutableSet<String> extractErrorCodes(Set<NoticeReport> notices) {
    ImmutableSet.Builder<String> errorCodesSetBuilder = new ImmutableSet.Builder<>();
    for (NoticeReport noticeReport : notices) {
      if (noticeReport.isError()) {
        errorCodesSetBuilder.add(noticeReport.getCode());
      }
    }
    return errorCodesSetBuilder.build();
  }

  @Override
  public ValidationReport deserialize(
      JsonElement json, Type typoOfT, JsonDeserializationContext context) {
    Set<NoticeReport> notices = new LinkedHashSet<>();
    JsonObject rootObject = json.getAsJsonObject();
    JsonArray noticesArray = rootObject.getAsJsonArray(NOTICES_MEMBER_NAME);
    for (JsonElement childObject : noticesArray) {
      notices.add(GSON.fromJson(childObject, NoticeReport.class));
    }
    return new ValidationReport(Collections.unmodifiableSet(notices), extractErrorCodes(notices));
  }

  public static <T extends Notice> ValidationReport serialize(
      List<T> notices,
      int maxExportsPerNoticeTypeAndSeverity,
      Map<String, Integer> noticesCountPerTypeAndSeverity) {
    Set<NoticeReport> noticeReports = new LinkedHashSet<>();
    Gson gson = new Gson();
    Set<String> errorCodes = new TreeSet<>();
    Type contextType = new TypeToken<Map<String, Object>>() {}.getType();
    for (Collection<T> noticesOfType :
        NoticeContainer.groupNoticesByTypeAndSeverity(notices).asMap().values()) {
      T firstNotice = noticesOfType.iterator().next();
      if (firstNotice.isError()) {
        errorCodes.add(firstNotice.getCode());
      }
      List<LinkedTreeMap<String, Object>> contexts = new ArrayList<>();
      int i = 0;
      for (T notice : noticesOfType) {
        ++i;
        if (i > maxExportsPerNoticeTypeAndSeverity) {
          // Do not export too many notices for this type.
          break;
        }
        contexts.add(gson.fromJson(notice.getContext(), contextType));
      }
      noticeReports.add(
          new NoticeReport(
              firstNotice.getCode(),
              firstNotice.getSeverityLevel(),
              noticesCountPerTypeAndSeverity.get(firstNotice.getMappingKey()),
              contexts));
    }
    return new ValidationReport(noticeReports, errorCodes);
  }
}
