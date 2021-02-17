package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;
import java.time.ZonedDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;

/**
 * A read-only context passed to particular validator objects. It gives information relevant for
 * validation: properties of the feed as a whole, system properties (current time) etc.
 */
@AutoValue
public abstract class ValidationContext {
  public static Builder builder() {
    return new AutoValue_ValidationContext.Builder();
  }

  /**
   * Represents a name of a GTFS feed, such as "nl-openov".
   *
   * @return the @code{GtfsFeedName} representing the feed's name
   */
  public abstract GtfsFeedName feedName();

  /**
   * The time when validation started.
   *
   * <p>Validator code should use this property instead of calling LocalDate.now() etc. for the
   * following reasons:
   *
   * <ul>
   *   <li>current date and time is changing but it should not randomly affect validation notices;
   *   <li>unit tests may need to override the current time.
   * </ul>
   *
   * @return The time when validation started as @code{ZonedDateTime}
   */
  public abstract ZonedDateTime now();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setFeedName(GtfsFeedName feedName);

    public abstract Builder setNow(ZonedDateTime now);

    public abstract ValidationContext build();
  }
}
