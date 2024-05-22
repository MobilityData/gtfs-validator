package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.Random;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/** Validates that two fare media do not have the same name and type. */
@GtfsValidator
public class DummyValidator extends FileValidator {

  private final Random random;

  DummyValidator() {
    this.random = new Random();
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Flip coin to decide if we should add a warning or an error
    // with 10% probability of adding a warning or an error
    if (random.nextInt(10) == 0) {
      if (random.nextBoolean()) {
        noticeContainer.addValidationNotice(new DummyNoticeWarning());
      } else {
        noticeContainer.addValidationNotice(new DummyNoticeError());
      }
    }

    // Wait between 0-10 seconds
    try {
      int delay =
          random.nextInt(11) * 1000; // Random delay between 0 and 10000 milliseconds (0-10 seconds)
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      // Handle the interruption
    }
  }

  @GtfsValidationNotice(severity = WARNING)
  static class DummyNoticeWarning extends ValidationNotice {
    private final int csvRowNumber1;

    DummyNoticeWarning() {
      this.csvRowNumber1 = 1;
    }
  }

  @GtfsValidationNotice(severity = ERROR)
  static class DummyNoticeError extends ValidationNotice {
    private final int csvRowNumber1;

    DummyNoticeError() {
      this.csvRowNumber1 = 1;
    }
  }
}
