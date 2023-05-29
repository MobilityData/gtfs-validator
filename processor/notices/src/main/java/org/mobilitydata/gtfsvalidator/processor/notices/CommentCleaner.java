package org.mobilitydata.gtfsvalidator.processor.notices;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * Provides methods to clean up Javadoc comments to get them into a sanitized format suitable for
 * output.
 */
public class CommentCleaner {

  /**
   * Some Javadoc elements like <pre> are used to escape Markdown content that would otherwise be
   * mangled by the Javadoc formatter.  We strip these elements.
   */
  private static final ImmutableSet<String> LINES_TO_DROP = ImmutableSet.of("<pre>", "</pre>");

  /** Returns the cleaned comment split into individual lines. */
  public List<String> cleanComment(String docComment) {
    if (docComment.isEmpty()) {
      return ImmutableList.of();
    }

    List<String> lines = Arrays.asList(docComment.split("\n"));

    // Extracted Javadoc comments will have leading whitespace on non-empty lines.  We don't
    // want to strip all leading whitespace (some might actually be part of the comment itself)
    // so instead we look for the minimal amount of whitespace that all non-empty lines have and
    // strip that amount.
    OptionalInt leadingSpaces =
        lines.stream()
            .filter(s -> !s.isEmpty())
            .mapToInt(CommentCleaner::numberOfLeadingSpaces)
            .min();
    if (leadingSpaces.isPresent()) {
      lines =
          lines.stream()
              .map(s -> s.isEmpty() ? s : s.substring(leadingSpaces.getAsInt()))
              .collect(Collectors.toList());
    }

    lines =
        lines.stream()
            // Comments like /** Blah */ will have trailing whitespace.
            .map(String::stripTrailing)
            // Strip out any Javadoc paragraph tags.
            .map(s -> s.replaceFirst("<p>", ""))
            // Filter out formatting lines that shouldn't be included in the output.
            .filter(s -> !LINES_TO_DROP.contains(s))
            // Drop the SeverityLevel comment, if it exists.
            .filter(s -> !s.startsWith("Severity: {@code SeverityLevel"))
            .collect(Collectors.toCollection(ArrayList::new));

    // After we stripped out the Severity comment, there may be trailing empty lines.  We remove
    // them here.
    while (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
      lines.remove(lines.size() - 1);
    }

    return lines;
  }

  /** Returns the number of leading whitespace characters at the beginning of the line. */
  private static int numberOfLeadingSpaces(String line) {
    int i = 0;
    while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
      i++;
    }
    return i;
  }

  public SplitComment splitLinesIntoSummaryAndAdditionalDocumentation(List<String> lines) {
    SplitComment comment = new SplitComment();
    if (lines.isEmpty()) {
      return comment;
    }
    comment.shortSummary = lines.get(0);
    int fromIndex = 1;
    while (fromIndex < lines.size() && lines.get(fromIndex).isBlank()) {
      fromIndex++;
    }
    if (fromIndex < lines.size()) {
      comment.additionalDocumentation =
          lines.subList(fromIndex, lines.size()).stream().collect(Collectors.joining("\n"));
    }
    return comment;
  }

  public final class SplitComment {
    String shortSummary;

    String additionalDocumentation;
  }
}
