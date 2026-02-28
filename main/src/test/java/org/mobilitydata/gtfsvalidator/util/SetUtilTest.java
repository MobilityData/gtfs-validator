package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SetUtilTest {
  @Test
  public void testUnion() {
    Set<Integer> s1 = Set.of(1, 2, 3, 4, 5, 6);
    Set<Integer> s2 = Set.of(2, 4, 6, 8, 10);
    Set<Integer> s3 = Set.of();

    assertThat(SetUtil.union(s1, s2)).isEqualTo(Set.of(1, 2, 3, 4, 5, 6, 8, 10));
    assertThat(SetUtil.union(s1, s3)).isEqualTo(s1);
  }

  @Test
  public void testDifference() {
    Set<Integer> s1 = Set.of(1, 2, 3, 4, 5, 6);
    Set<Integer> s2 = Set.of(2, 4, 6, 8, 10);
    Set<Integer> s3 = Set.of();

    assertThat(SetUtil.difference(s1, s2)).isEqualTo(Set.of(1, 3, 5));
    assertThat(SetUtil.difference(s2, s1)).isEqualTo(Set.of(8, 10));
    assertThat(SetUtil.difference(s1, s3)).isEqualTo(s1);
    assertThat(SetUtil.difference(s3, s1)).isEqualTo(Set.of());
  }
}
