package org.mobilitydata.gtfsvalidator.table;

import com.google.auto.value.AutoValue;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;

@AutoValue
public abstract class GtfsColumnDescriptor {
  public abstract String columnName();

  public abstract boolean headerRequired();

  public abstract FieldLevelEnum fieldLevel();

  public abstract Optional<RowParser.NumberBounds> numberBounds();

  public abstract boolean isCached();

  public abstract boolean isMixedCase();

  public boolean isRequired() {
    return FieldLevelEnum.REQUIRED.equals(fieldLevel());
  }

  public static GtfsColumnDescriptor.Builder builder() {
    return new AutoValue_GtfsColumnDescriptor.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setColumnName(String value);

    public abstract Builder setHeaderRequired(boolean value);

    public abstract Builder setFieldLevel(FieldLevelEnum value);

    public abstract Builder setNumberBounds(Optional<RowParser.NumberBounds> value);

    public abstract Builder setNumberBounds(RowParser.NumberBounds value);

    public abstract Builder setIsCached(boolean value);

    public abstract Builder setIsMixedCase(boolean value);

    public abstract GtfsColumnDescriptor build();
  }
}
