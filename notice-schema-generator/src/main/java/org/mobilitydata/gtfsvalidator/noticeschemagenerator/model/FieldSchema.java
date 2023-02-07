package org.mobilitydata.gtfsvalidator.noticeschemagenerator.model;

import com.google.auto.value.AutoValue;
import org.mobilitydata.gtfsvalidator.noticeschemagenerator.model.AutoValue_FieldSchema.Builder;

@AutoValue
public abstract class FieldSchema {

  public abstract String fieldName();

  public abstract String type();

  public abstract String description();

  public static Builder builder() {
    return new AutoValue_FieldSchema.Builder().setDescription("");
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setFieldName(String fieldName);

    public abstract Builder setType(String type);

    public abstract Builder setDescription(String description);

    public abstract FieldSchema build();
  }
}
