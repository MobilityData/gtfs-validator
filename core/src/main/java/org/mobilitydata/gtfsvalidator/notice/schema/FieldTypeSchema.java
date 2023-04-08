package org.mobilitydata.gtfsvalidator.notice.schema;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Represents the schema for the type of a field (integer, number, string, etc) in the JSON
 * validation schema export..
 *
 * <p>This class is intended for JSON serialization with a structure that is publicly exposed. Be
 * thoughtful when making changes.
 */
@Immutable
public class FieldTypeSchema {
  private final String type;

  @Nullable private final FieldTypeSchema contains;

  @Nullable private final Integer minItems;

  @Nullable private final Integer maxItems;

  public static final FieldTypeSchema BOOLEAN = new FieldTypeSchema("boolean");

  /** Represents bytes, shorts, ints, longs. */
  public static final FieldTypeSchema INTEGER = new FieldTypeSchema("integer");

  /** Represents floats, doubles. */
  public static final FieldTypeSchema NUMBER = new FieldTypeSchema("number");

  public static final FieldTypeSchema STRING = new FieldTypeSchema("string");
  public static final FieldTypeSchema ENUM = new FieldTypeSchema("enum");

  public static final FieldTypeSchema OBJECT = new FieldTypeSchema("object");

  public static FieldTypeSchema array(
      FieldTypeSchema contains, @Nullable Integer minItems, @Nullable Integer maxItems) {
    return new FieldTypeSchema("array", contains, minItems, maxItems);
  }

  private FieldTypeSchema(String type) {
    this(type, null, null, null);
  }

  public FieldTypeSchema(FieldTypeSchema other) {
    this(other.type, other.contains, other.minItems, other.maxItems);
  }

  private FieldTypeSchema(
      String type,
      @Nullable FieldTypeSchema contains,
      @Nullable Integer minItems,
      @Nullable Integer maxItems) {
    this.type = type;
    this.contains = contains;
    this.minItems = minItems;
    this.maxItems = maxItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldTypeSchema that = (FieldTypeSchema) o;
    return type.equals(that.type)
        && Objects.equals(contains, that.contains)
        && Objects.equals(minItems, that.minItems)
        && Objects.equals(maxItems, that.maxItems);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("FieldTypeSchema{");
    b.append("type='" + type + '\'');
    if (contains != null) {
      b.append(", contains=" + contains);
    }
    if (minItems != null) {
      b.append(", minItems=" + minItems);
    }
    if (maxItems != null) {
      b.append(", maxItems=" + maxItems);
    }
    b.append('}');
    return b.toString();
  }
}
