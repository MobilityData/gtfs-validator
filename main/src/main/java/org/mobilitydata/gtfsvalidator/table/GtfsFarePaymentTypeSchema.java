package org.mobilitydata.gtfsvalidator.table;

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("fare_payment_types.txt")
public interface GtfsFarePaymentTypeSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Index
  @Required
  String farePaymentTypeGroupId();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String farePaymentTypeName();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  GtfsFarePaymentMethod farePaymentType();
}
