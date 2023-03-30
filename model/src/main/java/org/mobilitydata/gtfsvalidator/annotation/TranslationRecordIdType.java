package org.mobilitydata.gtfsvalidator.annotation;

/**
 * For {@link PrimaryKey} fields, identifies if the given key can be used as a <code>
 * translations.txt</code> record id for entity translation lookup and, if so, how it corresponds to
 * the <code>record_id</code> and <code>record_sub_id</code> fields.
 */
public enum TranslationRecordIdType {
  /**
   * Indicates that the key field will match against the <code>record_id</code> field of <code>
   * translations.txt</code>.
   */
  RECORD_ID,

  /**
   * Indicates that the key field will match against the <code>record_sub_id</code> field of <code>
   * translations.txt</code>.
   */
  RECORD_SUB_ID,

  /**
   * Indicates that the key field will not match against any record id field of <code>
   * translations.txt</code>.
   */
  UNSUPPORTED
}
