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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class NoticeContainerTest {
    @Test
    public void exportJson() {
        NoticeContainer container = new NoticeContainer();
        container.addNotice(new MissingRequiredFileError("stops.txt"));
        container.addNotice(new MissingRequiredFileError("agency.txt"));
        container.addSystemError(new RuntimeExceptionInValidatorError(
            "FaultyValidator", "java.lang.IndexOutOfBoundsException",
            "Index 0 out of bounds"));
        assertThat(container.exportValidationNotices()).isEqualTo(
                "{\"notices\":[" +
                        "{\"code\":\"missing_required_file\",\"totalNotices\":2,\"notices\":" +
                        "[{\"filename\":\"stops.txt\"},{\"filename\":\"agency.txt\"}]}]}");
        assertThat(container.exportSystemErrors())
            .isEqualTo(
                "{\"notices\":[{\"code\":\"runtime_exception_in_validator\","
                + "\"totalNotices\":1,"
                + "\"notices\":"
                + "[{\"validator\":\"FaultyValidator\","
                + "\"exception\":\"java.lang.IndexOutOfBoundsException\","
                + "\"message\":\"Index 0 out of bounds\"}]}]}");
    }

    @Test
    public void exportNullInContext() {
        // Test that `null` value in the context is serialized properly.
        NoticeContainer container = new NoticeContainer();
        // Use HashMap because ImmutableMap does not support nulls.
        Map<String, Object> context = new HashMap<>();
        context.put("nullField", null);
        container.addNotice(new TestValidationNotice("test_notice", context));
        assertThat(container.exportValidationNotices()).isEqualTo(
                "{\"notices\":[{\"code\":\"test_notice\",\"totalNotices\":1,\"notices\":[{\"nullField\":null}]}]}");
    }

    @Test
    public void addAll() {
        ValidationNotice n1 = new MissingRequiredFileError("stops.txt");
        ValidationNotice n2 = new UnknownFileNotice("unknown.txt");
        SystemError e1 = new RuntimeExceptionInValidatorError(
            "Validator1", "java.lang.IndexOutOfBoundsException",
            "Index 0 out of bounds");
        SystemError e2 = new RuntimeExceptionInValidatorError(
            "Validator2", "java.lang.NegativeArraySizeException",
            "Index -1 out of bounds");
        NoticeContainer c1 = new NoticeContainer();
        c1.addNotice(n1);
        c1.addSystemError(e1);
        NoticeContainer c2 = new NoticeContainer();
        c2.addNotice(n2);
        c2.addSystemError(e2);
        c1.addAll(c2);
        assertThat(c1.getValidationNotices()).containsExactly(n1, n2);
        assertThat(c1.getSystemErrors()).containsExactly(e1, e2);
    }

    static private class TestValidationNotice extends ValidationNotice {
        private final String code;

        public TestValidationNotice(String code, Map<String, Object> context) {
            super(context);
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }
}