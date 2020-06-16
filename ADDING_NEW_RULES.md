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

### III. Implement your new rule exportation code

1. Add a new definition at the bottom of the [`NoticeExporter` interface](https://github.com/MobilityData/gtfs-validator/blob/master/domain/src/main/java/org/mobilitydata/gtfsvalidator/domain/entity/notice/NoticeExporter.java)

2. Add the required code to the interface implementations in the [`exporter` package](https://github.com/MobilityData/gtfs-validator/tree/master/adapter/exporter/src/main/java/org/mobilitydata/gtfsvalidator/exporter) located in the `adapter` module

**Note**: JSON export required at a minimum. Leaving the protobuf implementation empty will not lead to your PR being rejected.
The mechanism to use the protocol buffer exporter is very similar. At export time, the Notice data must be mapped to a [GTFSProblem](https://github.com/google/transitfeed/blob/cc351b9542b5dd1c75fb570063f36ded3da2bfd7/misc/gtfs_validation.proto)

### IV. Implement exportation code unit tests

Add a new test implementation to the [`*ExporterTest` classes](https://github.com/MobilityData/gtfs-validator/tree/master/adapter/exporter/src/test/java/org/mobilitydata/gtfsvalidator/exporter)

### V. Implement your new rule in a specific use case

All rules in the validator are implemented in self-contained classes in the [`usecase` package](https://github.com/MobilityData/gtfs-validator/tree/master/usecase/src/main/java/org/mobilitydata/gtfsvalidator/usecase) located in its own `usecase` module

1. Add a class constructor which should take three finalized parameters

- A `GtfsDataRepository` from which the feed content can be pulled
- A `ValidationResultRepository` to which any warning or error encountered can be pushed
- A `Logger` through which execution progress can be reported

2. Implement the verification code in the `execute` method pushing instances of your new `Notice` class upon detecting issues.
Don't forget to log a meaningful message specifying what rule you are about to validate.


### VI. Implement use case unit tests

Add a Test class for your new use case in the [use case test module](https://github.com/MobilityData/gtfs-validator/tree/master/usecase/src/test/java/org/mobilitydata/gtfsvalidator/usecase)
Those should test for the following
- Typical execution on valid data
- Each case in which a Notice is built
- The actual content of generated notices
- Null safety regarding fields defined as optional in the GTFS specification

### VII. Add a way to instantiate, retrieve and execute the use case

Instantiation is done through the [`DefaultConfig` class](https://github.com/MobilityData/gtfs-validator/blob/master/config/src/main/java/org/mobilitydata/gtfsvalidator/config/DefaultConfig.java)

For the CLI app, retrieval and execution in the [`Main.java` class](https://github.com/MobilityData/gtfs-validator/blob/master/application/cli-app/src/main/java/org/mobilitydata/gtfsvalidator/Main.java)

