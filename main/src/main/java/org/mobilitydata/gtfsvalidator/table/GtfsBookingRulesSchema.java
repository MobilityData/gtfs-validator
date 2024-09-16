package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.MixedCase;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;
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
