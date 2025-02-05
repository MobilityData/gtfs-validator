package org.mobilitydata.gtfsvalidator.util.geojson;

public class UnknownJsonKeyException extends RuntimeException {
  private String key;

  public UnknownJsonKeyException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}