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
    for (GtfsFareMedia medium : fareMediaTable.getEntities()) {
      GtfsFareMedia existing = mediaByKey.putIfAbsent(Key.create(medium), medium);
      if (existing != null) {
        noticeContainer.addValidationNotice(new DuplicateFareMediaNotice(existing, medium));
      }
    }
  }

  /**
   * Describes two fare medias that have the same name and type.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateFareMediaNotice extends ValidationNotice {

    private final FareMediaReference fareMedia1;
    private final FareMediaReference fareMedia2;

    DuplicateFareMediaNotice(GtfsFareMedia lhs, GtfsFareMedia rhs) {
      super(SeverityLevel.WARNING);
      this.fareMedia1 = FareMediaReference.create(lhs);
      this.fareMedia2 = FareMediaReference.create(rhs);
    }
  }

  @AutoValue
  abstract static class FareMediaReference {
    abstract int csvRowNumber();

    abstract String fareMediaId();

    static FareMediaReference create(GtfsFareMedia media) {
      return new AutoValue_DuplicateFareMediaValidator_FareMediaReference(
          media.csvRowNumber(), media.fareMediaId());
    }
  }

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
