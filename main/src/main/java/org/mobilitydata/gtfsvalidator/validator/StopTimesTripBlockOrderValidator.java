package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class StopTimesTripBlockOrderValidator extends FileValidator {
  private final GtfsStopTimeTableContainer stopTimeTable;

  private final Map<String, Integer> tripRowCount = new HashMap<>();
  private final Map<String, Integer> tripMinRow = new HashMap<>();
  private final Map<String, Integer> tripMaxRow = new HashMap<>();
  // last seen stop_sequence for a given trip (file order)
  private final Map<String, Integer> lastStopSequence = new HashMap<>();

  // Ensure each kind of notice is emitted once per trip
  private final Set<String> contiguityNotified = new HashSet<>();
  private final Set<String> unsortedNotified = new HashSet<>();

  @Inject
  StopTimesTripBlockOrderValidator(GtfsStopTimeTableContainer stopTimeTable) {
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {

    // PASS 1 — Collect statistics
    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      if (stopTime == null) continue;

      String tripId = stopTime.tripId();
      if (tripId == null) continue;

      int rowNumber = stopTime.csvRowNumber();
      int stopSeq = stopTime.stopSequence();

      int count = tripRowCount.getOrDefault(tripId, 0) + 1;
      tripRowCount.put(tripId, count);

      int minRow = tripMinRow.getOrDefault(tripId, rowNumber);
      int maxRow = tripMaxRow.getOrDefault(tripId, rowNumber);

      if (rowNumber < minRow) minRow = rowNumber;
      if (rowNumber > maxRow) maxRow = rowNumber;

      tripMinRow.put(tripId, minRow);
      tripMaxRow.put(tripId, maxRow);

      // Track stop_sequence ordering (file order)
      Integer last = lastStopSequence.get(tripId);
      if (last != null && stopSeq <= last) {
        unsortedNotified.add(tripId); // mark only, emit later
      }
      lastStopSequence.put(tripId, stopSeq);
    }

    // PASS 2 — Validate + Emit Notices
    for (String tripId : tripRowCount.keySet()) {

      int count = tripRowCount.get(tripId);
      int minRow = tripMinRow.get(tripId);
      int maxRow = tripMaxRow.get(tripId);

      int span = maxRow - minRow + 1;
      boolean nonContiguous = span > count;
      boolean unsortedSequence = unsortedNotified.contains(tripId);

      if (nonContiguous || unsortedSequence) {
        noticeContainer.addValidationNotice(new UnsortedStopTimesNotice(tripId, minRow, maxRow));
      }
    }
  }

  /**
   * Stop times are not sorted by trip_id and stop_sequence.
   *
   * <p>'stop_times.txt' entries for a given trip are not sorted by stop_sequence, or are not
   * contiguous in the file.
   */
  @GtfsValidationNotice(
      severity = INFO,
      files = @GtfsValidationNotice.FileRefs({GtfsStopTimeSchema.class}))
  static class UnsortedStopTimesNotice extends ValidationNotice {
    /** The faulty record's trip_id. */
    private final String tripId;

    /** CSV row number of the first stop_times entry for this trip. */
    private final int startCsvRowNumber;

    /** CSV row number of the last stop_times entry for this trip. */
    private final int endCsvRowNumber;

    public UnsortedStopTimesNotice(String tripId, int startCsvRowNumber, int endCsvRowNumber) {
      this.tripId = tripId;
      this.startCsvRowNumber = startCsvRowNumber;
      this.endCsvRowNumber = endCsvRowNumber;
    }
  }
}
