package org.mobilitydata.gtfsvalidator.util.geojson;

public class UnknownJsonKeyException extends RuntimeException {
  private final String message;
  private String key;

  public UnknownJsonKeyException(String key, String message) {
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