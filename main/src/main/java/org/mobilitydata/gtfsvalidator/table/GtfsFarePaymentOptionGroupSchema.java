package org.mobilitydata.gtfsvalidator.table;

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("fare_payment_option_groups.txt")
public interface GtfsFarePaymentOptionGroupSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  @Index
  String farePaymentOptionGroupId();

  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  @ForeignKey(table = "fare_payment_options.txt", field = "fare_payment_option_id")
  String farePaymentOptionId();
}
