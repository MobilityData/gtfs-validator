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

import com.beust.jcommander.JCommander;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The main entry point for GTFS Validator CLI.
 */
public class Main {
    public static void main(String[] argv) {
        Arguments args = new Arguments();
        new JCommander(args).parse(argv);

        ValidatorLoader validatorLoader = new ValidatorLoader();
        GtfsFeedLoader feedLoader = new GtfsFeedLoader();

        GtfsFeedName feedName = GtfsFeedName.parseString(args.feedName);
        System.out.println("Feed name: " + feedName.getCountryFirstName());
        System.out.println("Input: " + args.input);
        System.out.println("Output: " + args.outputBase);
        System.out.println("Table loaders: " + feedLoader.listTableLoaders());
        System.out.println("Validators:");
        System.out.println(validatorLoader.listValidators());

        // Input.
        feedLoader.setNumThreads(args.numThreads);
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsFeedContainer feedContainer;
        try {
            feedContainer = feedLoader.loadAndValidate(GtfsInput.createFromPath(args.input), feedName, validatorLoader,
                    noticeContainer);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Output.
        new File(args.outputBase).mkdirs();
        try {
            Files.write(Paths.get(args.outputBase, "report.json"),
                    noticeContainer.exportJson().getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        System.out.println(feedContainer.tableTotals());
    }
}
