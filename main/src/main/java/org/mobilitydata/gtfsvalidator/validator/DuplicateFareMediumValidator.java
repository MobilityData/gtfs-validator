package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedium;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumType;

@GtfsValidator
public class DuplicateFareMediumValidator extends FileValidator {

  private final GtfsFareMediumTableContainer fareMediumTable;

  @Inject
  DuplicateFareMediumValidator(GtfsFareMediumTableContainer fareMediumTable) {
    this.fareMediumTable = fareMediumTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<Key, GtfsFareMedium> mediaByKey = new HashMap<>();
    for (GtfsFareMedium medium : fareMediumTable.getEntities()) {
      GtfsFareMedium existing = mediaByKey.putIfAbsent(Key.create(medium), medium);
      if (existing != null) {
        noticeContainer.addValidationNotice(new DuplicateFareMediumNotice(existing, medium));
      }
    }
  }

  /**
   * Describes two fare medias that have the same name and type.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateFareMediumNotice extends ValidationNotice {

    private final FareMediumReference fareMedium1;
    private final FareMediumReference fareMedium2;

    DuplicateFareMediumNotice(GtfsFareMedium lhs, GtfsFareMedium rhs) {
      super(SeverityLevel.WARNING);
      this.fareMedium1 = FareMediumReference.create(lhs);
      this.fareMedium2 = FareMediumReference.create(rhs);
    }
  }

  @AutoValue
  abstract static class FareMediumReference {
    abstract int csvRowNumber();

    abstract String fareMediumId();

    static FareMediumReference create(GtfsFareMedium medium) {
      return new AutoValue_DuplicateFareMediumValidator_FareMediumReference(
          medium.csvRowNumber(), medium.fareMediumId());
    }
  }

  @AutoValue
  abstract static class Key {
    public abstract String name();

    public abstract GtfsFareMediumType type();

    static Key create(GtfsFareMedium medium) {
      return new AutoValue_DuplicateFareMediumValidator_Key(
          medium.fareMediumName(), medium.fareMediumType());
    }
  }
}
