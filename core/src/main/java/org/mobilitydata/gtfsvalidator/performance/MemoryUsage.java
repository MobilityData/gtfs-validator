package org.mobilitydata.gtfsvalidator.performance;

import java.text.DecimalFormat;
import org.apache.commons.lang3.StringUtils;

/** Represents memory usage information. */
public class MemoryUsage {
  private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");

  /**
   * The key value associated with the method is the registered memory usage. Example,
   * GtfsFeedLoader.loadTables
   */
  private String key;

  /**
   * The total amount of memory currently available for current and future objects, measured in
   * bytes. <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Runtime.html#totalMemory()"
   * target="_blank">Reference</a>
   */
  private long totalMemory;

  /**
   * The total amount of memory currently available for future allocated objects, measured in bytes.
   * <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Runtime.html#freeMemory()"
   * target="_blank">Reference</a>
   */
  private long freeMemory;

  /**
   * The maximum amount of memory that the virtual machine will attempt to use, measured in bytes.
   * <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Runtime.html#maxMemory()"
   * target="_blank">Reference</a>
   */
  private long maxMemory;

  /**
   * This is only applicable when the memory snapshot has the reference of its “previous” register,
   * for example, at the start and end of a method. The value reflects the free memory minus the
   * memory of the previous register(freeMemory - previousFreeMemory), measured in bytes. This
   * supports negative and positive values. Negative values mean the memory decreased at the end of
   * the method. Positive values mean the memory increased.
   */
  private Long diffMemory;

  public MemoryUsage() {}

  public MemoryUsage(
      String key, long totalMemory, long freeMemory, long maxMemory, Long diffMemory) {
    this.key = key;
    this.totalMemory = totalMemory;
    this.freeMemory = freeMemory;
    this.maxMemory = maxMemory;
    this.diffMemory = diffMemory;
  }

  /**
   * Converts bytes to human-readable memory.
   *
   * @param bytes
   * @return human-readable memory, e.g., "1.23 GiB"
   */
  public static String convertToHumanReadableMemory(Long bytes) {
    if (bytes == null) {
      return "N/A";
    }
    long size = Math.abs(bytes);
    if (size < 1024) {
      return bytes + " bytes";
    }
    if (size < 1048576) {
      return TWO_DECIMAL_FORMAT.format(Math.copySign(size / 1024.0, bytes)) + " KiB";
    }
    if (size < 1073741824) {
      return TWO_DECIMAL_FORMAT.format(Math.copySign(size / 1048576.0, bytes)) + " MiB";
    }
    if (size < 1099511627776L) {
      return TWO_DECIMAL_FORMAT.format(Math.copySign(size / 1073741824.0, bytes)) + " GiB";
    }
    return TWO_DECIMAL_FORMAT.format(Math.copySign(size / 1099511627776L, bytes)) + " TiB";
  }

  /**
   * The memory used is computed as the difference between the total memory and the free memory.
   *
   * @return the memory used.
   */
  public long usedMemory() {
    return totalMemory - freeMemory;
  }

  /**
   * Returns a human-readable string representation of the memory usage.
   *
   * @return a human-readable string representation of the memory usage.
   */
  public String humanReadablePrint() {
    StringBuffer result = new StringBuffer();
    result.append("Memory usage registered");
    if (StringUtils.isNotBlank(key)) {
      result.append(" for key: ").append(key);
    } else {
      result.append(":");
    }
    result.append(" Max: ").append(convertToHumanReadableMemory(maxMemory));
    result.append(" Total: ").append(convertToHumanReadableMemory(totalMemory));
    result.append(" Free: ").append(convertToHumanReadableMemory(freeMemory));
    result.append(" Used: ").append(convertToHumanReadableMemory(usedMemory()));
    result.append(" Diff: ").append(convertToHumanReadableMemory(diffMemory));
    return result.toString();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getTotalMemory() {
    return totalMemory;
  }

  public void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }

  public long getFreeMemory() {
    return freeMemory;
  }

  public void setFreeMemory(long freeMemory) {
    this.freeMemory = freeMemory;
  }

  public long getMaxMemory() {
    return maxMemory;
  }

  public void setMaxMemory(long maxMemory) {
    this.maxMemory = maxMemory;
  }

  public Long getDiffMemory() {
    return diffMemory;
  }

  public void setDiffMemory(Long diffMemory) {
    this.diffMemory = diffMemory;
  }

  @Override
  public String toString() {
    return "MemoryUsage{"
        + "key="
        + key
        + ", "
        + "totalMemory="
        + totalMemory
        + ", "
        + "freeMemory="
        + freeMemory
        + ", "
        + "maxMemory="
        + maxMemory
        + ", "
        + "diffMemory="
        + diffMemory
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof MemoryUsage) {
      MemoryUsage that = (MemoryUsage) o;
      return this.key.equals(that.getKey())
          && this.totalMemory == that.getTotalMemory()
          && this.freeMemory == that.getFreeMemory()
          && this.maxMemory == that.getMaxMemory()
          && (this.diffMemory == null
              ? that.getDiffMemory() == null
              : this.getDiffMemory().equals(that.getDiffMemory()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= key.hashCode();
    h$ *= 1000003;
    h$ ^= (int) ((totalMemory >>> 32) ^ totalMemory);
    h$ *= 1000003;
    h$ ^= (int) ((freeMemory >>> 32) ^ freeMemory);
    h$ *= 1000003;
    h$ ^= (int) ((maxMemory >>> 32) ^ maxMemory);
    h$ *= 1000003;
    h$ ^= (diffMemory == null) ? 0 : diffMemory.hashCode();
    return h$;
  }
}
