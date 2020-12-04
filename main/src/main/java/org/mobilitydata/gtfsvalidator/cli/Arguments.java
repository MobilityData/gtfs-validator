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

package org.mobilitydata.gtfsvalidator.cli;

import com.beust.jcommander.Parameter;

/**
 * Command-line arguments for GTFS Validator CLI.
 */
public class Arguments {

    @Parameter(names = {"-i", "--input"}, description = "Location of the input GTFS ZIP or unarchived directory")
    public String input;

    @Parameter(names = {"-o", "--output_base"}, description = "Base directory to store the outputs", required = true)
    public String outputBase;

    @Parameter(names = {"-t", "--threads"}, description = "Number of threads to use")
    public int numThreads = 1;

    @Parameter(names = {"-f", "--feed_name"}, description = "Name of the feed, e.g., `nl-openov`. " +
            "It must start from two-letter country code (ISO 3166-1 alpha-2)")
    public String feedName;
}
