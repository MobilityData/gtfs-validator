package org.mobilitydata.gtfsvalidator.report.model;

import com.google.gson.JsonObject;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;
import org.mobilitydata.gtfsvalidator.notice.ResolvedNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.schema.NoticeSchemaGenerator;

/** NoticeView is a wrapper class to display a Notice. */
public class NoticeView {
  private final ResolvedNotice notice;
  private final JsonObject json;
  private final List<String> fields;
  private final NoticeDocComments comments;

  public NoticeView(ResolvedNotice<? extends Notice> notice) {
    this.notice = notice;
    this.json = notice.getContext().toJsonTree().getAsJsonObject();
    this.fields = new ArrayList<>(json.keySet());
    this.comments = NoticeSchemaGenerator.loadComments(notice.getContext().getClass());
  }

  /**
   * Returns a name for this notice based on the class simple name.
   *
   * @return notice name, e.g., "ForeignKeyViolationNotice".
   */
  public String getName() {
    return notice.getContext().getClass().getSimpleName();
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
   * Returns the description text for the notice.
   *
   * @return description text
   */
  public String getCommentForField(String field) {
    if (field.isBlank()) {
      return field;
    } else {
      return comments.getFieldComment(field);
    }
  }

  /**
   * Returns the description text for the notice.
   *
   * @return description text
   */
  public String getDescription() {
    String markdown = this.comments.getCombinedDocumentation();

    Parser parser = Parser.builder().build();
    Document document = parser.parse(markdown == null ? "" : markdown);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    return renderer.render(document);
  }

  /**
   * Returns a descriptive type-specific name for this notice class simple name.
   *
   * @return notice code, e.g., "foreign_key_violation".
   */
  public String getCode() {
    return notice.getContext().getCode();
  }

  /**
   * Returns a list of all fields in the notice.
   *
   * @return list of all fields in the notice.
   */
  public List<String> getAllFields() {
    return notice.getContext().getAllFields();
  }
}
