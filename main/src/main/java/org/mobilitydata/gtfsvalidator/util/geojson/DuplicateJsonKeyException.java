package org.mobilitydata.gtfsvalidator.util.geojson;

public class DuplicateJsonKeyException extends RuntimeException {
  private String key;
  private String message;

  public DuplicateJsonKeyException(String key, String message) {
    this.key = key;
    this.message = message;
  }

  public String getKey() {
    return key;
  }

  public String getMessage() {
    return message;
  }
}
