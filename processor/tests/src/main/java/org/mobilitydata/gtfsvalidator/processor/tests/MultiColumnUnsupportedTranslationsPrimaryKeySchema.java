package org.mobilitydata.gtfsvalidator.processor.tests;

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;

@GtfsTable("multi_column_unsupported_translations_primary_key.txt")
public interface MultiColumnUnsupportedTranslationsPrimaryKeySchema {
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String idA();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String idB();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String idC();
}
