package org.mobilitydata.gtfsvalidator.processor.summary;

import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.mobilitydata.gtfsvalidator.annotation.GtfsReportSummary;

/**
 * A processor for {@link }-annotated notices that extracts source-file comments from the Java
 * source and stores them as JSON-serialized {@link } resources in for run-time access later by the
 * validator.
 */
@AutoService(Processor.class)
public class SummaryProcessor extends AbstractProcessor {

  private SummaryDocCommentsFactory docCommentsFactory;

  private final Gson GSON = new GsonBuilder().create();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(GtfsReportSummary.class.getName());
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    docCommentsFactory =
        new SummaryDocCommentsFactory(
            processingEnv.getElementUtils(), processingEnv.getTypeUtils());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedOptions() {
    return Collections.singleton("summaryMetadataOutputDir");
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // Retrieve the output directory provided via the processor option "summaryMetadataOutputDir"
    String outputDir = processingEnv.getOptions().get("summaryMetadataOutputDir");

    // Get all elements annotated with @GtfsReportSummary and cast them to TypeElement
    Set<TypeElement> gtfsReportSummaryElements =
        typesIn(roundEnv.getElementsAnnotatedWith(GtfsReportSummary.class));

    // If no summary element is found, return false.
    // TODO: Add test to ensure this is not reached.
    if (gtfsReportSummaryElements.isEmpty()) {
      return false;
    }

    // There should be exactly one @GtfsReportSummary annotation; multiple annotations would cause
    // ambiguity.
    // TODO: Add test to ensure this is not reached.
    if (gtfsReportSummaryElements.size() > 1) {
      return false;
    }

    // Retrieve the single summary report element
    TypeElement reportSummaryElement = gtfsReportSummaryElements.iterator().next();

    // Collect documentation for each field in the summary report class
    List<SummaryDocCommentsFactory.SummaryMetadata> fields = new ArrayList<>();
    for (VariableElement field : fieldsIn(reportSummaryElement.getEnclosedElements())) {
      Optional<SummaryDocCommentsFactory.SummaryMetadata> documentation =
          docCommentsFactory.create(field);
      if (documentation.isEmpty()) {
        continue;
      }
      fields.add(documentation.get());
    }

    // Write the collected metadata to a JSON file.
    try {
      if (outputDir != null && !outputDir.trim().isEmpty()) {
        // If an output directory is provided, write the file there using standard file I/O.
        Path outputPath = Paths.get(outputDir, "summary-metadata.json");
        // Ensure that the target directory exists
        Files.createDirectories(outputPath.getParent());
        try (Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
          GSON.toJson(fields, writer);
        }
      } else {
        // Fallback: Write the file using the Filer API to the CLASS_OUTPUT location.
        FileObject resource =
            processingEnv
                .getFiler()
                .createResource(
                    StandardLocation.CLASS_OUTPUT,
                    processingEnv
                        .getElementUtils()
                        .getPackageOf(reportSummaryElement)
                        .getQualifiedName()
                        .toString(),
                    "summary-metadata.json",
                    reportSummaryElement);
        try (Writer writer = resource.openWriter()) {
          GSON.toJson(fields, writer);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Return false to allow other processors to process these annotations if needed.
    return false;
  }
}
