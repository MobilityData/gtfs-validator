package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeDocComments;
import org.mobilitydata.gtfsvalidator.notice.schema.NoticeSchemaGenerator;

@RunWith(JUnit4.class)
public class NoticeDocumentationTest {

  @Test
  public void testThatAllValidationNoticesAreDocumented() {
    List<Class<?>> noticesWithoutDocComment =
        discoverValidationNoticeClasses()
            .filter(
                clazz -> {
                  NoticeDocComments docComments = NoticeSchemaGenerator.loadComments(clazz);
                  return docComments.getShortSummary() == null;
                })
            .collect(Collectors.toList());
    assertWithMessage(
            "We expect all validation notices to have a documentation comment.  If "
                + "this test fails, it likely means that a Javadoc /** */ documentation header needs to "
                + "be added to the following classes. "
                + "See https://github.com/MobilityData/gtfs-validator/blob/master/docs/NEW_RULES.md#2-document-the-new-rule for more details.")
        .that(noticesWithoutDocComment)
        .isEmpty();
  }

  @Test
  public void testThatAllValidationNoticesAreDocumentedWithFirstLine() {
    List<Class<?>> noticesWithImproperMultilineDocComment =
        discoverValidationNoticeClasses()
            .filter(
                clazz -> {
                  NoticeDocComments docComments = NoticeSchemaGenerator.loadComments(clazz);
                  if (docComments.getShortSummary() == null) {
                    return false;
                  }
                  return docComments.getShortSummary().contains(". ");
                })
            .collect(Collectors.toList());
    assertWithMessage(
            "We expect all validation notices to have a documentation comment of the "
                + "following form:\n"
                + "\n"
                + "  Short single-sentence text describing the notice on a single line (required).\n"
                + "  \n"
                + "  Additional text further describing the notice with multiple additional sentences "
                + "on multiple lines(optional).\n"
                + "\n"
                + "See https://github.com/MobilityData/gtfs-validator/blob/master/docs/NEW_RULES.md#2-document-the-new-rule for more details.<br/>\n"
                + "\n"
                + "The following notice classes do not match that convention:")
        .that(noticesWithImproperMultilineDocComment)
        .isEmpty();
  }

  @Test
  public void testThatValidationNoticesDoNotUseUnsupportedJavadocSyntax() {
    List<String> noticesWithInvalidJavadoc =
        discoverValidationNoticeClasses()
            .flatMap(NoticeDocumentationTest::checkNoticeForUnsupportedJavadocInComment)
            .collect(Collectors.toList());
    assertWithMessage(
            "Validation notice documentation should use Markdown formatting instead "
                + "of Javadoc formatting, where appropriate.  If this test fails, it likely means that "
                + " a Javadoc /** */ documentation header needs to be updated for the following classes. "
                + "See https://github.com/MobilityData/gtfs-validator/blob/master/docs/NEW_RULES.md#2-document-the-new-rule for more details.")
        .that(noticesWithInvalidJavadoc)
        .isEmpty();
  }

  private static final ImmutableList<String> UNSUPPORTED_JAVADOC =
      ImmutableList.of("{@code", "{@link", "<li>", "<ul>", "<pre>");

  private static Stream<String> checkNoticeForUnsupportedJavadocInComment(Class<?> noticeClass) {
    NoticeDocComments docComments = NoticeSchemaGenerator.loadComments(noticeClass);
    String docComment = docComments.getCombinedDocumentation();

    List<String> errors = new ArrayList<>();

    if (docComment != null) {
      for (String line : docComment.split("\n")) {
        for (String unsupportedToken : UNSUPPORTED_JAVADOC) {
          if (line.contains(unsupportedToken)) {
            errors.add(noticeClass.getSimpleName() + ": " + unsupportedToken + ": " + line);
          }
        }
      }
    }
    return errors.stream();
  }

  @Test
  public void testThatNoticeFieldsAreDocumented() {
    List<String> fieldsWithoutComments =
        discoverValidationNoticeClasses()
            .flatMap(
                clazz -> {
                  NoticeDocComments docComments = NoticeSchemaGenerator.loadComments(clazz);
                  return Arrays.stream(clazz.getDeclaredFields())
                      .filter(f -> docComments.getFieldComment(f.getName()) == null);
                })
            .map(f -> f.getDeclaringClass().getSimpleName() + "." + f.getName())
            .collect(Collectors.toList());
    assertWithMessage(
            "Every field of a validation notice much be documented with a JavaDoc comment (aka /** */, not //).  The following fields are undocumented:")
        .that(fieldsWithoutComments)
        .isEmpty();
  }

  private static Stream<Class<Notice>> discoverValidationNoticeClasses() {
    return ClassGraphDiscovery.discoverNoticeSubclasses(ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
        .stream();
  }
}
