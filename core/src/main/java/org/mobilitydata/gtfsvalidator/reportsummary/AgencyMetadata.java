package org.mobilitydata.gtfsvalidator.reportsummary;

public class AgencyMetadata {
  public final String name;
  public final String url;
  public final String phone;
  public final String email;
  public final String timezone;

  public AgencyMetadata(String name, String url, String phone, String email, String timezone) {
    this.name = name;
    this.url = url;
    this.phone = phone.isEmpty() ? "" : phone;
    this.email = email.isEmpty() ? "" : email;
    this.timezone = timezone.isEmpty() ? "" : timezone;
  }
}
