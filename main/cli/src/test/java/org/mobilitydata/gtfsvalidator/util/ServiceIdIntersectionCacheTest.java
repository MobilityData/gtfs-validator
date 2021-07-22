package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServiceIdIntersectionCacheTest {

  @Test
  public void findIntersectingDate() {
    final ServiceIdIntersectionCache cache =
        new ServiceIdIntersectionCache(
            ImmutableMap.of(
                "s1",
                ImmutableSortedSet.of(
                    LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 6)),
                "s2",
                ImmutableSortedSet.of(
                    LocalDate.of(2021, 1, 1),
                    LocalDate.of(2021, 1, 5),
                    LocalDate.of(2021, 1, 6),
                    LocalDate.of(2021, 1, 7))));

    // The first call puts the data to cache and the second retrieves it from
    // there.
    assertThat(cache.getCacheSize()).isEqualTo(0);
    assertThat(cache.findIntersectingDate("s1", "s2"))
        .isEqualTo(Optional.of(LocalDate.of(2021, 1, 5)));
    assertThat(cache.getCacheSize()).isEqualTo(1);
    assertThat(cache.findIntersectingDate("s1", "s2"))
        .isEqualTo(Optional.of(LocalDate.of(2021, 1, 5)));
    assertThat(cache.getCacheSize()).isEqualTo(1);
    assertThat(cache.findIntersectingDate("s2", "s1"))
        .isEqualTo(Optional.of(LocalDate.of(2021, 1, 5)));
    assertThat(cache.getCacheSize()).isEqualTo(1);

    // Test non-existent services.
    assertThat(cache.findIntersectingDate("s1", "notFound")).isEqualTo(Optional.empty());
    assertThat(cache.getCacheSize()).isEqualTo(2);
    assertThat(cache.findIntersectingDate("notFound", "notFound")).isEqualTo(Optional.empty());
    assertThat(cache.getCacheSize()).isEqualTo(3);
  }
}
