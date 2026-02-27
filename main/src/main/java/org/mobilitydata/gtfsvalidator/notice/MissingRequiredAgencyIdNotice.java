package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencySchema;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteSchema;

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
public class MissingRequiredAgencyIdNotice extends ValidationNotice {
  /** The name of the file where the error occurs. */
  private final String filename;

  /** The row number in the source file where the error occurs. */
  private final int csvRowNumber;

  /** The agency name if it can be obtained. */
  private final String agencyName;

  public MissingRequiredAgencyIdNotice(String filename, int csvRowNumber, String agencyName) {
    this.filename = filename;
    this.csvRowNumber = csvRowNumber;
    this.agencyName = agencyName;
  }
}
