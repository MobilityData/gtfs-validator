package org.mobilitydata.gtfsvalidator.util;

import java.util.HashSet;
import java.util.Set;

/** Set operations that don't mutate the sets. */
public class SetUtil {
  public static <T> Set<T> union(Set<T> s1, Set<T> s2) {
    Set<T> s = new HashSet<>(s1);
    s.addAll(s2);
    return s;
  }

  public static <T> Set<T> difference(Set<T> s1, Set<T> s2) {
    Set<T> s = new HashSet<>(s1);
    s.removeAll(s2);
    return s;
  }
}
