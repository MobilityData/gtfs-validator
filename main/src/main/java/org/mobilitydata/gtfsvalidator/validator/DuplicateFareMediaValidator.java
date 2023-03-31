package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedia;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaType;

/** Validates that two fare media do not have the same name and type. */
@GtfsValidator
public class DuplicateFareMediaValidator extends FileValidator {

  private final GtfsFareMediaTableContainer fareMediaTable;

  @Inject
  DuplicateFareMediaValidator(GtfsFareMediaTableContainer fareMediaTable) {
    this.fareMediaTable = fareMediaTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<Key, GtfsFareMedia> mediaByKey = new HashMap<>();
    for (GtfsFareMedia media : fareMediaTable.getEntities()) {
      GtfsFareMedia existing = mediaByKey.putIfAbsent(Key.create(media), media);
      if (existing != null) {
        noticeContainer.addValidationNotice(new DuplicateFareMediaNotice(existing, media));
      }
    }
  }

  /**
   * Describes two fare medias that have the same name and type.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateFareMediaNotice extends ValidationNotice {

    // The row number of the first fare media.
    private final int csvRowNumber1;

    // The id of the first fare media.
    private final String fareMediaId1;

    // The row number of the second fare media.
    private final int csvRowNumber2;

    // The id of the second fare media.
    private final String fareMediaId2;

    DuplicateFareMediaNotice(GtfsFareMedia lhs, GtfsFareMedia rhs) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber1 = lhs.csvRowNumber();
      this.fareMediaId1 = lhs.fareMediaId();
      this.csvRowNumber2 = rhs.csvRowNumber();
      this.fareMediaId2 = rhs.fareMediaId();
    }
  }

  /** A "primary key" type composed of fare media name + type. */
  @AutoValue
  abstract static class Key {
    public abstract String name();

    public abstract GtfsFareMediaType type();

    static Key create(GtfsFareMedia media) {
      return new AutoValue_DuplicateFareMediaValidator_Key(
          media.fareMediaName(), media.fareMediaType());
    }
  }
}
