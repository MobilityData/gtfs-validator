/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.Arrays;
import java.util.List;

/**
 * This represent the information extracted from an Apache command line or a .json file containing execution parameters
 * (and their values) that are passed are parameters of the main execution method.
 */
public class ExecParam {
    private String key;
    private List<String> value;

    public ExecParam() {
    }

    public ExecParam(final String key, final String value) {
        this.key = key;
        this.value = value == null ? null : List.of(value);
    }

    public ExecParam(final String key, final String[] value) {
        this.key = key;
        this.value = value == null ? null : Arrays.asList(value);
    }

    public String getKey() {
        return key;
    }

    public List<String> getValue() {
        return value;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setValue(final List<String> value) {
        this.value = value;
    }
}