package org.mobilitydata.gtfsvalidator.performance;

import com.google.auto.value.AutoValue;
import java.text.DecimalFormat;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

@AutoValue
public abstract class MemoryUsage {
  private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");

  public static MemoryUsage create(
      String key, long totalMemory, long freeMemory, long maxMemory, Long memoryDiff) {
    return new AutoValue_MemoryUsage(key, totalMemory, freeMemory, maxMemory, memoryDiff);
  }

  public static String convertToHumanReadableMemory(Long size) {
    if (size == null) {
      return "N/A";
    }
    if (size <= 0) {
      return "0";
    }
    if (size < 1024) {
      return size + " bytes";
    }
    if (size < 1048576) {
      return TWO_DECIMAL_FORMAT.format(size / 1024.0) + " KiB";
    }
    if (size < 1073741824) {
      return TWO_DECIMAL_FORMAT.format(size / 1048576.0) + " MiB";
    }
    if (size < 1099511627776L) {
      return TWO_DECIMAL_FORMAT.format(size / 1073741824.0) + " GiB";
    }
    return TWO_DECIMAL_FORMAT.format(size / 1099511627776L) + " TiB";
  }

  public abstract String key();

  public abstract long totalMemory();

  public abstract long freeMemory();

  public abstract long maxMemory();

  @Nullable
  public abstract Long diffMemory();

  public long usedMemory() {
    return totalMemory() - freeMemory();
  }

  public String humanReadablePrint() {
    StringBuffer result = new StringBuffer();
    result.append("Memory usage registered");
    if (StringUtils.isNotBlank(key())) {
      result.append(" for key: ").append(key());
    } else {
      result.append(":");
    }
    result.append(" Max: ").append(convertToHumanReadableMemory(maxMemory()));
    result.append(" Total: ").append(convertToHumanReadableMemory(totalMemory()));
    result.append(" Free: ").append(convertToHumanReadableMemory(freeMemory()));
    result.append(" Used: ").append(convertToHumanReadableMemory(usedMemory()));
    result.append(" Diff: ").append(convertToHumanReadableMemory(diffMemory()));
    return result.toString();
  }
}
