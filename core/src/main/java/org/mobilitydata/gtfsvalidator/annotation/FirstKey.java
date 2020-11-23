package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the first part of a composite key in tables like stop_times.txt (trip_id).
 * <p>
 * This annotation needs to be used in a combination with {@code @SequenceKey}.
 * <p>
 * Note that {@code @FirstKey} does not imply that the field is required and you need to put an extra {@code @Required}
 * annotation in this case.
 *
 * <pre>
 *   @GtfsTable("stop_times.txt")
 *   public interface GtfsStopTimeSchema extends GtfsEntity {
 *     @Required
 *     @ForeignKey(table = "trips.txt", field = "trip_id")
 *     @FirstKey
 *     String tripId();
 *
 *     @Required
 *     @NonNegative
 *     @SequenceKey
 *     int stopSequence();
 *  }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FirstKey {
}
