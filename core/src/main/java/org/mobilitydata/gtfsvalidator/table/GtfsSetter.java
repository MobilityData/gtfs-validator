package org.mobilitydata.gtfsvalidator.table;

@FunctionalInterface
public interface GtfsSetter<K extends Object, V extends Object> {
  K setValue(K builder, V value);

  static <K extends GtfsEntity, V> GtfsSetter<GtfsEntityBuilder<K>, V> noopSetter() {
    return (builder, value) -> builder;
  }
}
