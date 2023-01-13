package org.mobilitydata.gtfsvalidator.table;

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("fare_payment_options.txt")
public interface GtfsFarePaymentOptionSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Index
  @Required
  String farePaymentOptionGroupId();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String farePaymentOptionName();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  GtfsFarePaymentOptionType farePaymentOptionType();
}
