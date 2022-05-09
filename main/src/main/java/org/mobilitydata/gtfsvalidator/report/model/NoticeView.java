package org.mobilitydata.gtfsvalidator.report.model;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/** NoticeView is a wrapper class to display a Notice. */
public class NoticeView {
  private final Notice notice;
  private final JsonObject json;
  private final List<String> fields;

  public NoticeView(Notice notice) {
    this.notice = notice;
    this.json = notice.getContext().getAsJsonObject();
    this.fields = new ArrayList<>(json.keySet());
  }

  /**
   * Returns a name for this notice based on the class simple name.
   *
   * @return notice name, e.g., "ForeignKeyViolationNotice".
   */
  public String getName() {
    return notice.getClass().getSimpleName();
  }

  /**
   * Returns a list of strings based on the GSON context representation of the notice.
   *
   * @return notice fields as a list of strings.
   */
  public List<String> getFields() {
    return fields;
  }

  /**
   * Returns the value as an object for the specified notice field.
   *
   * @param field notice field as a string.
   * @return value as an object for the notice field.
   */
  public Object getValueForField(String field) {
    return json.get(field);
  }

  /**
   * Returns the severity level of the notice.
   *
   * @return severity level.
   */
  public SeverityLevel getSeverityLevel() {
    return notice.getSeverityLevel();
  }

  /**
   * Returns a descriptive type-specific name for this notice class simple name.
   *
   * @return notice code, e.g., "foreign_key_violation".
   */
  public String getCode() {
    return notice.getCode();
  }
}
