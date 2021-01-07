package org.mobilitydata.gtfsvalidator.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * A helper class for storing cached results that two given services overlap.
 * <p>
 * This class is not thread-safe.
 */
public class ServiceIdIntersectionCache {
    static private final LocalDate NO_OVERLAP = LocalDate.EPOCH;
    private final Map<String, SortedSet<LocalDate>> serviceDates;
    // The cache stores LocalDate instead of Optional<LocalDate> for size
    // efficiency. If services do not overlap, we store null.
    private final Table<String, String, LocalDate> cache =
            HashBasedTable.create();

    /**
     * Creates an intersection cache from the given service dates.
     *
     * @param serviceDates mapping from service id to a set of included days
     */
    public ServiceIdIntersectionCache(Map<String, SortedSet<LocalDate>> serviceDates) {
        this.serviceDates = serviceDates;
    }

    /**
     * Finds the first intersecting date in the given sets, if any, and caches the result. Returns
     * {@code Optional.empty()} if there is no intersection.
     * <p>
     * Note that if either service has no active service dates, then the method always returns false, even if the
     * service ids are the same.
     *
     * @param serviceId1 the first service id
     * @param serviceId2 the second service id
     * @return the first intersecting date or {@code Optional.empty()}
     */
    public Optional<LocalDate> findIntersectingDate(String serviceId1, String serviceId2) {
        // We generate the cache key with the smallest service id coming first.
        if (serviceId1.compareTo(serviceId2) > 0) {
            String tmp = serviceId2;
            serviceId2 = serviceId1;
            serviceId1 = tmp;
        }
        LocalDate inCache = cache.get(serviceId1, serviceId2);
        if (inCache != null) {
            return inCache.equals(NO_OVERLAP) ? Optional.empty()
                    : Optional.of(inCache);
        }
        SortedSet<LocalDate> dates1 = serviceDates.get(serviceId1);
        SortedSet<LocalDate> dates2 = serviceDates.get(serviceId2);
        final Optional<LocalDate> intersection =
                dates1 == null || dates2 == null ?
                        Optional.empty() :
                        CalendarUtil.firstIntersectingDate(dates1, dates2);
        cache.put(serviceId1, serviceId2, intersection.orElse(NO_OVERLAP));
        return intersection;
    }

    /**
     * Returns the cache size.
     *
     * @return cache size.
     */
    public int getCacheSize() {
        return cache.size();
    }
}

