package org.mobilitydata.gtfsvalidator.reportsummary;

public class JsonReportAgencyMetadata {
  /** `agency_name` from `agency.txt`. Full name of the transit agency. */
  private final String name;

  /** `agency_url` from `agency.txt`. URL of the transit agency. */
  private final String url;

  /** `agency_phone` from `agency.txt`. A voice telephone number for the transit agency. */
  private final String phone;

  /**
   * `agency_email` from `agency.txt`. Email address actively monitored by the agency’s customer
   * service department.
   */
  private final String email;

  /** `agency_timezone` from `agency.txt`. Timezone where the transit agency is located. */
  private final String timezone;

  public JsonReportAgencyMetadata(AgencyMetadata agencyMetadata) {
    name = agencyMetadata.name;
    url = agencyMetadata.url;
    phone = agencyMetadata.phone;
    email = agencyMetadata.email;
    timezone = agencyMetadata.timezone;
  }
}
