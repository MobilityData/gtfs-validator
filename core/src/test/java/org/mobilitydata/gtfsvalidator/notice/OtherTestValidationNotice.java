package org.mobilitydata.gtfsvalidator.notice;

import java.util.Map;

public class OtherTestValidationNotice extends ValidationNotice {

  private final String code;

  public OtherTestValidationNotice(
      String code, Map<String, Object> context, SeverityLevel severityLevel) {
    super(context, severityLevel);
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }
}
