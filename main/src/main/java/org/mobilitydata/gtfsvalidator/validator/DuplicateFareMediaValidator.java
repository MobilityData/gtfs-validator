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

    // Reference to the first fare media.
    private final FareMediaReference fareMedia1;
    // Reference to the second fare media.
    private final FareMediaReference fareMedia2;

    DuplicateFareMediaNotice(GtfsFareMedia lhs, GtfsFareMedia rhs) {
      super(SeverityLevel.WARNING);
      this.fareMedia1 = FareMediaReference.create(lhs);
      this.fareMedia2 = FareMediaReference.create(rhs);
    }
  }

  /** A reference to a particular fare media entry. */
  @AutoValue
  abstract static class FareMediaReference {
    // The row number of the fare media entry.
    abstract int csvRowNumber();

    // The id of the fare media entry.
    abstract String fareMediaId();

    static FareMediaReference create(GtfsFareMedia media) {
      return new AutoValue_DuplicateFareMediaValidator_FareMediaReference(
          media.csvRowNumber(), media.fareMediaId());
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
