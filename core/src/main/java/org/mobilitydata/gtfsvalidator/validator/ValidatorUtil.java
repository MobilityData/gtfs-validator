/*
 * Copyright 2021 Google LLC
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

import com.google.common.flogger.FluentLogger;
import java.util.List;
import java.util.function.Consumer;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInValidatorError;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

/** Methods for calling one or multiple validators and handle runtime exceptions gracefully. */
public final class ValidatorUtil {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  /**
   * Invokes all single-entity validators in the list.
   *
   * @param entity GTFS entity to validate
   * @param validators list of single-entity validators
   * @param noticeContainer container for accumulating notices
   * @param <T> type of the GTFS entity
   */
  public static <T extends GtfsEntity> void invokeSingleEntityValidators(
      T entity, List<SingleEntityValidator<T>> validators, NoticeContainer noticeContainer) {
    for (SingleEntityValidator<T> validator : validators) {
      safeValidate(c -> validator.validate(entity, c), validator.getClass(), noticeContainer);
    }
  }

  /**
   * Invokes single-file validators.
   *
   * @param validators list of single-file validators
   * @param noticeContainer container for accumulating notices
   * @param <T> type of the GTFS entity
   */
  public static <T extends GtfsEntity> void invokeSingleFileValidators(
      List<FileValidator> validators, NoticeContainer noticeContainer) {
    for (FileValidator validator : validators) {
      safeValidate(validator::validate, validator.getClass(), noticeContainer);
    }
  }

  /**
   * Invokes the given validation function.
   *
   * <p>If the function raises an exception, then a system error is stored in the {@code
   * noticeContainer}.
   *
   * @param validate a function to invoke
   * @param validatorClass the class of the validator for debugging purposes
   * @param noticeContainer the container for storing notices
   */
  public static void safeValidate(
      Consumer<NoticeContainer> validate, Class validatorClass, NoticeContainer noticeContainer) {
    try {
      validate.accept(noticeContainer);
    } catch (RuntimeException e) {
      logger.atSevere().withCause(e).log(
          "Runtime exception in validator %s", validatorClass.getCanonicalName());
      noticeContainer.addSystemError(
          new RuntimeExceptionInValidatorError(
              validatorClass.getCanonicalName(), e.getClass().getCanonicalName(), e.getMessage()));
    }
  }

  private ValidatorUtil() {}
}
