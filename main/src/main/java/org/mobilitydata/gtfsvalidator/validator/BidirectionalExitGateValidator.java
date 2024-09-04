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
public class BidirectionalExitGateValidator extends SingleEntityValidator<GtfsPathway> {

  @Override
  public void validate(GtfsPathway entity, NoticeContainer noticeContainer) {
    if (entity.pathwayMode().getNumber() == 7 && entity.isBidirectional().getNumber() == 1) {
      noticeContainer.addValidationNotice(new BidirectionalExitGateNotice(entity));
    }
  }

  /**
   * Pathway is bidirectional and has mode 7 (exit gate).
   *
   * <p>Exit gates (pathway_mode=7) must not be bidirectional.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs({GtfsPathwaySchema.class}))
  static class BidirectionalExitGateNotice extends ValidationNotice {
    /** The row number of the validated record. */
    private final int csvRowNumber;
    /** The pathway mode. */
    private final int pathwayMode;
    /** Whether the pathway is bidirectional. */
    private final int isBidirectional;

    BidirectionalExitGateNotice(GtfsPathway pathway) {
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayMode = pathway.pathwayMode().getNumber();
      this.isBidirectional = pathway.isBidirectional().getNumber();
    }
  }
}
