package org.mobilitydata.gtfsvalidator.processor.notices;

import static java.util.function.Predicate.not;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
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

    // The lines extracted and cleaned from a Javadoc notice comment should have the following
    // form at this point.
    //
    //    Initial short-summary text that possibly spans more
    //    than one line.
    //
    //    Optional additional descriptive text that possibly
    //    spans more than one line.  May have more than one
    //    sentence.
    //
    //    Even more additional descriptive text.

    Deque<String> remainingLines = new ArrayDeque<>(lines);
    // There shouldn't be any initial blank lines, but strip them just in case.
    consumeLines(remainingLines, String::isBlank);

    List<String> shortSummaryLines = consumeLines(remainingLines, not(String::isBlank));
    consumeLines(remainingLines, String::isBlank);

    if (!shortSummaryLines.isEmpty()) {
      comment.shortSummary = String.join(" ", shortSummaryLines);
    }
    if (!remainingLines.isEmpty()) {
      comment.additionalDocumentation = String.join("\n", remainingLines);
    }
    return comment;
  }

  private List<String> consumeLines(
      Deque<String> remainingLines, Predicate<String> matchCondition) {
    List<String> matching = new ArrayList<>();
    while (!remainingLines.isEmpty() && matchCondition.test(remainingLines.peekFirst())) {
      matching.add(remainingLines.pollFirst());
    }
    return matching;
  }

  public final class SplitComment {
    String shortSummary;

    String additionalDocumentation;
  }
}
