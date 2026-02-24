package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.validator.MissingRequiredAgencyIdValidator.MissingRequiredAgencyIdNotice;

public class MissingRequiredAgencyValidatorTest {

  // ── helpers ──────────────────────────────────────────────────────────────

  private static GtfsAgency agencyWithId(int row, String id, String name) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(row)
        .setAgencyId(id)
        .setAgencyName(name)
        .build();
  }

  private static GtfsRoute routeWithAgencyId(int row, String routeId, String agencyId) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(row)
        .setRouteId(routeId)
        .setRouteShortName("Route " + routeId)
        .setAgencyId(agencyId)
        .build();
  }

  private static GtfsFareAttribute fareWithAgencyId(int row, String fareId, String agencyId) {
    return new GtfsFareAttribute.Builder()
        .setCsvRowNumber(row)
        .setFareId(fareId)
        .setAgencyId(agencyId)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsAgency> agencies, List<GtfsRoute> routes, List<GtfsFareAttribute> fares) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsAgencyTableContainer agencyTable =
        GtfsAgencyTableContainer.forEntities(agencies, noticeContainer);
    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    GtfsFareAttributeTableContainer fareTable =
        GtfsFareAttributeTableContainer.forEntities(fares, noticeContainer);
    new MissingRequiredAgencyIdValidator(agencyTable, routeTable, fareTable)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  // ── single agency → early return, no notices ─────────────────────────────

  @Test
  public void singleAgencyWithId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(agencyWithId(1, "a1", "Agency 1")),
                ImmutableList.of(routeWithAgencyId(1, "r1", "a1")),
                ImmutableList.of(fareWithAgencyId(1, "f1", "a1"))))
        .isEmpty();
  }

  @Test
  public void singleAgencyWithoutId_noNotice() {
    // Only one agency ⇒ the validator returns early (size <= 1).
    assertThat(
            generateNotices(
                ImmutableList.of(agencyWithId(1, null, "Solo Agency")),
                ImmutableList.of(routeWithAgencyId(1, "r1", null)),
                ImmutableList.of(fareWithAgencyId(1, "f1", null))))
        .isEmpty();
  }

  // ── multiple agencies, all ids present → no notices ──────────────────────

  @Test
  public void multipleAgenciesAllIdsPresent_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, "a2", "Agency 2")),
                ImmutableList.of(
                    routeWithAgencyId(1, "r1", "a1"), routeWithAgencyId(2, "r2", "a2")),
                ImmutableList.of(fareWithAgencyId(1, "f1", "a1"), fareWithAgencyId(2, "f2", "a2"))))
        .isEmpty();
  }

  // ── agency table: missing agency_id ──────────────────────────────────────

  @Test
  public void multipleAgencies_agencyMissingId_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, null, "Agency 2")),
                ImmutableList.of(routeWithAgencyId(1, "r1", "a1")),
                ImmutableList.of(fareWithAgencyId(1, "f1", "a1"))))
        .containsExactly(new MissingRequiredAgencyIdNotice("agency.txt", 2, "Agency 2"));
  }

  // ── route table: missing agency_id ───────────────────────────────────────

  @Test
  public void multipleAgencies_routeMissingAgencyId_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, "a2", "Agency 2")),
                ImmutableList.of(
                    routeWithAgencyId(1, "r1", "a1"), routeWithAgencyId(2, "r2", null)),
                ImmutableList.of(fareWithAgencyId(1, "f1", "a1"))))
        .containsExactly(new MissingRequiredAgencyIdNotice("routes.txt", 2, null));
  }

  // ── fare attribute table: missing agency_id ──────────────────────────────

  @Test
  public void multipleAgencies_fareMissingAgencyId_generatesNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, "a2", "Agency 2")),
                ImmutableList.of(routeWithAgencyId(1, "r1", "a1")),
                ImmutableList.of(fareWithAgencyId(1, "f1", "a1"), fareWithAgencyId(2, "f2", null))))
        .containsExactly(new MissingRequiredAgencyIdNotice("fare_attributes.txt", 2, null));
  }

  // ── multiple tables with missing ids at the same time ────────────────────

  @Test
  public void multipleAgencies_missingIdsAcrossAllTables_generatesMultipleNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, null, "Agency 2")),
                ImmutableList.of(routeWithAgencyId(3, "r1", null)),
                ImmutableList.of(fareWithAgencyId(4, "f1", null))))
        .containsExactly(
            new MissingRequiredAgencyIdNotice("agency.txt", 2, "Agency 2"),
            new MissingRequiredAgencyIdNotice("routes.txt", 3, null),
            new MissingRequiredAgencyIdNotice("fare_attributes.txt", 4, null));
  }

  // ── empty route / fare tables ────────────────────────────────────────────

  @Test
  public void multipleAgencies_emptyRoutesAndFares_onlyAgencyNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, null, "No-Id Agency")),
                ImmutableList.of(),
                ImmutableList.of()))
        .containsExactly(new MissingRequiredAgencyIdNotice("agency.txt", 2, "No-Id Agency"));
  }

  @Test
  public void multipleAgenciesAllValid_emptyRoutesAndFares_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    agencyWithId(1, "a1", "Agency 1"), agencyWithId(2, "a2", "Agency 2")),
                ImmutableList.of(),
                ImmutableList.of()))
        .isEmpty();
  }
}
