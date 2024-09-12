package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedia;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaType;
import org.mobilitydata.gtfsvalidator.validator.DuplicateFareMediaValidator.DuplicateFareMediaNotice;

@RunWith(JUnit4.class)
public class DuplicateFareMediaValidatorTest {

  private static List<ValidationNotice> generateNotices(List<GtfsFareMedia> media) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateFareMediaValidator(GtfsFareMediaTableContainer.forEntities(media, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testUniqueEntries() {
    ImmutableList<GtfsFareMedia> media =
        ImmutableList.of(
            GtfsFareMedia.builder()
                .setCsvRowNumber(1)
                .setFareMediaId("a")
                .setFareMediaName("Transit Card")
                .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                .build(),
            GtfsFareMedia.builder()
                .setCsvRowNumber(2)
                .setFareMediaId("b")
                .setFareMediaName("Transit App")
                .setFareMediaType(GtfsFareMediaType.MOBILE_APP)
                .build());

    assertThat(generateNotices(media)).isEmpty();
  }

  @Test
  public void testDuplicateEntriesTransitCard() {
    ImmutableList<GtfsFareMedia> media =
        ImmutableList.of(
            GtfsFareMedia.builder()
                .setCsvRowNumber(1)
                .setFareMediaId("a")
                .setFareMediaName("Transit Card")
                .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                .build(),
            GtfsFareMedia.builder()
                .setCsvRowNumber(2)
                .setFareMediaId("b")
                .setFareMediaName("Transit Card")
                .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(media))
        .containsExactly(new DuplicateFareMediaNotice(media.get(0), media.get(1)));
  }

  @Test
  public void testTransitCardsWithDifferentNames() {
    ImmutableList<GtfsFareMedia> media =
        ImmutableList.of(
            GtfsFareMedia.builder()
                .setCsvRowNumber(1)
                .setFareMediaId("a")
                .setFareMediaName("Transit Card A")
                .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                .build(),
            GtfsFareMedia.builder()
                .setCsvRowNumber(2)
                .setFareMediaId("b")
                .setFareMediaName("Transit Card B")
                .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(media)).isEmpty();
  }
}
