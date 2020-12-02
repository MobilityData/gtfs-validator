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

package org.mobilitydata.gtfsvalidator.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class NoticeContainerTest {
    @Test
    public void exportJson() throws JsonProcessingException {
        NoticeContainer container = new NoticeContainer();
        container.addNotice(new MissingRequiredFileError("stops.txt"));
        container.addNotice(new MissingRequiredFileError("agency.txt"));

        ObjectMapper mapper = new ObjectMapper();

        assertThat(container.exportJson()).isEqualTo(
                "{\"notices\":[" +
                        "{\"code\":\"missing_required_file\",\"totalNotices\":2,\"notices\":" +
                        "[{\"filename\":\"stops.txt\"},{\"filename\":\"agency.txt\"}]}]}");
    }
}
