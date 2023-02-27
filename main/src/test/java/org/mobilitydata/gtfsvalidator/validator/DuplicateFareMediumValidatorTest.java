package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedium;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumType;
import org.mobilitydata.gtfsvalidator.validator.DuplicateFareMediumValidator.DuplicateFareMediumNotice;

@RunWith(JUnit4.class)
public class DuplicateFareMediumValidatorTest {

  private static List<ValidationNotice> generateNotices(List<GtfsFareMedium> media) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateFareMediumValidator(
            GtfsFareMediumTableContainer.forEntities(media, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testUniqueEntries() {
    ImmutableList<GtfsFareMedium> media =
        ImmutableList.of(
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(1)
                .setFareMediumId("a")
                .setFareMediumName("Transit Card")
                .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                .build(),
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(2)
                .setFareMediumId("b")
                .setFareMediumName("Transit App")
                .setFareMediumType(GtfsFareMediumType.MOBILE_APP)
                .build());

    assertThat(generateNotices(media)).isEmpty();
  }

  @Test
  public void testDuplicateEntriesTransitCard() {
    ImmutableList<GtfsFareMedium> media =
        ImmutableList.of(
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(1)
                .setFareMediumId("a")
                .setFareMediumName("Transit Card")
                .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                .build(),
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(2)
                .setFareMediumId("b")
                .setFareMediumName("Transit Card")
                .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(media))
        .containsExactly(new DuplicateFareMediumNotice(media.get(0), media.get(1)));
  }

  @Test
  public void testTransitCardsWithDifferentNames() {
    ImmutableList<GtfsFareMedium> media =
        ImmutableList.of(
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(1)
                .setFareMediumId("a")
                .setFareMediumName("Transit Card A")
                .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                .build(),
            new GtfsFareMedium.Builder()
                .setCsvRowNumber(2)
                .setFareMediumId("b")
                .setFareMediumName("Transit Card B")
                .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(media)).isEmpty();
  }
}
