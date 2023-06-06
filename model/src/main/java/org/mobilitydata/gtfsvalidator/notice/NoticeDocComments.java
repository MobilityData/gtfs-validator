package org.mobilitydata.gtfsvalidator.notice;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;

/**
 * Documentation comments associated with a notice and its fields, as extracted from source code.
 *
 * <p>This is ultimately serialized as JSON to a resource file that can be loaded at runtime to
 * access notice documentation.
 *
 * <p>See `NoticeProcessor` for more details.
 */
public class NoticeDocComments {

  /** A short single-line summary describing the notice. May contain Markdown markup. */
  @Nullable private String shortSummary;

  /**
   * Additional multi-line textual description associated with this notice. It will not contain the
   * `shortSummary` string, but is typically combined as `shortSummary` + "\n\n" + `description`.
   * May contain Markdown markup.
   */
  @Nullable private String additionalDocumentation;

  /** Field-specific comments, keyed by field name. */
  private Map<String, String> fieldDocComments = new HashMap<>();

  public static String getResourceNameForClass(Class<?> noticeType) {
    return getResourceNameForSimpleName(noticeType.getSimpleName());
  }

  public static String getResourceNameForTypeElement(TypeElement type) {
    return getResourceNameForSimpleName(type.getSimpleName().toString());
  }

  private static String getResourceNameForSimpleName(String simpleName) {
    return simpleName + "-DocComments.json";
  }

  /** Returns true if there are no actual comments specified. */
  public boolean isEmpty() {
    return shortSummary == null && additionalDocumentation == null && fieldDocComments.isEmpty();
  }

  /** A short single-line summary describing the notice. May contain Markdown markup. */
  public String getShortSummary() {
    return shortSummary;
  }

  public void setShortSummary(@Nullable String shortSummary) {
    this.shortSummary = shortSummary;
  }

  /**
   * Additional multi-line textual description associated with this notice. It will not contain the
   * `shortSummary` string, but is typically combined as `shortSummary` + "\n\n" + `description`
   * (see {@link #getCombinedDocumentation()}). May be null if a notice only includes a short
   * summary. May contain Markdown markup.
   */
  @Nullable
  public String getAdditionalDocumentation() {
    return additionalDocumentation;
  }

  public void setAdditionalDocumentation(@Nullable String additionalDocumentation) {
    this.additionalDocumentation = additionalDocumentation;
  }

  /**
   * Returns a combined string with the `shortSummary` and `additionalDocumentation` joined with
   * "\n\n".
   */
  public String getCombinedDocumentation() {
    String docs = this.shortSummary;
    if (this.additionalDocumentation != null) {
      docs += "\n\n" + this.additionalDocumentation;
    }
    return docs;
  }

  public void putFieldComment(String fieldName, String docComment) {
    this.fieldDocComments.put(fieldName, docComment);
  }

  @Nullable
  public String getFieldComment(String fieldName) {
    return fieldDocComments.get(fieldName);
  }
}
