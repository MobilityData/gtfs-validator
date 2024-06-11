package org.mobilitydata.gtfsvalidator.columns;

import com.google.common.base.Preconditions;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A factory for producing various collection classes that are designed to hold {@link
 * GtfsColumnBasedEntity} entries in a memory efficient way.
 *
 * <p>The main idea is that instead of holding an entity reference directly, we just store the
 * entity's row index in the column store instead and only construct the entity facade when needed.
 */
public class GtfsColumnBasedCollectionFactory<T extends GtfsColumnBasedEntity> {

  /**
   * Factory method for constructing an entity facade.
   *
   * @param <T>
   */
  public interface EntityFactory<T extends GtfsColumnBasedEntity> {
    T create(GtfsColumnStore store, GtfsColumnAssignments assignments, int rowIndex);
  }

  private final GtfsColumnStore store;
  private final GtfsColumnAssignments assignments;
  private final EntityFactory<T> entityFactory;

  public GtfsColumnBasedCollectionFactory(
      GtfsColumnStore store, GtfsColumnAssignments assignments, EntityFactory<T> entityFactory) {
    this.store = store;
    this.assignments = assignments;
    this.entityFactory = entityFactory;
  }

  /**
   * If the specified collection is associated with a collection factory, the collection factory
   * instance will be returned.
   */
  public static <T> Optional<GtfsColumnBasedCollectionFactory> getForList(List<T> c) {
    if (c instanceof HasFactory) {
      return Optional.of(((HasFactory) c).getFactory());
    }
    return Optional.empty();
  }

  /**
   * Ensures that a given entity is associated with the column store and assignments of this
   * collection factory. Trying to use an entity with a different store or column assignments would
   * cause errors.
   */
  private void checkOwnership(T entity) {
    Preconditions.checkArgument(this.store == entity.store, "store mismatch");
    Preconditions.checkArgument(
        this.assignments == entity.getAssignments(), "assignments mismatch");
  }

  /**
   * Returns a List designed to contain all entities in the column store. Entities must be added
   * sequentially in order. This is more memory efficient than {@link #createSomeEntitiesList()}.
   */
  public List<T> createAllEntitiesList() {
    return new AllEntitiesListImpl();
  }

  private class AllEntitiesListImpl extends AbstractList<T> implements HasFactory<T> {

    private int size = 0;

    @Override
    public boolean add(T t) {
      checkOwnership(t);
      Preconditions.checkArgument(t.rowIndex == size);
      size++;
      return true;
    }

    @Override
    public T get(int index) {
      return entityFactory.create(store, assignments, index);
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public GtfsColumnBasedCollectionFactory<T> getFactory() {
      return GtfsColumnBasedCollectionFactory.this;
    }
  }

  /**
   * Returns a List designed to contain some subset of entities in the column store, in arbitrary
   * order. This is less memory efficient than {@link #createAllEntitiesList()}.
   */
  public List<T> createSomeEntitiesList() {
    return new SomeEntitiesListImpl();
  }

  private class SomeEntitiesListImpl extends AbstractList<T> implements HasFactory<T> {

    private int[] indices = new int[10];

    private int size = 0;

    @Override
    public boolean add(T t) {
      checkOwnership(t);

      size++;

      if (indices.length < size) {
        int newCapacity = indices.length;
        while (newCapacity < size) {
          newCapacity <<= 1;
        }
        this.indices = Arrays.copyOf(this.indices, newCapacity);
      }

      this.indices[size - 1] = t.rowIndex;

      return true;
    }

    @Override
    public T get(int index) {
      return entityFactory.create(store, assignments, indices[index]);
    }

    @Override
    public T set(int index, T element) {
      checkOwnership(element);
      int existingRowIndex = this.indices[index];
      this.indices[index] = element.rowIndex;
      return entityFactory.create(store, assignments, existingRowIndex);
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public GtfsColumnBasedCollectionFactory<T> getFactory() {
      return GtfsColumnBasedCollectionFactory.this;
    }
  }

  /** Returns a Map designed to store entities as values. */
  public <K> Map<K, T> createSomeEntitiesMap() {
    return new MapImpl<>();
  }

  private class MapImpl<K> extends AbstractMap<K, T> {
    private final Map<K, Integer> indicesByKey = new HashMap<>();

    private final EntrySetAdapter entrySetAdapter = new EntrySetAdapter();

    @Override
    public boolean containsKey(Object key) {
      return indicesByKey.containsKey(key);
    }

    @Override
    public T get(Object key) {
      Integer index = indicesByKey.get(key);
      if (index == null) {
        return null;
      }
      return entityFactory.create(store, assignments, index);
    }

    @Override
    public T put(K key, T value) {
      checkOwnership(value);
      Integer existingIndex = indicesByKey.put(key, value.rowIndex);
      if (existingIndex == null) {
        return null;
      }
      return entityFactory.create(store, assignments, existingIndex);
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public T remove(Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, T>> entrySet() {
      return entrySetAdapter;
    }

    private class EntrySetAdapter extends AbstractSet<Map.Entry<K, T>> {

      @Override
      public Iterator<Entry<K, T>> iterator() {
        Iterator<Entry<K, Integer>> iterator = indicesByKey.entrySet().iterator();
        return new Iterator<>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public Entry<K, T> next() {
            Entry<K, Integer> entry = iterator.next();
            return new Map.Entry<K, T>() {

              @Override
              public K getKey() {
                return entry.getKey();
              }

              @Override
              public T getValue() {
                return entityFactory.create(store, assignments, entry.getValue());
              }

              @Override
              public T setValue(T value) {
                throw new UnsupportedOperationException();
              }
            };
          }
        };
      }

      @Override
      public int size() {
        return indicesByKey.size();
      }
    }
  }

  private interface HasFactory<T extends GtfsColumnBasedEntity> {
    GtfsColumnBasedCollectionFactory<T> getFactory();
  }
}
