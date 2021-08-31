/*
 * Copyright 2021 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsLevelTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLevelTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.validator.LevelPresenceValidator.MissingLevelFileNotice;

@RunWith(JUnit4.class)
public class LevelPresenceValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsLevel> levels, List<GtfsPathway> pathways) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new LevelPresenceValidator(
            GtfsLevelTableContainer.forEntities(levels, noticeContainer),
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static ValidationContext validationContext =
      org.mobilitydata.gtfsvalidator.validator.ValidationContext.builder()
      .setCountryCode(
      CountryCode.forStringOrUnknown(CountryCode.ZZ))
      .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
      .build();
  private static List<ValidationNotice> generateNoticesForMissingLevelFile(List<GtfsPathway> pathways)
      throws ValidatorLoaderException {
    NoticeContainer noticeContainer = new NoticeContainer();
     ValidatorLoader validatorLoader = new ValidatorLoader();
     GtfsLevelTableContainer levels = (GtfsLevelTableContainer) new GtfsLevelTableLoader().loadMissingFile(new DefaultValidatorProvider(validationContext, validatorLoader), noticeContainer);
    new LevelPresenceValidator(levels,
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsLevel createLevel(String levelId, long csvRowNumber) {
    return new GtfsLevel.Builder().setLevelId(levelId).setCsvRowNumber(csvRowNumber).build();
  }

  private static GtfsPathway createPathway(
      String pathwayId, long csvRowNumber, GtfsPathwayMode pathwayMode) {
    return new GtfsPathway.Builder()
        .setPathwayId(pathwayId)
        .setCsvRowNumber(csvRowNumber)
        .setFromStopId("from stop id value")
        .setToStopId("to stop id value")
        .setPathwayMode(pathwayMode)
        .build();
  }

  @Test
  public void nonEmptyLevelWithPathwayModeFive_yieldsZeroNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createLevel("level id value", 44),
                    createLevel("other level id value", 55),
                    createLevel("some level id value", 66)),
                ImmutableList.of(
                    createPathway("elevator id value", 77, GtfsPathwayMode.ELEVATOR),
                    createPathway("exit gate id value", 1, GtfsPathwayMode.EXIT_GATE),
                    createPathway("stairs id value", 189, GtfsPathwayMode.STAIRS))))
        .isEmpty();
  }

  @Test
  public void emptyLevelWithPathwayModeFive_yieldsNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(),
                ImmutableList.of(
                    createPathway("elevator id value", 77, GtfsPathwayMode.ELEVATOR),
                    createPathway("other elevator id value", 1, GtfsPathwayMode.ELEVATOR),
                    createPathway("exit gate id value", 144, GtfsPathwayMode.EXIT_GATE),
                    createPathway("stairs id value", 277, GtfsPathwayMode.STAIRS))))
        .containsExactly(new MissingLevelFileNotice(77, "elevator id value"));
  }

  @Test
  public void missingLevelWithPathwayModeFive_yieldsNotice() throws ValidatorLoaderException {
    assertThat(
            generateNoticesForMissingLevelFile(
                ImmutableList.of(
                    createPathway("elevator id value", 77, GtfsPathwayMode.ELEVATOR),
                    createPathway("other elevator id value", 1, GtfsPathwayMode.ELEVATOR),
                    createPathway("exit gate id value", 144, GtfsPathwayMode.EXIT_GATE),
                    createPathway("stairs id value", 277, GtfsPathwayMode.STAIRS))))
        .containsExactly(new MissingLevelFileNotice(77, "elevator id value"));
  }

  @Test
  public void missingPathwayFile_zeroNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createLevel("level id value", 44),
                    createLevel("other level id value", 55)),
                ImmutableList.of()))
        .isEmpty();
  }
}
