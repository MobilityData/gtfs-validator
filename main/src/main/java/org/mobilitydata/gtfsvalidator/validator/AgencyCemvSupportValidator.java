package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;

/**
 * Validates the `cemv_support` field in the `agency.txt` file.
 *
 * <p>This validator ensures that the `cemv_support` field contains only valid enum values:
 * `NO_INFORMATION`, `SUPPORTED`, or `NOT_SUPPORTED`. If an invalid value is encountered, an
 * `UnexpectedEnumValueNotice` is added to the `NoticeContainer`.
 *
 * <p>Generated notices:
 * <ul>
 *   <li>{@link UnexpectedEnumValueNotice} - if the `cemv_support` field contains an invalid value.
 * </ul>
 */
@GtfsValidator
public class AgencyCemvSupportValidator extends SingleEntityValidator<GtfsAgency> {
  final String AGENCY_FILE_NAME = "agency.txt";
  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return header.hasColumn(GtfsAgency.CEMV_SUPPORT_FIELD_NAME);
  }

  @Override
  public void validate(GtfsAgency agency, NoticeContainer noticeContainer) {
    GtfsCemvSupport cemvSupport = agency.cemvSupport();
    if (cemvSupport != GtfsCemvSupport.NO_INFORMATION
        && cemvSupport != GtfsCemvSupport.SUPPORTED
        && cemvSupport != GtfsCemvSupport.NOT_SUPPORTED) {
      noticeContainer.addValidationNotice(
          new UnexpectedEnumValueNotice(
                  AGENCY_FILE_NAME,
              agency.csvRowNumber(),
              cemvSupport.name(),
              cemvSupport.getNumber()));
    }
  }
}
