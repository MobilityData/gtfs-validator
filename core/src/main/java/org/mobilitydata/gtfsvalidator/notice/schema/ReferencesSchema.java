package org.mobilitydata.gtfsvalidator.notice.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the documentation references of validation notice in the JSON validation schema
 * export.
 *
 * <p>This class is intended for JSON serialization with a structure that is publicly exposed. Be
 * thoughtful when making changes.
 */
public class ReferencesSchema {

  /** References to files in the GTFS spec (e.g. `stops.txt`). */
  private final List<String> fileReferences = new ArrayList<>();

  /** References to files in the GTFS Best Practices document (e.g. `stops.txt`). */
  private final List<String> bestPracticesFileReferences = new ArrayList();

  private final List<String> sectionReferences = new ArrayList<>();

  /** References to arbitrary URLs. */
  private final List<UrlReference> urlReferences = new ArrayList<>();

  public boolean isEmpty() {
    return fileReferences.isEmpty()
        && bestPracticesFileReferences.isEmpty()
        && sectionReferences.isEmpty()
        && urlReferences.isEmpty();
  }

  public List<String> getFileReferences() {
    return this.fileReferences;
  }

  public void addFileReference(String fileName) {
    fileReferences.add(fileName);
  }

  public List<String> getBestPracticesFileReferences() {
    return this.bestPracticesFileReferences;
  }

  public void addBestPracticesFileReference(String fileName) {
    bestPracticesFileReferences.add(fileName);
  }

  public List<String> getSectionReferences() {
    return this.sectionReferences;
  }

  public void addSectionReference(String sectionReference) {
    this.sectionReferences.add(sectionReference);
  }

  public List<UrlReference> getUrlReferences() {
    return this.urlReferences;
  }

  public void addUrlReference(UrlReference ref) {
    urlReferences.add(ref);
  }

  public static class UrlReference {
    private final String label;
    private final String url;

    public UrlReference() {
      this(null, null);
    }

    public UrlReference(String label, String url) {
      this.label = label;
      this.url = url;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      UrlReference that = (UrlReference) o;
      return Objects.equals(label, that.label) && Objects.equals(url, that.url);
    }

    @Override
    public String toString() {
      return "UrlReference{" + "label='" + label + '\'' + ", url='" + url + '\'' + '}';
    }
  }
}
