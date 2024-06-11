package org.mobilitydata.gtfsvalidator.table;

import com.google.auto.value.AutoValue;
import org.mobilitydata.gtfsvalidator.parsing.RowParser.EnumCreator;

@AutoValue
public abstract class GtfsEnumDescriptor {
  public abstract EnumCreator<GtfsEnum> creator();

  public abstract GtfsEnum defaultValue();

  public static GtfsEnumDescriptor create(EnumCreator<GtfsEnum> creator, GtfsEnum defaultValue) {
    return new AutoValue_GtfsEnumDescriptor(creator, defaultValue);
  }
}
