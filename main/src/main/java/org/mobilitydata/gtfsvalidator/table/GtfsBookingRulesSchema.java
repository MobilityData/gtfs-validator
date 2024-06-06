package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.MixedCase;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("booking_rules.txt")
public interface GtfsBookingRulesSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String bookingRuleId();

  @Required
  GtfsBookingTypeEnum bookingType();

  @ConditionallyRequired
  Integer priorNoticeDurationMin();

  @ConditionallyRequired
  Integer priorNoticeDurationMax();

  @ConditionallyRequired
  Integer priorNoticeStartDay();

  @ConditionallyRequired
  String priorNoticeStartTime();

  @ConditionallyRequired
  Integer priorNoticeLastDay();

  @ConditionallyRequired
  String priorNoticeLastTime();

  @ConditionallyRequired
  String priorNoticeServiceId();

  @MixedCase
  String message();

  @MixedCase
  String pickupMessage();

  @MixedCase
  String dropOffMessage();

  @FieldType(FieldTypeEnum.PHONE_NUMBER)
  String phoneNumber();

  @FieldType(FieldTypeEnum.URL)
  String infoUrl();

  @FieldType(FieldTypeEnum.URL)
  String bookingUrl();
}
