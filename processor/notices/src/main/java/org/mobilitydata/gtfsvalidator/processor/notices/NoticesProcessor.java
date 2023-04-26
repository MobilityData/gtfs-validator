package org.mobilitydata.gtfsvalidator.processor.notices;

import static javax.lang.model.util.ElementFilter.typesIn;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;

/**
 * A processor for {@link GtfsValidationNotice}-annotated notices that extracts source-file comments
 * from the Java source and stores them as JSON-serialized {@link NoticeDocComments} resources in
 * for run-time access later by the validator.
 */
@AutoService(Processor.class)
public class NoticesProcessor extends AbstractProcessor {

  private NoticeDocCommentsFactory docCommentsFactory;

  private final Gson GSON = new GsonBuilder().create();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(GtfsValidationNotice.class.getName());
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    docCommentsFactory = new NoticeDocCommentsFactory(processingEnv.getElementUtils());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement element :
        typesIn(roundEnv.getElementsAnnotatedWith(GtfsValidationNotice.class))) {
      Optional<NoticeDocComments> comments = docCommentsFactory.create(element);
      if (comments.isEmpty()) {
        continue;
      }

      PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
      String resourceName = NoticeDocComments.getResourceNameForTypeElement(element);

      try {
        FileObject resource =
            processingEnv
                .getFiler()
                .createResource(
                    StandardLocation.CLASS_OUTPUT,
                    packageElement.getQualifiedName(),
                    resourceName,
                    element);
        try (Writer writer = resource.openWriter()) {
          GSON.toJson(comments.get(), writer);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }
}
