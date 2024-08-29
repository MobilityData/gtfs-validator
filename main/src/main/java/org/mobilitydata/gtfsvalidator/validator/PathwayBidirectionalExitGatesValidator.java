package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class PathwayBidirectionalExitGatesValidator extends FileValidator {
  private final GtfsPathwayTableContainer pathwayTable;

  @Inject
  public PathwayBidirectionalExitGatesValidator(GtfsPathwayTableContainer pathwayTable) {
    this.pathwayTable = pathwayTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsPathway pathway : pathwayTable.getEntities()) {
      if (pathway.pathwayMode().getNumber() == 7 && pathway.isBidirectional().getNumber() == 1) {
        noticeContainer.addValidationNotice(new PathwayBidirectionalExitGatesNotice(pathway));
      }
    }
  }

  /**
   * Pathway is bidirectional and has mode 7 (exit gate).
   *
   * <p>Exit gates (pathway_mode=7) must not be bidirectional.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs({GtfsPathwaySchema.class}))
  static class PathwayBidirectionalExitGatesNotice extends ValidationNotice {
    private final int csvRowNumber;
    private final GtfsPathwayMode pathwayMode;
    private final GtfsPathwayIsBidirectional isBidirectional;

    PathwayBidirectionalExitGatesNotice(GtfsPathway pathway) {
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayMode = pathway.pathwayMode();
      this.isBidirectional = pathway.isBidirectional();
    }
  }
}
