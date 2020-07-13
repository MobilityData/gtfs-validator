# Adding new rules

We will want to add new rules to this validator as the [GTFS spec](https://github.com/google/transit/tree/master/gtfs) and the surrounding applications and tools change.  This page outlines the process of adding new rules to this tool.

### I. Prepare for implementation 
1. Check the list of [currently implemented rules](RULES.md) to make sure the rule doesn't already exist.
2. Check the list of [planned future rules](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aissue+is%3Aopen+label%3A%22new+rule%22) to see if an issue already exists for the proposed rule.
    * If no existing issue exists, open a new issue with the ["new rule" label](https://github.com/MobilityData/gtfs-validator/issues?q=is%3Aissue+is%3Aopen+label%3A%22new+rule%22).
3. Discuss the rule with the community via the Github issue and come to a general consensus on the exact logic, and if it should be an `ERROR` or `WARNING`.  Generally, errors are behavior that directly violate the GTFS documentation.  Warnings are behavior that is not advised (e.g., against best practices) but not explicitly forbidden in the GTSF documentation.
4. Implement new rule using the process below

For the below example, let's look at implementing a new rule that verify that entries in calendar.txt does not have end_date before start_date

If you want to take a look at a complete set of changes that implement this new rule before diving into the instructions, see [this commit on Github](https://github.com/MobilityData/gtfs-validator/commit/1cd810295a3292afd829cc2d16bc6b7a39fe36ed).

### II. Add the rule to [`RULES.md`](RULES.md)

1. Add the rule to the error or warnings table at the top of [`RULES.md`](RULES.md):

~~~
| [E032](#E032) | `calendar.txt` `end_date` is before `start_date` |
~~~

2. Add a definition of that rule at the bottom of the errors or warnings section:

~~~
<a name="E032"/>

### E032 - `calendar.txt` `end_date` is before `start_date`

In `calendar.txt`, the `end_date` of a service record must not be earlier than the `start_date`.

#### References:
* [calendar.txt specification](https://gtfs.org/reference/static/#calendartxt)
~~~

### III. Add a new `*Notice.java` class for the new rule output

All classes that implements rules output are under the [`notice` package](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice) of the domain layer.
They must extend either `ErrorNotice` or `WarningNotice` and fit the `*Notice` format.

1. Add the error code definition to either `ErrorNotice` or `WarningNotice` class located in the [`base`](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/base) package:

    `protected static final String E_013 = 13;`

2. Add your new class in the [`error`](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/error) or [`warning`](https://github.com/MobilityData/gtfs-validator/tree/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/warning) package accordingly

3. Override and define an `export` method:
~~~
@Override
public void export(final NoticeExporter exporter) throws IOException {
    exporter.export(this);
}
~~~
Note: you will have a compiler warning "Cannot resolve method" until you complete step III

4. Implement your own constructor, calling `super` and passing it `filename`, `code`, `title`, `description` and `entityId`

Optional: if your Notice has specific data, you have to do the following for each piece of data:

5. Add a key in the form of a static String in [Notice.java](https://github.com/MobilityData/gtfs-validator/blob/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/base/Notice.java)
6. In your class constructor, call the method `putNoticeSpecific`, passing it as parameters the newly defined key and the data

Here an example with [CannotParseDateNotice](https://github.com/MobilityData/gtfs-validator/blob/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/error/CannotParseDateNotice.java) 

~~~
public class CannotParseDateNotice extends ErrorNotice {

    public CannotParseDateNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, E_017,
                "Invalid date value",
                "Value: '" + rawValue + "' of field: " + fieldName +
                        " with type date can't be parsed in file: " + filename + " at row: " + lineNumber,
                null);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_LINE_NUMBER, lineNumber);
        putNoticeSpecific(KEY_RAW_VALUE, rawValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
~~~

### III - a. Configure numeric ranges

Ranges for validation of numeric fields can be configured via command line arguments or within file `execution-parameter.json`.
To do so, two classes have to be modified: [ExecParamRepository](https://github.com/MobilityData/gtfs-validator/blob/master/usecase/src/main/java/org/mobilitydata/gtfsvalidator/usecase/port/ExecParamRepository.java) and [InMemoryExecParamRepository](https://github.com/MobilityData/gtfs-validator/blob/master/adapter/repository/in-memory-simple/src/main/java/org/mobilitydata/gtfsvalidator/db/InMemoryExecParamRepository.java).

1. Add a key in the form of a static String in [ExecParamRepository](https://github.com/MobilityData/gtfs-validator/blob/master/usecase/src/main/java/org/mobilitydata/gtfsvalidator/usecase/port/ExecParamRepository.java)

Here an example:

~~~
public interface ExecParamRepository {
    String HELP_KEY = "help";
    String EXTRACT_KEY = "extract";
    String OUTPUT_KEY = "output";
    String PROTO_KEY = "proto";
    String URL_KEY = "url";
    String INPUT_KEY = "input";
    String EXCLUSION_KEY = "exclude";
    String TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN = "transferMinTransferTimeRangeMin";
    String TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX = "transferMinTransferTimeRangeMax";

    ExecParam getExecParamByKey(final String optionName);

    Map<String, ExecParam> getExecParamCollection();

    ExecParam addExecParam(final ExecParam newExecParam) throws IllegalArgumentException;

    boolean hasExecParam(final String key);

    boolean hasExecParamValue(final String key);

    ExecParamParser getParser(final String parameterJsonString, final String[] args, final Logger logger);

    String getExecParamValue(final String key) throws IllegalArgumentException;

    Options getOptions();

    boolean isEmpty();

    interface ExecParamParser {

        Map<String, ExecParam> parse() throws IOException;
    }
}
~~~

2. Add a command line option in [InMemoryExecParamRepository](https://github.com/MobilityData/gtfs-validator/blob/master/adapter/repository/in-memory-simple/src/main/java/org/mobilitydata/gtfsvalidator/db/InMemoryExecParamRepository.java)

Here an example:
~~~
public class InMemoryExecParamRepository implements ExecParamRepository {
    private final Map<String, ExecParam> execParamCollection = new HashMap<>();
    private final Map<String, ExecParam> defaultValueCollection;
    private final Logger logger;

    public InMemoryExecParamRepository(final String defaultParameterJsonString, final Logger logger) {
        this.defaultValueCollection = new JsonExecParamParser(defaultParameterJsonString,
                new ObjectMapper().readerFor(ExecParam.class), logger).parse();
        this.logger = logger;
    }

    @Override
    public ExecParam getExecParamByKey(final String key) {
        return execParamCollection.get(key);
    }

    @Override
    public Map<String, ExecParam> getExecParamCollection() {
        return Collections.unmodifiableMap(execParamCollection);
    }

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

    @Override
    public boolean hasExecParam(final String key) {
        return execParamCollection.containsKey(key);
    }

    @Override
    public boolean hasExecParamValue(final String key) {
        return hasExecParam(key) && getExecParamByKey(key).getValue() != null;
    }

    @Override
    public ExecParamParser getParser(final String parameterJsonString,
                                     final String[] args,
                                     final Logger logger) {
        if (Strings.isNullOrEmpty(parameterJsonString) && args.length == 0) {
            // true when json configuration file is not present and no arguments are provided
            logger.info("No configuration file nor arguments provided" + System.lineSeparator());
            return new JsonExecParamParser(parameterJsonString, new ObjectMapper().readerFor(ExecParam.class), logger);
        } else if (!Strings.isNullOrEmpty(parameterJsonString) || args.length == 0) {
            // true when no arguments are provided or when json configuration is provided
            logger.info("Retrieving execution parameters from execution-parameters.json file" + System.lineSeparator());
            return new JsonExecParamParser(parameterJsonString, new ObjectMapper().readerFor(ExecParam.class), logger);
        } else {
            // true when only arguments are provided
            logger.info("Retrieving execution parameters from command-line" + System.lineSeparator());
            return new ApacheExecParamParser(new DefaultParser(), getOptions(), args);
        }
    }

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

            case INPUT_KEY: {
                String zipInputPath = hasExecParamValue(INPUT_KEY)
                        ? getExecParamByKey(INPUT_KEY).getValue().get(0)
                        : System.getProperty("user.dir");

                if (!hasExecParamValue(URL_KEY) & !hasExecParamValue(INPUT_KEY)) {
                    logger.info("--url and relative path to zip file(--zip option) not provided. Trying to " +
                            "find zip in: " + zipInputPath + System.lineSeparator());
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
                        logger.error("no zip file found - exiting" + System.lineSeparator());
                        System.exit(0);
                    } else if (zipList.size() > 1) {
                        logger.error("multiple zip files found - exiting" + System.lineSeparator());
                        System.exit(0);
                    } else {
                        logger.info("zip file found: " + zipList.get(0) + System.lineSeparator());
                        zipInputPath = zipList.get(0);
                    }
                } else if (!hasExecParamValue(INPUT_KEY)) {
                    zipInputPath += File.separator + "input.zip";
                }
                return zipInputPath;
            }

            case EXCLUSION_KEY: {
                return hasExecParamValue(EXCLUSION_KEY) ? getExecParamByKey(EXCLUSION_KEY).getValue().toString() : null;
            }

            case TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN :
            case TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX : {
                return hasExecParamValue(key) ? getExecParamByKey(key).getValue().toString() : defaultValue.get(0);
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
        options.addOption(String.valueOf(INPUT_KEY.charAt(0)), INPUT_KEY, true,
                "if --url is used, where to place " +
                        "the downloaded archive. Otherwise, relative path pointing to a valid GTFS zipped archive on disk");
        options.addOption(String.valueOf(EXTRACT_KEY.charAt(0)), EXTRACT_KEY, true,
                "Relative path where to extract the zip content");
        options.addOption(String.valueOf(OUTPUT_KEY.charAt(0)), OUTPUT_KEY, true,
                "Relative path where to place output files");
        options.addOption(String.valueOf(HELP_KEY.charAt(0)), HELP_KEY, false, "Print this message");
        options.addOption(String.valueOf(PROTO_KEY.charAt(0)), PROTO_KEY, false,
                "Export validation results as proto");

        // define options related to GTFS file `pathways.txt`
        options.addOption(String.valueOf(EXCLUSION_KEY.charAt(1)), EXCLUSION_KEY, true,
                "Exclude files from semantic GTFS validation");
        options.addOption(TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN, TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN, true,
                "Minimum allowed value for field min_transfer_time of file transfers.txt");
        options.addOption(TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX, TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX, true,
                "Maximum allowed value for field min_transfer_time of file transfers.txt");

        // Commands --proto and --help take no arguments, contrary to command --exclude that can take multiple arguments
        // Other commands only take 1 argument
        options.getOptions().forEach(option -> {
            switch (option.getLongOpt()) {
                case ExecParamRepository.PROTO_KEY:
                case ExecParamRepository.HELP_KEY: {
                    option.setArgs(0);
                    break;
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
~~~

3. Define default values for said ranges in [default-execution-parameters.json](https://github.com/MobilityData/gtfs-validator/blob/master/config/src/main/resources/default-execution-parameters.json).

Here an example:

```
{
  "help": false,
  "extract": "input",
  "output": "output",
  "proto": false,
  "url": null,
  "input": null,
  "exclude": null,
  "transferMinTransferTimeRangeMin": 0,
  "transferMinTransferTimeRangeMax": 86400
}
```

4. Configure method `getExecParamValue` of [InMemoryExecParamRepository](https://github.com/MobilityData/gtfs-validator/blob/master/adapter/repository/in-memory-simple/src/main/java/org/mobilitydata/gtfsvalidator/db/InMemoryExecParamRepository.java) class to retrieve value of newly added option

~~~
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

            case INPUT_KEY: {
                String zipInputPath = hasExecParamValue(INPUT_KEY)
                        ? getExecParamByKey(INPUT_KEY).getValue().get(0)
                        : System.getProperty("user.dir");

                if (!hasExecParamValue(URL_KEY) & !hasExecParamValue(INPUT_KEY)) {
                    logger.info("--url and relative path to zip file(--zip option) not provided. Trying to " +
                            "find zip in: " + zipInputPath + System.lineSeparator());
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
                        logger.error("no zip file found - exiting" + System.lineSeparator());
                        System.exit(0);
                    } else if (zipList.size() > 1) {
                        logger.error("multiple zip files found - exiting" + System.lineSeparator());
                        System.exit(0);
                    } else {
                        logger.info("zip file found: " + zipList.get(0) + System.lineSeparator());
                        zipInputPath = zipList.get(0);
                    }
                } else if (!hasExecParamValue(INPUT_KEY)) {
                    zipInputPath += File.separator + "input.zip";
                }
                return zipInputPath;
            }

            case EXCLUSION_KEY: {
                return hasExecParamValue(EXCLUSION_KEY) ? getExecParamByKey(EXCLUSION_KEY).getValue().toString() : null;
            }

            case TRANSFER_MIN_TRANSFER_TIME_RANGE_MIN :
            case TRANSFER_MIN_TRANSFER_TIME_RANGE_MAX : {
                return hasExecParamValue(key) ? getExecParamByKey(key).getValue().toString() : defaultValue.get(0);
            }
        }
        throw new IllegalArgumentException("Requested key is not handled");
    }
~~~

### IV. Implement your new rule exportation code

1. Add a new definition at the bottom of the [`NoticeExporter` interface](https://github.com/MobilityData/gtfs-validator/blob/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/NoticeExporter.java)

2. Add the required code to the interface implementations in the [`exporter` package](https://github.com/MobilityData/gtfs-validator/tree/master/adapter/exporter/src/main/java/org/mobilitydata/gtfsvalidator/exporter) located in the `adapter` module

**Note**: JSON export required at a minimum. Leaving the protobuf implementation empty will not lead to your PR being rejected.
The mechanism to use the protocol buffer exporter is very similar. At export time, the Notice data must be mapped to a [GTFSProblem](https://github.com/google/transitfeed/blob/cc351b9542b5dd1c75fb570063f36ded3da2bfd7/misc/gtfs_validation.proto)

### V. Implement exportation code unit tests

Add a new test implementation to the [`*ExporterTest` classes](https://github.com/MobilityData/gtfs-validator/tree/master/adapter/exporter/src/test/java/org/mobilitydata/gtfsvalidator/exporter)

### VI. Implement your new rule in a specific use case

All rules in the validator are implemented in self-contained classes in the [`usecase` package](https://github.com/MobilityData/gtfs-validator/tree/master/usecase/src/main/java/org/mobilitydata/gtfsvalidator/usecase) located in its own `usecase` module

1. Add a class constructor which should take three finalized parameters

- A `GtfsDataRepository` from which the feed content can be pulled
- A `ValidationResultRepository` to which any warning or error encountered can be pushed
- A `Logger` through which execution progress can be reported

2. Implement the verification code in the `execute` method pushing instances of your new `Notice` class upon detecting issues.
Don't forget to log a meaningful message specifying what rule you are about to validate.


### VII. Implement use case unit tests

Add a Test class for your new use case in the [use case test module](https://github.com/MobilityData/gtfs-validator/tree/master/usecase/src/test/java/org/mobilitydata/gtfsvalidator/usecase)
Those should test for the following
- Typical execution on valid data
- Each case in which a Notice is built
- The actual content of generated notices
- Null safety regarding fields defined as optional in the GTFS specification

### VIII. Add a way to instantiate, retrieve and execute the use case

Instantiation is done through the [`DefaultConfig` class](https://github.com/MobilityData/gtfs-validator/blob/master/config/src/main/java/org/mobilitydata/gtfsvalidator/config/DefaultConfig.java)

For the CLI app, retrieval and execution in the [`Main.java` class](https://github.com/MobilityData/gtfs-validator/blob/master/application/cli-app/src/main/java/org/mobilitydata/gtfsvalidator/Main.java)

