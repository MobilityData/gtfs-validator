package org.mobilitydata.gtfsvalidator.table;

import java.time.ZoneId;
import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("agency.txt")
@Required
public interface GtfsNetworkSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String networkId();

  @MixedCase
  String networkName();
}
