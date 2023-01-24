package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOption;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionType;

@GtfsValidator
public class DuplicateFarePaymentOptionValidator extends FileValidator {

  private final GtfsFarePaymentOptionTableContainer farePaymentOptionTable;

  @Inject
  DuplicateFarePaymentOptionValidator(GtfsFarePaymentOptionTableContainer farePaymentOptionTable) {
    this.farePaymentOptionTable = farePaymentOptionTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<Key, GtfsFarePaymentOption> optionsByKey = new HashMap<>();
    for (GtfsFarePaymentOption fpo : farePaymentOptionTable.getEntities()) {
      GtfsFarePaymentOption existing = optionsByKey.putIfAbsent(Key.create(fpo), fpo);
      if (existing != null) {
        noticeContainer.addValidationNotice(new DuplicateFarePaymentOptionNotice(existing, fpo));
      }
    }
  }

  /**
   * Describes two routes that have the same long and short names, route type and belong to the same
   * agency.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateFarePaymentOptionNotice extends ValidationNotice {

    private final FarePaymentOptionReference farePaymentOption1;
    private final FarePaymentOptionReference farePaymentOption2;

    DuplicateFarePaymentOptionNotice(GtfsFarePaymentOption lhs, GtfsFarePaymentOption rhs) {
      super(SeverityLevel.WARNING);
      this.farePaymentOption1 = FarePaymentOptionReference.create(lhs);
      this.farePaymentOption2 = FarePaymentOptionReference.create(rhs);
    }
  }

  @AutoValue
  abstract static class FarePaymentOptionReference {
    abstract int csvRowNumber();

    abstract String farePaymentOptionId();

    static FarePaymentOptionReference create(GtfsFarePaymentOption fpo) {
      return new AutoValue_DuplicateFarePaymentOptionValidator_FarePaymentOptionReference(
          fpo.csvRowNumber(), fpo.farePaymentOptionId());
    }
  }

  @AutoValue
  abstract static class Key {
    public abstract String name();

    public abstract GtfsFarePaymentOptionType type();

    static Key create(GtfsFarePaymentOption fpo) {
      return new AutoValue_DuplicateFarePaymentOptionValidator_Key(
          fpo.farePaymentOptionName(), fpo.farePaymentOptionType());
    }
  }
}
