# Release instructions

We create releases from the command-line using the [shadow Gradle plugin](https://github.com/johnrengelman/shadow).

Usage:

```
/.gradlew shadowJar
```

...which will output a file to `\cli-app\build\libs` such as `gtfs-validator-1.1.0-SNAPSHOT-all.jar`.

This file can then be run from the command-line with the normal Java conventions:

```
java -jar gtfs-validator-1.1.0-SNAPSHOT-all.jar -u https://transitfeeds.com/p/mbta/64/latest/download -z input.zip -i input -o output
```


