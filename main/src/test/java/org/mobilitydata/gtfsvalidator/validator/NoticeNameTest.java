package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

@RunWith(JUnit4.class)
public class NoticeNameTest {

  @Test
  public void testAllValidationNoticeClassesEndWithNotice() {
    List<Class<Notice>> noticesWithoutNoticeSuffix =
        ClassGraphDiscovery.discoverNoticeSubclasses(ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
            .stream()
            .filter(c -> ValidationNotice.class.isAssignableFrom(c))
            .filter(c -> !c.getSimpleName().endsWith("Notice"))
            .collect(Collectors.toList());
    assertWithMessage(
            "All GtfsValidationNotice subclasses must have \"Notice\" as their class name suffix, but the following classes do not:")
        .that(noticesWithoutNoticeSuffix)
        .isEmpty();
  }
}
