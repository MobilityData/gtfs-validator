package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * Rows of a table declared with {@code @GtfsTable(storage = TableStorage.COLUMNAR)}, and their
 * indices.
 *
 * <p>Once the table reaches {@code columnThreshold} rows, rows are kept as primitive columns by a
 * generated {@link Columns}, and entities are created on access. Columns are split into chunks of
 * {@link #CHUNK_SIZE} rows, so that they grow without copying and without arrays large enough to
 * need contiguous heap regions. See docs/ARCHITECTURE.md.
 */
public final class ColumnarEntityList<T extends GtfsEntity> extends AbstractList<T>
    implements RandomAccess {

  /** Row {@code r} of a column is at {@code column[r >>> CHUNK_SHIFT][r & CHUNK_MASK]}. */
  public static final int CHUNK_SHIFT = 15;

  public static final int CHUNK_SIZE = 1 << CHUNK_SHIFT;
  public static final int CHUNK_MASK = CHUNK_SIZE - 1;

  /** Columns of a table, generated from its schema. */
  public interface Columns<T> {
    /** Sets a row. Rows are set in order, starting from 0. */
    void set(int row, T entity);

    T get(int row);

    /** Releases what is only needed while rows are added. */
    void compact();

    /** Number of indexed fields. */
    int indexCount();

    /** Distinct values of an indexed field. */
    ColumnDictionary<?> dictionary(int index);

    /** Code of an indexed field of a row. */
    int codeAt(int index, int row);

    /** Code of an indexed field of the entity, added to the dictionary if it is new. */
    int code(int index, T entity);

    /**
     * Index whose rows are sorted by the sequence field, and whose rows with the same key and
     * sequence are reported as duplicate keys; -1 if the table has no sequence.
     */
    int sequenceIndex();

    /** Sequence of a row. */
    int sequenceAt(int row);

    int sequence(T entity);

    /** Names of the primary key columns that the entity has, separated by commas. */
    String definedKeys(T entity);

    /** Values of the primary key columns that the entity has, separated by commas. */
    Object definedValues(T entity);
  }

  private final Columns<T> columns;
  private final int columnThreshold;
  private int size;
  @Nullable private List<T> objects = new ArrayList<>();

  // For each index, the rows of group g are rows[starts[g]] to rows[starts[g + 1] - 1].
  private int[][] starts;
  private int[][] rows;

  public ColumnarEntityList(Columns<T> columns, int columnThreshold) {
    this.columns = columns;
    this.columnThreshold = columnThreshold;
  }

  public boolean isColumnar() {
    return objects == null;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public T get(int row) {
    Objects.checkIndex(row, size);
    return objects != null ? objects.get(row) : columns.get(row);
  }

  @Override
  public boolean add(T entity) {
    if (objects != null && size >= columnThreshold) {
      toColumns();
    }
    if (objects != null) {
      objects.add(entity);
    } else {
      columns.set(size, entity);
    }
    size++;
    return true;
  }

  private void toColumns() {
    List<T> entities = objects;
    objects = null;
    for (int r = 0; r < entities.size(); r++) {
      columns.set(r, entities.get(r));
    }
  }

  /** Builds the indices and reports duplicate keys. Must be called once, after the last add. */
  public void finish(String filename, NoticeContainer noticeContainer) {
    int n = size;
    columns.compact();
    int indexCount = columns.indexCount();
    starts = new int[indexCount][];
    rows = new int[indexCount][];
    for (int i = 0; i < indexCount; i++) {
      IntUnaryOperator groupOf;
      if (objects == null) {
        int index = i;
        groupOf = (r) -> columns.codeAt(index, r);
      } else {
        // Codes are added to the dictionary here, so they are computed before it is sized.
        int[] codes = new int[n];
        for (int r = 0; r < n; r++) {
          codes[r] = columns.code(i, objects.get(r));
        }
        groupOf = (r) -> codes[r];
      }
      starts[i] = new int[columns.dictionary(i).size() + 1];
      rows[i] = groupRows(groupOf, n, starts[i]);
    }
    int s = columns.sequenceIndex();
    if (s >= 0) {
      sortBySequence(starts[s], rows[s], filename, noticeContainer);
    }
  }

  /** Sorts the rows of each group by sequence, then by row, and reports duplicate sequences. */
  private void sortBySequence(
      int[] start, int[] groupRows, String filename, NoticeContainer noticeContainer) {
    IntUnaryOperator sequenceOf =
        objects == null ? columns::sequenceAt : (r) -> columns.sequence(objects.get(r));
    int longest = 0;
    for (int g = 0; g + 1 < start.length; g++) {
      longest = Math.max(longest, start[g + 1] - start[g]);
    }
    long[] keys = new long[longest];
    List<Long> duplicates = new ArrayList<>();
    for (int g = 0; g + 1 < start.length; g++) {
      int from = start[g];
      int length = start[g + 1] - from;
      for (int i = 0; i < length; i++) {
        int r = groupRows[from + i];
        keys[i] = ((long) sequenceOf.applyAsInt(r) << 32) | r;
      }
      Arrays.sort(keys, 0, length);
      int first = 0;
      for (int i = 0; i < length; i++) {
        groupRows[from + i] = (int) keys[i];
        if (i > 0 && (int) (keys[i] >> 32) == (int) (keys[i - 1] >> 32)) {
          duplicates.add(((long) groupRows[from + i] << 32) | groupRows[from + first]);
        } else {
          first = i;
        }
      }
    }
    Collections.sort(duplicates);
    for (long duplicate : duplicates) {
      T oldEntity = get((int) duplicate);
      T newEntity = get((int) (duplicate >>> 32));
      noticeContainer.addValidationNotice(
          new DuplicateKeyNotice(
              filename,
              oldEntity.csvRowNumber(),
              newEntity.csvRowNumber(),
              columns.definedKeys(oldEntity),
              columns.definedValues(oldEntity)));
    }
  }

  /** Returns the rows with the given value of an indexed field. */
  public List<T> byIndex(int index, Object key) {
    int g = columns.dictionary(index).find(key);
    return g < 0 ? Collections.emptyList() : group(index, g);
  }

  /** Returns the rows of each value of an indexed field, created on access. */
  @SuppressWarnings("unchecked")
  public <K> Map<K, List<T>> byIndexAsMap(int index) {
    Map<K, Integer> codes = (Map<K, Integer>) columns.dictionary(index).codes();
    return Maps.transformValues(codes, g -> group(index, g));
  }

  /** Returns the rows of each value of an indexed field, all created at once. */
  @SuppressWarnings("unchecked")
  public <K> ListMultimap<K, T> byIndexMultimap(int index) {
    ListMultimap<K, T> map = ArrayListMultimap.create();
    ((Map<K, Integer>) columns.dictionary(index).codes())
        .forEach((key, g) -> map.putAll(key, group(index, g)));
    return map;
  }

  /** Returns the first row, in file order, with the given key and sequence. */
  public Optional<T> byIndexAndSequence(int index, Object key, int sequence) {
    for (T entity : byIndex(index, key)) {
      if (columns.sequence(entity) == sequence) {
        return Optional.of(entity);
      }
    }
    return Optional.empty();
  }

  private List<T> group(int index, int g) {
    return new Rows(rows[index], starts[index][g], starts[index][g + 1]);
  }

  /** Rows of an index range. In columnar mode, each row is created once per list. */
  private final class Rows extends AbstractList<T> implements RandomAccess {
    private final int[] rows;
    private final int from;
    private final int to;
    private Object[] cache;

    Rows(int[] rows, int from, int to) {
      this.rows = rows;
      this.from = from;
      this.to = to;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int i) {
      Objects.checkIndex(i, to - from);
      int r = rows[from + i];
      if (objects != null) {
        return objects.get(r);
      }
      if (cache == null) {
        cache = new Object[to - from];
      }
      if (cache[i] == null) {
        cache[i] = columns.get(r);
      }
      return (T) cache[i];
    }

    @Override
    public int size() {
      return to - from;
    }
  }

  private static int[] groupRows(IntUnaryOperator groupOf, int n, int[] start) {
    int[] rows = new int[n];
    for (int r = 0; r < n; r++) {
      start[groupOf.applyAsInt(r) + 1]++;
    }
    for (int g = 0; g + 1 < start.length; g++) {
      start[g + 1] += start[g];
    }
    int[] next = Arrays.copyOf(start, start.length);
    for (int r = 0; r < n; r++) {
      rows[next[groupOf.applyAsInt(r)]++] = r;
    }
    return rows;
  }
}
