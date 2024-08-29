package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that:
 *
 * <ul>
 *   <li>Exit gates (pathway_mode=7) must not be bidirectional.
 * </ul>
 */
@GtfsValidator
public class PathwayBidirectionalExitGatesValidator extends SingleEntityValidator<GtfsPathway> {

  @Override
  public void validate(GtfsPathway entity, NoticeContainer noticeContainer) {
    if (entity.pathwayMode().getNumber() == 7 && entity.isBidirectional().getNumber() == 1) {
      noticeContainer.addValidationNotice(new PathwayBidirectionalExitGatesNotice(entity));
    }
  }

  /**
   * Pathway is bidirectional and has mode 7 (exit gate).
   *
   * <p>Exit gates (pathway_mode=7) must not be bidirectional.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs({GtfsPathwaySchema.class}))
  static class PathwayBidirectionalExitGatesNotice extends ValidationNotice {
    /** The row number of the validated record. */
    private final int csvRowNumber;
    /** The pathway mode. */
    private final GtfsPathwayMode pathwayMode;
    /** Whether the pathway is bidirectional. */
    private final GtfsPathwayIsBidirectional isBidirectional;

    PathwayBidirectionalExitGatesNotice(GtfsPathway pathway) {
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayMode = pathway.pathwayMode();
      this.isBidirectional = pathway.isBidirectional();
    }
  }
}
