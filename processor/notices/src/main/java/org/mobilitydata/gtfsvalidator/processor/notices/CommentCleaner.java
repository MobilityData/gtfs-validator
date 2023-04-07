package org.mobilitydata.gtfsvalidator.processor.notices;

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
  public String cleanComment(String docComment) {
    if (docComment.isEmpty()) {
      return docComment;
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
            // Drop the SeverityLevel comment, if it exists.
            .filter(s -> !s.startsWith("Severity: {@code SeverityLevel"))
            .collect(Collectors.toCollection(ArrayList::new));

    // After we stripped out the Severity comment, there may be trailing empty lines.  We remove
    // them here.
    while (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
      lines.remove(lines.size() - 1);
    }

    return lines.stream().collect(Collectors.joining("\n"));
  }

  /** Returns the number of leading whitespace characters at the beginning of the line. */
  private static int numberOfLeadingSpaces(String line) {
    int i = 0;
    while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
      i++;
    }
    return i;
  }
}
