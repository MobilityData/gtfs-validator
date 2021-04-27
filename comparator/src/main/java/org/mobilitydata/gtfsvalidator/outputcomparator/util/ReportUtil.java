package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ReportUtil {

  private static final String MEMBER_NAME = "notices";

  private ReportUtil() {}

  public static boolean areReportsEqual(JsonElement referenceReport, JsonElement latestReport) {
    return referenceReport.equals(latestReport);
  }

  public static boolean containSameErrors(
      Map<String, Integer> referenceReportErrorListing,
      Map<String, Integer> latestReportErrorListing) {
    return Maps.difference(referenceReportErrorListing, latestReportErrorListing).areEqual();
  }

  public static Map<String, Integer> getErrorEntries(JsonElement jsonElement) {
    ImmutableMap.Builder<String, Integer> mapBuilder = new ImmutableMap.Builder<>();
    NoticeAggregate[] firstJsonNoticeAggregates =
        new GsonBuilder()
            .create()
            .fromJson(
                jsonElement.getAsJsonObject().get(MEMBER_NAME).getAsJsonArray(),
                NoticeAggregate[].class);
    for (NoticeAggregate noticeAggregate : firstJsonNoticeAggregates) {
      if (noticeAggregate.isError()) {
        mapBuilder.put(noticeAggregate.getCode(), noticeAggregate.getTotalNotices());
      }
    }
    return mapBuilder.build();
  }

  public static int getNewErrorCount(
      Map<String, Integer> referenceReportErrorListing,
      Map<String, Integer> latestReportErrorListing) {
    return Maps.difference(referenceReportErrorListing, latestReportErrorListing)
        .entriesOnlyOnRight()
        .size();
  }

  public static void exportJson(
      ImmutableMap<String, Object> integrationReport,
      String outputBase,
      String integrationReportName)
      throws IOException {
    Gson gson = new GsonBuilder().serializeNulls().create();
    Files.write(
        Paths.get(outputBase, integrationReportName),
        gson.toJson(integrationReport).getBytes(StandardCharsets.UTF_8));
  }
}
