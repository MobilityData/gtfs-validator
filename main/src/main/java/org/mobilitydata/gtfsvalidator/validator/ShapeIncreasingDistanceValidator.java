/*
 * Copyright 2020 Google LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.util.S2Earth.getDistanceMeters;

import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;

/**
 * Validates that the shape_dist_traveled along a shape in "shapes.txt" is increasing.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link DecreasingShapeDistanceNotice}
 *   <li>{@link EqualShapeDistanceSameCoordinatesNotice}
 *   <li>{@link EqualShapeDistanceDiffCoordinatesNotice}
 * </ul>
 */
@GtfsValidator
public class ShapeIncreasingDistanceValidator extends FileValidator {

  private final GtfsShapeTableContainer table;

  @Inject
  ShapeIncreasingDistanceValidator(GtfsShapeTableContainer table) {
    this.table = table;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsShape> shapeList : Multimaps.asMap(table.byShapeIdMap()).values()) {
      // GtfsShape objects are sorted based on @SequenceKey annotation on shape_pt_sequence field.
      for (int i = 1; i < shapeList.size(); ++i) {
        GtfsShape prev = shapeList.get(i - 1);
        GtfsShape curr = shapeList.get(i);
        if (!(prev.hasShapeDistTraveled() && curr.hasShapeDistTraveled())) {
          continue;
        }
        if (prev.shapeDistTraveled() > curr.shapeDistTraveled()) {
          noticeContainer.addValidationNotice(new DecreasingShapeDistanceNotice(prev, curr));
          continue;
        }
        if (prev.shapeDistTraveled() != curr.shapeDistTraveled()) {
          continue;
        }
        // equal shape_dist_traveled and different coordinates
        if (!(curr.shapePtLon() == prev.shapePtLon() && curr.shapePtLat() == prev.shapePtLat())) {
          double distanceBetweenShapePoints =
              getDistanceMeters(curr.shapePtLatLon(), prev.shapePtLatLon());
          if (distanceBetweenShapePoints > 1.11) {
            noticeContainer.addValidationNotice(
                new EqualShapeDistanceDiffCoordinatesNotice(prev, curr));
          }
        } else {
          // equal shape_dist_traveled and same coordinates
          noticeContainer.addValidationNotice(
              new EqualShapeDistanceSameCoordinatesNotice(prev, curr));
        }
      }
    }
  }

  /**
   * Decreasing `shape_dist_traveled` in `shapes.txt`.
   *
   * <p>When sorted by `shape.shape_pt_sequence`, two consecutive shape points must not have
   * decreasing values for `shape_dist_traveled`.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsShapeSchema.class))
  static class DecreasingShapeDistanceNotice extends ValidationNotice {

    /** The id of the faulty shape. */
    private final String shapeId;

    /** The row number from `shapes.txt`. */
    private final int csvRowNumber;

    /** Actual distance traveled along the shape from the first shape point to the faulty record. */
    private final double shapeDistTraveled;

    /** The faulty record's `shapes.shape_pt_sequence`. */
    private final int shapePtSequence;

    /** The row number from `shapes.txt` of the previous shape point. */
    private final long prevCsvRowNumber;

    /**
     * Actual distance traveled along the shape from the first shape point to the previous shape
     * point.
     */
    private final double prevShapeDistTraveled;

    /** The previous record's `shapes.shape_pt_sequence`. */
    private final int prevShapePtSequence;

    DecreasingShapeDistanceNotice(GtfsShape current, GtfsShape previous) {
      this.shapeId = current.shapeId();
      this.csvRowNumber = current.csvRowNumber();
      this.shapeDistTraveled = current.shapeDistTraveled();
      this.shapePtSequence = current.shapePtSequence();
      this.prevCsvRowNumber = previous.csvRowNumber();
      this.prevShapeDistTraveled = previous.shapeDistTraveled();
      this.prevShapePtSequence = previous.shapePtSequence();
    }
  }

  /**
   * Two consecutive points have equal `shape_dist_traveled` and the same lat/lon coordinates in
   * `shapes.txt`.
   *
   * <p>When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase
   * along a shape. Two consecutive points with equal values for `shape_dist_traveled` and the same
   * coordinates indicate a duplicative shape point.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsShapeSchema.class, GtfsStopSchema.class}))
  static class EqualShapeDistanceSameCoordinatesNotice extends ValidationNotice {

    /** The id of the faulty shape. */
    private final String shapeId;

    /** The row number from `shapes.txt`. */
    private final int csvRowNumber;

    /** Actual distance traveled along the shape from the first shape point to the faulty record. */
    private final double shapeDistTraveled;

    /** The faulty record's `shapes.shape_pt_sequence`. */
    private final int shapePtSequence;

    /** The row number from `shapes.txt` of the previous shape point. */
    private final long prevCsvRowNumber;

    /**
     * Actual distance traveled along the shape from the first shape point to the previous shape
     * point.
     */
    private final double prevShapeDistTraveled;

    /** The previous record's `shapes.shape_pt_sequence`. */
    private final int prevShapePtSequence;

    EqualShapeDistanceSameCoordinatesNotice(GtfsShape previous, GtfsShape current) {
      this.shapeId = current.shapeId();
      this.csvRowNumber = current.csvRowNumber();
      this.shapeDistTraveled = current.shapeDistTraveled();
      this.shapePtSequence = current.shapePtSequence();
      this.prevCsvRowNumber = previous.csvRowNumber();
      this.prevShapeDistTraveled = previous.shapeDistTraveled();
      this.prevShapePtSequence = previous.shapePtSequence();
    }
  }

  /**
   * Two consecutive points have equal `shape_dist_traveled` and different lat/lon coordinates in
   * `shapes.txt`.
   *
   * <p>When sorted by `shape.shape_pt_sequence`, the values for `shape_dist_traveled` must increase
   * along a shape. Two consecutive points with equal values for `shape_dist_traveled` and different
   * coordinates indicate an error.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsShapeSchema.class, GtfsStopSchema.class}))
  static class EqualShapeDistanceDiffCoordinatesNotice extends ValidationNotice {

    /** The id of the faulty shape. */
    private final String shapeId;

    /** The row number from `shapes.txt`. */
    private final int csvRowNumber;

    /** The faulty record's `shape_dist_traveled` value. */
    private final double shapeDistTraveled;

    /** The faulty record's `shapes.shape_pt_sequence`. */
    private final int shapePtSequence;

    /** The row number from `shapes.txt` of the previous shape point. */
    private final long prevCsvRowNumber;

    /** The previous shape point's `shape_dist_traveled` value. */
    private final double prevShapeDistTraveled;

    /** The previous record's `shapes.shape_pt_sequence`. */
    private final int prevShapePtSequence;

    // Actual distance traveled along the shape from the first shape point to the previous shape
    /** point. */
    private final double actualDistanceBetweenShapePoints;

    EqualShapeDistanceDiffCoordinatesNotice(GtfsShape previous, GtfsShape current) {
      this.shapeId = current.shapeId();
      this.csvRowNumber = current.csvRowNumber();
      this.shapeDistTraveled = current.shapeDistTraveled();
      this.shapePtSequence = current.shapePtSequence();
      this.prevCsvRowNumber = previous.csvRowNumber();
      this.prevShapeDistTraveled = previous.shapeDistTraveled();
      this.prevShapePtSequence = previous.shapePtSequence();
      this.actualDistanceBetweenShapePoints =
          getDistanceMeters(current.shapePtLatLon(), previous.shapePtLatLon());
    }
  }
}
