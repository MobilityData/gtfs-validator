package org.mobilitydata.gtfsvalidator.util.geojson;

public class DuplicateJsonKeyException extends RuntimeException {
  private String key;

  public DuplicateJsonKeyException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
