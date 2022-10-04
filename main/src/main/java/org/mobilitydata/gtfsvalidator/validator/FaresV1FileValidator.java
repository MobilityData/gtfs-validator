package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.notice.ForbiddenFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;

public class FaresV1FileValidator extends FileValidator {

  private final GtfsFareAttributeTableContainer fareAttributeTable;
  private final GtfsFareRuleTableContainer fareRuleTable;

  @Inject
  public FaresV1FileValidator(
      GtfsFareAttributeTableContainer fareAttributeTable,
      GtfsFareRuleTableContainer fareRuleTable) {
    this.fareAttributeTable = fareAttributeTable;
    this.fareRuleTable = fareRuleTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (!fareAttributeTable.isMissingFile() && fareRuleTable.isMissingFile()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFileNotice(fareRuleTable.gtfsFilename()));
    } else if (fareAttributeTable.isMissingFile() && !fareRuleTable.isMissingFile()) {
      noticeContainer.addValidationNotice(new ForbiddenFileNotice(fareRuleTable.gtfsFilename()));
    }
  }
}
