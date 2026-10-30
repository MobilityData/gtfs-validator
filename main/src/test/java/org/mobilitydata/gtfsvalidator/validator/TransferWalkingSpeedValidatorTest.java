package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.TableStatus;

public class TransferWalkingSpeedValidatorTest {

  private static final double EXPECTED_DISTANCE_METERS = 111.19510117748394;

  // U01: A short transfer time should produce one notice.
  @Test
  public void testInsufficientTransferTimeProducesNotice() {
    NoticeContainer notices = validateTransfer("A", "B", 30);

    assertThat(notices.getValidationNotices()).hasSize(1);

    JsonObject result = notices.getValidationNotices().get(0).toJsonTree().getAsJsonObject();

    assertThat(result.get("csvRowNumber").getAsInt()).isEqualTo(5);
    assertThat(result.get("fromStopId").getAsString()).isEqualTo("A");
    assertThat(result.get("toStopId").getAsString()).isEqualTo("B");
    assertThat(result.get("minTransferTime").getAsInt()).isEqualTo(30);

    assertThat(result.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);
  }

  // U02: A sufficient transfer time should not produce a notice.
  @Test
  public void testSufficientTransferTimeProducesNoNotice() {
    NoticeContainer notices = validateTransfer("A", "B", 60);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U02: A transfer time of 55 seconds should produce a notice.
  @Test
  public void testTransferTimeJustBelowRequiredTimeProducesNotice() {
    NoticeContainer notices = validateTransfer("A", "B", 55);

    assertThat(notices.getValidationNotices()).hasSize(1);

    JsonObject result = notices.getValidationNotices().get(0).toJsonTree().getAsJsonObject();

    assertThat(result.get("minTransferTime").getAsInt()).isEqualTo(55);

    assertThat(result.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);
  }

  // U02: A transfer time of 56 seconds should not produce a notice.
  @Test
  public void testTransferTimeJustAboveRequiredTimeProducesNoNotice() {
    NoticeContainer notices = validateTransfer("A", "B", 56);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U03: Zero distance and zero time should not trigger the rule.
  @Test
  public void testZeroDistanceAndZeroTimeProducesNoNotice() {
    NoticeContainer notices = validateTransfer("A", "A", 0);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U04: Positive distance with zero time should produce a notice.
  @Test
  public void testPositiveDistanceAndZeroTimeProducesNotice() {
    NoticeContainer notices = validateTransfer("A", "B", 0);

    assertThat(notices.getValidationNotices()).hasSize(1);

    JsonObject result = notices.getValidationNotices().get(0).toJsonTree().getAsJsonObject();

    assertThat(result.get("minTransferTime").getAsInt()).isEqualTo(0);
    assertThat(result.get("csvRowNumber").getAsInt()).isEqualTo(5);
  }

  // U05: Missing and negative transfer times should be skipped.
  @Test
  public void testMissingAndNegativeTransferTimes() {
    NoticeContainer missingTime = validateRows(List.of(transfer(10, "A", "B", null)), basicStops());

    NoticeContainer negativeTime = validateRows(List.of(transfer(11, "A", "B", -1)), basicStops());

    assertThat(missingTime.getValidationNotices()).isEmpty();
    assertThat(negativeTime.getValidationNotices()).isEmpty();
  }

  // U06: Missing or empty stop IDs should be skipped.
  @Test
  public void testMissingStopIds() {
    for (String missingId : new String[] {null, ""}) {
      NoticeContainer missingFrom =
          validateRows(List.of(transfer(12, missingId, "B", 0)), basicStops());

      NoticeContainer missingTo =
          validateRows(List.of(transfer(13, "A", missingId, 0)), basicStops());

      assertThat(missingFrom.getValidationNotices()).isEmpty();
      assertThat(missingTo.getValidationNotices()).isEmpty();
    }
  }

  // U07: Unknown stop IDs should not produce notices.
  @Test
  public void testUnknownStopIds() {
    NoticeContainer unknownFrom =
        validateRows(List.of(transfer(14, "UNKNOWN", "B", 0)), basicStops());

    NoticeContainer unknownTo =
        validateRows(List.of(transfer(15, "A", "UNKNOWN", 0)), basicStops());

    assertThat(unknownFrom.getValidationNotices()).isEmpty();
    assertThat(unknownTo.getValidationNotices()).isEmpty();
  }

  // U08: Missing coordinates should fall back to parent stations.
  @Test
  public void testParentStationCoordinates() {
    List<GtfsStop> stopRows =
        List.of(
            stop("A", 0.0, 0.0, null),
            stop("B", 0.0, 0.001, null),
            stop("childA", null, null, "A"),
            stop("childB", null, null, "B"));

    NoticeContainer notices = validateRows(List.of(transfer(16, "childA", "childB", 30)), stopRows);

    assertThat(notices.getValidationNotices()).hasSize(1);

    JsonObject result = notices.getValidationNotices().get(0).toJsonTree().getAsJsonObject();

    assertThat(result.get("fromStopId").getAsString()).isEqualTo("childA");
    assertThat(result.get("toStopId").getAsString()).isEqualTo("childB");

    assertThat(result.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);
  }

  // U08: Grandparent coordinates should also be supported.
  @Test
  public void testGrandparentStationCoordinates() {
    List<GtfsStop> stopRows =
        List.of(
            stop("A", 0.0, 0.0, null),
            stop("B", 0.0, 0.001, null),
            stop("parent", null, null, "A"),
            stop("child", null, null, "parent"));

    NoticeContainer notices = validateRows(List.of(transfer(17, "child", "B", 30)), stopRows);

    assertThat(notices.getValidationNotices()).hasSize(1);

    JsonObject result = notices.getValidationNotices().get(0).toJsonTree().getAsJsonObject();

    assertThat(result.get("fromStopId").getAsString()).isEqualTo("child");
    assertThat(result.get("csvRowNumber").getAsInt()).isEqualTo(17);

    assertThat(result.get("stopDistance").getAsDouble())
        .isWithin(1e-6)
        .of(EXPECTED_DISTANCE_METERS);
  }

  // U09: A stop's own coordinates should take priority.
  @Test
  public void testOwnCoordinatesTakePriority() {
    List<GtfsStop> stopRows =
        List.of(
            stop("A", 0.0, 0.0, null), stop("B", 0.0, 0.001, null), stop("child", 0.0, 0.001, "A"));

    NoticeContainer notices = validateRows(List.of(transfer(18, "child", "B", 0)), stopRows);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U10: Stops without usable coordinates should be skipped.
  @Test
  public void testMissingCoordinates() {
    List<GtfsStop> stopRows = List.of(stop("A", null, null, null), stop("B", 0.0, 0.001, null));

    NoticeContainer notices = validateRows(List.of(transfer(19, "A", "B", 0)), stopRows);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U10: Cyclic parent relationships should terminate safely.
  @Test
  public void testCyclicParentStations() {
    List<GtfsStop> stopRows =
        List.of(
            stop("childA", null, null, "childB"),
            stop("childB", null, null, "childA"),
            stop("B", 0.0, 0.001, null));

    NoticeContainer notices = validateRows(List.of(transfer(20, "childA", "B", 0)), stopRows);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U11: Coordinates at (0,0) are valid.
  @Test
  public void testZeroCoordinatesAreValid() {
    NoticeContainer notices = validateRows(List.of(transfer(21, "A", "B", 30)), basicStops());

    assertThat(notices.getValidationNotices()).hasSize(1);
  }

  // U11: Non-finite and out-of-range coordinates should be skipped.
  @Test
  public void testInvalidCoordinates() {
    Double[][] invalidCoordinates = {
      {Double.NaN, 0.0},
      {Double.POSITIVE_INFINITY, 0.0},
      {91.0, 0.0},
      {-91.0, 0.0},
      {0.0, 181.0},
      {0.0, -181.0}
    };

    for (Double[] coordinates : invalidCoordinates) {
      List<GtfsStop> stopRows =
          List.of(stop("A", coordinates[0], coordinates[1], null), stop("B", 0.0, 0.001, null));

      NoticeContainer notices = validateRows(List.of(transfer(22, "A", "B", 0)), stopRows);

      assertThat(notices.getValidationNotices()).isEmpty();
    }
  }

  // U12: Each invalid transfer row should produce its own notice.
  @Test
  public void testMultipleTransferRows() {
    List<GtfsTransfer> transferRows =
        List.of(transfer(31, "A", "B", 30), transfer(32, "B", "A", 30), transfer(33, "A", "B", 60));

    NoticeContainer notices = validateRows(transferRows, basicStops());

    assertThat(notices.getValidationNotices()).hasSize(2);

    List<Integer> reportedRows = new ArrayList<>();

    for (var notice : notices.getValidationNotices()) {
      JsonObject result = notice.toJsonTree().getAsJsonObject();
      reportedRows.add(result.get("csvRowNumber").getAsInt());
    }

    assertThat(reportedRows).containsExactly(31, 32);
  }

  // U13: Missing or invalid transfer tables should be skipped safely.
  @Test
  public void testInvalidTransferTableStatus() {
    for (TableStatus status :
        List.of(
            TableStatus.MISSING_FILE,
            TableStatus.EMPTY_FILE,
            TableStatus.INVALID_HEADERS,
            TableStatus.UNPARSABLE_ROWS)) {

      GtfsTransferTableContainer transfers = GtfsTransferTableContainer.forStatus(status);

      assertValidatorSkipped(transfers, standardStopTable());
    }
  }

  // U13: Missing or invalid stop tables should be skipped safely.
  @Test
  public void testInvalidStopTableStatus() {
    for (TableStatus status :
        List.of(
            TableStatus.MISSING_FILE,
            TableStatus.EMPTY_FILE,
            TableStatus.INVALID_HEADERS,
            TableStatus.UNPARSABLE_ROWS)) {

      GtfsStopTableContainer stops = GtfsStopTableContainer.forStatus(status);

      assertValidatorSkipped(standardTransferTable(), stops);
    }
  }

  // U13: Missing required transfer columns should prevent validation.
  @Test
  public void testMissingRequiredTransferColumns() {
    String[][] incompleteHeaders = {
      {"to_stop_id", "min_transfer_time"},
      {"from_stop_id", "min_transfer_time"},
      {"from_stop_id", "to_stop_id"}
    };

    for (String[] header : incompleteHeaders) {
      GtfsTransferTableContainer transfers =
          GtfsTransferTableContainer.forHeaderAndEntities(
              new GtfsTransferTableDescriptor(),
              new CsvHeader(header),
              List.of(transfer(34, "A", "B", 30)),
              new NoticeContainer());

      assertValidatorSkipped(transfers, standardStopTable());
    }
  }

  // U13: A missing stop_id column should prevent validation.
  @Test
  public void testMissingRequiredStopColumn() {
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forHeaderAndEntities(
            new GtfsStopTableDescriptor(),
            new CsvHeader(new String[] {"stop_lat", "stop_lon"}),
            basicStops(),
            new NoticeContainer());

    assertValidatorSkipped(standardTransferTable(), stops);
  }

  // U13: Null table containers should not cause an exception.
  @Test
  public void testNullTableContainers() {
    assertValidatorSkipped(null, standardStopTable());
    assertValidatorSkipped(standardTransferTable(), null);
  }

  // U14: An empty transfer table with valid headers is still valid.
  @Test
  public void testEmptyTransferTable() {
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forHeaderAndEntities(
            new GtfsTransferTableDescriptor(),
            new CsvHeader(new String[] {"from_stop_id", "to_stop_id", "min_transfer_time"}),
            List.of(),
            new NoticeContainer());

    TransferWalkingSpeedValidator validator =
        new TransferWalkingSpeedValidator(transfers, standardStopTable());

    assertThat(validator.shouldCallValidate()).isTrue();

    NoticeContainer notices = new NoticeContainer();
    validator.validate(notices);

    assertThat(notices.getValidationNotices()).isEmpty();
  }

  // U15: Sufficient and very large transfer times should not trigger.
  @Test
  public void testLargeAndSufficientTransferTimes() {
    NoticeContainer sufficientTime =
        validateRows(List.of(transfer(35, "A", "B", 60)), basicStops());

    NoticeContainer maximumTime =
        validateRows(List.of(transfer(36, "A", "B", Integer.MAX_VALUE)), basicStops());

    assertThat(sufficientTime.getValidationNotices()).isEmpty();
    assertThat(maximumTime.getValidationNotices()).isEmpty();
  }

  // Creates a transfer record for testing.
  private static GtfsTransfer transfer(int row, String from, String to, Integer seconds) {

    GtfsTransfer.Builder builder =
        new GtfsTransfer.Builder().setCsvRowNumber(row).setFromStopId(from).setToStopId(to);

    if (seconds != null) {
      builder.setMinTransferTime(seconds);
    }

    return builder.build();
  }

  // Creates a stop with optional coordinates and parent information.
  private static GtfsStop stop(String id, Double latitude, Double longitude, String parent) {

    return new GtfsStop.Builder()
        .setCsvRowNumber(2)
        .setStopId(id)
        .setStopLat(latitude)
        .setStopLon(longitude)
        .setParentStation(parent)
        .build();
  }

  // Standard stop coordinates used in the tests.
  private static List<GtfsStop> basicStops() {
    return List.of(stop("A", 0.0, 0.0, null), stop("B", 0.0, 0.001, null));
  }

  // Runs the validator using the standard test data.
  private static NoticeContainer validateTransfer(
      String fromStopId, String toStopId, int minTransferTime) {

    GtfsTransfer transfer = transfer(5, fromStopId, toStopId, minTransferTime);

    return validateRows(List.of(transfer), basicStops());
  }

  // Runs the actual validator with custom transfer and stop records.
  private static NoticeContainer validateRows(
      List<GtfsTransfer> transferRows, List<GtfsStop> stopRows) {

    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forHeaderAndEntities(
            new GtfsTransferTableDescriptor(),
            new CsvHeader(new String[] {"from_stop_id", "to_stop_id", "min_transfer_time"}),
            transferRows,
            new NoticeContainer());

    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forHeaderAndEntities(
            new GtfsStopTableDescriptor(),
            new CsvHeader(new String[] {"stop_id", "stop_lat", "stop_lon", "parent_station"}),
            stopRows,
            new NoticeContainer());

    NoticeContainer notices = new NoticeContainer();

    new TransferWalkingSpeedValidator(transfers, stops).validate(notices);

    return notices;
  }

  // Creates a valid transfer table.
  private static GtfsTransferTableContainer standardTransferTable() {
    return GtfsTransferTableContainer.forHeaderAndEntities(
        new GtfsTransferTableDescriptor(),
        new CsvHeader(new String[] {"from_stop_id", "to_stop_id", "min_transfer_time"}),
        List.of(transfer(37, "A", "B", 30)),
        new NoticeContainer());
  }

  // Creates a valid stop table.
  private static GtfsStopTableContainer standardStopTable() {
    return GtfsStopTableContainer.forHeaderAndEntities(
        new GtfsStopTableDescriptor(),
        new CsvHeader(new String[] {"stop_id", "stop_lat", "stop_lon", "parent_station"}),
        basicStops(),
        new NoticeContainer());
  }

  // Checks that invalid input is skipped safely.
  private static void assertValidatorSkipped(
      GtfsTransferTableContainer transfers, GtfsStopTableContainer stops) {

    TransferWalkingSpeedValidator validator = new TransferWalkingSpeedValidator(transfers, stops);

    assertThat(validator.shouldCallValidate()).isFalse();

    NoticeContainer notices = new NoticeContainer();
    validator.validate(notices);

    assertThat(notices.getValidationNotices()).isEmpty();
  }
}
