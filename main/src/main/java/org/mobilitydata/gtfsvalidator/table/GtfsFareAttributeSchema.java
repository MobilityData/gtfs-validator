package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

import java.util.Currency;

@GtfsTable("fare_attributes.txt")
public interface GtfsFareAttributeSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String fareId();

    @NonNegative
    double price();

    @Required
    Currency currencyType();

    @Required
    GtfsFareAttributePaymentMethod paymentMethod();

    @Required
    GtfsFareAttributeTransfers transfers();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "agency.txt", field = "agency_id")
    String agencyId();

    @NonNegative
    int transfer_duration();
}
