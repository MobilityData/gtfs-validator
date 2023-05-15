package org.mobilitydata.gtfsvalidator.notice;

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.annotation.Nullable;


/**
 * Documentation comments associated with a notice and its fields, as extracted from source code.
 *
 * <p>This is ultimately serialized as JSON to a resource file that can be loaded at runtime to
 * access notice documentation.
 *
 * <p>See `NoticeProcessor` for more details.
 */
public class NoticeDocComments {

  /** The main notice description, extracted from the notice's Javadoc comment. */
  @Nullable
  private String docComment;

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
    return docComment == null && fieldDocComments.isEmpty();
  }

  public String getDocComment() {
    return docComment;
  }

  public void setDocComment(String docComment) {
    this.docComment = docComment;
  }

  public void putFieldComment(String fieldName, String docComment) {
    this.fieldDocComments.put(fieldName, docComment);
  }

  @Nullable
  public String getFieldComment(String fieldName) {
    return fieldDocComments.get(fieldName);
  }
}
