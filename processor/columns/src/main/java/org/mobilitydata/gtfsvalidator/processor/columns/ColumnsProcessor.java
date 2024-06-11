package org.mobilitydata.gtfsvalidator.processor.columns;

import static javax.lang.model.util.ElementFilter.typesIn;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.mobilitydata.gtfsvalidator.annotation.ColumnStoreTypes;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;

/**
 * A processor for {@link GtfsValidationNotice}-annotated notices that extracts source-file comments
 * from the Java source and stores them as JSON-serialized {@link NoticeDocComments} resources in
 * for run-time access later by the validator.
 */
@AutoService(Processor.class)
public class ColumnsProcessor extends AbstractProcessor {

  private ColumnStoreGenerator generator;

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(ColumnStoreTypes.class.getName());
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    generator = new ColumnStoreGenerator();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement element : typesIn(roundEnv.getElementsAnnotatedWith(ColumnStoreTypes.class))) {
      PackageElement pkg = processingEnv.getElementUtils().getPackageOf(element);
      TypeSpec typeSpec = generator.generate(element);
      writeJavaFile(JavaFile.builder(pkg.toString(), typeSpec).build());
    }
    return false;
  }

  private void writeJavaFile(JavaFile javaFile) {
    try {
      javaFile.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              String.format("failed to generate output file: %s", e.getMessage()));
    }
  }
}
