/*
 * Copyright 2020 Google LLC
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
package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;

public class NoticeSet {

  private final Map<String, List<NoticeReport>> noticesByCode;

  public NoticeSet(Collection<NoticeReport> notices) {
    this(notices.stream());
  }

  public NoticeSet(Stream<NoticeReport> notices) {
    this.noticesByCode = notices.collect(Collectors.groupingBy(NoticeReport::getCode));
  }

  /**
   * Compares two notice sets: returns true if they contain the same set of notice codes.
   *
   * @param other the other {@code NoticeSet}.
   * @return true if the two {@code NoticeSet}s contain the same set of notice codes, false
   *     otherwise.
   */
  public boolean hasSameNoticeCodes(NoticeSet other) {
    return this.noticesByCode.keySet().equals(other.noticesByCode.keySet());
  }

  /**
   * Returns the listing of new error codes introduced by the other {@code NoticeSet} passed as
   * parameter, e.g. if this {@code NoticeSet} has the following notice codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   * </ul>
   *
   * <p>and the other {@code NoticeSet} has the following notice codes:
   *
   * <ul>
   *   <li>invalid_phone_number;
   *   <li>number_out_of_range;
   *   <li>invalid_email_address;
   *   <li>invalid_url;
   * </ul>
   *
   * <p>then this returns a {@code Set} that contains the two new notice codes
   * (invalid_email_address, invalid_url) not present in this {@code NoticeSet}
   *
   * @param other the other {@code Notice}
   * @return the listing of new notice codes introduced by the other {@code NoticeSet} passed as
   *     parameter.
   */
  public Set<String> getNewNotices(NoticeSet other) {
    return Sets.difference(other.noticesByCode.keySet(), this.noticesByCode.keySet());
  }

  public int getTotalNoticeCountForCode(String noticeCode) {
    return this.noticesByCode.getOrDefault(noticeCode, Collections.emptyList()).stream()
        .mapToInt(NoticeReport::getTotalNotices)
        .sum();
  }
}
