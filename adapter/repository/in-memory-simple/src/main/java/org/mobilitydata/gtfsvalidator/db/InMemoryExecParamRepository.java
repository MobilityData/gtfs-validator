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

package org.mobilitydata.gtfsvalidator.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.ExecParam;
import org.mobilitydata.gtfsvalidator.parser.ApacheExecParamParser;
import org.mobilitydata.gtfsvalidator.parser.JsonExecParamParser;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This holds information about the execution parameter the user of the validation tool passed as arguments of the main
 * execution method. This repository supports the parsing of execution parameters from either a json file or via
 * Apache command line. This is created  when creating a new default configuration. Operations on this repository
 * are executed ihe main execution method and inside the relevant use cases.
 */
public class InMemoryExecParamRepository implements ExecParamRepository {
    private final Map<String, ExecParam> execParamCollection = new HashMap<>();
    private final Map<String, ExecParam> defaultValueCollection;
    private final Logger logger;

    public InMemoryExecParamRepository(final String defaultParameterJsonString, final Logger logger) {
        this.defaultValueCollection = new JsonExecParamParser(defaultParameterJsonString,
                new ObjectMapper().readerFor(ExecParam.class), logger).parse();
        this.logger = logger;
    }

    /**
     * This method returns the {@code ExecParam} that matches the key passed as parameter
     *
     * @param key the key of the {@link ExecParam} that is supposed to be retrieved from the query
     * @return the {@link ExecParam} that matches the key passed as parameter
     */
    @Override
    public ExecParam getExecParamByKey(final String key) {
        return execParamCollection.get(key);
    }

    /**
     * This method returns the collection of {@link ExecParam} mapped on a key
     *
     * @return the collection of {@link ExecParam} mapped on a key
     */
    @Override
    public Map<String, ExecParam> getExecParamCollection() {
        return Collections.unmodifiableMap(execParamCollection);
    }

    /**
     * This method adds a new {@link ExecParam} to the repository if the key of said parameter is handled by the
     * repository. If the key is not recognized, throws an {@link IllegalArgumentException}. Finally, if the entity
     * is added to the repository, the methods returns the {@link ExecParam} passed as parameter.
     *
     * @param newExecParam the new entity to add to the repository
     * @return the {@link ExecParam} passed as parameter
     * @throws IllegalArgumentException if the key of entity passed as parameter is not handled by the repository
     */
    @Override
    public ExecParam addExecParam(final ExecParam newExecParam) throws IllegalArgumentException {
        if (defaultValueCollection.containsKey(newExecParam.getKey())) {
            execParamCollection.put(newExecParam.getKey(), newExecParam);
            return newExecParam;
        } else {
            throw new IllegalArgumentException("Execution parameter with key: " +
                    newExecParam.getKey() + " found in configuration file is not handled");
        }
    }

    /**
     * This method verifies if the repository contains an {@link ExecParam} with a specified key passed as parameter
     *
     * @param key the key of the {@link ExecParam} that is searched for
     * @return true if the repository contains an {@link ExecParam} with the same key as the one passed as
     * parameter, false if no {@link ExecParam} with matching key was found.
     */
    @Override
    public boolean hasExecParam(final String key) {
        return execParamCollection.containsKey(key);
    }

    /**
     * This method verifies if the repository contains an {@link ExecParam} with a specified key passed as parameter
     * and if said {@link ExecParam} has field paramValue not set to null.
     *
     * @param key the key of the {@link ExecParam} that is searched for
     * @return true if the repository contains an {@link ExecParam} with the same key as the one passed as
     * parameter with field paramValue not null
     * false if no {@link ExecParam} with matching key was found or if a {@link ExecParam} matching the key
     * passed as parameter has been found, but its field paramValue is set to null.
     */
    @Override
    public boolean hasExecParamValue(final String key) {
        return hasExecParam(key) && getExecParamByKey(key).getValue() != null;
    }

    /**
     * This method returns a parser for execution parameters based on a boolean passed as parameter. This methods
     * either returns {@code ApacheExecParamParser} if the boolean value is set to false, or returns
     * {@code JsonExecParamParser} if the boolean value is set to true.
     *
     * @param parameterJsonString the configuration .json file content to extract the execution parameters from.
     *                            If this parameter is null then, execution parameters are extracted from {@param args}.
     * @param args                the argument line to parse {@link ExecParam} when {@param parameterJsonString} is null
     * @return {@code ApacheExecParamParser} if the boolean passed as parameter is set to false,
     * or {@code JsonExecParamParser} if this boolean value is set to true.
     */
    @Override
    public ExecParamParser getParser(final String parameterJsonString,
                                     final String[] args,
                                     final Logger logger) {
        if (args.length != 0) {
            logger.info("Retrieving execution parameters from command-line");
            return new ApacheExecParamParser(new DefaultParser(), getOptions(), args);
        } else {
            logger.info("Retrieving execution parameters from execution-parameters.json file");
            return new JsonExecParamParser(parameterJsonString, new ObjectMapper().readerFor(ExecParam.class), logger);
        }
    }

