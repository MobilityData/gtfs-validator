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

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ResolvedNotice;

/**
 * Used to (de)serialize a JSON validation report. This represents a validation report as a list of
 * {@code NoticeReport} which provides information about each notice generated during a GTFS dataset
 * validation.
 */
public class ValidationReportDeserializer implements JsonDeserializer<ValidationReport> {

  private static final String NOTICES_MEMBER_NAME = "notices";
  private static final String SUMMARY_MEMBER_NAME = "summary";
  private static final String VALIDATION_TIME_MEMBER_NAME = "validationTimeSeconds";

  @Override
  public ValidationReport deserialize(
      JsonElement json, Type typoOfT, JsonDeserializationContext context) {
    Set<NoticeReport> notices = new LinkedHashSet<>();
    JsonObject rootObject = json.getAsJsonObject();
    // Note that the json file contains the summary in addition to the notices, but it is ignored
    // since currently the report comparison is only on the notices
    Double validationTimeSeconds = null;
    if (rootObject.has(SUMMARY_MEMBER_NAME)) {
      JsonObject summaryObject = rootObject.getAsJsonObject(SUMMARY_MEMBER_NAME);
      if (summaryObject.has(VALIDATION_TIME_MEMBER_NAME)) {
        validationTimeSeconds = summaryObject.get(VALIDATION_TIME_MEMBER_NAME).getAsDouble();
      }
    }
    JsonArray noticesArray = rootObject.getAsJsonArray(NOTICES_MEMBER_NAME);
    for (JsonElement childObject : noticesArray) {
      notices.add(Notice.GSON.fromJson(childObject, NoticeReport.class));
    }
    return new ValidationReport(notices, validationTimeSeconds);
  }

  public static <T extends Notice> JsonObject serialize(
      List<ResolvedNotice<T>> resolvedNotices,
      int maxExportsPerNoticeTypeAndSeverity,
      Map<String, Integer> noticesCountPerTypeAndSeverity) {
    Set<NoticeReport> noticeReports = new LinkedHashSet<>();
    for (Collection<ResolvedNotice<T>> noticesOfType :
        NoticeContainer.groupNoticesByTypeAndSeverity(resolvedNotices).asMap().values()) {
      ResolvedNotice<T> firstNotice = noticesOfType.iterator().next();
      ImmutableList.Builder<JsonElement> noticesToExport = ImmutableList.builder();
      int i = 0;
      for (ResolvedNotice<T> notice : noticesOfType) {
        ++i;
        if (i > maxExportsPerNoticeTypeAndSeverity) {
          // Do not export too many notices for this type.
          break;
        }
        noticesToExport.add(notice.getContext().toJsonTree());
      }
      noticeReports.add(
          new NoticeReport(
              firstNotice.getContext().getCode(),
              firstNotice.getSeverityLevel(),
              noticesCountPerTypeAndSeverity.get(firstNotice.getMappingKey()),
              noticesToExport.build()));
    }
    return Notice.GSON.toJsonTree(new ValidationReport(noticeReports)).getAsJsonObject();
  }

  public static <T extends Notice> ValidationReport createValidationReport(
      List<ResolvedNotice<T>> resolvedNotices,
      int maxExportsPerNoticeTypeAndSeverity,
      Map<String, Integer> noticesCountPerTypeAndSeverity) {
    Set<NoticeReport> noticeReports = new LinkedHashSet<>();
    for (Collection<ResolvedNotice<T>> noticesOfType :
        NoticeContainer.groupNoticesByTypeAndSeverity(resolvedNotices).asMap().values()) {
      ResolvedNotice<T> firstNotice = noticesOfType.iterator().next();
      ImmutableList.Builder<JsonElement> noticesToExport = ImmutableList.builder();
      int i = 0;
      for (ResolvedNotice<T> notice : noticesOfType) {
        ++i;
        if (i > maxExportsPerNoticeTypeAndSeverity) {
          // Do not export too many notices for this type.
          break;
        }
        noticesToExport.add(notice.getContext().toJsonTree());
      }
      noticeReports.add(
          new NoticeReport(
              firstNotice.getContext().getCode(),
              firstNotice.getSeverityLevel(),
              noticesCountPerTypeAndSeverity.get(firstNotice.getMappingKey()),
              noticesToExport.build()));
    }
    return new ValidationReport(noticeReports);
  }
}
