/*
 * Copyright 2026 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLicense;
import org.mobilitydata.gtfsvalidator.table.GtfsLicenseTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.LicenseValidator.ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice;
import org.mobilitydata.gtfsvalidator.validator.LicenseValidator.LicensesUnknownTableNameNotice;
import org.mobilitydata.gtfsvalidator.validator.LicenseValidator.MissingLicenseSpdxIdOrCustomLicenseUrlNotice;

@RunWith(JUnit4.class)
public final class LicenseValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsLicense... licenses) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLicenseTableContainer licenseTable =
        GtfsLicenseTableContainer.forEntities(ImmutableList.copyOf(licenses), noticeContainer);
    GtfsShapeTableContainer shapeTable =
        GtfsShapeTableContainer.forEntities(ImmutableList.<GtfsShape>of(), noticeContainer);
    GtfsStopTableContainer stopTable =
        GtfsStopTableContainer.forEntities(ImmutableList.<GtfsStop>of(), noticeContainer);
    new LicenseValidator(
            licenseTable,
            new GtfsFeedContainer(ImmutableList.of(licenseTable, shapeTable, stopTable)))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsLicense.Builder license(int csvRowNumber) {
    return new GtfsLicense.Builder().setCsvRowNumber(csvRowNumber);
  }

  @Test
  public void validSpdxIdOnly_noNotice() {
    assertThat(generateNotices(license(2).setLicenseSpdxId("CC-BY-4.0").build())).isEmpty();
  }

  @Test
  public void validCustomUrlOnly_noNotice() {
    assertThat(
            generateNotices(
                license(2)
                    .setLicensedTableName("shapes")
                    .setCustomLicenseUrl("https://example.com/license")
                    .build()))
        .isEmpty();
  }

  @Test
  public void globalLicenseAndTwoFileLicenses_noNotice() {
    assertThat(
            generateNotices(
                license(2).setLicenseSpdxId("CC0-1.0").build(),
                license(3).setLicensedTableName("shapes").setLicenseSpdxId("ODbL-1.0").build(),
                license(4)
                    .setLicensedTableName("stops")
                    .setCustomLicenseUrl("https://example.com/license")
                    .build()))
        .isEmpty();
  }

  @Test
  public void unknownTableName_yieldsNotice() {
    GtfsLicense license =
        license(2).setLicensedTableName("unknown_table").setLicenseSpdxId("MIT").build();
    assertThat(generateNotices(license))
        .containsExactly(new LicensesUnknownTableNameNotice(license));
  }

  @Test
  public void tableNameWithTxtExtension_yieldsNotice() {
    GtfsLicense license =
        license(2).setLicensedTableName("shapes.txt").setLicenseSpdxId("MIT").build();
    assertThat(generateNotices(license))
        .containsExactly(new LicensesUnknownTableNameNotice(license));
  }

  @Test
  public void locationsTableName_noNotice() {
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("locations").setLicenseSpdxId("MIT").build()))
        .isEmpty();
  }

  @Test
  public void locationsGeoJsonTableName_noNotice() {
    assertThat(
            generateNotices(
                license(2)
                    .setLicensedTableName("locations.geojson")
                    .setLicenseSpdxId("MIT")
                    .build()))
        .isEmpty();
  }

  @Test
  public void tableNameWithDifferentCase_noNotice() {
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("Shapes").setLicenseSpdxId("MIT").build(),
                license(3)
                    .setLicensedTableName("Locations.GeoJSON")
                    .setLicenseSpdxId("MIT")
                    .build()))
        .isEmpty();
  }

  @Test
  public void sameTableNameWithDifferentCase_yieldsDuplicateKeyNotice() {
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("shapes").setLicenseSpdxId("MIT").build(),
                license(3).setLicensedTableName("Shapes").setLicenseSpdxId("CC0-1.0").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, "Shapes"));
  }

  @Test
  public void locationsAndLocationsGeoJson_yieldsDuplicateKeyNotice() {
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("locations").setLicenseSpdxId("MIT").build(),
                license(3)
                    .setLicensedTableName("locations.geojson")
                    .setLicenseSpdxId("CC0-1.0")
                    .build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME,
                2,
                3,
                GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME,
                "locations.geojson"));
  }

  @Test
  public void identicalLocationsRows_onlyPrimaryKeyNotice() {
    // Identical values are reported once, by the primary key check while loading.
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("locations").setLicenseSpdxId("MIT").build(),
                license(3).setLicensedTableName("locations").setLicenseSpdxId("CC0-1.0").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME,
                2,
                3,
                GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME,
                "locations"));
  }

  @Test
  public void missingSpdxIdAndCustomUrl_yieldsNotice() {
    assertThat(generateNotices(license(2).build()))
        .containsExactly(new MissingLicenseSpdxIdOrCustomLicenseUrlNotice(2));
  }

  @Test
  public void forbiddenSpdxIdAndCustomUrl_yieldsNotice() {
    assertThat(
            generateNotices(
                license(3)
                    .setLicenseSpdxId("MIT")
                    .setCustomLicenseUrl("https://example.com/license")
                    .build()))
        .containsExactly(new ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice(3));
  }

  @Test
  public void twoFeedWideLicensesConflictingSpdxId_yieldsDuplicateKeyNotice() {
    assertThat(
            generateNotices(
                license(2).setLicenseSpdxId("CC0-1.0").build(),
                license(3).setLicenseSpdxId("ODbL-1.0").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, ""));
  }

  @Test
  public void twoFeedWideLicensesConflictingCustomUrl_yieldsDuplicateKeyNotice() {
    assertThat(
            generateNotices(
                license(2).setCustomLicenseUrl("https://example.com/license1").build(),
                license(3).setCustomLicenseUrl("https://example.com/license2").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, ""));
  }

  @Test
  public void twoIdenticalFeedWideLicenses_yieldsDuplicateKeyNotice() {
    assertThat(
            generateNotices(
                license(2).setLicenseSpdxId("CC0-1.0").build(),
                license(3).setLicenseSpdxId("CC0-1.0").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, ""));
  }

  @Test
  public void threeFeedWideLicenses_eachExtraRowReportedAgainstFirst() {
    assertThat(
            generateNotices(
                license(2).setLicenseSpdxId("CC0-1.0").build(),
                license(3).setLicenseSpdxId("MIT").build(),
                license(4).setCustomLicenseUrl("https://example.com/license").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, ""),
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 4, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, ""));
  }

  @Test
  public void validAndInvalidRows_onlyInvalidRowsFlagged() {
    GtfsLicense unknown =
        license(4).setLicensedTableName("unknown_table").setLicenseSpdxId("MIT").build();
    assertThat(
            generateNotices(
                license(2).setLicenseSpdxId("CC0-1.0").build(),
                license(3).setLicensedTableName("shapes").build(),
                unknown,
                license(5)
                    .setLicensedTableName("stops")
                    .setLicenseSpdxId("MIT")
                    .setCustomLicenseUrl("https://example.com/license")
                    .build()))
        .containsExactly(
            new MissingLicenseSpdxIdOrCustomLicenseUrlNotice(3),
            new LicensesUnknownTableNameNotice(unknown),
            new ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice(5));
  }

  @Test
  public void unknownTableNameAndMissingLicense_yieldsBothNotices() {
    GtfsLicense license = license(2).setLicensedTableName("unknown_table").build();
    assertThat(generateNotices(license))
        .containsExactly(
            new LicensesUnknownTableNameNotice(license),
            new MissingLicenseSpdxIdOrCustomLicenseUrlNotice(2));
  }

  @Test
  public void unknownTableNamesDifferingByCase_yieldsWarningsAndDuplicateKeyNotice() {
    GtfsLicense lower =
        license(2).setLicensedTableName("ticketing_identifiers").setLicenseSpdxId("MIT").build();
    GtfsLicense mixed =
        license(3).setLicensedTableName("Ticketing_Identifiers").setLicenseSpdxId("MIT").build();
    assertThat(generateNotices(lower, mixed))
        .containsExactly(
            new LicensesUnknownTableNameNotice(lower),
            new LicensesUnknownTableNameNotice(mixed),
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME,
                2,
                3,
                GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME,
                "Ticketing_Identifiers"));
  }

  @Test
  public void threeRowsForSameTable_eachExtraRowReportedAgainstFirst() {
    assertThat(
            generateNotices(
                license(2).setLicensedTableName("shapes").setLicenseSpdxId("MIT").build(),
                license(3).setLicensedTableName("Shapes").setLicenseSpdxId("MIT").build(),
                license(4).setLicensedTableName("SHAPES").setLicenseSpdxId("MIT").build()))
        .containsExactly(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 3, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, "Shapes"),
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME, 2, 4, GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME, "SHAPES"));
  }
}
