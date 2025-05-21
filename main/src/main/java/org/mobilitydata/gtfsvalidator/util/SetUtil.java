package org.mobilitydata.gtfsvalidator.util;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {
  /**
   * Given a collection of sets, compute their intersection (the set of elements present in all of
   * them).
   */
  public static <T> Set<T> intersectAll(Collection<Set<T>> sets) {
    if (sets.isEmpty()) {
      return emptySet();
    }

    Set<T> intersection = null;
    for (Set<T> set : sets) {
      if (intersection == null) {
        intersection = new HashSet<>(set);
      } else {
        intersection.retainAll(set);
      }
    }
    return intersection;
  }
}
