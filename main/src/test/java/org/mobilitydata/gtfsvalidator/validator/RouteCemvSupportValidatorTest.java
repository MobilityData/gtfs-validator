package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

@RunWith(JUnit4.class)
public class RouteCemvSupportValidatorTest {
  private NoticeContainer noticeContainer;
  private RouteCemvSupportValidator validator;

  @Before
  public void setUp() {
    noticeContainer = new NoticeContainer();
    validator = new RouteCemvSupportValidator();
  }

  @Test
  public void validate_validCemvSupport_noNoticeAdded() {
    GtfsRoute route =
            new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setCemvSupport(GtfsCemvSupport.SUPPORTED)
                    .build();

    validator.validate(route, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void validate_invalidCemvSupport_noticeAdded() {
    GtfsRoute route =
            new GtfsRoute.Builder()
                    .setCsvRowNumber(5)
                    .setCemvSupport(GtfsCemvSupport.UNRECOGNIZED)
                    .build();

    validator.validate(route, noticeContainer);

    UnexpectedEnumValueNotice notice =
            new UnexpectedEnumValueNotice(
                    "routes.txt", 5, "UNRECOGNIZED", GtfsCemvSupport.UNRECOGNIZED.getNumber());
    assertThat(noticeContainer.getValidationNotices()).contains(notice);
  }
}