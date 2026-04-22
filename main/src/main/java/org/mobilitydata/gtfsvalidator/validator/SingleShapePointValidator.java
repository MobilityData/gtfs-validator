package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

/**
 * Validates that every shape (identified by shape_id) has more than one shape_point
 *
 * <p>Generated notice: {@link SingleShapePointNotice}
 */
@GtfsValidator
public class SingleShapePointValidator extends FileValidator {
  private final GtfsShapeTableContainer shapeTable;

  @Inject
  SingleShapePointValidator(GtfsShapeTableContainer shapeTable) {
    this.shapeTable = shapeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<String, Integer> shapePointCounts = new HashMap<>();
    Map<String, Integer> shapePointRowNumbers = new HashMap<>();
    for (GtfsShape shape : shapeTable.getEntities()) {
      String shapeId = shape.shapeId();
      shapePointCounts.put(shapeId, shapePointCounts.getOrDefault(shapeId, 0) + 1);
      shapePointRowNumbers.put(shapeId, shape.csvRowNumber());
    }

    for (Map.Entry<String, Integer> entry : shapePointCounts.entrySet()) {
      if (entry.getValue() == 1) {
        noticeContainer.addValidationNotice(
            new SingleShapePointNotice(
                entry.getKey(), shapePointRowNumbers.getOrDefault(entry.getKey(), 0)));
      }
    }
  }

  /**
   * The shape within `shapes.txt` contains a single shape point.
   *
   * <p>A shape should contain more than one shape point to visualize the route
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @GtfsValidationNotice.FileRefs({GtfsShapeSchema.class}),
      urls = {
        @GtfsValidationNotice.UrlRef(
            label = "Shapes Data Guidance",
            url =
                "https://gtfs.org/resources/gtfs-schedule-feature-guides/shapes/#shapes-data-guidance")
      })
  static class SingleShapePointNotice extends ValidationNotice {
    /** The faulty record's id. */
    private final String shapeId;

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    SingleShapePointNotice(String shapeId, int csvRowNumber) {
      this.shapeId = shapeId;
      this.csvRowNumber = csvRowNumber;
    }
  }
}
