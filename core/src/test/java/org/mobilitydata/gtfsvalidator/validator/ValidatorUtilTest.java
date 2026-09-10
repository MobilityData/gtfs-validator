/*
 * Copyright 2026 MobilityData
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
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInValidatorError;
import org.mobilitydata.gtfsvalidator.notice.SystemError;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

@RunWith(JUnit4.class)
public class ValidatorUtilTest {

  @Test
  public void invokeSingleEntityValidators_invokesEveryValidatorWithTheEntity() {
    List<String> invocations = new ArrayList<>();
    GtfsEntity entity = () -> 7;
    NoticeContainer noticeContainer = new NoticeContainer();

    ValidatorUtil.invokeSingleEntityValidators(
        entity,
        ImmutableList.of(
            recordingValidator("first", invocations), recordingValidator("second", invocations)),
        noticeContainer);

    assertThat(invocations).containsExactly("first:7", "second:7").inOrder();
    assertThat(noticeContainer.getSystemErrors()).isEmpty();
  }

  /** A validator that throws must not stop the others, and must be reported as a system error. */
  @Test
  public void invokeSingleEntityValidators_validatorThrows_reportsSystemErrorAndContinues() {
    List<String> invocations = new ArrayList<>();
    GtfsEntity entity = () -> 7;
    NoticeContainer noticeContainer = new NoticeContainer();

    ValidatorUtil.invokeSingleEntityValidators(
        entity,
        ImmutableList.of(throwingValidator(), recordingValidator("after", invocations)),
        noticeContainer);

    assertThat(invocations).containsExactly("after:7");
    assertThat(noticeContainer.getSystemErrors()).hasSize(1);
    SystemError error = noticeContainer.getSystemErrors().get(0);
    assertThat(error).isInstanceOf(RuntimeExceptionInValidatorError.class);
    assertThat(error.toJsonTree().getAsJsonObject().get("message").getAsString())
        .isEqualTo("broken validator");
  }

  private static SingleEntityValidator<GtfsEntity> recordingValidator(
      String name, List<String> invocations) {
    return new SingleEntityValidator<>() {
      @Override
      public void validate(GtfsEntity entity, NoticeContainer noticeContainer) {
        invocations.add(name + ":" + entity.csvRowNumber());
      }
    };
  }

  private static SingleEntityValidator<GtfsEntity> throwingValidator() {
    return new SingleEntityValidator<>() {
      @Override
      public void validate(GtfsEntity entity, NoticeContainer noticeContainer) {
        throw new IllegalStateException("broken validator");
      }
    };
  }
}
