package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;

public abstract class GtfsFieldLoader<T extends GtfsEntityBuilder, V> {
  public abstract void load(
      RowParser rowParser,
      int columnIndex,
      GtfsColumnDescriptor columnDescriptor,
      @Nullable FieldCache<V> fieldCache,
      T builder);

  protected static <V> V addToCacheIfPresent(V value, @Nullable FieldCache<V> fieldCache) {
    if (fieldCache == null) {
      return value;
    }
    return fieldCache.addIfAbsent(value);
  }
}
