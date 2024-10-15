package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("booking_rules.txt")
public interface GtfsBookingRulesSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String bookingRuleId();

  @Required
  GtfsBookingType bookingType();

  @ConditionallyRequired
  int priorNoticeDurationMin();

  @ConditionallyRequired
  int priorNoticeDurationMax();

  @ConditionallyRequired
  int priorNoticeStartDay();

  @ConditionallyRequired
  GtfsTime priorNoticeStartTime();

  @ConditionallyRequired
  int priorNoticeLastDay();

  @ConditionallyRequired
  GtfsTime priorNoticeLastTime();

  @ConditionallyRequired
  String priorNoticeServiceId();

  @MixedCase
  @NoInvalidCharacters
  String message();

  @MixedCase
  @NoInvalidCharacters
  String pickupMessage();

  @MixedCase
  @NoInvalidCharacters
  String dropOffMessage();

  @FieldType(FieldTypeEnum.PHONE_NUMBER)
  @NoInvalidCharacters
  String phoneNumber();

  @FieldType(FieldTypeEnum.URL)
  @NoInvalidCharacters
  String infoUrl();

  @FieldType(FieldTypeEnum.URL)
  @NoInvalidCharacters
  String bookingUrl();
}
