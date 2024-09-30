package org.mobilitydata.gtfsvalidator.performance;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class MemoryMonitorAspect {

  //  @Around("@annotation(MemoryMonitor)")
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

  private String extractKey(ProceedingJoinPoint joinPoint) {
    var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    var memoryMonitor = method.getAnnotation(MemoryMonitor.class);
    return memoryMonitor != null && StringUtils.isNotBlank(memoryMonitor.key())
        ? memoryMonitor.key()
        : method.getDeclaringClass().getCanonicalName() + "." + method.getName();
  }
}
