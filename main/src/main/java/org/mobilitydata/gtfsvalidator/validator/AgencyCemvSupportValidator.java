package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCemvSupport;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

import javax.inject.Inject;

@GtfsValidator
public class AgencyCemvSupportValidator extends SingleEntityValidator<GtfsAgency> {
  private final GtfsAgencyTableContainer agencyTable;

  @Inject
  AgencyCemvSupportValidator(GtfsAgencyTableContainer agencyTable) {
    this.agencyTable = agencyTable;
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return agencyTable != null && !agencyTable.isMissingFile()
            && header.hasColumn(GtfsAgency.CEMV_SUPPORT_FIELD_NAME);
  }

  @Override
  public void validate(GtfsAgency agency, NoticeContainer noticeContainer) {
    GtfsCemvSupport cemvSupport = agency.cemvSupport();
    if (cemvSupport != GtfsCemvSupport.NO_INFORMATION
            && cemvSupport != GtfsCemvSupport.SUPPORTED
            && cemvSupport != GtfsCemvSupport.NOT_SUPPORTED) {
      noticeContainer.addValidationNotice(
              new UnexpectedEnumValueNotice(
                      agencyTable.gtfsFilename(),
                      agency.csvRowNumber(),
                      cemvSupport.name(),
                      cemvSupport.getNumber()));
    }
  }

}