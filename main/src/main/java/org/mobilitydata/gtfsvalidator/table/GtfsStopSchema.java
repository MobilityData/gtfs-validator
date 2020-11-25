package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

import java.util.TimeZone;

@GtfsTable("stops.txt")
public interface GtfsStopSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @PrimaryKey
    String stopId();

    String stopCode();

    @ConditionallyRequired
    String stopName();

    String ttsStopName();

    String stopDesc();

    @FieldType(FieldTypeEnum.LATITUDE)
    @ConditionallyRequired
    double stopLat();

    @FieldType(FieldTypeEnum.LONGITUDE)
    @ConditionallyRequired
    double stopLon();

    @FieldType(FieldTypeEnum.ID)
    @Index
    @ConditionallyRequired
    String zoneId();

    @FieldType(FieldTypeEnum.URL)
    String stopUrl();

    GtfsLocationType locationType();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "stops.txt", field = "stop_id")
    @ConditionallyRequired
    String parentStation();

    @FieldType(FieldTypeEnum.TIMEZONE)
    TimeZone stopTimezone();

    GtfsWheelchairBoarding wheelchairBoarding();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "levels.txt", field = "level_id")
    String levelId();

    String platformCode();
}
