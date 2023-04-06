package org.mobilitydata.gtfsvalidator;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

public class TestUtils {

  protected TestUtils() {
    //        Hiding default constructor to avoid instantiation
  }

  public static InputStream retrieveInputStream(String path) {
    return TestUtils.class.getResourceAsStream(path);
  }

  public static void assertValidationNotice(
      NoticeContainer noticeContainer, Class<? extends ValidationNotice> noticeClass) {
    noticeContainer.getValidationNotices().stream()
        .filter(notice -> noticeClass.isInstance(notice))
        .findAny()
        .ifPresentOrElse(
            x -> {}, () -> fail("Expected notice not found: " + noticeClass.getCanonicalName()));
  }

  public static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }
}
