package org.mobilitydata.gtfsvalidator.report.model;

import java.util.Objects;

public class FeatureMetadata {
  private final String featureName;
  private final String featureGroup;
  private static final String BASE_DOC_URL = "https://gtfs.org/getting_started/features/";

  public FeatureMetadata(String featureName, String featureGroup) {
    this.featureName = featureName;
    this.featureGroup = featureGroup != null ? featureGroup : "base_add-ons";
  }

  public String getFeatureName() {
    return featureName;
  }

  public String getDocUrl() {
    String formattedFeatureName = featureName.toLowerCase().replace(' ', '-');
    String formattedFeatureGroup = featureGroup.toLowerCase().replace(' ', '_');
    return BASE_DOC_URL + formattedFeatureGroup + "/#" + formattedFeatureName;
  }

  // Override equals and hashCode to use featureName as key
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FeatureMetadata that = (FeatureMetadata) o;
    return Objects.equals(featureName, that.featureName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(featureName);
  }
}
