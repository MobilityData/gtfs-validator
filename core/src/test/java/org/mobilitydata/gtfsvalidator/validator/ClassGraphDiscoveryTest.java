package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DocumentedNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DoubleFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.GtfsTypesValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.S2LatLngNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.StringFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.TestValidator.TestInnerNotice;

public class ClassGraphDiscoveryTest {
  private static final String TEST_NOTICES_PACKAGE =
      "org.mobilitydata.gtfsvalidator.notice.testnotices";

  @Test
  public void discoverNoticeSubclasses() {
    assertThat(ClassGraphDiscovery.discoverNoticeSubclasses(ImmutableList.of(TEST_NOTICES_PACKAGE)))
        .containsExactly(
            DocumentedNotice.class,
            DoubleFieldNotice.class,
            TestInnerNotice.class,
            GtfsTypesValidationNotice.class,
            S2LatLngNotice.class,
            StringFieldNotice.class);
  }
}
