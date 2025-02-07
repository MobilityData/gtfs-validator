package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.processor.summary.SummaryDocCommentsFactory;
import org.mobilitydata.gtfsvalidator.reportSummary.JsonReportSummary;

@RunWith(JUnit4.class)
public class MetadataDocumentationTest {

  @Test
  public void testThatAllMetadataFieldsAreDocumented() throws IOException {
    // Parse the JSON file into a list of Metadata objects.
    Map<String, SummaryDocCommentsFactory.SummaryMetadata> metadataMap = getSummaryMetadataMap();

    // Get all declared field names from the summary class.
    List<String> summaryFieldNames =
        Arrays.stream(JsonReportSummary.class.getDeclaredFields())
            .map(
                field -> {
                  SerializedName annotation = field.getAnnotation(SerializedName.class);
                  return annotation != null ? annotation.value() : field.getName();
                })
            .collect(Collectors.toList());
    List<String> missingDocumentationFields =
        summaryFieldNames.stream()
            .filter(
                fieldName -> {
                  SummaryDocCommentsFactory.SummaryMetadata meta = metadataMap.get(fieldName);
                  // Field is considered undocumented if it is missing from the JSON
                  // or its description is null or empty.
                  return meta == null
                      || meta.description() == null
                      || meta.description().trim().isEmpty();
                })
            .collect(Collectors.toList());

    // Assert that all fields in the summary class have documentation.
    assertWithMessage(
            "The following fields in JsonReportSummary are missing documentation: "
                + missingDocumentationFields)
        .that(missingDocumentationFields)
        .isEmpty();

    // Unexpected fields in the JSON file.
    List<String> unexpectedFields =
        metadataMap.keySet().stream()
            .filter(fieldName -> !summaryFieldNames.contains(fieldName))
            .collect(Collectors.toList());
    assertWithMessage(
            "The following fields in the metadata file are not present in JsonReportSummary: "
                + unexpectedFields)
        .that(unexpectedFields)
        .isEmpty();
  }

  @Test
  public void testThatCustomSummaryObjectsAreDocumented() throws IOException {
    // Parse the JSON file into a map of metadata.
    Map<String, SummaryDocCommentsFactory.SummaryMetadata> metadataMap = getSummaryMetadataMap();
    List<String> missingDocumentation = new ArrayList<>();

    // For each field in JsonReportSummary, if its type is a custom type, verify nested
    // documentation.
    for (Field field : JsonReportSummary.class.getDeclaredFields()) {
      SerializedName annotation = field.getAnnotation(SerializedName.class);
      String fieldName = annotation != null ? annotation.value() : field.getName();
      Class<?> fieldType = field.getType();
      // Check if the field's type is defined in your package (i.e. is custom).
      if (fieldType.getName().startsWith("org.mobilitydata.gtfsvalidator")) {
        SummaryDocCommentsFactory.SummaryMetadata meta = metadataMap.get(fieldName);
        if (meta == null) {
          missingDocumentation.add(
              "Missing metadata for field " + fieldName + " of type " + fieldType.getName());
        } else {
          missingDocumentation.addAll(validateCustomTypeDocumentation(fieldType, meta));
        }
      }
    }

    assertWithMessage("Missing documentation for custom summary objects: " + missingDocumentation)
        .that(missingDocumentation)
        .isEmpty();
  }

  /**
   * Recursively validates that each declared field of a custom type is documented in the metadata.
   */
  private static List<String> validateCustomTypeDocumentation(
      Class<?> clazz, SummaryDocCommentsFactory.SummaryMetadata meta) {
    List<String> missingDocs = new ArrayList<>();

    // Iterate over each declared (non-static) field in the class.
    for (Field field : clazz.getDeclaredFields()) {
      // Skip static fields.
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      SerializedName annotation = field.getAnnotation(SerializedName.class);
      String expectedName = annotation != null ? annotation.value() : field.getName();

      // Look for matching nested metadata in meta.fields()
      SummaryDocCommentsFactory.SummaryMetadata nestedMeta = null;
      if (meta.fields() != null) {
        for (SummaryDocCommentsFactory.SummaryMetadata nm : meta.fields()) {
          if (expectedName.equals(nm.name())) {
            nestedMeta = nm;
            break;
          }
        }
      }
      if (nestedMeta == null) {
        missingDocs.add(
            "Missing metadata for nested field: " + expectedName + " in type " + clazz.getName());
      } else if (nestedMeta.description() == null || nestedMeta.description().trim().isEmpty()) {
        missingDocs.add(
            "Empty documentation for nested field: "
                + expectedName
                + " in type "
                + clazz.getName());
      }
      // If the nested field's type is custom, recursively validate its documentation.
      Class<?> nestedFieldType = field.getType();
      if (nestedFieldType.getName().startsWith("org.mobilitydata.gtfsvalidator")
          && nestedMeta != null) {
        missingDocs.addAll(validateCustomTypeDocumentation(nestedFieldType, nestedMeta));
      }
    }
    return missingDocs;
  }

  private static @NotNull Map<String, SummaryDocCommentsFactory.SummaryMetadata>
      getSummaryMetadataMap() throws IOException {
    String fileLocation = "web/client/static/summary-metadata.json";
    Path metadataPath = Paths.get(System.getProperty("user.dir")).getParent().resolve(fileLocation);
    // Verify that the file exists.
    assertWithMessage("Metadata file not found at " + fileLocation)
        .that(Files.exists(metadataPath))
        .isTrue();

    // Read the file content.
    String json = Files.readString(metadataPath, StandardCharsets.UTF_8);
    ObjectMapper mapper = new ObjectMapper();

    TypeReference<List<SummaryDocCommentsFactory.SummaryMetadata>> typeRef =
        new TypeReference<>() {};
    List<SummaryDocCommentsFactory.SummaryMetadata> metadataList = mapper.readValue(json, typeRef);

    // Build a lookup map from field name to its metadata.
    Map<String, SummaryDocCommentsFactory.SummaryMetadata> metadataMap = new HashMap<>();
    for (SummaryDocCommentsFactory.SummaryMetadata meta : metadataList) {
      metadataMap.put(meta.name(), meta);
    }

    return metadataMap;
  }
}
