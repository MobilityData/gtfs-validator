package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

public class ServiceHasNoActiveDayOfTheWeekValidatorTest {
  private record CalendarMetadata(String serviceId, boolean hasADayOfTheWeek) {}

  private static List<GtfsCalendar> createCalendarTable(
      List<CalendarMetadata> calendarMetadataList) {
    return calendarMetadataList.stream()
        .map(
            calendarMetadata -> {
              GtfsCalendar.Builder builder =
                  new GtfsCalendar.Builder()
                      .setCsvRowNumber(1)
                      .setServiceId(calendarMetadata.serviceId());
              if (calendarMetadata.hasADayOfTheWeek) {
                builder.setMonday(1);
              }
              return builder.build();
            })
        .collect(ImmutableList.toImmutableList());
  }

  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ServiceHasNoActiveDayOfTheWeekValidator(
            GtfsCalendarTableContainer.forEntities(
                createCalendarTable(calendarMetadataList), noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void serviceHasNoActiveDayOfTheWeek() {
    List<CalendarMetadata> calendarMetadataList =
        ImmutableList.of(
            new CalendarMetadata("service_1", false), new CalendarMetadata("service_2", true));
    List<ValidationNotice> notices = generateNotices(calendarMetadataList);
    assertThat(notices.size()).isEqualTo(1);
  }

  @Test
  public void serviceHasAnActiveDayOfTheWeek() {
    List<CalendarMetadata> calendarMetadataList =
        ImmutableList.of(
            new CalendarMetadata("service_1", true), new CalendarMetadata("service_2", true));
    List<ValidationNotice> notices = generateNotices(calendarMetadataList);
    assertThat(notices).isEmpty();
  }
}
