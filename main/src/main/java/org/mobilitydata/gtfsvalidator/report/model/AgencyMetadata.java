package org.mobilitydata.gtfsvalidator.report.model;

import org.mobilitydata.gtfsvalidator.table.GtfsAgency;

public class AgencyMetadata {
  public final String name;
  public final String url;
  public final String phone;
  public final String email;
  public final String timezone;

  public AgencyMetadata(String name, String url, String phone, String email, String timezone) {
    this.name = name;
    this.url = url;
    this.phone = phone.isEmpty() ? "N/A" : phone;
    this.email = email.isEmpty() ? "N/A" : email;
    this.timezone = timezone;
  }

  public static AgencyMetadata from(GtfsAgency agency) {
    return new AgencyMetadata(
        agency.agencyName(),
        agency.agencyUrl(),
        agency.agencyPhone(),
        agency.agencyEmail(),
        agency.agencyTimezone().getId());
  }
}
