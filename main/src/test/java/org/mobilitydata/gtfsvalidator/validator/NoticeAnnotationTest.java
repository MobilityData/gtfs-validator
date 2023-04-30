package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;

@RunWith(JUnit4.class)
public class NoticeAnnotationTest {

  @Test
  public void testAllNoticesAnnotatedWithGtfsValidationNotice() {
    List<Class<Notice>> noticesWithoutAnnotation =
        discoverValidationNoticeClasses()
            .filter(c -> c.getAnnotation(GtfsValidationNotice.class) == null)
            .collect(Collectors.toList());
    assertWithMessage(
            "All Notice subclasses must be annotated with @GtfsValidationNotice, but the following classes were not:")
        .that(noticesWithoutAnnotation)
        .isEmpty();
  }

  private static Stream<Class<Notice>> discoverValidationNoticeClasses() {
    return ClassGraphDiscovery.discoverNoticeSubclasses(ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
        .stream();
  }
}
