package org.mobilitydata.gtfsvalidator.outputcomparator.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {

  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  static void writeFile(JsonObject fileData, Path path) throws IOException {
    Path parentDir = path.getParent();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }
    Files.write(path, GSON.toJson(fileData).getBytes(StandardCharsets.UTF_8));
  }
}
