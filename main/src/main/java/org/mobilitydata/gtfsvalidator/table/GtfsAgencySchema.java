package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

import java.util.Locale;
import java.util.TimeZone;


@GtfsTable("agency.txt")
public interface GtfsAgencySchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @ConditionallyRequired
    String agencyId();

    @Required
    String agencyName();

    @FieldType(FieldTypeEnum.URL)
    @Required
    String agencyUrl();

    @Required
    TimeZone agencyTimezone();

    Locale agencyLang();

    @FieldType(FieldTypeEnum.PHONE_NUMBER)
    String agencyPhone();

    @FieldType(FieldTypeEnum.URL)
    String agencyFareUrl();

    @FieldType(FieldTypeEnum.EMAIL)
    String agencyEmail();
}
