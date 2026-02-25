package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencySchema;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

@GtfsValidator
public class MissingRequiredAgencyIdValidator extends FileValidator {
  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;
  private final GtfsFareAttributeTableContainer fareAttributeTable;

  @Inject
  MissingRequiredAgencyIdValidator(
      GtfsAgencyTableContainer agencyTable,
      GtfsRouteTableContainer routeTable,
      GtfsFareAttributeTableContainer fareAttributeTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
    this.fareAttributeTable = fareAttributeTable;
  }

  @Override
  public boolean shouldCallValidate() {
    if (agencyTable == null) {
      // agencyTable is a required file, so if it's null there will be a notice issued by another
      // validator.
      return false;
    }
    // Check if there are more than one agency in the agency table
    return agencyTable.getEntities().size() > 1;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {

    // At this point we know that there is at least 2 agencies in agency.txt (as per
    // shouldCallValidate).
    // Check if all agencyId fields have an actual value in the 3 tables.
    for (GtfsAgency agency : agencyTable.getEntities()) {
      addNoticeIfMissingAgencyId(
          agencyTable.gtfsFilename(),
          agency.csvRowNumber(),
          agency.agencyId(),
          agency.agencyName(),
          noticeContainer);
    }
    if (routeTable != null) {
      for (GtfsRoute route : routeTable.getEntities()) {
        addNoticeIfMissingAgencyId(
            routeTable.gtfsFilename(),
            route.csvRowNumber(),
            route.agencyId(),
            null,
            noticeContainer);
      }
    }

    if (fareAttributeTable != null) {
      for (GtfsFareAttribute fareAttribute : fareAttributeTable.getEntities()) {
        addNoticeIfMissingAgencyId(
            fareAttributeTable.gtfsFilename(),
            fareAttribute.csvRowNumber(),
            fareAttribute.agencyId(),
            null,
            noticeContainer);
      }
    }
  }

  private void addNoticeIfMissingAgencyId(
      String filename,
      int csvRowNumber,
      String agencyId,
      String agencyName,
      NoticeContainer noticeContainer) {
    if (agencyId == null || agencyId.isEmpty()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredAgencyIdNotice(filename, csvRowNumber, agencyName));
    }
  }

  /**
   * Agency id is required when there are multiple agencies.
   *
   * <p>`agency.agency_id`, `routes.agency_id` or `fare_attributes.agency_id` is required when there
   * are multiple agencies.
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.ERROR,
      files =
          @GtfsValidationNotice.FileRefs({
            GtfsAgencySchema.class,
            GtfsRouteSchema.class,
            GtfsFareAttributeSchema.class
          }))
  static class MissingRequiredAgencyIdNotice extends ValidationNotice {
    /** The name of the file where the error occurs. */
    private final String filename;

    /** The row number in the source file where the error occurs. */
    private final int csvRowNumber;

    /** The agency name if it can be obtained. */
    private final String agencyName;

    MissingRequiredAgencyIdNotice(String filename, int csvRowNumber, String agencyName) {
      this.filename = filename;
      this.csvRowNumber = csvRowNumber;
      this.agencyName = agencyName;
    }
  }
}