    /**
     * This method constructs and returns path as string variables that correspond to a given key. This method is
     * used in the main execution method. Throws a {@code IllegalArgumentException} if the key passed as parameter is
     * not handled by the repository.
     * If the key is HELP_KEY or PROTO_KEY the method returns the paramValue associated to the key if present, else
     * the default paramValue.
     * In other cases, the method constructs paths according to conditions on the repository and the working directory.
     *
     * @param key the key associated to the path that is to be built
     * @return a working path associated to the key provided as parameter
     * @throws IllegalArgumentException if the key passed as parameter is not handled by the repository.
     */
    @Override
    public String getExecParamValue(final String key) throws IllegalArgumentException {
        final List<String> defaultValue = defaultValueCollection.get(key).getValue();

        switch (key) {

            case HELP_KEY:
            case PROTO_KEY: {
                if (hasExecParam(key)) {
                    return hasExecParam(key) ? String.valueOf(true) : defaultValue.get(0);
                } else {
                    return defaultValue.get(0);
                }
            }

            case EXTRACT_KEY:
            case OUTPUT_KEY: {
                return hasExecParamValue(key) && hasExecParamValue(key)
                        ? getExecParamByKey(key).getValue().get(0)
                        : System.getProperty("user.dir") + File.separator + defaultValue.get(0);
            }

            case URL_KEY: {
                return hasExecParamValue(key) ? getExecParamByKey(URL_KEY).getValue().get(0) : defaultValue.get(0);
            }

            case ZIP_KEY: {
                String zipInputPath = hasExecParamValue(ZIP_KEY)
                        ? getExecParamByKey(ZIP_KEY).getValue().get(0)
                        : System.getProperty("user.dir");

                if (!hasExecParamValue(URL_KEY) & !hasExecParamValue(ZIP_KEY)) {
                    logger.info("--url and relative path to zip file(--zip option) not provided. Trying to " +
                            "find zip in: " + zipInputPath);
                    List<String> zipList;
                    try {
                        zipList = Files.walk(Paths.get(zipInputPath))
                                .map(Path::toString)
                                .filter(f -> f.endsWith(".zip"))
                                .collect(Collectors.toUnmodifiableList());
                    } catch (IOException e) {
                        zipList = Collections.emptyList();
                    }

                    if (zipList.isEmpty()) {
                        logger.error("no zip file found - exiting");
                        System.exit(0);
                    } else if (zipList.size() > 1) {
                        logger.error("multiple zip files found - exiting");
                        System.exit(0);
                    } else {
                        logger.info("zip file found: " + zipList.get(0));
                        zipInputPath = zipList.get(0);
                    }
                } else if (!hasExecParamValue(ZIP_KEY)) {
                    zipInputPath += File.separator + "input.zip";
                }
                return zipInputPath;
            }

            case EXCLUSION_KEY: {
                return hasExecParamValue(EXCLUSION_KEY) ? getExecParamByKey(EXCLUSION_KEY).getValue().toString() : null;
            }
        }
        throw new IllegalArgumentException("Requested key is not handled");
    }

    /**
     * This method returns the collection of available {@code Option} as {@code Options}. This method is used to print
     * help when {@link ExecParam} with key HELP_KEY="help" is present in the repository.
     *
     * @return the collection of available {@code Option} as {@code Options} when {@link ExecParam} with key
     * HELP_KEY="help" is present in the repository.
     */
    @Override
    public Options getOptions() {
        final Options options = new Options();
        options.addOption(String.valueOf(URL_KEY.charAt(0)), URL_KEY, true,
                "URL to GTFS zipped archive");
        options.addOption(String.valueOf(ZIP_KEY.charAt(0)), ZIP_KEY, true,
                "if --url is used, where to place " +
                        "the downloaded archive. Otherwise, relative path pointing to a valid GTFS zipped archive on disk");
        options.addOption(String.valueOf(EXTRACT_KEY.charAt(0)), EXTRACT_KEY, true,
                "Relative path where to extract the zip content");
        options.addOption(String.valueOf(OUTPUT_KEY.charAt(0)), OUTPUT_KEY, true,
                "Relative path where to place output files");
        options.addOption(String.valueOf(HELP_KEY.charAt(0)), HELP_KEY, false, "Print this message");
        options.addOption(String.valueOf(PROTO_KEY.charAt(0)), PROTO_KEY, false,
                "Export validation results as proto");
        options.addOption(String.valueOf(EXCLUSION_KEY.charAt(1)), EXCLUSION_KEY, true,
                "Exclude files from semantic GTFS validation");
        // Commands --proto and --help take no arguments, contrary to command --exclude that can take multiple arguments
        // Other commands only take 1 argument
        options.getOptions().forEach(option -> {
            switch (option.getLongOpt()) {
                case ExecParamRepository.PROTO_KEY:
                case ExecParamRepository.HELP_KEY: {
                    option.setArgs(0);
                    break;
                }
                case EXCLUSION_KEY: {
                    option.setArgs(Option.UNLIMITED_VALUES);
                }
                default: {
                    option.setArgs(1);
                }
            }
        });
        return options;
    }

    /**
     * This method returns true if the repository is empty, else false
     *
     * @return true if the repository is empty, else false
     */
    @Override
    public boolean isEmpty() {
        return execParamCollection.isEmpty();
    }
}