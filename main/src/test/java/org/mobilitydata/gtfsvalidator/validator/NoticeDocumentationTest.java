package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.Notice;

@RunWith(JUnit4.class)
public class NoticeDocumentationTest {
  private static Pattern MARKDOWN_NOTICE_SIMPLE_CLASS_NAME_ANCHOR_PATTERN =
      Pattern.compile("^<a name=\"(\\w+(Error|Notice))\"/>");

  private static Pattern MARKDOWN_NOTICE_CODE_HEADER_PATTERN =
      Pattern.compile("^### (([a-z0-9]+_)*[a-z0-9]+)");

  /**
   * If this test is failing, it likely means you need to update RULES.md in the project root
   * directory to include an entry for a new notice.
   */
  @Test
  public void testThatRulesMarkdownContainsAnchorsForAllValidationNotices() throws IOException {
    Set<String> fromMarkdown = readNoticeSimpleClassNamesFromRulesMarkdown();
    Set<String> fromSource =
        discoverValidationNoticeClasses().map(Class::getSimpleName).collect(Collectors.toSet());

    assertThat(fromMarkdown).isEqualTo(fromSource);
  }

  /**
   * If this test is failing, it likely means you need to update RULES.md in the project root
   * directory to include an entry for a new notice.
   */
  @Test
  public void testThatRulesMarkdownContainsHeadersForAllValidationNotices() throws IOException {
    Set<String> fromMarkdown = readNoticeCodesFromRulesMarkdown();
    Set<String> fromSource =
        discoverValidationNoticeClasses().map(Notice::getCode).collect(Collectors.toSet());

    assertThat(fromMarkdown).isEqualTo(fromSource);
  }

  private static Stream<Class<Notice>> discoverValidationNoticeClasses() {
    return ClassGraphDiscovery.discoverNoticeSubclasses(ClassGraphDiscovery.DEFAULT_NOTICE_PACKAGES)
        .stream();
  }

  private static Set<String> readNoticeSimpleClassNamesFromRulesMarkdown() throws IOException {
    return readValuesFromRulesMarkdown(MARKDOWN_NOTICE_SIMPLE_CLASS_NAME_ANCHOR_PATTERN);
  }

  private static Set<String> readNoticeCodesFromRulesMarkdown() throws IOException {
    return readValuesFromRulesMarkdown(MARKDOWN_NOTICE_CODE_HEADER_PATTERN);
  }

  private static Set<String> readValuesFromRulesMarkdown(Pattern pattern) throws IOException {
    // RULES.md is copied into the main/build/resources/test resource directory by a custom copy
    // rule in the main/build.gradle file.
    try (InputStream in = NoticeDocumentationTest.class.getResourceAsStream("/RULES.md")) {
      // Scan lines from the markdown file, find those that match our regex pattern, and pull out
      // the matching group.
      return new BufferedReader(new InputStreamReader(in))
          .lines()
          .map(line -> maybeMatchAndExtract(pattern, line))
          .flatMap(Optional::stream)
          .collect(Collectors.toSet());
    }
  }

  private static Optional<String> maybeMatchAndExtract(Pattern p, String line) {
    Matcher m = p.matcher(line);
    if (m.matches()) {
      return Optional.of(m.group(1));
    } else {
      return Optional.empty();
    }
  }
}
