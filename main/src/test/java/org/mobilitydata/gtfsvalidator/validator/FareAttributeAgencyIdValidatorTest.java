package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;

public class FareAttributeAgencyIdValidatorTest {

  public static GtfsFareAttribute createFare(int rowNumber, String fareId, String agencyId) {

    return new GtfsFareAttribute.Builder()
        .setCsvRowNumber(rowNumber)
        .setAgencyId(agencyId)
        .setFareId(fareId)
        .build();
  }

  public static GtfsAgency createAgency(int csvRowNumber, String agencyId, String agencyName) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setAgencyId(agencyId)
        .setAgencyName(agencyName)
        .build();
  }

  @Test
  public void agencyIdRequiredErrorWhenMoreThanOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                createAgency(0, "Agency 1", "Agency 1"), createAgency(1, "Agency 2", "Agency 2")),
            noticeContainer);
    GtfsFareAttributeTableContainer fareTable =
        GtfsFareAttributeTableContainer.forEntities(
            ImmutableList.of(createFare(0, "fare 0", "agency0"), createFare(1, "fare_1", null)),
            noticeContainer);
    new FareAttributeAgencyIdValidator(agencyTable, fareTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRequiredFieldNotice(
                fareTable.gtfsFilename(), 1, GtfsFareAttribute.AGENCY_ID_FIELD_NAME));
  }

  @Test
  public void agencyIdRecommendedWarningWhenOnlyOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(createAgency(1, null, "Agency with no ID")), noticeContainer);
    GtfsFareAttributeTableContainer fareTable =
        GtfsFareAttributeTableContainer.forEntities(
            ImmutableList.of(createFare(0, "fare_0", null)), noticeContainer);
    new FareAttributeAgencyIdValidator(agencyTable, fareTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRecommendedFieldNotice(
                fareTable.gtfsFilename(), 0, GtfsFareAttribute.AGENCY_ID_FIELD_NAME));
  }
}
