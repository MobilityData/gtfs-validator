package org.mobilitydata.gtfsvalidator.notice.schema;

import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Represents the schema of a single field of a validation notice in the JSON validation schema
 * export.
 *
 * <p>This class is intended for JSON serialization with a structure that is publicly exposed. Be
 * thoughtful when making changes.
 */
public class FieldSchema extends FieldTypeSchema {

  private final String fieldName;

  @Nullable private final String description;

  // Default constructor for GSON
  public FieldSchema() {
    this(FieldTypeSchema.OBJECT, "", null);
  }

  public FieldSchema(FieldTypeSchema type, String fieldName, String description) {
    super(type);
    this.fieldName = fieldName;
    this.description = description;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldSchema that = (FieldSchema) o;
    return super.equals(that)
        && fieldName.equals(that.fieldName)
        && Objects.equals(description, that.description);
  }

  @Override
  public String toString() {
    return "FieldSchema{"
        + "type="
        + super.toString()
        + ", fieldName='"
        + fieldName
        + '\''
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
