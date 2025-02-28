package org.mobilitydata.gtfsvalidator.notice.schema;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/**
 * Represents the schema of a validation notice in the JSON validation schema export.
 *
 * <p>This class is intended for JSON serialization with a structure that is publicly exposed. Be
 * thoughtful when making changes.
 */
public class NoticeSchema {

  private final String code;

  private final SeverityLevel severityLevel;

  /** This field is kept for compatibility with the original schema serialization code. */
  private final String type = "object";

  /** A short single-line summary describing the notice. May contain Markdown markup. */
  @Nullable private String shortSummary;

  /**
   * Additional textual description associated with this notice. It will not contain the
   * `shortSummary` string, but is typically combined as `shortSummary` + "\n\n" + `description`.
   * May contain Markdown markup.
   */
  @Nullable private String description;

  @Nullable private ReferencesSchema references;

  /**
   * We keep this as "properties" to match the existing notice schema convention. This is a mapping
   * from field name to FieldSchema.
   */
  private Map<String, FieldSchema> properties = new TreeMap<>();

  /**
   * Whether the notice is deprecated. Deprecated notices are not used in the validator, but are
   * still supported in the documentation.
   */
  private boolean deprecated = false;

  /**
   * Reason for the deprecation of the notice. This field is only used if {@link #deprecated} is
   * true.
   */
  @Nullable private String deprecationReason;

  /**
   * Version on which the notice was deprecated. This field is only used if {@link #deprecated} is
   * true.
   */
  @Nullable private String deprecationVersion;

  /**
   * Replacement notice codes for the deprecated notice. This field is only used if {@link
   * #deprecated} is true and the notice has replacements.
   */
  @Nullable private List<String> replacementNoticeCodes;

  public NoticeSchema(String code, SeverityLevel severityLevel) {
    this.code = code;
    this.severityLevel = severityLevel;
  }

  public String getCode() {
    return this.code;
  }

  public SeverityLevel getSeverityLevel() {
    return this.severityLevel;
  }

  @Nullable
  public String getShortSummary() {
    return shortSummary;
  }

  public void setShortSummary(@Nullable String shortSummary) {
    this.shortSummary = shortSummary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ReferencesSchema getReferences() {
    return references;
  }

  public void setReferences(ReferencesSchema references) {
    this.references = references;
  }

  public void addField(FieldSchema field) {
    this.properties.put(field.getFieldName(), field);
  }

  public Map<String, FieldSchema> getFields() {
    return properties;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  @Nullable
  public String getDeprecationReason() {
    return deprecationReason;
  }

  public void setDeprecationReason(@Nullable String deprecationReason) {
    this.deprecationReason = deprecationReason;
  }

  @Nullable
  public String getDeprecationVersion() {
    return deprecationVersion;
  }

  public void setDeprecationVersion(@Nullable String deprecationVersion) {
    this.deprecationVersion = deprecationVersion;
  }

  @Nullable
  public List<String> getReplacementNoticeCodes() {
    return replacementNoticeCodes;
  }

  public void setReplacementNoticeCodes(@Nullable List<String> replacementNoticeCodes) {
    this.replacementNoticeCodes = replacementNoticeCodes;
  }
}
