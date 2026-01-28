package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SetUtilTest {
  @Test
  public void testIntersectAll() {
    assertThat(
            SetUtil.intersectAll(
                List.of(
                    Set.of(1, 2, 3, 4, 5, 6),
                    Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                    Set.of(2, 4, 6, 8, 10))))
        .isEqualTo(Set.of(2, 4, 6));

    assertThat(SetUtil.intersectAll(emptyList())).isEqualTo(emptySet());
  }
}
