package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A bounded priority queue that keeps the N smallest elements. If the queue is full and a new
 * element is offered, the largest element is removed. The smallest element is computed using a
 * comparator or its natural order.
 *
 * @param <E>
 */
public class BoundedPriorityQueue<E> extends PriorityQueue<E> {
  private final int maxCapacity;

  public BoundedPriorityQueue(int maxCapacity) {
    super();
    if (maxCapacity <= 0) {
      throw new IllegalArgumentException("Max capacity must be greater than zero");
    }
    this.maxCapacity = maxCapacity;
  }

  public BoundedPriorityQueue(int maxCapacity, int initialCapacity, Comparator<E> comparator) {
    super(initialCapacity, comparator);
    if (maxCapacity <= 0) {
      throw new IllegalArgumentException("Max capacity must be greater than zero");
    }
    this.maxCapacity = maxCapacity;
  }

  @Override
  public boolean offer(E e) {
    if (size() >= maxCapacity) {
      E head = peek();
      if (head != null && compare(e, head) > 0) {
        poll();
      } else {
        return false;
      }
    }
    return super.offer(e);
  }

  @SuppressWarnings("unchecked")
  private int compare(E a, E b) {
    if (comparator() != null) {
      return comparator().compare(a, b);
    }
    return ((Comparable<? super E>) a).compareTo(b);
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }
}
