package org.mobilitydata.gtfsvalidator.performance;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/** Aspect to monitor memory usage of a method. */
@Aspect
public class MemoryMonitorAspect {

  @Around("execution(@org.mobilitydata.gtfsvalidator.performance.MemoryMonitor * *(..))")
  public Object monitorMemoryUsage(ProceedingJoinPoint joinPoint) throws Throwable {
    String key = extractKey(joinPoint);
    MemoryUsage before = MemoryUsageRegister.getInstance().getMemoryUsageSnapshot(key, null);
    try {
      Object result = joinPoint.proceed();
      return result;
    } finally {
      MemoryUsage after = MemoryUsageRegister.getInstance().getMemoryUsageSnapshot(key, before);
      MemoryUsageRegister.getInstance().registerMemoryUsage(after);
    }
  }

  /**
   * Extracts the key from the method signature or the annotation.
   *
   * @param joinPoint the join point
   * @return the key either from the annotation or the method signature.
   */
  private String extractKey(ProceedingJoinPoint joinPoint) {
    var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    var memoryMonitor = method.getAnnotation(MemoryMonitor.class);
    return memoryMonitor != null && StringUtils.isNotBlank(memoryMonitor.key())
        ? memoryMonitor.key()
        : method.getDeclaringClass().getCanonicalName() + "." + method.getName();
  }
}
