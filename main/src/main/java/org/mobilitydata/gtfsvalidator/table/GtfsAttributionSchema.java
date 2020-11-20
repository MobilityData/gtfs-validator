package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("attributions.txt")
public interface GtfsAttributionSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    String attributionId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "agency.txt", field = "agency_id")
    String agencyId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "routes.txt", field = "route_id")
    String routeId();

    @ForeignKey(table = "trips.txt", field = "trip_id")
    String tripId();

    @Required
    String organizationName();

    boolean isProducer();

    boolean isOperator();

    boolean isAuthority();

    @FieldType(FieldTypeEnum.URL)
    String attributionUrl();

    @FieldType(FieldTypeEnum.EMAIL)
    String attributionEmail();

    @FieldType(FieldTypeEnum.PHONE_NUMBER)
    String attributionPhone();
}
