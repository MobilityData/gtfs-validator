package org.mobilitydata.gtfsvalidator.noticeschemagenerator.model;

import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

public class NoticeSchema {
  private final String code;

  private final SeverityLevel severityLevel;

  private String description;
  private List<FieldSchema> fields = new ArrayList<>();

  public NoticeSchema(String code, SeverityLevel severityLevel) {
    this.code = code;
    this.severityLevel = severityLevel;
  }

  public String code() {
    return this.code;
  }

  public SeverityLevel severityLevel() {
    return this.severityLevel;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addField(FieldSchema field) {
    this.fields.add(field);
  }
}
