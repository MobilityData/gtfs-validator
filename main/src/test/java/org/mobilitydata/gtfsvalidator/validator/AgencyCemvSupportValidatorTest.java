package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;

@RunWith(JUnit4.class)
public class AgencyCemvSupportValidatorTest {
  private GtfsAgencyTableContainer agencyTable;
  private NoticeContainer noticeContainer;
  private AgencyCemvSupportValidator validator;

  @Before
  public void setUp() {
    agencyTable = GtfsAgencyTableContainer.forEntities(List.of(), new NoticeContainer());
    noticeContainer = new NoticeContainer();
    validator = new AgencyCemvSupportValidator(agencyTable);
  }

  @Test
  public void validate_validCemvSupport_noNoticeAdded() {
    GtfsAgency agency =
        new GtfsAgency.Builder()
            .setCsvRowNumber(1)
            .setCemvSupport(GtfsCemvSupport.SUPPORTED)
            .build();

    validator.validate(agency, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void validate_invalidCemvSupport_noticeAdded() {
    GtfsAgency agency =
        new GtfsAgency.Builder()
            .setCsvRowNumber(5)
            .setCemvSupport(GtfsCemvSupport.UNRECOGNIZED)
            .build();

    validator.validate(agency, noticeContainer);

    UnexpectedEnumValueNotice notice =
        new UnexpectedEnumValueNotice(
            "agency.txt", 5, "UNRECOGNIZED", GtfsCemvSupport.UNRECOGNIZED.getNumber());
    assertThat(noticeContainer.getValidationNotices()).contains(notice);
  }
}
