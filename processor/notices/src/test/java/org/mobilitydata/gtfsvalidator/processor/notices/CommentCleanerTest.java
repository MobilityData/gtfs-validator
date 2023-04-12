package org.mobilitydata.gtfsvalidator.processor.notices;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class CommentCleanerTest {

  private CommentCleaner cleaner = new CommentCleaner();

  @Test
  public void testCleanComment_noModification() {
    assertThat(cleaner.cleanComment("Comment.")).isEqualTo("Comment.");
  }

  @Test
  public void testCleanComment_removeTrailingWhitespace() {
    assertThat(cleaner.cleanComment("Comment.  ")).isEqualTo("Comment.");
  }

  @Test
  public void testCleanComment_removeConsistentLeadingWhitespace() {
    assertThat(cleaner.cleanComment("  Two spaces.\n   Three spaces.\n  Two spaces.  "))
        .isEqualTo("Two spaces.\n Three spaces.\nTwo spaces.");
  }

  @Test
  public void testCleanComment_removeJavadocParagraphTags() {
    assertThat(cleaner.cleanComment("Line 1.\n\n<p>Line 2.\n\n<p>Line 3."))
        .isEqualTo("Line 1.\n\nLine 2.\n\nLine 3.");
  }

  @Test
  public void testCleanComment_removeSeverityComment() {
    assertThat(cleaner.cleanComment("Line 1.\n\n<p>Severity: {@code SeverityLevel.ERROR}"))
        .isEqualTo("Line 1.");
  }

  @Test
  public void testCleanComment_what() {
    assertThat(
            cleaner.cleanComment(
                " Describes two trips with the same block id that have overlapping stop times.\n"
                    + "\n"
                    + " \u003cp\u003eSeverity: {@code SeverityLevel.ERROR}\n"))
        .isEqualTo("Describes two trips with the same block id that have overlapping stop times.");
  }
}
