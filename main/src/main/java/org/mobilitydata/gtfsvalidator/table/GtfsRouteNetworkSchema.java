package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("route_networks.txt")
public interface GtfsRouteNetworkSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "routes.txt", field = "route_id")
  @Required
  @PrimaryKey
  String routeId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "networks.txt", field = "network_id")
  @Required
  String networkId();
}
