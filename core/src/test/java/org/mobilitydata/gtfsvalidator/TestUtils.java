package org.mobilitydata.gtfsvalidator;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;

public class TestUtils {

  protected TestUtils() {
    //        Hiding default constructor to avoid instantiation
  }

  // In TestUtils...
  public static List<Class<? extends ValidationNotice>> validationNoticeTypes(
      NoticeContainer notices) {
    return notices.getValidationNotices().stream().map(obj -> obj.getClass()).collect(toList());
  }

  public static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  public static ValidationContext contextForTest() {
    return ValidationContext.builder()
        .setCountryCode(CountryCode.forStringOrUnknown("ca"))
        .setDateForValidation(new DateForValidation(LocalDate.now()))
        .build();
  }
}
