/*
 * Copyright 2020-2021 Google LLC, MobilityData IO
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
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.mobilitydata.gtfsvalidator.notice.schema.NoticeSchemaGenerator;
import org.mobilitydata.gtfsvalidator.runner.ApplicationType;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunner;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.validator.ClassGraphDiscovery;

/** The main entry point for GTFS Validator CLI. */
public class Main {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final String NOTICE_SCHEMA_JSON = "notice_schema.json";

  public static void main(String[] argv) {
    Arguments args = new Arguments();
    JCommander jCommander = new JCommander(args);
    jCommander.parse(argv);

    if (args.getHelp()) {
      jCommander.usage();
      System.out.println(
          "⚠️ Note that parameters marked with an asterisk (*) in the help menu are mandatory.");
      System.out.println(
          "⚠️ Note that exactly one of the following options must be provided: --url or --input.");
      System.out.println(
          "⚠️ Note that --storage_directory must not be provided if --url is not provided.");
      System.exit(0);
    }

    if (!args.validate()) {
      System.exit(1);
    }

    try {
      if (args.getExportNoticeSchema()) {
        exportNoticeSchema(args);
        if (args.abortAfterNoticeSchemaExport()) {
          System.exit(0);
        }
      }

      ValidationRunner runner = new ValidationRunner(new VersionResolver(ApplicationType.CLI));
      if (runner.run(args.toConfig()) != ValidationRunner.Status.SUCCESS) {
        System.exit(-1);
      }
    } catch (Exception ex) {
      logger.atSevere().withCause(ex).log("Error running validation");
      System.exit(-1);
    }

    System.exit(0);
  }

  private static void exportNoticeSchema(final Arguments args) {
    new File(args.getOutputBase()).mkdirs();

    GsonBuilder gsonBuilder = new GsonBuilder();
    if (args.getPretty()) {
      gsonBuilder.setPrettyPrinting();
    }
    Gson gson = gsonBuilder.create();

    try {
      Files.write(
          Paths.get(args.getOutputBase(), NOTICE_SCHEMA_JSON),
          gson.toJson(
                  NoticeSchemaGenerator.generateSchemasForNoticesInPackages(
                      ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES))
              .getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Cannot store notice schema file");
    }
  }
}
