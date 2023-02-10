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

  @Test
  public void agencyIdRequiredErrorWhenMoreThanOneAgency() {

    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("Agency 1")
                    .setAgencyName("Agency 1")
                    .build(),
                new GtfsAgency.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId("Agency 2")
                    .setAgencyName("Agency 2")
                    .build()),
            noticeContainer);

    GtfsFareAttributeTableContainer fareTable =
        GtfsFareAttributeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsFareAttribute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId("agency0")
                    .setFareId("fare 0")
                    .build(),
                new GtfsFareAttribute.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId(null)
                    .setFareId("fare_1")
                    .build()),
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
            ImmutableList.of(
                new GtfsAgency.Builder()
                    .setCsvRowNumber(1)
                    .setAgencyId(null)
                    .setAgencyName("Agency with no ID")
                    .build()),
            noticeContainer);

    GtfsFareAttributeTableContainer fareTable =
        GtfsFareAttributeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsFareAttribute.Builder()
                    .setCsvRowNumber(0)
                    .setAgencyId(null)
                    .setFareId("fare_0")
                    .build()),
            noticeContainer);
    new FareAttributeAgencyIdValidator(agencyTable, fareTable).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRecommendedFieldNotice(
                fareTable.gtfsFilename(), 0, GtfsFareAttribute.AGENCY_ID_FIELD_NAME));
  }
}
