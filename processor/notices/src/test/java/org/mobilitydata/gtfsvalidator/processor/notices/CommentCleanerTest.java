package org.mobilitydata.gtfsvalidator.processor.notices;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.processor.notices.CommentCleaner.SplitComment;

public class CommentCleanerTest {

  private CommentCleaner cleaner = new CommentCleaner();

  @Test
  public void testCleanComment_noModification() {
    assertThat(cleaner.cleanComment("Comment.")).containsExactly("Comment.");
  }

  @Test
  public void testCleanComment_removeTrailingWhitespace() {
    assertThat(cleaner.cleanComment("Comment.  ")).containsExactly("Comment.");
  }

  @Test
  public void testCleanComment_removeConsistentLeadingWhitespace() {
    assertThat(cleaner.cleanComment("  Two spaces.\n   Three spaces.\n  Two spaces.  "))
        .containsExactly("Two spaces.", " Three spaces.", "Two spaces.");
  }

  @Test
  public void testCleanComment_removeJavadocParagraphTags() {
    assertThat(cleaner.cleanComment("Line 1.\n\n<p>Line 2.\n\n<p>Line 3."))
        .containsExactly("Line 1.", "", "Line 2.", "", "Line 3.");
  }

  @Test
  public void testCleanComment_removeSeverityComment() {
    assertThat(cleaner.cleanComment("Line 1.\n\n<p>Severity: {@code SeverityLevel.ERROR}"))
        .containsExactly("Line 1.");
  }

  @Test
  public void testCleanComment_preBlock() {
    assertThat(cleaner.cleanComment("Line 1.\n\n<pre>\n```\nverbatim\n```\n</pre>"))
        .containsExactly("Line 1.", "", "```", "verbatim", "```");
  }

  @Test
  public void testCleanComment_removeSeverityTag() {
    assertThat(
            cleaner.cleanComment(
                " Describes two trips with the same block id that have overlapping stop times.\n"
                    + "\n"
                    + " \u003cp\u003eSeverity: {@code SeverityLevel.ERROR}\n"))
        .containsExactly(
            "Describes two trips with the same block id that have overlapping stop times.");
  }

  @Test
  public void testSplitLinesIntoSummaryAndAdditionalDocumentation_summaryOnly() {
    SplitComment split =
        cleaner.splitLinesIntoSummaryAndAdditionalDocumentation(ImmutableList.of("A."));

    assertThat(split.shortSummary).isEqualTo("A.");
    assertThat(split.additionalDocumentation).isNull();
  }

  @Test
  public void testSplitLinesIntoSummaryAndAdditionalDocumentation_bothPresent() {
    SplitComment split =
        cleaner.splitLinesIntoSummaryAndAdditionalDocumentation(
            ImmutableList.of("A.", "", "B.", "C."));

    assertThat(split.shortSummary).isEqualTo("A.");
    assertThat(split.additionalDocumentation).isEqualTo("B.\nC.");
  }

  @Test
  public void testSplitLinesIntoSummaryAndAdditionalDocumentation_multipleBlankLines() {
    SplitComment split =
        cleaner.splitLinesIntoSummaryAndAdditionalDocumentation(
            ImmutableList.of("A.", "", "", "B."));

    assertThat(split.shortSummary).isEqualTo("A.");
    assertThat(split.additionalDocumentation).isEqualTo("B.");
  }

  @Test
  public void testSplitLinesIntoSummaryAndAdditionalDocumentation_noBlankLines() {
    SplitComment split =
        cleaner.splitLinesIntoSummaryAndAdditionalDocumentation(ImmutableList.of("A.", "B."));

    assertThat(split.shortSummary).isEqualTo("A.");
    assertThat(split.additionalDocumentation).isEqualTo("B.");
  }
}
