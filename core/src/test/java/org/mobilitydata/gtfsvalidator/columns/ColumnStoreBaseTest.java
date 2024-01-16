package org.mobilitydata.gtfsvalidator.columns;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.columns.ColumnStoreBase.hasPrimitive;
import static org.mobilitydata.gtfsvalidator.columns.ColumnStoreBase.reserveColumn;
import static org.mobilitydata.gtfsvalidator.columns.ColumnStoreBase.reservePrimitivePresence;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ColumnStoreBaseTest {

  @Test
  public void testReserveColumn() {
    List<String[]> values = new ArrayList<>();
    values.add(new String[2]);

    int columnIndex = reserveColumn(values, String.class, 3);

    assertThat(columnIndex).isEqualTo(1);
    assertThat(values).hasSize(2);
    assertThat(values.get(1)).asList().containsExactly(null, null, null);
  }

  @Test
  public void testReservePrimitivePresence_newColumn() {
    List<byte[]> presence = new ArrayList<>();

    reservePrimitivePresence(presence, 0, 3);

    assertThat(presence).hasSize(1);
    assertThat(presence.get(0)).asList().containsExactly(b(0), b(0), b(0));
  }

  @Test
  public void testReservePrimitivePresence_existingColumn() {
    List<byte[]> presence = new ArrayList<>();
    presence.add(new byte[2]);

    // None of these should trigger a new column, since they fit within the existing 8 bits of
    // a byte.
    reservePrimitivePresence(presence, 0, 3);
    reservePrimitivePresence(presence, 3, 3);
    reservePrimitivePresence(presence, 7, 3);

    assertThat(presence).hasSize(1);
    assertThat(presence.get(0)).asList().containsExactly(b(0), b(0));
  }

  @Test
  public void testReservePrimitivePresence_extendColumns() {
    List<byte[]> presence = new ArrayList<>();
    presence.add(new byte[2]);

    reservePrimitivePresence(presence, 8, 3);

    assertThat(presence).hasSize(2);
    assertThat(presence.get(0)).asList().containsExactly(b(0), b(0));
    assertThat(presence.get(1)).asList().containsExactly(b(0), b(0), b(0));
  }

  @Test
  public void testHasPrimitive() {
    List<byte[]> presence = new ArrayList<>();
    presence.add(new byte[] {b(0b00001010), b(0b01010000)});
    presence.add(new byte[] {b(0b00000101), b(0b10100000)});

    assertThat(hasPrimitive(presence, 0, 0)).isFalse();
    assertThat(hasPrimitive(presence, 1, 0)).isTrue();
    assertThat(hasPrimitive(presence, 2, 0)).isFalse();
    assertThat(hasPrimitive(presence, 3, 0)).isTrue();
    assertThat(hasPrimitive(presence, 4, 0)).isFalse();
    assertThat(hasPrimitive(presence, 7, 0)).isFalse();
    assertThat(hasPrimitive(presence, 8, 0)).isTrue();
    assertThat(hasPrimitive(presence, 9, 0)).isFalse();
    assertThat(hasPrimitive(presence, 10, 0)).isTrue();
    assertThat(hasPrimitive(presence, 11, 0)).isFalse();

    assertThat(hasPrimitive(presence, 0, 1)).isFalse();
    assertThat(hasPrimitive(presence, 3, 1)).isFalse();
    assertThat(hasPrimitive(presence, 4, 1)).isTrue();
    assertThat(hasPrimitive(presence, 5, 1)).isFalse();
    assertThat(hasPrimitive(presence, 6, 1)).isTrue();
    assertThat(hasPrimitive(presence, 7, 1)).isFalse();
    assertThat(hasPrimitive(presence, 8, 1)).isFalse();
    assertThat(hasPrimitive(presence, 12, 1)).isFalse();
    assertThat(hasPrimitive(presence, 13, 1)).isTrue();
    assertThat(hasPrimitive(presence, 14, 1)).isFalse();
    assertThat(hasPrimitive(presence, 15, 1)).isTrue();
  }

  @Test
  public void testSetPrimitivePresence_existingCapacity() {
    List<byte[]> presence = new ArrayList<>();
    presence.add(new byte[2]);
    presence.add(new byte[2]);
    ColumnStoreBase.setPrimitivePresence(presence, 0, 0);
    ColumnStoreBase.setPrimitivePresence(presence, 2, 0);
    ColumnStoreBase.setPrimitivePresence(presence, 1, 1);
    ColumnStoreBase.setPrimitivePresence(presence, 10, 1);
    ColumnStoreBase.setPrimitivePresence(presence, 11, 0);

    assertThat(presence.get(0)).asList().containsExactly(b(0b00000101), b(0b00000010));
    assertThat(presence.get(1)).asList().containsExactly(b(0b00001000), b(0b00000100));
  }

  @Test
  public void testSetPrimitivePresence_extendCapacity() {
    List<byte[]> presence = new ArrayList<>();
    presence.add(new byte[2]);

    ColumnStoreBase.setPrimitivePresence(presence, 1, 0);
    ColumnStoreBase.setPrimitivePresence(presence, 2, 1);
    ColumnStoreBase.setPrimitivePresence(presence, 5, 2);
    ColumnStoreBase.setPrimitivePresence(presence, 6, 3);

    assertThat(presence.get(0))
        .asList()
        .containsExactly(b(0b00000010), b(0b00000100), b(0b00100000), b(0b01000000));
  }

  @Test
  public void testSetValue_withinCapacity() {
    List<String[]> values = new ArrayList<>();
    values.add(new String[2]);
    values.add(new String[2]);

    ColumnStoreBase.setValue(values, 0, 1, "a");

    assertThat(values.get(0)).asList().containsExactly(null, "a");
    assertThat(values.get(1)).asList().containsExactly(null, null);

    ColumnStoreBase.setValue(values, 1, 0, "b");

    assertThat(values.get(0)).asList().containsExactly(null, "a");
    assertThat(values.get(1)).asList().containsExactly("b", null);
  }

  @Test
  public void testSetValue_extendCapacity() {
    List<String[]> values = new ArrayList<>();
    values.add(new String[2]);

    ColumnStoreBase.setValue(values, 0, 0, "a");
    ColumnStoreBase.setValue(values, 0, 2, "c");

    assertThat(values.get(0)).asList().containsExactly("a", null, "c", null);
  }

  private static byte b(int v) {
    return (byte) v;
  }
}
